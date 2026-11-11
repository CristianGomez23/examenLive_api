package com.examenLive.resultadosservice.resultados.dominio.puertos.entrada;

import com.examenLive.resultadosservice.resultados.dominio.Resultado;

/**
 * Resultado de la operación de generación: indica si el resultado se creó ahora o ya existía
 * (la operación es idempotente por sesión y estudiante).
 */
public record ResultadoGenerado(Resultado resultado, boolean creado) {
}
