package com.examenLive.examenLive_api.preguntas.infraestructura.salida.persistencia;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "preguntas")
public class PreguntaEntity {

	@Id
	private UUID id;
	private String tipo;
	@Column(nullable = false, length = 2000)
	private String enunciado;
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "pregunta_respuestas_correctas", joinColumns = @JoinColumn(name = "pregunta_id"))
	@Column(name = "respuesta", nullable = false)
	private Set<String> respuestasCorrectas = new HashSet<>();
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal puntaje;
	@Column(nullable = false)
	private boolean activa;
	@Column(nullable = false)
	private boolean usadaEnSesionAplicada;

	protected PreguntaEntity() {
	}

	public PreguntaEntity(
			UUID id,
			String tipo,
			String enunciado,
			Set<String> respuestasCorrectas,
			BigDecimal puntaje,
			boolean activa,
			boolean usadaEnSesionAplicada
	) {
		this.id = id;
		this.tipo = tipo;
		this.enunciado = enunciado;
		this.respuestasCorrectas = new HashSet<>(respuestasCorrectas);
		this.puntaje = puntaje;
		this.activa = activa;
		this.usadaEnSesionAplicada = usadaEnSesionAplicada;
	}

	public UUID getId() {
		return id;
	}

	public String getTipo() {
		return tipo;
	}

	public String getEnunciado() {
		return enunciado;
	}

	public Set<String> getRespuestasCorrectas() {
		return Set.copyOf(respuestasCorrectas);
	}

	public BigDecimal getPuntaje() {
		return puntaje;
	}

	public boolean isActiva() {
		return activa;
	}

	public boolean isUsadaEnSesionAplicada() {
		return usadaEnSesionAplicada;
	}
}