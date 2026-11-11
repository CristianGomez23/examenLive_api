package com.examenLive.examenesservice.comunicacion.infraestructura;


import com.examenLive.examenesservice.comunicacion.aplicacion.PublicadorEventos;
import com.examenLive.examenesservice.comunicacion.contratos.*;
import com.examenLive.examenesservice.examenes.dominio.IntentoExamen;
import com.examenLive.examenesservice.examenes.dominio.puertos.salida.NotificadorIntentoFinalizado;
import com.examenLive.examenesservice.examenes.dominio.PreguntaExamen;
import org.springframework.stereotype.Component;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.*;
import tools.jackson.databind.ObjectMapper;
import java.util.*;
import java.time.Instant;
@Component
public class IntentoFinalizadoOutbox implements NotificadorIntentoFinalizado {
    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;
    private final PublicadorEventos publicador;
    public IntentoFinalizadoOutbox(JdbcTemplate jdbc,ObjectMapper mapper,PublicadorEventos publicador) {
        this.jdbc=jdbc;this.mapper=mapper;this.publicador=publicador;
    }
    @Transactional(propagation=Propagation.MANDATORY)
    public void notificar(IntentoExamen intento,List<PreguntaExamen> preguntas) {
        if (!intento.finalizado()) throw new IllegalStateException("Sólo se publican intentos finalizados.");
        var datos=new DatosIntento(intento.intentoId(),intento.sesionId(),intento.estudianteId(),
            intento.respuestasPorPregunta().entrySet().stream().map(e -> new DatosIntento.Respuesta(e.getKey(),e.getValue())).toList(),
            preguntas.stream().map(p -> new DatosIntento.Pregunta(p.preguntaId(),p.respuestasCorrectas(),p.puntaje())).toList());
        int nuevos=jdbc.update("INSERT INTO calificacion_saga(intento_id,sesion_id,estudiante_id,snapshot) VALUES (?,?,?,?) ON CONFLICT DO NOTHING",
            datos.intentoId(),datos.sesionId(),datos.estudianteId(),mapper.writeValueAsString(datos));
        if (nuevos==0) return;
        var evento=new IntentoFinalizadoEvento(UUID.randomUUID(),1,Instant.now(),datos);
        publicador.guardar(evento.eventId(),"examenes.intentos-finalizados.v1",datos.intentoId().toString(),evento);
    }
}
