package com.examenLive.examenLive_api.examenes.aplicacion.servicios;

import com.examenLive.examenLive_api.examenes.dominio.Examen;
import com.examenLive.examenLive_api.examenes.dominio.IntentoExamen;
import com.examenLive.examenLive_api.examenes.dominio.puertos.entrada.ExamenUseCase;
import com.examenLive.examenLive_api.examenes.dominio.puertos.entrada.IntentoExamenVista;
import com.examenLive.examenLive_api.examenes.dominio.puertos.entrada.PreguntaEstudiante;
import com.examenLive.examenLive_api.examenes.dominio.puertos.salida.ExamenRepository;
import com.examenLive.examenLive_api.examenes.dominio.puertos.salida.IntentoExamenRepository;
import com.examenLive.examenLive_api.identidad.dominio.puertos.salida.GeneradorId;
import com.examenLive.examenLive_api.identidad.dominio.puertos.salida.Reloj;
import com.examenLive.examenLive_api.preguntas.dominio.puertos.salida.PreguntaRepository;
import com.examenLive.examenLive_api.preguntas.dominio.Pregunta;
import com.examenLive.examenLive_api.resultados.dominio.PreguntaCalificable;
import com.examenLive.examenLive_api.resultados.dominio.Resultado;
import com.examenLive.examenLive_api.resultados.dominio.puertos.entrada.ResultadoUseCase;
import com.examenLive.examenLive_api.sesiones.dominio.Sesion;
import com.examenLive.examenLive_api.sesiones.dominio.puertos.salida.SesionRepository;
import com.examenLive.examenLive_api.identidad.dominio.RecursoNoEncontradoException;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ExamenService implements ExamenUseCase {

	private final ExamenRepository examenes;
	private final IntentoExamenRepository intentos;
	private final PreguntaRepository preguntas;
	private final GeneradorId generadorId;
	private final SesionRepository sesiones;
	private final Reloj reloj;
	private final ResultadoUseCase resultados;

	public ExamenService(
			ExamenRepository examenes,
			IntentoExamenRepository intentos,
			PreguntaRepository preguntas,
			GeneradorId generadorId,
			SesionRepository sesiones,
			Reloj reloj,
			ResultadoUseCase resultados
	) {
		this.examenes = examenes;
		this.intentos = intentos;
		this.preguntas = preguntas;
		this.generadorId = generadorId;
		this.sesiones = sesiones;
		this.reloj = reloj;
		this.resultados = resultados;
	}

	@Override
	@Transactional
	public Examen crear(String titulo, List<Long> preguntasIds, long duracionMinutos) {
		if (preguntasIds == null || preguntasIds.isEmpty()) {
			throw new IllegalArgumentException("El examen debe contener preguntas.");
		}
		if (new HashSet<>(preguntasIds).size() != preguntasIds.size()) {
			throw new IllegalArgumentException("El examen no puede repetir una pregunta.");
		}
		for (Long preguntaId : preguntasIds) {
			Pregunta pregunta = buscarPregunta(preguntaId);
			if (!pregunta.estaActiva()) {
				throw new IllegalArgumentException("No se puede agregar una pregunta inactiva al examen.");
			}
			if (!pregunta.tieneOpcionesIncorrectasSuficientes()) {
				throw new IllegalArgumentException("Cada pregunta debe tener al menos dos opciones incorrectas.");
			}
		}
		return examenes.guardar(new Examen(
				generadorId.siguienteId(), titulo, preguntasIds, Duration.ofMinutes(duracionMinutos)
		));
	}

	@Override
	@Transactional(readOnly = true)
	public Examen consultar(Long examenId) {
		return buscarExamen(examenId);
	}

	@Override
	@Transactional
	public IntentoExamenVista iniciarIntento(Long sesionId, Long estudianteId) {
		if (estudianteId == null || estudianteId <= 0) {
			throw new IllegalArgumentException("El identificador del estudiante debe ser positivo.");
		}
		LocalDateTime ahora = reloj.ahora();
		Optional<IntentoExamen> intentoExistente = intentos.buscarPorSesionYEstudiante(sesionId, estudianteId);
		if (intentoExistente.isPresent()) {
			IntentoExamen intento = finalizarSiVencido(intentoExistente.orElseThrow(), ahora);
			return crearVista(intento, buscarExamen(intento.examenId()), cargarPreguntas(intento.preguntasIds()));
		}

		Sesion sesion = buscarSesion(sesionId);
		if (!sesion.getVentanaTiempo().estaActiva(ahora)) {
			throw new IllegalStateException("La sesión está fuera de su ventana de acceso.");
		}
		Examen examen = buscarExamen(sesion.getExamenId());
		List<Pregunta> preguntasDelExamen = cargarPreguntas(examen.preguntasIds());
		for (Pregunta pregunta : preguntasDelExamen) {
			if (!pregunta.estaActiva() || !pregunta.tieneOpcionesIncorrectasSuficientes()) {
				throw new IllegalStateException("Una pregunta del examen ya no está disponible.");
			}
		}
		LocalDateTime vencimientoPorDuracion = ahora.plus(examen.duracion());
		LocalDateTime venceEn = vencimientoPorDuracion.isBefore(sesion.getVentanaTiempo().fin())
				? vencimientoPorDuracion
				: sesion.getVentanaTiempo().fin();
		IntentoExamen intento = intentos.guardar(new IntentoExamen(
				generadorId.siguienteId(), sesionId, examen.examenId(), estudianteId,
				examen.preguntasIds(), ahora, venceEn
		));
		sesion.agregarEstudiante(estudianteId);
		sesiones.guardar(sesion);
		preguntasDelExamen.forEach(pregunta -> {
			pregunta.registrarUsoEnSesionAplicada();
			preguntas.guardar(pregunta);
		});
		return crearVista(intento, examen, preguntasDelExamen);
	}

	@Override
	@Transactional
	public IntentoExamenVista consultarIntento(Long intentoId) {
		IntentoExamen intento = finalizarSiVencido(buscarIntento(intentoId), reloj.ahora());
		Examen examen = buscarExamen(intento.examenId());
		return crearVista(intento, examen, cargarPreguntas(intento.preguntasIds()));
	}

	@Override
	@Transactional
	public IntentoExamenVista registrarRespuesta(Long intentoId, Long preguntaId, Set<String> seleccionadas) {
		IntentoExamen intento = buscarIntento(intentoId);
		if (intento.estaVencido(reloj.ahora()) && !intento.finalizado()) {
			intento = cerrarIntento(intento);
			return crearVista(intento, buscarExamen(intento.examenId()), cargarPreguntas(intento.preguntasIds()));
		}
		Pregunta pregunta = buscarPregunta(preguntaId);
		intento.responder(preguntaId, seleccionadas, pregunta);
		intentos.guardar(intento);
		return crearVista(intento, buscarExamen(intento.examenId()), cargarPreguntas(intento.preguntasIds()));
	}

	@Override
	@Transactional
	public IntentoExamenVista finalizarIntento(Long intentoId) {
		IntentoExamen intento = buscarIntento(intentoId);
		Examen examen = buscarExamen(intento.examenId());
		List<Pregunta> preguntasDelExamen = cargarPreguntas(intento.preguntasIds());
		cerrarIntento(intento, preguntasDelExamen);
		return crearVista(intento, examen, preguntasDelExamen);
	}

	@Scheduled(fixedDelayString = "${examenes.revisar-vencimientos-ms:1000}")
	@Transactional
	public void finalizarIntentosVencidos() {
		LocalDateTime ahora = reloj.ahora();
		for (IntentoExamen intento : intentos.buscarVencidos(ahora)) {
			cerrarIntento(intento);
		}
	}

	private IntentoExamenVista crearVista(IntentoExamen intento, Examen examen, List<Pregunta> preguntasDelExamen) {
		var respuestas = intento.respuestasPorPregunta();
		List<PreguntaEstudiante> preguntasVista = preguntasDelExamen.stream()
				.map(pregunta -> {
					Set<String> seleccionadas = respuestas.getOrDefault(pregunta.preguntaId(), Set.of());
					return new PreguntaEstudiante(
							pregunta.preguntaId(),
							pregunta.enunciado(),
							pregunta.opciones().stream().map(opcion -> opcion.texto()).toList(),
							seleccionadas.stream().toList(),
							!seleccionadas.isEmpty()
					);
				})
				.toList();
		BigDecimal puntajeMaximo = preguntasDelExamen.stream()
				.map(Pregunta::puntaje)
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		long segundosRestantes = intento.finalizado() ? 0
				: Math.max(0, Duration.between(reloj.ahora(), intento.venceEn()).toSeconds());
		Long resultadoId = intento.finalizado()
				? generarResultado(intento, preguntasDelExamen).resultadoId()
				: null;
		return new IntentoExamenVista(
				intento.intentoId(),
				intento.sesionId(),
				intento.examenId(),
				examen.titulo(),
				intento.iniciadoEn(),
				intento.venceEn(),
				segundosRestantes,
				preguntasVista,
				intento.preguntasRespondidas(),
				intento.preguntasPendientes(),
				intento.finalizado(),
				puntajeMaximo,
				intento.calificacion(),
				resultadoId
		);
	}

	private List<Pregunta> cargarPreguntas(List<Long> preguntasIds) {
		return preguntasIds.stream().map(this::buscarPregunta).toList();
	}

	private Pregunta buscarPregunta(Long preguntaId) {
		return preguntas.buscarPorId(preguntaId)
				.orElseThrow(() -> new RecursoNoEncontradoException("No existe la pregunta indicada."));
	}

	private Examen buscarExamen(Long examenId) {
		return examenes.buscarPorId(examenId)
				.orElseThrow(() -> new RecursoNoEncontradoException("No existe el examen indicado."));
	}

	private IntentoExamen buscarIntento(Long intentoId) {
		return intentos.buscarPorId(intentoId)
				.orElseThrow(() -> new RecursoNoEncontradoException("No existe el intento indicado."));
	}

	private Sesion buscarSesion(Long sesionId) {
		return sesiones.buscarPorId(sesionId)
				.orElseThrow(() -> new RecursoNoEncontradoException("No existe la sesión indicada."));
	}

	private IntentoExamen finalizarSiVencido(IntentoExamen intento, LocalDateTime ahora) {
		if (!intento.finalizado() && intento.estaVencido(ahora)) {
			return cerrarIntento(intento);
		}
		return intento;
	}

	private IntentoExamen cerrarIntento(IntentoExamen intento) {
		return cerrarIntento(intento, cargarPreguntas(intento.preguntasIds()));
	}

	/**
	 * Califica y finaliza el intento, lo persiste y genera su Resultado (con id propio),
	 * que es lo que consulta el estudiante al cierre (HU-07) y en su historial (HU-09).
	 */
	private IntentoExamen cerrarIntento(IntentoExamen intento, List<Pregunta> preguntasDelExamen) {
		intento.finalizar(preguntasDelExamen);
		intentos.guardar(intento);
		generarResultado(intento, preguntasDelExamen);
		return intento;
	}

	/**
	 * Idempotente: si el resultado ya existe lo devuelve. También "sana" intentos finalizados
	 * antes de existir la integración, que se quedaron sin resultado.
	 */
	private Resultado generarResultado(IntentoExamen intento, List<Pregunta> preguntasDelExamen) {
		List<PreguntaCalificable> calificables = preguntasDelExamen.stream()
				.map(pregunta -> new PreguntaCalificable(
						pregunta.preguntaId(), pregunta.respuestasCorrectas(), pregunta.puntaje()
				))
				.toList();
		return resultados.generarResultado(
				intento.sesionId(), intento.estudianteId(), intento.respuestasPorPregunta(), calificables
		);
	}
}