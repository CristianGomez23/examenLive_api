package com.examenLive.examenLive_api.resultados.infraestructura.salida.persistencia;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.Instant;

/**
 * Una fila por cada opción seleccionada en una pregunta del resultado.
 */
@Embeddable
public class RespuestaResultadoEntity {

    @Column(name = "pregunta_id", nullable = false)
    private Long preguntaId;
    @Column(name = "valor", nullable = false)
    private String valor;
    @Column(name = "enviada_en", nullable = false)
    private Instant enviadaEn;

    protected RespuestaResultadoEntity() {}

    public RespuestaResultadoEntity(Long preguntaId, String valor, Instant enviadaEn) {
        this.preguntaId = preguntaId;
        this.valor = valor;
        this.enviadaEn = enviadaEn;
    }

    public Long getPreguntaId() { return preguntaId; }
    public String getValor() { return valor; }
    public Instant getEnviadaEn() { return enviadaEn; }
}
