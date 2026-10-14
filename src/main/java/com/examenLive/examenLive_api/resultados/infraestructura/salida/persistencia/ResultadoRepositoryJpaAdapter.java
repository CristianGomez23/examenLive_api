package com.examenLive.examenLive_api.resultados.infraestructura.salida.persistencia;

import com.examenLive.examenLive_api.resultados.dominio.Calificacion;
import com.examenLive.examenLive_api.resultados.dominio.EstadoResultado;
import com.examenLive.examenLive_api.resultados.dominio.Respuesta;
import com.examenLive.examenLive_api.resultados.dominio.Resultado;
import com.examenLive.examenLive_api.resultados.dominio.puertos.salida.RepositorioResultados;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
public class ResultadoRepositoryJpaAdapter implements RepositorioResultados {

    private final ResultadoRepository resultadoRepository;

    public ResultadoRepositoryJpaAdapter(ResultadoRepository resultadoRepository){
        this.resultadoRepository = resultadoRepository;
    }

    @Override
    public Resultado guardar(Resultado resultado){
        List<RespuestaResultadoEntity> respuestas = new ArrayList<>();
        for (Respuesta respuesta : resultado.respuestas()) {
            for (String valor : respuesta.valoresSeleccionados()) {
                respuestas.add(new RespuestaResultadoEntity(
                    respuesta.preguntaId(), valor, respuesta.enviadaEn()
                ));
            }
        }
        Calificacion calificacion = resultado.calificacion().orElse(null);
        resultadoRepository.save(new ResultadoEntity(
            resultado.resultadoId(),
            resultado.sesionId(),
            resultado.estudianteId(),
            resultado.creadoEn(),
            resultado.estado().name(),
            respuestas,
            calificacion == null ? null : calificacion.puntajeObtenido(),
            calificacion == null ? null : calificacion.puntajeMaximo(),
            calificacion == null ? null : calificacion.respuestasCorrectas(),
            calificacion == null ? null : calificacion.totalPreguntas()
        ));
        return resultado;
    }

    @Override
    public Optional<Resultado> buscarPorId(Long id){
        return resultadoRepository.findById(id).map(this::aDominio);
    }

    @Override
    public Optional<Resultado> buscarPorSesionYEstudiante(Long sesionId, Long estudianteId){
        return resultadoRepository.findFirstBySesionIdAndEstudianteId(sesionId, estudianteId)
            .map(this::aDominio);
    }

    @Override
    public List<Resultado> buscarPorEstudiante(Long estudianteId){
        return resultadoRepository.findByEstudianteIdOrderByCreadoEnDesc(estudianteId).stream()
            .map(this::aDominio)
            .toList();
    }

    private Resultado aDominio(ResultadoEntity e){
        // Agrupa las filas (pregunta, valor) en una Respuesta por pregunta.
        Map<Long, Set<String>> valores = new LinkedHashMap<>();
        Map<Long, java.time.Instant> instantes = new LinkedHashMap<>();
        for (RespuestaResultadoEntity fila : e.getRespuestas()) {
            valores.computeIfAbsent(fila.getPreguntaId(), id -> new LinkedHashSet<>()).add(fila.getValor());
            instantes.putIfAbsent(fila.getPreguntaId(), fila.getEnviadaEn());
        }
        List<Respuesta> respuestas = valores.entrySet().stream()
            .map(entrada -> new Respuesta(entrada.getKey(), entrada.getValue(), instantes.get(entrada.getKey())))
            .toList();
        Calificacion calificacion = e.getPuntajeObtenido() == null ? null : new Calificacion(
            e.getPuntajeObtenido(), e.getPuntajeMaximo(), e.getRespuestasCorrectas(), e.getTotalPreguntas()
        );
        return Resultado.reconstruir(
            e.getId(), e.getSesionId(), e.getEstudianteId(), e.getCreadoEn(),
            EstadoResultado.valueOf(e.getEstado()), respuestas, calificacion
        );
    }
}
