package com.examenLive.preguntasservice.identidad.dominio.puertos.salida;

@FunctionalInterface
public interface GeneradorId {

	long siguienteId();
}
