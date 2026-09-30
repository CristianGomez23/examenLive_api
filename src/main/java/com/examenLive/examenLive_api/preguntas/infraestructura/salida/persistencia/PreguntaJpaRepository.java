package com.examenLive.examenLive_api.preguntas.infraestructura.salida.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PreguntaJpaRepository extends JpaRepository<PreguntaEntity, UUID> {

	List<PreguntaEntity> findByActivaTrueOrderByEnunciadoAsc();
}