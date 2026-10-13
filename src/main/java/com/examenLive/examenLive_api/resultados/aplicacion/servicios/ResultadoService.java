package com.examenLive.examenLive_api.resultados.aplicacion.servicios;

import com.examenLive.examenLive_api.resultados.aplicacion.ResultadoFactory;
import com.examenLive.examenLive_api.resultados.dominio.Resultado;
import com.examenLive.examenLive_api.resultados.dominio.Respuesta;
import com.examenLive.examenLive_api.resultados.dominio.Calificacion;
import com.examenLive.examenLive_api.resultados.dominio.PreguntaCalificable;
import com.examenLive.examenLive_api.resultados.dominio.puertos.entrada.ResultadoUseCase;
import com.examenLive.examenLive_api.resultados.dominio.puertos.salida.RepositorioResultados;
import org.springframework.stereotype.Service;
import java.util.List;

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
    public Resultado iniciar(Long sesionId, Long estudianteId) {
        return repositorioResultados.guardar(
            resultadoFactory.crear(sesionId, estudianteId)
        );
    }

    @Override
    public Resultado consultar(Long resultadoId) {
        return repositorioResultados.buscarPorId(resultadoId)
            .orElseThrow(() -> new IllegalArgumentException("Resultado no encontrado"));
    }

    public void registrarRespuesta(Resultado resultado, Respuesta respuesta) {
        resultado.registrarRespuesta(respuesta);
        repositorioResultados.guardar(resultado);
    }

    public Calificacion cerrarYCalificar(Resultado resultado, List<PreguntaCalificable> preguntas) {
        resultado.cerrarRecepcionRespuestas();
        Calificacion calificacion = calificacionAutomaticaService.calificar(resultado, preguntas);
        resultado.registrarCalificacion(calificacion);
        repositorioResultados.guardar(resultado);
        return calificacion;
    }
}
