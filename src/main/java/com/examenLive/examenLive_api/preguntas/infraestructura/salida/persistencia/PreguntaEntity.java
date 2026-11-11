package com.examenLive.examenLive_api.preguntas.infraestructura.salida.persistencia;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "preguntas")
public class PreguntaEntity {

	@Id
	private Long id;
	private String tipo;
	@Column(nullable = false, length = 2000)
	private String enunciado;
	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "pregunta_opciones", joinColumns = @JoinColumn(name = "pregunta_id"))
	@OrderColumn(name = "orden")
	private List<OpcionPreguntaEntity> opciones = new ArrayList<>();
	@Column(nullable = false, precision = 10, scale = 2)
	private BigDecimal puntaje;
	@Column(nullable = false)
	private boolean activa;
	@Column(nullable = false)
	private boolean usadaEnSesionAplicada;

	protected PreguntaEntity() {
	}

	public PreguntaEntity(
			Long id,
			String tipo,
			String enunciado,
			List<OpcionPreguntaEntity> opciones,
			BigDecimal puntaje,
			boolean activa,
			boolean usadaEnSesionAplicada
	) {
		this.id = id;
		this.tipo = tipo;
		this.enunciado = enunciado;
		this.opciones = new ArrayList<>(opciones);
		this.puntaje = puntaje;
		this.activa = activa;
		this.usadaEnSesionAplicada = usadaEnSesionAplicada;
	}

	public Long getId() {
		return id;
	}

	public String getTipo() {
		return tipo;
	}

	public String getEnunciado() {
		return enunciado;
	}

	public List<OpcionPreguntaEntity> getOpciones() {
		return List.copyOf(opciones);
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