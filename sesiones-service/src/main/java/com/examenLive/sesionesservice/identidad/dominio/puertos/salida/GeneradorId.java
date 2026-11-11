package com.examenLive.sesionesservice.identidad.dominio.puertos.salida;

@FunctionalInterface
public interface GeneradorId {

	long siguienteId();
}
