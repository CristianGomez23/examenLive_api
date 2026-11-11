package com.examenLive.examenesservice.examenes;

import com.examenLive.examenesservice.examenes.aplicacion.servicios.ExamenService;
import com.examenLive.examenesservice.examenes.dominio.Examen;
import com.examenLive.examenesservice.examenes.dominio.IntentoExamen;
import com.examenLive.examenesservice.examenes.dominio.puertos.entrada.IntentoExamenVista;
import com.examenLive.examenesservice.examenes.dominio.puertos.entrada.PreguntaEstudiante;
import com.examenLive.examenesservice.examenes.dominio.puertos.salida.ExamenRepository;
import com.examenLive.examenesservice.examenes.dominio.puertos.salida.IntentoExamenRepository;
import com.examenLive.examenesservice.identidad.dominio.puertos.salida.Reloj;
import com.examenLive.examenesservice.identidad.GeneradorIdNumericoFake;
import com.examenLive.examenesservice.fixtures.preguntas.dominio.puertos.salida.PreguntaRepository;
import com.examenLive.examenesservice.fixtures.preguntas.aplicacion.servicios.PreguntaService;
import com.examenLive.examenesservice.fixtures.preguntas.dominio.OpcionPregunta;
import com.examenLive.examenesservice.fixtures.preguntas.dominio.Pregunta;
import com.examenLive.examenesservice.fixtures.preguntas.dominio.TipoPregunta;
import com.examenLive.examenesservice.fixtures.sesiones.dominio.puertos.salida.SesionRepository;
import com.examenLive.examenesservice.fixtures.sesiones.dominio.Sesion;
import com.examenLive.examenesservice.fixtures.sesiones.dominio.SesionFactory;
import com.examenLive.examenesservice.fixtures.sesiones.dominio.VentanaTiempo;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExamenServiceTest {

	@Test
	void creaExamenYCalculaPuntajeMostrandoProgresoSinExponerClaves() {
		GeneradorIdNumericoFake ids = new GeneradorIdNumericoFake(100);
		FakePreguntaRepository preguntas = new FakePreguntaRepository();
		PreguntaService banco = new PreguntaService(preguntas, ids);
		Pregunta primera = banco.registrar(
				new TipoPregunta("seleccion-multiple"),
				"Selecciona A y B",
				List.of(new OpcionPregunta("A", true), new OpcionPregunta("B", true),
						new OpcionPregunta("C", false), new OpcionPregunta("D", false)),
				new BigDecimal("2.5")
		);
		Pregunta segunda = banco.registrar(
				new TipoPregunta("seleccion-multiple"),
				"Selecciona X",
				List.of(new OpcionPregunta("X", true), new OpcionPregunta("Y", false),
						new OpcionPregunta("Z", false)),
				BigDecimal.ONE
		);
		FakeExamenRepository examenes = new FakeExamenRepository();
		FakeIntentoRepository intentos = new FakeIntentoRepository();
		FakeSesionRepository sesiones = new FakeSesionRepository();
		Reloj reloj = reloj("2026-10-05T10:00:00Z");
		ExamenService servicio = new ExamenService(examenes, intentos, adaptar(preguntas), ids, adaptar(sesiones), reloj, (i,preguntasFinales)->{});

		Examen examen = servicio.crear(
				"Evaluación de prueba", List.of(primera.preguntaId(), segunda.preguntaId()), 30
		);
		Sesion sesion = new SesionFactory(ids).crear(
				examen.examenId(), ventana("2026-10-05T10:00:00", "2026-10-05T11:59:00")
		);
		sesiones.guardar(sesion);
		IntentoExamenVista alIniciar = servicio.iniciarIntento(sesion.getId(), 700L);

		assertEquals(0, alIniciar.preguntasRespondidas());
		assertEquals(2, alIniciar.preguntasPendientes());
		assertEquals(LocalDateTime.parse("2026-10-05T10:30:00"), alIniciar.venceEn());
		assertEquals(1800, alIniciar.segundosRestantes());
		assertEquals(List.of("A", "B", "C", "D"), alIniciar.preguntas().getFirst().opciones());
		assertFalse(tieneCampoCorrecta(PreguntaEstudiante.class));

		IntentoExamenVista reconectado = servicio.iniciarIntento(sesion.getId(), 700L);
		assertEquals(alIniciar.intentoId(), reconectado.intentoId());
		assertEquals(alIniciar.iniciadoEn(), reconectado.iniciadoEn());
		assertEquals(alIniciar.venceEn(), reconectado.venceEn());

		IntentoExamenVista enCurso = servicio.registrarRespuesta(
				reconectado.intentoId(), primera.preguntaId(), Set.of("A", "B")
		);
		assertEquals(1, enCurso.preguntasRespondidas());
		assertEquals(1, enCurso.preguntasPendientes());

		IntentoExamenVista finalizado = servicio.finalizarIntento(reconectado.intentoId());
		assertTrue(finalizado.finalizado());
		assertEquals(1, finalizado.preguntasPendientes());
		assertEquals(1, finalizado.calificacion().preguntasCorrectas());
		assertEquals(2, finalizado.calificacion().preguntasTotales());
		assertEquals(new BigDecimal("2.5"), finalizado.calificacion().puntajeObtenido());
		assertEquals(new BigDecimal("3.5"), finalizado.calificacion().puntajeMaximo());
		assertEquals(4, List.of(
				primera.preguntaId(), segunda.preguntaId(), examen.examenId(), alIniciar.intentoId()
		).stream().distinct().count());
	}

	@Test
	void venceEnElCierreAbsolutoSiLlegaAntesDeDuracion() {
		GeneradorIdNumericoFake ids = new GeneradorIdNumericoFake(1);
		FakePreguntaRepository preguntas = new FakePreguntaRepository();
		PreguntaService banco = new PreguntaService(preguntas, ids);
		Pregunta pregunta = banco.registrar(
				new TipoPregunta("seleccion-multiple"), "Pregunta límite",
				List.of(new OpcionPregunta("Sí", true), new OpcionPregunta("No", false),
						new OpcionPregunta("Tal vez", false)), BigDecimal.ONE
		);
		FakeExamenRepository examenes = new FakeExamenRepository();
		FakeIntentoRepository intentos = new FakeIntentoRepository();
		FakeSesionRepository sesiones = new FakeSesionRepository();
		ExamenService servicioAlInicio = new ExamenService(
				examenes, intentos, adaptar(preguntas), ids, adaptar(sesiones), reloj("2026-10-05T11:50:00Z"), (i,preguntasFinales)->{}
		);
		Examen examen = servicioAlInicio.crear("Cierre temprano", List.of(pregunta.preguntaId()), 60);
		Sesion sesion = new SesionFactory(ids).crear(
				examen.examenId(), ventana("2026-10-05T10:00:00", "2026-10-05T11:59:00")
		);
		sesiones.guardar(sesion);
		IntentoExamenVista intento = servicioAlInicio.iniciarIntento(sesion.getId(), 702L);
		assertEquals(LocalDateTime.parse("2026-10-05T11:59:00"), intento.venceEn());
		assertEquals(540, intento.segundosRestantes());

		ExamenService servicioTrasCierre = new ExamenService(
				examenes, intentos, adaptar(preguntas), ids, adaptar(sesiones), reloj("2026-10-05T12:00:00Z"), (i,preguntasFinales)->{}
		);
		IntentoExamenVista vencido = servicioTrasCierre.consultarIntento(intento.intentoId());
		assertTrue(vencido.finalizado());
		assertEquals(0, vencido.calificacion().puntajeObtenido().signum());
		assertEquals(0, vencido.segundosRestantes());
		org.junit.jupiter.api.Assertions.assertThrows(
				IllegalStateException.class,
				() -> servicioTrasCierre.registrarRespuesta(intento.intentoId(), pregunta.preguntaId(), Set.of("Sí"))
		);
		assertEquals(0, servicioTrasCierre.consultarIntento(intento.intentoId()).preguntasRespondidas());
	}

	@Test
	void rechazaComposicionConPreguntasDuplicadas() {
		GeneradorIdNumericoFake ids = new GeneradorIdNumericoFake(1);
		FakePreguntaRepository preguntas = new FakePreguntaRepository();
		PreguntaService banco = new PreguntaService(preguntas, ids);
		Pregunta pregunta = banco.registrar(
				new TipoPregunta("seleccion-multiple"), "Enunciado",
				List.of(new OpcionPregunta("A", true), new OpcionPregunta("B", false),
						new OpcionPregunta("C", false)), BigDecimal.ONE
		);
		ExamenService servicio = new ExamenService(
				new FakeExamenRepository(), new FakeIntentoRepository(), adaptar(preguntas), ids,
				adaptar(new FakeSesionRepository()), reloj("2026-10-05T10:00:00Z"), (i,preguntasFinales)->{}
		);

		org.junit.jupiter.api.Assertions.assertThrows(
				IllegalArgumentException.class,
				() -> servicio.crear("Duplicado", List.of(pregunta.preguntaId(), pregunta.preguntaId()), 30)
		);
	}

	private static Reloj reloj(String instante) {
		return () -> LocalDateTime.ofInstant(Instant.parse(instante), ZoneOffset.UTC);
	}

	private static VentanaTiempo ventana(String inicio, String fin) {
		return new VentanaTiempo(LocalDateTime.parse(inicio), LocalDateTime.parse(fin));
	}

	private static boolean tieneCampoCorrecta(Class<?> tipo) {
		return java.util.Arrays.stream(tipo.getRecordComponents())
				.anyMatch(componente -> componente.getName().equals("correcta"));
	}

	private static class FakePreguntaRepository implements PreguntaRepository {
		private final Map<Long, Pregunta> datos = new HashMap<>();

		@Override
		public void guardar(Pregunta pregunta) {
			datos.put(pregunta.preguntaId(), pregunta);
		}

		@Override
		public Optional<Pregunta> buscarPorId(Long preguntaId) {
			return Optional.ofNullable(datos.get(preguntaId));
		}

		@Override
		public List<Pregunta> buscarActivas() {
			return datos.values().stream().filter(Pregunta::estaActiva).toList();
		}

		@Override
		public void eliminar(Pregunta pregunta) {
			datos.remove(pregunta.preguntaId());
		}
	}

	private static class FakeExamenRepository implements ExamenRepository {
		private final Map<Long, Examen> datos = new HashMap<>();

		@Override
		public Examen guardar(Examen examen) {
			datos.put(examen.examenId(), examen);
			return examen;
		}

		@Override
		public Optional<Examen> buscarPorId(Long examenId) {
			return Optional.ofNullable(datos.get(examenId));
		}
	}

	private static class FakeIntentoRepository implements IntentoExamenRepository {
		private final Map<Long, IntentoExamen> datos = new HashMap<>();

		@Override
		public IntentoExamen guardar(IntentoExamen intento) {
			datos.put(intento.intentoId(), intento);
			return intento;
		}

		@Override
		public Optional<IntentoExamen> buscarPorId(Long intentoId) {
			return Optional.ofNullable(datos.get(intentoId));
		}

		@Override
		public Optional<IntentoExamen> buscarPorSesionYEstudiante(Long sesionId, Long estudianteId) {
			return datos.values().stream()
					.filter(intento -> intento.sesionId().equals(sesionId)
							&& intento.estudianteId().equals(estudianteId))
					.findFirst();
		}

		@Override
		public List<IntentoExamen> buscarVencidos(LocalDateTime ahora) {
			return datos.values().stream()
					.filter(intento -> !intento.finalizado() && intento.estaVencido(ahora))
					.toList();
		}
	}

	private static class FakeSesionRepository implements SesionRepository {
		private final Map<Long, Sesion> datos = new HashMap<>();

		@Override
		public Optional<Sesion> buscarPorId(Long sesionId) {
			return Optional.ofNullable(datos.get(sesionId));
		}

		@Override
		public void guardar(Sesion sesion) {
			datos.put(sesion.getId(), sesion);
		}
	}

 private static com.examenLive.examenesservice.examenes.dominio.puertos.salida.BancoPreguntas adaptar(FakePreguntaRepository repo){
  return new com.examenLive.examenesservice.examenes.dominio.puertos.salida.BancoPreguntas(){
   public Optional<com.examenLive.examenesservice.examenes.dominio.PreguntaExamen> buscarPorId(Long id){return repo.buscarPorId(id).map(p->new com.examenLive.examenesservice.examenes.dominio.PreguntaExamen(p.preguntaId(),p.enunciado(),p.opciones().stream().map(o->new com.examenLive.examenesservice.examenes.dominio.PreguntaExamen.Opcion(o.texto(),o.correcta())).toList(),p.puntaje(),p.estaActiva()));}
   public void registrarUso(Long id){repo.buscarPorId(id).orElseThrow().registrarUsoEnSesionAplicada();}
  };
 }
 private static com.examenLive.examenesservice.examenes.dominio.puertos.salida.ConsultaSesiones adaptar(FakeSesionRepository repo){
  return new com.examenLive.examenesservice.examenes.dominio.puertos.salida.ConsultaSesiones(){
   public Optional<com.examenLive.examenesservice.examenes.dominio.SesionExamen> buscarPorId(Long id){return repo.buscarPorId(id).map(s->new com.examenLive.examenesservice.examenes.dominio.SesionExamen(s.getId(),s.getExamenId(),new com.examenLive.examenesservice.examenes.dominio.SesionExamen.Ventana(s.getVentanaTiempo().inicio(),s.getVentanaTiempo().fin())));}
   public void agregarEstudiante(Long s,Long e){repo.buscarPorId(s).orElseThrow().agregarEstudiante(e);}
  };
 }
}
