package com.examenLive.examenesservice.examenes.dominio.puertos.salida;

import com.examenLive.examenesservice.examenes.dominio.Examen;

import java.util.Optional;

public interface ExamenRepository {

	Examen guardar(Examen examen);

	Optional<Examen> buscarPorId(Long examenId);
}
