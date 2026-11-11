package com.examenLive.examenLive_api.examenes.infraestructura.salida.persistencia;

import com.examenLive.examenLive_api.examenes.dominio.puertos.salida.ExamenRepository;
import com.examenLive.examenLive_api.examenes.dominio.Examen;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
public class ExamenRepositoryJpaAdapter implements ExamenRepository {

	private final ExamenJpaRepository repositorioJpa;

	public ExamenRepositoryJpaAdapter(ExamenJpaRepository repositorioJpa) {
		this.repositorioJpa = repositorioJpa;
	}

	@Override
	public Examen guardar(Examen examen) {
		repositorioJpa.save(new ExamenEntity(
				examen.examenId(), examen.titulo(), examen.duracion().toMinutes(), examen.preguntasIds()
		));
		return examen;
	}

	@Override
	public Optional<Examen> buscarPorId(Long examenId) {
		return repositorioJpa.findById(examenId)
				.map(entity -> new Examen(
						entity.getId(), entity.getTitulo(), entity.getPreguntasIds(),
						Duration.ofMinutes(entity.getDuracionMinutos())
				));
	}
}