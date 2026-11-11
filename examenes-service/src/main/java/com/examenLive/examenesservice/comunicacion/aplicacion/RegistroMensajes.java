package com.examenLive.examenesservice.comunicacion.aplicacion;


import java.util.UUID;
public interface RegistroMensajes {
    boolean registrarSiNuevo(String consumidor, UUID eventId);
}
