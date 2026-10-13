package com.examenLive.examenLive_api.resultados.infraestructura.entrada.web;

import com.examenLive.examenLive_api.resultados.dominio.puertos.entrada.ResultadoUseCase;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/resultados")
public class ResultadoController {

    private final ResultadoUseCase resultadoUseCase;

    public ResultadoController(ResultadoUseCase resultadoUseCase){
        this.resultadoUseCase = resultadoUseCase;
    }

    @GetMapping("/{id}")
    public Object consultar(@PathVariable Long id){
        return resultadoUseCase.consultar(id);
    }
}
