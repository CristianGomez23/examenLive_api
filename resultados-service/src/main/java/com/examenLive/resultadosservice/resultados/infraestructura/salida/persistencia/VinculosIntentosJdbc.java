package com.examenLive.resultadosservice.resultados.infraestructura.salida.persistencia;


import com.examenLive.resultadosservice.resultados.dominio.puertos.salida.VinculosIntentos;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
@Component
@Transactional(propagation=Propagation.MANDATORY)
public class VinculosIntentosJdbc implements VinculosIntentos {
    private final JdbcTemplate jdbc;
    public VinculosIntentosJdbc(JdbcTemplate jdbc) { this.jdbc=jdbc; }
    public void bloquear(Long id) { jdbc.queryForList("SELECT pg_advisory_xact_lock(?)",id); }
    public Optional<Long> buscarResultado(Long id) {
        return jdbc.queryForList("SELECT resultado_id FROM intento_resultado WHERE intento_id=?",Long.class,id).stream().findFirst();
    }
    public void guardar(Long id,Long r,Long s,Long e) {
        jdbc.update("INSERT INTO intento_resultado(intento_id,resultado_id,sesion_id,estudiante_id) VALUES (?,?,?,?)",id,r,s,e);
    }
}
