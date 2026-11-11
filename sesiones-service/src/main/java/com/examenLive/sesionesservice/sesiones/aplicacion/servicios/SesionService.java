package com.examenLive.sesionesservice.sesiones.aplicacion.servicios;

import com.examenLive.sesionesservice.sesiones.dominio.puertos.salida.VerificadorExamen;
import com.examenLive.sesionesservice.identidad.dominio.puertos.salida.GeneradorId;
import com.examenLive.sesionesservice.sesiones.dominio.puertos.entrada.SesionUseCase;
import com.examenLive.sesionesservice.sesiones.dominio.puertos.salida.SesionRepository;
import com.examenLive.sesionesservice.sesiones.dominio.Sesion;
import com.examenLive.sesionesservice.sesiones.dominio.SesionFactory;
import com.examenLive.sesionesservice.sesiones.dominio.VentanaTiempo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SesionService implements SesionUseCase {

	private final SesionRepository sesiones;
	private final VerificadorExamen examenes;
	private final SesionFactory factory;

	public SesionService(SesionRepository sesiones, VerificadorExamen examenes, GeneradorId generadorId) {
		this.sesiones = sesiones;
		this.examenes = examenes;
		this.factory = new SesionFactory(generadorId);
	}

	@Override
	@Transactional
	public Sesion programar(Long examenId, LocalDateTime inicio, LocalDateTime fin) {
		if (!examenes.existe(examenId)) throw new IllegalArgumentException("No existe el examen indicado.");
		Sesion sesion = factory.crear(examenId, new VentanaTiempo(inicio, fin));
		sesiones.guardar(sesion);
		return sesion;
	}

	@Override
	@Transactional(readOnly = true)
	public Sesion consultar(Long sesionId) {
		return sesiones.buscarPorId(sesionId)
				.orElseThrow(() -> new IllegalArgumentException("No existe la sesión indicada."));
	}
}
