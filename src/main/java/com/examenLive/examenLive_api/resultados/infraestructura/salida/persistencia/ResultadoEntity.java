package com.examenLive.examenLive_api.resultados.infraestructura.salida.persistencia;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "resultados",
    uniqueConstraints = @UniqueConstraint(
        name = "resultados_sesion_estudiante_unico",
        columnNames = {"sesion_id", "estudiante_id"}
    )
)
public class ResultadoEntity {
    @Id
    private Long id;
    @Column(name = "sesion_id")
    private Long sesionId;
    @Column(name = "estudiante_id")
    private Long estudianteId;
    private Instant creadoEn;
    private String estado;
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "resultado_respuestas", joinColumns = @JoinColumn(name = "resultado_id"))
    private List<RespuestaResultadoEntity> respuestas = new ArrayList<>();
    @Column(precision = 10, scale = 2)
    private BigDecimal puntajeObtenido;
    @Column(precision = 10, scale = 2)
    private BigDecimal puntajeMaximo;
    private Integer respuestasCorrectas;
    private Integer totalPreguntas;

    protected ResultadoEntity(){}

    public ResultadoEntity(
            Long id,
            Long sesionId,
            Long estudianteId,
            Instant creadoEn,
            String estado,
            List<RespuestaResultadoEntity> respuestas,
            BigDecimal puntajeObtenido,
            BigDecimal puntajeMaximo,
            Integer respuestasCorrectas,
            Integer totalPreguntas
    ){
        this.id=id;
        this.sesionId=sesionId;
        this.estudianteId=estudianteId;
        this.creadoEn=creadoEn;
        this.estado=estado;
        this.respuestas=new ArrayList<>(respuestas);
        this.puntajeObtenido=puntajeObtenido;
        this.puntajeMaximo=puntajeMaximo;
        this.respuestasCorrectas=respuestasCorrectas;
        this.totalPreguntas=totalPreguntas;
    }

    public Long getId(){return id;}
    public Long getSesionId(){return sesionId;}
    public Long getEstudianteId(){return estudianteId;}
    public Instant getCreadoEn(){return creadoEn;}
    public String getEstado(){return estado;}
    public List<RespuestaResultadoEntity> getRespuestas(){return List.copyOf(respuestas);}
    public BigDecimal getPuntajeObtenido(){return puntajeObtenido;}
    public BigDecimal getPuntajeMaximo(){return puntajeMaximo;}
    public Integer getRespuestasCorrectas(){return respuestasCorrectas;}
    public Integer getTotalPreguntas(){return totalPreguntas;}
}
