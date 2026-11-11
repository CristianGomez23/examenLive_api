package com.examenLive.resultadosservice.compartido;

/**
 * Se lanza cuando un recurso referenciado por id no existe. Extiende IllegalArgumentException
 * y la capa web la traduce a HTTP 404.
 */
public class RecursoNoEncontradoException extends IllegalArgumentException {

	public RecursoNoEncontradoException(String mensaje) {
		super(mensaje);
	}
}
