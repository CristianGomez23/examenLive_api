package com.examenLive.resultadosservice.resultados.aplicacion.servicios;


import com.examenLive.resultadosservice.comunicacion.aplicacion.*;
import com.examenLive.resultadosservice.comunicacion.contratos.*;
import com.examenLive.resultadosservice.resultados.dominio.*;
import com.examenLive.resultadosservice.resultados.dominio.puertos.entrada.*;
import com.examenLive.resultadosservice.resultados.dominio.puertos.salida.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.time.Instant;
@Service
public class ProcesadorIntentoFinalizado {
    private final ResultadoUseCase resultados;
    private final VinculosIntentos vinculos;
    private final PublicadorEventos eventos;
    private final RegistroMensajes inbox;
    public ProcesadorIntentoFinalizado(ResultadoUseCase resultados,VinculosIntentos vinculos,
            PublicadorEventos eventos,RegistroMensajes inbox) {
        this.resultados=resultados; this.vinculos=vinculos; this.eventos=eventos; this.inbox=inbox;
    }
    @Transactional
    public Optional<ResultadoGenerado> buscar(Long intentoId) {
        return vinculos.buscarResultado(intentoId).map(id -> new ResultadoGenerado(resultados.consultar(id),false));
    }
    @Transactional
    public void consumir(IntentoFinalizadoEvento evento) {
        evento.validar();
        if (!inbox.registrarSiNuevo("resultados-intentos-v1",evento.eventId())) return;
        procesar(evento.intento());
    }
    @Transactional
    public ResultadoGenerado procesar(DatosIntento intento) {
        intento.validar();
        // Serializa Kafka y REST para el mismo intento, incluso en varias réplicas.
        vinculos.bloquear(intento.intentoId());
        var previo=buscar(intento.intentoId());
        if (previo.isPresent()) return previo.get();
        Map<Long,Set<String>> respuestas=new LinkedHashMap<>();
        for (var r:intento.respuestas()) {
            if (r==null || r.preguntaId()==null || r.opcionesSeleccionadas()==null
                    || respuestas.containsKey(r.preguntaId()))
                throw new IllegalArgumentException("Respuesta inválida o duplicada.");
            respuestas.put(r.preguntaId(),r.opcionesSeleccionadas());
        }
        var preguntas=intento.preguntas().stream().map(p -> {
            if (p==null) throw new IllegalArgumentException("Pregunta nula.");
            return new PreguntaCalificable(p.preguntaId(),p.respuestasCorrectas(),p.puntaje());
        }).toList();
        var generado=resultados.generarResultado(intento.sesionId(),intento.estudianteId(),respuestas,preguntas);
        var r=generado.resultado();
        if (!generado.creado()) throw new IllegalStateException("Ya existe otro resultado para esta sesión y estudiante.");
        vinculos.guardar(intento.intentoId(),r.resultadoId(),r.sesionId(),r.estudianteId());
        var c=r.calificacion().orElseThrow();
        var evento=new ResultadoGeneradoEvento(UUID.randomUUID(),1,Instant.now(),intento.intentoId(),
            r.sesionId(),r.estudianteId(),r.resultadoId(),c.puntajeObtenido(),c.puntajeMaximo());
        eventos.guardar(evento.eventId(),"resultados.generados.v1",intento.intentoId().toString(),evento);
        return generado;
    }
}
