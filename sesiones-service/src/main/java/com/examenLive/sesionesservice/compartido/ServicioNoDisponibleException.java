package com.examenLive.sesionesservice.compartido;

public class ServicioNoDisponibleException extends RuntimeException {
 public ServicioNoDisponibleException(String mensaje,Throwable causa){super(mensaje,causa);}
}
