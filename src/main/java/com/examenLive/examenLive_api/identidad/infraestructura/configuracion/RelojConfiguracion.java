package com.examenLive.examenLive_api.identidad.infraestructura.configuracion;

import com.examenLive.examenLive_api.identidad.dominio.puertos.salida.Reloj;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Configuration
public class RelojConfiguracion {

	@Bean
	Reloj relojSistema() {
		return LocalDateTime::now;
	}
}