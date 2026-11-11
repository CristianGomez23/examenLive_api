package com.examenLive.resultadosservice.comunicacion;

import com.examenLive.resultadosservice.comunicacion.infraestructura.OutboxPoller;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import static org.mockito.Mockito.*;

class OutboxPollerTest {
    JdbcTemplate jdbc=mock(JdbcTemplate.class);
    @SuppressWarnings("unchecked") KafkaTemplate<String,String> kafka=mock(KafkaTemplate.class);
    OutboxPoller poller=new OutboxPoller(jdbc,kafka);
    void pendientes() {
        when(jdbc.queryForObject(anyString(),eq(Boolean.class))).thenReturn(true);
        when(jdbc.queryForList(anyString())).thenReturn(List.of(
            Map.of("secuencia",1L,"topico","resultados.generados.v1","clave","1","payload","{}"),
            Map.of("secuencia",2L,"topico","resultados.generados.v1","clave","2","payload","{}")));
    }
    @Test void brokerCaidoNoMarcaPublicadoNiAdelantaSiguiente() {
        pendientes();
        when(kafka.send(anyString(),anyString(),anyString())).thenReturn(CompletableFuture.failedFuture(new RuntimeException("Broker caído")));
        poller.publicarPendientes();
        verify(jdbc,never()).update(anyString(),any(Object.class));
        verify(kafka,times(1)).send(anyString(),anyString(),anyString());
    }
    @Test void acuseRecibidoMarcaPublicado() {
        pendientes();
        when(kafka.send(anyString(),anyString(),anyString())).thenReturn(CompletableFuture.completedFuture(null));
        poller.publicarPendientes();
        verify(jdbc,times(2)).update(anyString(),any(Object.class));
    }
    @Test void otraReplicaConBloqueoEvitaPublicar() {
        when(jdbc.queryForObject(anyString(),eq(Boolean.class))).thenReturn(false);
        poller.publicarPendientes();
        verifyNoInteractions(kafka);
    }
}
