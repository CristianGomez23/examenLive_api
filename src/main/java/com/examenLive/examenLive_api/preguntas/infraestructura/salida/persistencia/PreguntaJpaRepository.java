package com.examenLive.examenLive_api.preguntas.infraestructura.salida.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreguntaJpaRepository extends JpaRepository<PreguntaEntity, Long> {

	List<PreguntaEntity> findByActivaTrueOrderByEnunciadoAsc();
}