package com.examenLive.sesionesservice.identidad.dominio.puertos.salida;

import java.time.LocalDateTime;

@FunctionalInterface
public interface Reloj {

	LocalDateTime ahora();
}
