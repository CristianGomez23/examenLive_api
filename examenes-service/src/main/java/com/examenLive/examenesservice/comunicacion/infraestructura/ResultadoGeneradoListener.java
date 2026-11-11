package com.examenLive.examenesservice.comunicacion.infraestructura;


import com.examenLive.examenesservice.comunicacion.aplicacion.ConfirmarResultadoUseCase;
import com.examenLive.examenesservice.comunicacion.contratos.ResultadoGeneradoEvento;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
@Component
public class ResultadoGeneradoListener {
    private final ConfirmarResultadoUseCase casos;
    private final ObjectMapper mapper;
    public ResultadoGeneradoListener(ConfirmarResultadoUseCase casos,ObjectMapper mapper) {this.casos=casos;this.mapper=mapper;}
    @KafkaListener(topics="resultados.generados.v1")
    public void recibir(String json) {casos.confirmar(mapper.readValue(json,ResultadoGeneradoEvento.class));}
}
