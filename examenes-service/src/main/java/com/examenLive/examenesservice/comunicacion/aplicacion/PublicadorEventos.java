package com.examenLive.examenesservice.comunicacion.aplicacion;


import java.util.UUID;
public interface PublicadorEventos {
    void guardar(UUID eventId, String topico, String clave, Object evento);
}
