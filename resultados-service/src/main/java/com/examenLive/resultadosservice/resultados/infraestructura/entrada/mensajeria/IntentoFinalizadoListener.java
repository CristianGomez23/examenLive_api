package com.examenLive.resultadosservice.resultados.infraestructura.entrada.mensajeria;


import com.examenLive.resultadosservice.comunicacion.contratos.IntentoFinalizadoEvento;
import com.examenLive.resultadosservice.resultados.dominio.puertos.entrada.ComunicacionResultadosUseCase;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
@Component
public class IntentoFinalizadoListener {
    private final ComunicacionResultadosUseCase casos;
    private final ObjectMapper mapper;
    public IntentoFinalizadoListener(ComunicacionResultadosUseCase casos,ObjectMapper mapper) {this.casos=casos;this.mapper=mapper;}
    @KafkaListener(topics="examenes.intentos-finalizados.v1")
    public void recibir(String json) {casos.alFinalizarIntento(mapper.readValue(json,IntentoFinalizadoEvento.class));}
}
