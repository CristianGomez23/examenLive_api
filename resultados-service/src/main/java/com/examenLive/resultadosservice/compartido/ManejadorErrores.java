package com.examenLive.resultadosservice.compartido;

import com.examenLive.resultadosservice.resultados.dominio.PreguntaSinPuntajeException;
import com.examenLive.resultadosservice.resultados.dominio.RespuestaNoModificableException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

/**
 * Traduce las excepciones a respuestas HTTP con cuerpo JSON uniforme.
 * Se duplica en cada servicio: ya no existe un @ControllerAdvice compartido.
 */
@RestControllerAdvice
public class ManejadorErrores {

	public record ErrorResponse(Instant fecha, int estado, String error, String mensaje) {
	}

	@ExceptionHandler(RecursoNoEncontradoException.class)
	public ResponseEntity<ErrorResponse> noEncontrado(RecursoNoEncontradoException e) {
		return responder(404, "Not Found", e.getMessage());
	}

	@ExceptionHandler({
			IllegalArgumentException.class,
			HttpMessageNotReadableException.class,
			MethodArgumentTypeMismatchException.class,
			MissingServletRequestParameterException.class
	})
	public ResponseEntity<ErrorResponse> solicitudInvalida(Exception e) {
		return responder(400, "Bad Request", e.getMessage());
	}

	/** Regla de negocio: un examen no puede calificarse si tiene preguntas sin puntaje. */
	@ExceptionHandler(PreguntaSinPuntajeException.class)
	public ResponseEntity<ErrorResponse> sinPuntaje(PreguntaSinPuntajeException e) {
		return responder(422, "Unprocessable Content", e.getMessage());
	}

	@ExceptionHandler({IllegalStateException.class, RespuestaNoModificableException.class})
	public ResponseEntity<ErrorResponse> conflicto(RuntimeException e) {
		return responder(409, "Conflict", e.getMessage());
	}

	/** Dos solicitudes simultáneas para el mismo (sesión, estudiante): la segunda debe reintentarse. */
	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<ErrorResponse> integridad(DataIntegrityViolationException e) {
		return responder(409, "Conflict",
				"Ya existe un resultado para esa sesión y estudiante. Reintenta la solicitud.");
	}

    @ExceptionHandler(ServicioNoDisponibleException.class)
    public ResponseEntity<ErrorResponse> noDisponible(ServicioNoDisponibleException e) {
        return responder(503,"Service Unavailable",e.getMessage());
    }

	private ResponseEntity<ErrorResponse> responder(int estado, String error, String mensaje) {
		return ResponseEntity.status(estado).body(new ErrorResponse(Instant.now(), estado, error, mensaje));
	}
}
