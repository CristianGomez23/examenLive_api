package com.examenLive.resultadosservice.resultados.dominio.puertos.entrada;


import com.examenLive.resultadosservice.comunicacion.contratos.IntentoFinalizadoEvento;
public interface ComunicacionResultadosUseCase {
    ResultadoGenerado generarDesdeIntento(Long intentoId);
    void alFinalizarIntento(IntentoFinalizadoEvento evento);
}
