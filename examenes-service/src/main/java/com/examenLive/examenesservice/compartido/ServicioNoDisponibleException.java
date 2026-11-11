package com.examenLive.examenesservice.compartido;

public class ServicioNoDisponibleException extends RuntimeException {
 public ServicioNoDisponibleException(String mensaje,Throwable causa){super(mensaje,causa);}
}
