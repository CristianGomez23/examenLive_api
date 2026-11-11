package com.examenLive.resultadosservice.resultados.infraestructura.salida.persistencia;

import com.examenLive.resultadosservice.resultados.dominio.puertos.salida.GeneradorId;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Genera ids con la secuencia propia de este servicio (resultados_id_seq, creada por Flyway).
 */
@Component
public class GeneradorIdPostgres implements GeneradorId {

	private final JdbcTemplate jdbcTemplate;

	public GeneradorIdPostgres(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public long siguienteId() {
		Long id = jdbcTemplate.queryForObject("select nextval('resultados_id_seq')", Long.class);
		if (id == null) {
			throw new IllegalStateException("PostgreSQL no generó un identificador.");
		}
		return id;
	}
}
