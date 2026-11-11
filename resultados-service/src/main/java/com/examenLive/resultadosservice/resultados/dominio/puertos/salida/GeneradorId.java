package com.examenLive.resultadosservice.resultados.dominio.puertos.salida;

/**
 * Puerto de salida: genera identificadores únicos para los resultados de ESTE servicio.
 * Cada microservicio tiene su propia secuencia (Database per Service).
 */
@FunctionalInterface
public interface GeneradorId {

	long siguienteId();
}
