package com.examenLive.examenLive_api.sesiones.infraestructura.salida.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SesionJpaRepository extends JpaRepository<SesionEntity, Long> {
}