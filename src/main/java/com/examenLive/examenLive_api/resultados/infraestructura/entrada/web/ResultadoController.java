package com.examenLive.examenLive_api.resultados.infraestructura.entrada.web;

import com.examenLive.examenLive_api.resultados.aplicacion.ResultadoUseCase;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/resultados")
public class ResultadoController {

    private final ResultadoUseCase resultadoUseCase;

    public ResultadoController(ResultadoUseCase resultadoUseCase){
        this.resultadoUseCase = resultadoUseCase;
    }

    @GetMapping("/{id}")
    public Object consultar(@PathVariable UUID id){
        return resultadoUseCase.consultar(id);
    }
}
