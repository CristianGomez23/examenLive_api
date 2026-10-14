package com.examenLive.examenLive_api.examenes.infraestructura.entrada.web;

import com.examenLive.examenLive_api.examenes.dominio.puertos.entrada.ExamenUseCase;
import com.examenLive.examenLive_api.examenes.dominio.puertos.entrada.IntentoExamenVista;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ExamenController {

	private final ExamenUseCase examenes;

	public ExamenController(ExamenUseCase examenes) {
		this.examenes = examenes;
	}

	@PostMapping("/examenes")
	public ResponseEntity<ExamenResponse> crear(@RequestBody CrearExamenRequest solicitud) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ExamenResponse.desde(examenes.crear(
						solicitud.titulo(), solicitud.preguntasIds(), solicitud.duracionMinutos()
				)));
	}

	@GetMapping("/examenes/{examenId}")
	public ExamenResponse consultar(@PathVariable Long examenId) {
		return ExamenResponse.desde(examenes.consultar(examenId));
	}

	@PostMapping("/sesiones/{sesionId}/intentos")
	public ResponseEntity<IntentoExamenVista> iniciar(
			@PathVariable Long sesionId,
			@RequestBody IniciarIntentoRequest solicitud
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(examenes.iniciarIntento(sesionId, solicitud.estudianteId()));
	}

	@GetMapping("/intentos-examen/{intentoId}")
	public IntentoExamenVista consultarIntento(@PathVariable Long intentoId) {
		return examenes.consultarIntento(intentoId);
	}

	@PutMapping("/intentos-examen/{intentoId}/preguntas/{preguntaId}/respuesta")
	public IntentoExamenVista responder(
			@PathVariable Long intentoId,
			@PathVariable Long preguntaId,
			@RequestBody RegistrarRespuestaRequest solicitud
	) {
		return examenes.registrarRespuesta(intentoId, preguntaId, solicitud.opcionesSeleccionadas());
	}

	@PostMapping("/intentos-examen/{intentoId}/finalizar")
	public IntentoExamenVista finalizar(@PathVariable Long intentoId) {
		return examenes.finalizarIntento(intentoId);
	}
}