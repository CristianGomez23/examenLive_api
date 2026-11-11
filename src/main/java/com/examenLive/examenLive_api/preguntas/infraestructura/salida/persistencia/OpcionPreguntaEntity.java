package com.examenLive.examenLive_api.preguntas.infraestructura.salida.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class OpcionPreguntaEntity {

	@Column(name = "texto", nullable = false)
	private String texto;
	@Column(name = "correcta", nullable = false)
	private boolean correcta;

	protected OpcionPreguntaEntity() {
	}

	public OpcionPreguntaEntity(String texto, boolean correcta) {
		this.texto = texto;
		this.correcta = correcta;
	}

	public String getTexto() {
		return texto;
	}

	public boolean isCorrecta() {
		return correcta;
	}
}