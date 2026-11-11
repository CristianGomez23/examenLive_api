package com.examenLive.examenesservice.examenes.dominio;

import java.math.BigDecimal;
import java.util.*;
public record PreguntaExamen(Long preguntaId,String enunciado,List<Opcion> opciones,BigDecimal puntaje,boolean activa) {
 public record Opcion(String texto,boolean correcta){}
 public PreguntaExamen { opciones=List.copyOf(opciones); }
 public Set<String> respuestasCorrectas(){return opciones.stream().filter(Opcion::correcta).map(Opcion::texto).collect(java.util.stream.Collectors.toUnmodifiableSet());}
 public boolean tieneOpcionesIncorrectasSuficientes(){return opciones.stream().filter(o -> !o.correcta()).count()>=2;}
 public boolean estaActiva(){return activa;}
}
