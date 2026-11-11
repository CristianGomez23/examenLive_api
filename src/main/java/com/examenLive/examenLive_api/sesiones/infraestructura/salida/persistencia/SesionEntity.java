package com.examenLive.examenLive_api.sesiones.infraestructura.salida.persistencia;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "sesiones")
public class SesionEntity {

	@Id
	private Long id;
	@Column(nullable = false)
	private Long examenId;
	@Column(nullable = false)
	private LocalDateTime inicio;
	@Column(nullable = false)
	private LocalDateTime fin;
	@ElementCollection
	@CollectionTable(name = "sesion_estudiantes", joinColumns = @JoinColumn(name = "sesion_id"))
	@Column(name = "estudiante_id", nullable = false)
	private Set<Long> estudiantesIds = new HashSet<>();

	protected SesionEntity() {
	}

	public SesionEntity(Long id, Long examenId, LocalDateTime inicio, LocalDateTime fin, Set<Long> estudiantesIds) {
		this.id = id;
		this.examenId = examenId;
		this.inicio = inicio;
		this.fin = fin;
		this.estudiantesIds = new HashSet<>(estudiantesIds);
	}

	public Long getId() {
		return id;
	}

	public Long getExamenId() {
		return examenId;
	}

	public LocalDateTime getInicio() {
		return inicio;
	}

	public LocalDateTime getFin() {
		return fin;
	}

	public Set<Long> getEstudiantesIds() {
		return Set.copyOf(estudiantesIds);
	}
}