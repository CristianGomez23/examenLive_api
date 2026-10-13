package com.examenLive.examenLive_api.examenes.infraestructura.salida.persistencia;

import com.examenLive.examenLive_api.examenes.dominio.puertos.salida.IntentoExamenRepository;
import com.examenLive.examenLive_api.examenes.dominio.CalificacionIntento;
import com.examenLive.examenLive_api.examenes.dominio.IntentoExamen;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class IntentoExamenRepositoryJpaAdapter implements IntentoExamenRepository {

	private final IntentoExamenJpaRepository repositorioJpa;

	public IntentoExamenRepositoryJpaAdapter(IntentoExamenJpaRepository repositorioJpa) {
		this.repositorioJpa = repositorioJpa;
	}

	@Override
	public IntentoExamen guardar(IntentoExamen intento) {
		List<RespuestaSeleccionadaEntity> respuestas = intento.respuestasPorPregunta().entrySet().stream()
				.flatMap(entrada -> entrada.getValue().stream()
						.map(opcion -> new RespuestaSeleccionadaEntity(entrada.getKey(), opcion)))
				.toList();
		CalificacionIntento calificacion = intento.calificacion();
		repositorioJpa.save(new IntentoExamenEntity(
				intento.intentoId(),
				intento.sesionId(),
				intento.examenId(),
				intento.estudianteId(),
				intento.iniciadoEn(),
				intento.venceEn(),
				intento.preguntasIds(),
				respuestas,
				calificacion == null ? null : calificacion.puntajeObtenido(),
				calificacion == null ? null : calificacion.puntajeMaximo(),
				calificacion == null ? null : calificacion.preguntasCorrectas(),
				calificacion == null ? null : calificacion.preguntasTotales()
		));
		return intento;
	}

	@Override
	public Optional<IntentoExamen> buscarPorId(Long intentoId) {
		return repositorioJpa.findById(intentoId).map(this::aDominio);
	}

	@Override
	public Optional<IntentoExamen> buscarPorSesionYEstudiante(Long sesionId, Long estudianteId) {
		return repositorioJpa.findFirstBySesionIdAndEstudianteIdOrderByIdDesc(sesionId, estudianteId)
				.map(this::aDominio);
	}

	@Override
	public List<IntentoExamen> buscarVencidos(LocalDateTime ahora) {
		return repositorioJpa.findByPuntajeObtenidoIsNullAndVenceEnLessThanEqualOrderByVenceEnAsc(ahora)
				.stream().map(this::aDominio).toList();
	}

	private IntentoExamen aDominio(IntentoExamenEntity entity) {
			Map<Long, Set<String>> respuestas = entity.getRespuestas().stream()
					.collect(Collectors.groupingBy(
							RespuestaSeleccionadaEntity::getPreguntaId,
							HashMap::new,
							Collectors.mapping(RespuestaSeleccionadaEntity::getOpcion, Collectors.toSet())
					));
			CalificacionIntento calificacion = entity.getPuntajeObtenido() == null ? null
					: new CalificacionIntento(
							entity.getPuntajeObtenido(), entity.getPuntajeMaximo(),
							entity.getPreguntasCorrectas(), entity.getPreguntasTotales()
					);
			return IntentoExamen.reconstruir(
					entity.getId(), entity.getSesionId(), entity.getExamenId(), entity.getEstudianteId(),
					entity.getPreguntasIds(), entity.getIniciadoEn(), entity.getVenceEn(), respuestas, calificacion
			);
	}
}