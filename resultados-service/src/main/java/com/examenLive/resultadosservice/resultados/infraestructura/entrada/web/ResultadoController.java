package com.examenLive.resultadosservice.resultados.infraestructura.entrada.web;

import com.examenLive.resultadosservice.resultados.dominio.PreguntaCalificable;
import com.examenLive.resultadosservice.resultados.dominio.puertos.entrada.ResultadoGenerado;
import com.examenLive.resultadosservice.resultados.dominio.puertos.entrada.ResultadoUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/resultados")
public class ResultadoController {

    private final boolean permitirAutocontenido;
    private final ResultadoUseCase resultadoUseCase;

    public ResultadoController(ResultadoUseCase resultadoUseCase,
            @org.springframework.beans.factory.annotation.Value("${resultados.permitir-autocontenido:false}") boolean permitirAutocontenido){
        this.permitirAutocontenido = permitirAutocontenido;
        this.resultadoUseCase = resultadoUseCase;
    }

    /**
     * Genera y califica el resultado de un intento terminado.
     * 201 si se creó; 200 si ya existía uno para esa sesión y estudiante (idempotente).
     */
    @PostMapping("/autocontenido")
    public ResponseEntity<ResultadoResponse> generar(@RequestBody GenerarResultadoRequest solicitud){
        if (!permitirAutocontenido) {
            throw new IllegalStateException("La recepción autocontenida está deshabilitada. Use /api/resultados/desde-intento.");
        }
        if (solicitud == null) {
            throw new IllegalArgumentException("El cuerpo de la solicitud es obligatorio.");
        }
        ResultadoGenerado generado = resultadoUseCase.generarResultado(
            solicitud.sesionId(),
            solicitud.estudianteId(),
            aMapaDeRespuestas(solicitud.respuestas()),
            aPreguntasCalificables(solicitud.preguntas())
        );
        ResultadoResponse cuerpo = ResultadoResponse.desde(generado.resultado());
        if (generado.creado()) {
            return ResponseEntity
                .created(URI.create("/api/resultados/" + cuerpo.resultadoId()))
                .body(cuerpo);
        }
        return ResponseEntity.ok(cuerpo);
    }

    /** HU-07: resultado de un intento terminado. */
    @GetMapping("/{id}")
    public ResultadoResponse consultar(@PathVariable Long id){
        return ResultadoResponse.desde(resultadoUseCase.consultar(id));
    }

    /**
     * HU-09: historial de un estudiante. Con {@code sesionId} filtra el resultado de esa sesión.
     */
    @GetMapping
    public List<ResultadoResponse> consultarPorEstudiante(
            @RequestParam Long estudianteId,
            @RequestParam(required = false) Long sesionId
    ){
        if (sesionId != null) {
            return resultadoUseCase.buscarPorSesionYEstudiante(sesionId, estudianteId)
                .map(ResultadoResponse::desde)
                .stream()
                .toList();
        }
        return resultadoUseCase.consultarPorEstudiante(estudianteId).stream()
            .map(ResultadoResponse::desde)
            .toList();
    }

    private static Map<Long, Set<String>> aMapaDeRespuestas(List<GenerarResultadoRequest.RespuestaRequest> respuestas){
        Map<Long, Set<String>> mapa = new LinkedHashMap<>();
        if (respuestas == null) {
            return mapa;
        }
        for (GenerarResultadoRequest.RespuestaRequest respuesta : respuestas) {
            if (respuesta == null || respuesta.preguntaId() == null) {
                throw new IllegalArgumentException("Cada respuesta debe identificar la pregunta.");
            }
            if (mapa.put(respuesta.preguntaId(), respuesta.opcionesSeleccionadas()) != null) {
                throw new IllegalArgumentException(
                    "Hay más de una respuesta para la pregunta " + respuesta.preguntaId() + "."
                );
            }
        }
        return mapa;
    }

    private static List<PreguntaCalificable> aPreguntasCalificables(List<GenerarResultadoRequest.PreguntaRequest> preguntas){
        if (preguntas == null || preguntas.isEmpty()) {
            throw new IllegalArgumentException("Se requiere al menos una pregunta para calificar.");
        }
        return preguntas.stream()
            .map(pregunta -> {
                if (pregunta == null) {
                    throw new IllegalArgumentException("La lista de preguntas no puede contener valores nulos.");
                }
                return new PreguntaCalificable(
                    pregunta.preguntaId(), pregunta.respuestasCorrectas(), pregunta.puntaje()
                );
            })
            .toList();
    }
}
