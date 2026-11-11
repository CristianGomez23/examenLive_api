package com.examenLive.examenesservice.examenes.dominio;

import java.time.LocalDateTime;
public record SesionExamen(Long id,Long examenId,Ventana ventana) {
 public record Ventana(LocalDateTime inicio,LocalDateTime fin){
  public boolean estaActiva(LocalDateTime momento){return !momento.isBefore(inicio)&&momento.isBefore(fin);}
 }
 public Long getId(){return id;}
 public Long getExamenId(){return examenId;}
 public Ventana getVentanaTiempo(){return ventana;}
}
