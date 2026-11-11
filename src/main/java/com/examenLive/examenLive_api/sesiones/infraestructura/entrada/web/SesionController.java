package com.examenLive.examenLive_api.sesiones.infraestructura.entrada.web;

import com.examenLive.examenLive_api.sesiones.dominio.puertos.entrada.SesionUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sesiones")
public class SesionController {

	private final SesionUseCase sesiones;

	public SesionController(SesionUseCase sesiones) {
		this.sesiones = sesiones;
	}

	@PostMapping
	public ResponseEntity<SesionResponse> programar(@RequestBody ProgramarSesionRequest solicitud) {
		return ResponseEntity.status(HttpStatus.CREATED).body(SesionResponse.desde(
				sesiones.programar(solicitud.examenId(), solicitud.inicio(), solicitud.fin())
		));
	}

	@GetMapping("/{sesionId}")
	public SesionResponse consultar(@PathVariable Long sesionId) {
		return SesionResponse.desde(sesiones.consultar(sesionId));
	}
}