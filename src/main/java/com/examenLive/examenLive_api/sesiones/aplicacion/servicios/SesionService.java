package com.examenLive.examenLive_api.sesiones.aplicacion.servicios;

import com.examenLive.examenLive_api.examenes.dominio.puertos.salida.ExamenRepository;
import com.examenLive.examenLive_api.identidad.dominio.puertos.salida.GeneradorId;
import com.examenLive.examenLive_api.sesiones.dominio.puertos.entrada.SesionUseCase;
import com.examenLive.examenLive_api.sesiones.dominio.puertos.salida.SesionRepository;
import com.examenLive.examenLive_api.sesiones.dominio.Sesion;
import com.examenLive.examenLive_api.sesiones.dominio.SesionFactory;
import com.examenLive.examenLive_api.sesiones.dominio.VentanaTiempo;
import com.examenLive.examenLive_api.identidad.dominio.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SesionService implements SesionUseCase {

	private final SesionRepository sesiones;
	private final ExamenRepository examenes;
	private final SesionFactory factory;

	public SesionService(SesionRepository sesiones, ExamenRepository examenes, GeneradorId generadorId) {
		this.sesiones = sesiones;
		this.examenes = examenes;
		this.factory = new SesionFactory(generadorId);
	}

	@Override
	@Transactional
	public Sesion programar(Long examenId, LocalDateTime inicio, LocalDateTime fin) {
		examenes.buscarPorId(examenId)
				.orElseThrow(() -> new RecursoNoEncontradoException("No existe el examen indicado."));
		Sesion sesion = factory.crear(examenId, new VentanaTiempo(inicio, fin));
		sesiones.guardar(sesion);
		return sesion;
	}

	@Override
	@Transactional(readOnly = true)
	public Sesion consultar(Long sesionId) {
		return sesiones.buscarPorId(sesionId)
				.orElseThrow(() -> new RecursoNoEncontradoException("No existe la sesión indicada."));
	}
}