package com.examenLive.examenLive_api.examenes.infraestructura.salida.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamenJpaRepository extends JpaRepository<ExamenEntity, Long> {
}