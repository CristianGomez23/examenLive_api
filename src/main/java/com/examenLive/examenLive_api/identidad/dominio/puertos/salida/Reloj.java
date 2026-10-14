package com.examenLive.examenLive_api.identidad.dominio.puertos.salida;

import java.time.LocalDateTime;

@FunctionalInterface
public interface Reloj {

	LocalDateTime ahora();
}