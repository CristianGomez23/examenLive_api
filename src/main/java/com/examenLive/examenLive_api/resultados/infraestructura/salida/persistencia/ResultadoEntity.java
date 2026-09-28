package com.examenLive.examenLive_api.resultados.infraestructura.salida.persistencia;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="resultados")
public class ResultadoEntity {
    @Id
    private UUID id;
    private UUID sesionId;
    private UUID estudianteId;
    private Instant creadoEn;
    private String estado;

    protected ResultadoEntity(){}

    public ResultadoEntity(UUID id, UUID sesionId, UUID estudianteId, Instant creadoEn, String estado){
        this.id=id;
        this.sesionId=sesionId;
        this.estudianteId=estudianteId;
        this.creadoEn=creadoEn;
        this.estado=estado;
    }

    public UUID getId(){return id;}
    public UUID getSesionId(){return sesionId;}
    public UUID getEstudianteId(){return estudianteId;}
    public Instant getCreadoEn(){return creadoEn;}
    public String getEstado(){return estado;}
}
