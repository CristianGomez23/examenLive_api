package com.examenLive.examenesservice.comunicacion.infraestructura;


import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.util.Map;
@RestController
public class EstadoCalificacionController {
    private final JdbcTemplate jdbc;
    public EstadoCalificacionController(JdbcTemplate jdbc) {this.jdbc=jdbc;}
    @GetMapping("/api/intentos-examen/{id}/estado-calificacion")
    public Map<String,Object> estado(@PathVariable Long id) {
        var filas=jdbc.queryForList("SELECT intento_id,estado,resultado_id,actualizado_en FROM calificacion_saga WHERE intento_id=?",id);
        if (filas.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND,"No hay solicitud de calificación para este intento.");
        return filas.getFirst();
    }
}
