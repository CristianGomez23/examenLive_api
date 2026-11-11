package com.examenLive.sesionesservice.sesiones.infraestructura.salida.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SesionJpaRepository extends JpaRepository<SesionEntity, Long> {
}
