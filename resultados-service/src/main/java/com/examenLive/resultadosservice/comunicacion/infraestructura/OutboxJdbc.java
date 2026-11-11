package com.examenLive.resultadosservice.comunicacion.infraestructura;


import com.examenLive.resultadosservice.comunicacion.aplicacion.PublicadorEventos;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import java.util.UUID;
@Component
public class OutboxJdbc implements PublicadorEventos {
    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;
    public OutboxJdbc(JdbcTemplate jdbc, ObjectMapper mapper) { this.jdbc=jdbc; this.mapper=mapper; }
    @Override
    @Transactional(propagation=Propagation.MANDATORY)
    public void guardar(UUID id, String topico, String clave, Object evento) {
        jdbc.update("INSERT INTO outbox_event(event_id,topico,clave,payload) VALUES (?,?,?,?)",
            id,topico,clave,mapper.writeValueAsString(evento));
    }
}
