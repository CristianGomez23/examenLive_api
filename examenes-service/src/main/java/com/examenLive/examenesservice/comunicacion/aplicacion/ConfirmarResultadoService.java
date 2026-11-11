package com.examenLive.examenesservice.comunicacion.aplicacion;


import com.examenLive.examenesservice.comunicacion.contratos.ResultadoGeneradoEvento;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
@Service
public class ConfirmarResultadoService implements ConfirmarResultadoUseCase {
    private final JdbcTemplate jdbc;
    private final RegistroMensajes inbox;
    public ConfirmarResultadoService(JdbcTemplate jdbc,RegistroMensajes inbox) {this.jdbc=jdbc;this.inbox=inbox;}
    @Transactional
    public void confirmar(ResultadoGeneradoEvento e) {
        e.validar();
        if (!inbox.registrarSiNuevo("examenes-resultados-v1",e.eventId())) return;
        var filas=jdbc.queryForList("SELECT sesion_id,estudiante_id,resultado_id FROM calificacion_saga WHERE intento_id=? FOR UPDATE",e.intentoId());
        if (filas.isEmpty()) throw new IllegalStateException("No existe la saga del intento "+e.intentoId());
        var s=filas.getFirst();
        if (!e.sesionId().equals(s.get("sesion_id")) || !e.estudianteId().equals(s.get("estudiante_id")))
            throw new IllegalArgumentException("La confirmación no corresponde al intento.");
        if (s.get("resultado_id")!=null && !e.resultadoId().equals(s.get("resultado_id")))
            throw new IllegalStateException("La saga ya tiene otro resultado.");
        jdbc.update("UPDATE calificacion_saga SET estado='COMPLETADA',resultado_id=?,actualizado_en=CURRENT_TIMESTAMP WHERE intento_id=?",e.resultadoId(),e.intentoId());
    }
}
