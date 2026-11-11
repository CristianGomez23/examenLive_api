package com.examenLive.resultadosservice.plataforma;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

/**
 * Cada servicio expone su propio /api/status (ver taller de descomposición).
 */
@RestController
@RequestMapping("/api")
public class StatusController {

	@GetMapping("/status")
	public Map<String, Object> status() {
		return Map.of(
				"servicio", "resultados-service",
				"estado", "UP",
				"hora", Instant.now().toString()
		);
	}
}
