package com.examenLive.examenLive_api.identidad.dominio;

/**
 * Se lanza cuando un recurso referenciado por id no existe. Extiende IllegalArgumentException
 * para no romper el código que ya capturaba esa excepción; la capa web la traduce a HTTP 404.
 */
public class RecursoNoEncontradoException extends IllegalArgumentException {

	public RecursoNoEncontradoException(String mensaje) {
		super(mensaje);
	}
}
