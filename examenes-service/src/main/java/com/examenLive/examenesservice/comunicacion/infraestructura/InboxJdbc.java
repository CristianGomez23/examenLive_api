package com.examenLive.examenesservice.comunicacion.infraestructura;


import com.examenLive.examenesservice.comunicacion.aplicacion.RegistroMensajes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
@Component
public class InboxJdbc implements RegistroMensajes {
    private final JdbcTemplate jdbc;
    public InboxJdbc(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    @Override
    @Transactional(propagation=Propagation.MANDATORY)
    public boolean registrarSiNuevo(String consumidor, UUID eventId) {
        return jdbc.update("INSERT INTO inbox_event(consumidor,event_id) VALUES (?,?) ON CONFLICT DO NOTHING",
            consumidor,eventId)==1;
    }
}
