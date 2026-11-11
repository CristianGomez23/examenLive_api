package com.examenLive.preguntasservice.preguntas.infraestructura.salida.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PreguntaJpaRepository extends JpaRepository<PreguntaEntity, Long> {

 @Override
 @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
 java.util.Optional<PreguntaEntity> findById(Long id);

	List<PreguntaEntity> findByActivaTrueOrderByEnunciadoAsc();
}
