package com.examenLive.examenLive_api.resultados.infraestructura.salida.persistencia;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name="resultados")
public class ResultadoEntity {
    @Id
    private Long id;
    private Long sesionId;
    private Long estudianteId;
    private Instant creadoEn;
    private String estado;

    protected ResultadoEntity(){}

    public ResultadoEntity(Long id, Long sesionId, Long estudianteId, Instant creadoEn, String estado){
        this.id=id;
        this.sesionId=sesionId;
        this.estudianteId=estudianteId;
        this.creadoEn=creadoEn;
        this.estado=estado;
    }

    public Long getId(){return id;}
    public Long getSesionId(){return sesionId;}
    public Long getEstudianteId(){return estudianteId;}
    public Instant getCreadoEn(){return creadoEn;}
    public String getEstado(){return estado;}
}
