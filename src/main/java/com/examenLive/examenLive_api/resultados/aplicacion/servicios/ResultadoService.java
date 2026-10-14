package com.examenLive.examenLive_api.resultados.aplicacion.servicios;

import com.examenLive.examenLive_api.identidad.dominio.RecursoNoEncontradoException;
import com.examenLive.examenLive_api.resultados.aplicacion.ResultadoFactory;
import com.examenLive.examenLive_api.resultados.dominio.Calificacion;
import com.examenLive.examenLive_api.resultados.dominio.PreguntaCalificable;
import com.examenLive.examenLive_api.resultados.dominio.Respuesta;
import com.examenLive.examenLive_api.resultados.dominio.Resultado;
import com.examenLive.examenLive_api.resultados.dominio.puertos.entrada.ResultadoUseCase;
import com.examenLive.examenLive_api.resultados.dominio.puertos.salida.RepositorioResultados;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class ResultadoService implements ResultadoUseCase {

    private final RepositorioResultados repositorioResultados;
    private final ResultadoFactory resultadoFactory;
    private final CalificacionAutomaticaService calificacionAutomaticaService;

    public ResultadoService(RepositorioResultados repositorioResultados,
                            ResultadoFactory resultadoFactory,
                            CalificacionAutomaticaService calificacionAutomaticaService) {
        this.repositorioResultados = repositorioResultados;
        this.resultadoFactory = resultadoFactory;
        this.calificacionAutomaticaService = calificacionAutomaticaService;
    }

    @Override
    @Transactional
    public Resultado iniciar(Long sesionId, Long estudianteId) {
        return repositorioResultados.guardar(
            resultadoFactory.crear(sesionId, estudianteId)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Resultado consultar(Long resultadoId) {
        return repositorioResultados.buscarPorId(resultadoId)
            .orElseThrow(() -> new RecursoNoEncontradoException("Resultado no encontrado"));
    }

    @Override
    @Transactional
    public Resultado generarResultado(
            Long sesionId,
            Long estudianteId,
            Map<Long, Set<String>> respuestas,
            List<PreguntaCalificable> preguntas
    ) {
        Optional<Resultado> existente = repositorioResultados.buscarPorSesionYEstudiante(sesionId, estudianteId);
        if (existente.isPresent()) {
            return existente.get();
        }
        Resultado resultado = resultadoFactory.crear(sesionId, estudianteId);
        Instant ahora = Instant.now();
        respuestas.forEach((preguntaId, valores) ->
                resultado.registrarRespuesta(new Respuesta(preguntaId, valores, ahora))
        );
        cerrarYCalificar(resultado, preguntas);
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Resultado> buscarPorSesionYEstudiante(Long sesionId, Long estudianteId) {
        return repositorioResultados.buscarPorSesionYEstudiante(sesionId, estudianteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Resultado> consultarPorEstudiante(Long estudianteId) {
        return repositorioResultados.buscarPorEstudiante(estudianteId);
    }

    public void registrarRespuesta(Resultado resultado, Respuesta respuesta) {
        resultado.registrarRespuesta(respuesta);
        repositorioResultados.guardar(resultado);
    }

    public Calificacion cerrarYCalificar(Resultado resultado, List<PreguntaCalificable> preguntas) {
        resultado.cerrarRecepcionRespuestas();
        // calificar() ya registra la calificación dentro del resultado; registrarla de nuevo
        // lanzaba IllegalStateException porque el estado ya era CALIFICADO.
        Calificacion calificacion = calificacionAutomaticaService.calificar(resultado, preguntas);
        repositorioResultados.guardar(resultado);
        return calificacion;
    }
}
