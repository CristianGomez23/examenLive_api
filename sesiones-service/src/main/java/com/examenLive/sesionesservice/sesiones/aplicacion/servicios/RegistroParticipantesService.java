package com.examenLive.sesionesservice.sesiones.aplicacion.servicios;
import com.examenLive.sesionesservice.sesiones.dominio.puertos.salida.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class RegistroParticipantesService {
 private final SesionRepository sesiones;private final Participantes participantes;
 public RegistroParticipantesService(SesionRepository sesiones,Participantes participantes){this.sesiones=sesiones;this.participantes=participantes;}
 @Transactional
 public void agregar(Long sesionId,Long estudianteId){
  var sesion=sesiones.buscarPorId(sesionId).orElseThrow(()->new java.util.NoSuchElementException("No existe la sesión indicada."));
  sesion.agregarEstudiante(estudianteId);
  participantes.agregar(sesionId,estudianteId);
 }
}
