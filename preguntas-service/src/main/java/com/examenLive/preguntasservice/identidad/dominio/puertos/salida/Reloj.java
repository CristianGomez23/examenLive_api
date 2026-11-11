package com.examenLive.preguntasservice.identidad.dominio.puertos.salida;

import java.time.LocalDateTime;

@FunctionalInterface
public interface Reloj {

	LocalDateTime ahora();
}
