package com.examenLive.resultadosservice.comunicacion.aplicacion;


import java.util.UUID;
public interface RegistroMensajes {
    boolean registrarSiNuevo(String consumidor, UUID eventId);
}
