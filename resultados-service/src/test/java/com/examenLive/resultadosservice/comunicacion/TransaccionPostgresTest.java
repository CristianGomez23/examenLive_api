package com.examenLive.resultadosservice.comunicacion;


import com.examenLive.resultadosservice.comunicacion.contratos.*;
import com.examenLive.resultadosservice.resultados.aplicacion.servicios.ProcesadorIntentoFinalizado;
import com.examenLive.resultadosservice.resultados.dominio.PreguntaSinPuntajeException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.*;
import java.math.BigDecimal;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;
@EnabledIfEnvironmentVariable(named="EXAMENLIVE_INTEGRATION_TESTS",matches="true")
@SpringBootTest(properties={"eureka.client.enabled=false","comunicacion.outbox.enabled=false","spring.kafka.listener.auto-startup=false","spring.kafka.admin.auto-create=false"})
class TransaccionPostgresTest {
    @Autowired ProcesadorIntentoFinalizado procesador;
    @Autowired JdbcTemplate jdbc;
    @Autowired org.springframework.transaction.PlatformTransactionManager transacciones;
    long id;
    final List<UUID> eventosDePrueba=new ArrayList<>();
    UUID nuevoEventId() {var valor=UUID.randomUUID();eventosDePrueba.add(valor);return valor;}
    @BeforeEach void identificar() { id=Math.abs(UUID.randomUUID().getMostSignificantBits() % 1000000000000L)+10000; }
    @AfterEach void limpiarDatosDeEstaPrueba() {
        jdbc.update("DELETE FROM outbox_event WHERE clave=? AND topico='resultados.generados.v1'",Long.toString(id));
        jdbc.update("DELETE FROM intento_resultado WHERE intento_id=?",id);
        jdbc.update("DELETE FROM resultados WHERE sesion_id=? AND estudiante_id=?",id,id);
        for (UUID eventId:eventosDePrueba) jdbc.update("DELETE FROM inbox_event WHERE event_id=?",eventId);
    }
    DatosIntento datos(BigDecimal puntaje) {return new DatosIntento(id,id,id,List.of(new DatosIntento.Respuesta(101L,Set.of("A"))),List.of(new DatosIntento.Pregunta(101L,Set.of("A"),puntaje)));}
    @Test void commitAtomicoYReplayTrasOtraTransaccion() {
        var e=new IntentoFinalizadoEvento(nuevoEventId(),1,Instant.now(),datos(BigDecimal.ONE));
        procesador.consumir(e);procesador.consumir(e);
        procesador.consumir(new IntentoFinalizadoEvento(nuevoEventId(),1,Instant.now(),e.intento()));
        assertEquals(1,jdbc.queryForObject("SELECT count(*) FROM resultados WHERE sesion_id=?",Integer.class,id));
        assertEquals(1,jdbc.queryForObject("SELECT count(*) FROM outbox_event WHERE clave=?",Integer.class,Long.toString(id)));
        assertEquals(1,jdbc.queryForObject("SELECT count(*) FROM intento_resultado WHERE intento_id=?",Integer.class,id));
    }
    @Test void falloDeNegocioRevierteInboxResultadoYOutbox() {
        UUID eventId=nuevoEventId();
        assertThrows(PreguntaSinPuntajeException.class,()->procesador.consumir(new IntentoFinalizadoEvento(eventId,1,Instant.now(),datos(null))));
        assertEquals(0,jdbc.queryForObject("SELECT count(*) FROM inbox_event WHERE event_id=?",Integer.class,eventId));
        assertEquals(0,jdbc.queryForObject("SELECT count(*) FROM resultados WHERE sesion_id=?",Integer.class,id));
        assertEquals(0,jdbc.queryForObject("SELECT count(*) FROM outbox_event WHERE clave=?",Integer.class,Long.toString(id)));
    }
    @Test void peticionesConcurrentesCreanUnResultadoYUnEvento() throws Exception {
        try(var executor=java.util.concurrent.Executors.newFixedThreadPool(4)) {
            var tareas=java.util.stream.IntStream.range(0,4).mapToObj(i -> (java.util.concurrent.Callable<Object>)()->procesador.procesar(datos(BigDecimal.ONE))).toList();
            for(var futuro:executor.invokeAll(tareas)) futuro.get();
        }
        assertEquals(1,jdbc.queryForObject("SELECT count(*) FROM resultados WHERE sesion_id=?",Integer.class,id));
        assertEquals(1,jdbc.queryForObject("SELECT count(*) FROM outbox_event WHERE clave=?",Integer.class,Long.toString(id)));
    }
    @Test void rollbackPosteriorAlGuardadoRevierteResultadoVinculoYOutbox() {
        var tx=new org.springframework.transaction.support.TransactionTemplate(transacciones);
        assertThrows(IllegalStateException.class,()->tx.execute(status -> {
            procesador.procesar(datos(BigDecimal.ONE));
            throw new IllegalStateException("Fallo antes del commit");
        }));
        assertEquals(0,jdbc.queryForObject("SELECT count(*) FROM resultados WHERE sesion_id=?",Integer.class,id));
        assertEquals(0,jdbc.queryForObject("SELECT count(*) FROM intento_resultado WHERE intento_id=?",Integer.class,id));
        assertEquals(0,jdbc.queryForObject("SELECT count(*) FROM outbox_event WHERE clave=?",Integer.class,Long.toString(id)));
    }
}
