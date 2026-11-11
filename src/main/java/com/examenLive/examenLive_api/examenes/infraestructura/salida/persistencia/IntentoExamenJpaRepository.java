package com.examenLive.examenLive_api.examenes.infraestructura.salida.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface IntentoExamenJpaRepository extends JpaRepository<IntentoExamenEntity, Long> {

	Optional<IntentoExamenEntity> findFirstBySesionIdAndEstudianteIdOrderByIdDesc(
			Long sesionId,
			Long estudianteId
	);

	List<IntentoExamenEntity> findByPuntajeObtenidoIsNullAndVenceEnLessThanEqualOrderByVenceEnAsc(
			LocalDateTime ahora
	);
}