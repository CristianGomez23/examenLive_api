package com.examenLive.examenLive_api.examenes.infraestructura.salida.persistencia;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "examenes")
public class ExamenEntity {

	@Id
	private Long id;
	@Column(nullable = false, length = 200)
	private String titulo;
	@Column(nullable = false)
	private Long duracionMinutos;
	@ElementCollection
	@CollectionTable(name = "examen_preguntas", joinColumns = @JoinColumn(name = "examen_id"))
	@Column(name = "pregunta_id", nullable = false)
	@OrderColumn(name = "orden")
	private List<Long> preguntasIds = new ArrayList<>();

	protected ExamenEntity() {
	}

	public ExamenEntity(Long id, String titulo, Long duracionMinutos, List<Long> preguntasIds) {
		this.id = id;
		this.titulo = titulo;
		this.duracionMinutos = duracionMinutos;
		this.preguntasIds = new ArrayList<>(preguntasIds);
	}

	public Long getId() {
		return id;
	}

	public String getTitulo() {
		return titulo;
	}

	public Long getDuracionMinutos() {
		return duracionMinutos;
	}

	public List<Long> getPreguntasIds() {
		return List.copyOf(preguntasIds);
	}
}