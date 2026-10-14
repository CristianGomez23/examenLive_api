package com.examenLive.examenLive_api.identidad.infraestructura.entrada.web;

import com.examenLive.examenLive_api.identidad.dominio.RecursoNoEncontradoException;
import com.examenLive.examenLive_api.resultados.dominio.PreguntaSinPuntajeException;
import com.examenLive.examenLive_api.resultados.dominio.RespuestaNoModificableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

/**
 * Traduce las excepciones del dominio a respuestas HTTP con cuerpo JSON,
 * en lugar de un 500 genérico.
 */
@RestControllerAdvice
public class ManejadorErrores {

	public record ErrorResponse(Instant fecha, int estado, String error, String mensaje) {
	}

	@ExceptionHandler(RecursoNoEncontradoException.class)
	public ResponseEntity<ErrorResponse> noEncontrado(RecursoNoEncontradoException e) {
		return responder(HttpStatus.NOT_FOUND, e.getMessage());
	}

	@ExceptionHandler({
			IllegalArgumentException.class,
			HttpMessageNotReadableException.class,
			MethodArgumentTypeMismatchException.class,
			MissingServletRequestParameterException.class
	})
	public ResponseEntity<ErrorResponse> solicitudInvalida(Exception e) {
		return responder(HttpStatus.BAD_REQUEST, e.getMessage());
	}

	/** Reglas de negocio violadas: respuesta tras cierre, sesión fuera de ventana, intento ya finalizado, etc. */
	@ExceptionHandler({
			IllegalStateException.class,
			RespuestaNoModificableException.class,
			PreguntaSinPuntajeException.class
	})
	public ResponseEntity<ErrorResponse> reglaDeNegocio(RuntimeException e) {
		return responder(HttpStatus.CONFLICT, e.getMessage());
	}

	private ResponseEntity<ErrorResponse> responder(HttpStatus estado, String mensaje) {
		return ResponseEntity.status(estado)
				.body(new ErrorResponse(Instant.now(), estado.value(), estado.getReasonPhrase(), mensaje));
	}
}
