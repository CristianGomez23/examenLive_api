package com.examenLive.examenesservice.identidad.dominio.puertos.salida;

@FunctionalInterface
public interface GeneradorId {

	long siguienteId();
}
