package com.examenLive.examenLive_api.preguntas.infraestructura.salida.persistencia;

import com.examenLive.examenLive_api.preguntas.dominio.puertos.salida.PreguntaRepository;
import com.examenLive.examenLive_api.preguntas.dominio.Pregunta;
import com.examenLive.examenLive_api.preguntas.dominio.OpcionPregunta;
import com.examenLive.examenLive_api.preguntas.dominio.TipoPregunta;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

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
	public Optional<Pregunta> buscarPorId(Long preguntaId) {
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
				pregunta.opciones().stream()
						.map(opcion -> new OpcionPreguntaEntity(opcion.texto(), opcion.correcta()))
						.toList(),
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
				entidad.getOpciones().stream()
						.map(opcion -> new OpcionPregunta(opcion.getTexto(), opcion.isCorrecta()))
						.toList(),
				entidad.getPuntaje(),
				entidad.isActiva(),
				entidad.isUsadaEnSesionAplicada()
		);
	}
}