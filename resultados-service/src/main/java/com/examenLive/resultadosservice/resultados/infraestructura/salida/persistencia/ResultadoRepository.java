package com.examenLive.resultadosservice.resultados.infraestructura.salida.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResultadoRepository extends JpaRepository<ResultadoEntity, Long> {

    Optional<ResultadoEntity> findFirstBySesionIdAndEstudianteId(Long sesionId, Long estudianteId);

    List<ResultadoEntity> findByEstudianteIdOrderByCreadoEnDesc(Long estudianteId);
}
