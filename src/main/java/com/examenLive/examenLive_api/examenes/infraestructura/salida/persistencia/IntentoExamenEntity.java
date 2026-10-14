package com.examenLive.examenLive_api.examenes.infraestructura.salida.persistencia;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "intentos_examen")
public class IntentoExamenEntity {

	@Id
	private Long id;
	@Column(nullable = false)
	private Long examenId;
	private Long sesionId;
	@Column(nullable = false)
	private Long estudianteId;
	private LocalDateTime iniciadoEn;
	private LocalDateTime venceEn;
	@ElementCollection
	@CollectionTable(name = "intento_preguntas", joinColumns = @JoinColumn(name = "intento_id"))
	@Column(name = "pregunta_id", nullable = false)
	@OrderColumn(name = "orden")
	private List<Long> preguntasIds = new ArrayList<>();
	@ElementCollection
	@CollectionTable(name = "intento_respuestas", joinColumns = @JoinColumn(name = "intento_id"))
	@OrderColumn(name = "orden")
	private List<RespuestaSeleccionadaEntity> respuestas = new ArrayList<>();
	private BigDecimal puntajeObtenido;
	private BigDecimal puntajeMaximo;
	private Integer preguntasCorrectas;
	private Integer preguntasTotales;

	protected IntentoExamenEntity() {
	}

	public IntentoExamenEntity(
			Long id,
			Long sesionId,
			Long examenId,
			Long estudianteId,
			LocalDateTime iniciadoEn,
			LocalDateTime venceEn,
			List<Long> preguntasIds,
			List<RespuestaSeleccionadaEntity> respuestas,
			BigDecimal puntajeObtenido,
			BigDecimal puntajeMaximo,
			Integer preguntasCorrectas,
			Integer preguntasTotales
	) {
		this.id = id;
		this.sesionId = sesionId;
		this.examenId = examenId;
		this.estudianteId = estudianteId;
		this.iniciadoEn = iniciadoEn;
		this.venceEn = venceEn;
		this.preguntasIds = new ArrayList<>(preguntasIds);
		this.respuestas = new ArrayList<>(respuestas);
		this.puntajeObtenido = puntajeObtenido;
		this.puntajeMaximo = puntajeMaximo;
		this.preguntasCorrectas = preguntasCorrectas;
		this.preguntasTotales = preguntasTotales;
	}

	public Long getId() {
		return id;
	}

	public Long getExamenId() {
		return examenId;
	}

	public Long getSesionId() {
		return sesionId;
	}

	public Long getEstudianteId() {
		return estudianteId;
	}

	public LocalDateTime getIniciadoEn() {
		return iniciadoEn;
	}

	public LocalDateTime getVenceEn() {
		return venceEn;
	}

	public List<Long> getPreguntasIds() {
		return List.copyOf(preguntasIds);
	}

	public List<RespuestaSeleccionadaEntity> getRespuestas() {
		return List.copyOf(respuestas);
	}

	public BigDecimal getPuntajeObtenido() {
		return puntajeObtenido;
	}

	public BigDecimal getPuntajeMaximo() {
		return puntajeMaximo;
	}

	public Integer getPreguntasCorrectas() {
		return preguntasCorrectas;
	}

	public Integer getPreguntasTotales() {
		return preguntasTotales;
	}
}