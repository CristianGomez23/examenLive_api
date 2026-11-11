package com.examenLive.examenLive_api.examenes.dominio.puertos.salida;

import com.examenLive.examenLive_api.examenes.dominio.Examen;

import java.util.Optional;

public interface ExamenRepository {

	Examen guardar(Examen examen);

	Optional<Examen> buscarPorId(Long examenId);
}