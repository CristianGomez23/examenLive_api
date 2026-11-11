package com.examenLive.examenLive_api.identidad.infraestructura.salida;

import com.examenLive.examenLive_api.identidad.dominio.puertos.salida.GeneradorId;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class GeneradorIdPostgres implements GeneradorId {

	private final JdbcTemplate jdbcTemplate;

	public GeneradorIdPostgres(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	@Override
	public long siguienteId() {
		Long id = jdbcTemplate.queryForObject("select nextval('ids_globales_seq')", Long.class);
		if (id == null) {
			throw new IllegalStateException("PostgreSQL no generó un identificador.");
		}
		return id;
	}
}