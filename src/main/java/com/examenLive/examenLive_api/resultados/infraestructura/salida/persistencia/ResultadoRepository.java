package com.examenLive.examenLive_api.resultados.infraestructura.salida.persistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ResultadoRepository extends JpaRepository<ResultadoEntity, UUID> {
}
