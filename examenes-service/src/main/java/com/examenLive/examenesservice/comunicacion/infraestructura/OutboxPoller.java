package com.examenLive.examenesservice.comunicacion.infraestructura;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import java.util.concurrent.TimeUnit;
@Component
@ConditionalOnProperty(name="comunicacion.outbox.enabled",havingValue="true",matchIfMissing=true)
public class OutboxPoller {
    private static final Logger log=LoggerFactory.getLogger(OutboxPoller.class);
    private final JdbcTemplate jdbc;
    private final KafkaTemplate<String,String> kafka;
    public OutboxPoller(JdbcTemplate jdbc, KafkaTemplate<String,String> kafka) { this.jdbc=jdbc; this.kafka=kafka; }
    @Scheduled(fixedDelayString="${comunicacion.outbox.intervalo-ms:2000}")
    @Transactional
    public void publicarPendientes() {
        // Un poller activo por base: conserva el orden incluso con varias réplicas.
        if (!Boolean.TRUE.equals(jdbc.queryForObject("SELECT pg_try_advisory_xact_lock(6172606)",Boolean.class))) return;
        var pendientes=jdbc.queryForList("SELECT secuencia,topico,clave,payload FROM outbox_event WHERE publicado=false ORDER BY secuencia LIMIT 25 FOR UPDATE");
        for (var e:pendientes) {
            try {
                kafka.send((String)e.get("topico"),(String)e.get("clave"),(String)e.get("payload")).get(5,TimeUnit.SECONDS);
                jdbc.update("UPDATE outbox_event SET publicado=true, publicado_en=CURRENT_TIMESTAMP WHERE secuencia=?",e.get("secuencia"));
                log.info("Outbox {} publicado en {}",e.get("secuencia"),e.get("topico"));
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt(); return;
            } catch (Exception ex) {
                log.warn("Outbox {} pendiente; se reintentará: {}",e.get("secuencia"),ex.toString());
                return;
            }
        }
    }
}
