package com.examenLive.resultadosservice.resultados.dominio.puertos.salida;


import com.examenLive.resultadosservice.comunicacion.contratos.DatosIntento;
public interface ConsultaIntentos { DatosIntento obtenerFinalizado(Long intentoId); }
