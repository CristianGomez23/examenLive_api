package com.examenLive.examenLive_api.examenes.infraestructura.salida.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class RespuestaSeleccionadaEntity {

	@Column(name = "pregunta_id", nullable = false)
	private Long preguntaId;
	@Column(name = "opcion", nullable = false)
	private String opcion;

	protected RespuestaSeleccionadaEntity() {
	}

	public RespuestaSeleccionadaEntity(Long preguntaId, String opcion) {
		this.preguntaId = preguntaId;
		this.opcion = opcion;
	}

	public Long getPreguntaId() {
		return preguntaId;
	}

	public String getOpcion() {
		return opcion;
	}
}