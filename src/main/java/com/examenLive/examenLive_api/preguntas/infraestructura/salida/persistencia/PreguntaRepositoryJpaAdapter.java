package com.examenLive.examenLive_api.preguntas.infraestructura.salida.persistencia;

import com.examenLive.examenLive_api.preguntas.aplicacion.PreguntaRepository;
import com.examenLive.examenLive_api.preguntas.dominio.Pregunta;
import com.examenLive.examenLive_api.preguntas.dominio.TipoPregunta;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class PreguntaRepositoryJpaAdapter implements PreguntaRepository {

	private final PreguntaJpaRepository repositorioJpa;

	public PreguntaRepositoryJpaAdapter(PreguntaJpaRepository repositorioJpa) {
		this.repositorioJpa = repositorioJpa;
	}

	@Override
	public void guardar(Pregunta pregunta) {
		repositorioJpa.save(aEntidad(pregunta));
	}

	@Override
	public Optional<Pregunta> buscarPorId(UUID preguntaId) {
		return repositorioJpa.findById(preguntaId).map(this::aDominio);
	}

	@Override
	public List<Pregunta> buscarActivas() {
		return repositorioJpa.findByActivaTrueOrderByEnunciadoAsc().stream()
				.map(this::aDominio)
				.toList();
	}

	@Override
	public void eliminar(Pregunta pregunta) {
		repositorioJpa.deleteById(pregunta.preguntaId());
	}

	private PreguntaEntity aEntidad(Pregunta pregunta) {
		return new PreguntaEntity(
				pregunta.preguntaId(),
				pregunta.tipo().nombre(),
				pregunta.enunciado(),
				pregunta.respuestasCorrectas(),
				pregunta.puntaje(),
				pregunta.estaActiva(),
				pregunta.usadaEnSesionAplicada()
		);
	}

	private Pregunta aDominio(PreguntaEntity entidad) {
		return Pregunta.reconstruir(
				entidad.getId(),
				new TipoPregunta(entidad.getTipo()),
				entidad.getEnunciado(),
				entidad.getRespuestasCorrectas(),
				entidad.getPuntaje(),
				entidad.isActiva(),
				entidad.isUsadaEnSesionAplicada()
		);
	}
}