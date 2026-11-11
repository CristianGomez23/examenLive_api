package com.examenLive.examenesservice.examenes.dominio.puertos.salida;


import com.examenLive.examenesservice.examenes.dominio.IntentoExamen;
import com.examenLive.examenesservice.examenes.dominio.PreguntaExamen;
import java.util.List;
public interface NotificadorIntentoFinalizado { void notificar(IntentoExamen intento,List<PreguntaExamen> preguntas); }
