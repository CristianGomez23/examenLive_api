package com.examenLive.examenLive_api.identidad.dominio.puertos.salida;

@FunctionalInterface
public interface GeneradorId {

	long siguienteId();
}