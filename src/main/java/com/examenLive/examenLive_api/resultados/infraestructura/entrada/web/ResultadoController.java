package com.examenLive.examenLive_api.resultados.infraestructura.entrada.web;

import com.examenLive.examenLive_api.resultados.dominio.puertos.entrada.ResultadoUseCase;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resultados")
public class ResultadoController {

    private final ResultadoUseCase resultadoUseCase;

    public ResultadoController(ResultadoUseCase resultadoUseCase){
        this.resultadoUseCase = resultadoUseCase;
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
}
