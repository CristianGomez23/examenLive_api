package com.examenLive.resultadosservice.resultados.infraestructura.entrada.web;


import com.examenLive.resultadosservice.resultados.dominio.puertos.entrada.ComunicacionResultadosUseCase;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.net.URI;
@RestController
public class ResultadoRemotoController {
    private final ComunicacionResultadosUseCase casos;
    public ResultadoRemotoController(ComunicacionResultadosUseCase casos) {this.casos=casos;}
    public record Solicitud(Long intentoId) {}
    @PostMapping("/api/resultados/desde-intento")
    public ResponseEntity<ResultadoResponse> generar(@RequestBody Solicitud solicitud) {
        var generado=casos.generarDesdeIntento(solicitud.intentoId());
        var respuesta=ResultadoResponse.desde(generado.resultado());
        return generado.creado()?ResponseEntity.created(URI.create("/api/resultados/"+respuesta.resultadoId())).body(respuesta)
            :ResponseEntity.ok(respuesta);
    }
}
