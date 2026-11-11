package com.examenLive.examenesservice.identidad.infraestructura.configuracion;

import com.examenLive.examenesservice.identidad.dominio.puertos.salida.Reloj;

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
