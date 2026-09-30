package com.examenLive.examenLive_api.preguntas.infraestructura.entrada.web;

import com.examenLive.examenLive_api.preguntas.aplicacion.PreguntaUseCase;
import com.examenLive.examenLive_api.preguntas.dominio.TipoPregunta;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/preguntas")
public class PreguntaController {

	private final PreguntaUseCase preguntas;

	public PreguntaController(PreguntaUseCase preguntas) {
		this.preguntas = preguntas;
	}

	@PostMapping
	public ResponseEntity<PreguntaResponse> registrar(@RequestBody RegistrarPreguntaRequest solicitud) {
		PreguntaResponse respuesta = PreguntaResponse.desde(preguntas.registrar(
				new TipoPregunta(solicitud.tipo()),
				solicitud.enunciado(),
				solicitud.respuestasCorrectas(),
				solicitud.puntaje()
		));
		return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
	}

	@GetMapping
	public List<PreguntaResponse> consultarActivas() {
		return preguntas.consultarActivas().stream().map(PreguntaResponse::desde).toList();
	}

	@GetMapping("/{preguntaId}")
	public PreguntaResponse consultar(@PathVariable UUID preguntaId) {
		return PreguntaResponse.desde(preguntas.consultar(preguntaId));
	}

	@PatchMapping("/{preguntaId}/desactivar")
	public PreguntaResponse desactivar(@PathVariable UUID preguntaId) {
		preguntas.desactivar(preguntaId);
		return PreguntaResponse.desde(preguntas.consultar(preguntaId));
	}

	@DeleteMapping("/{preguntaId}")
	public ResponseEntity<Void> eliminar(@PathVariable UUID preguntaId) {
		preguntas.eliminar(preguntaId);
		return ResponseEntity.noContent().build();
	}
}