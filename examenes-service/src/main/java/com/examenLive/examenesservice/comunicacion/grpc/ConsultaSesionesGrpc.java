package com.examenLive.examenesservice.comunicacion.grpc;
import com.examenLive.contratos.contextos.v1.*;
import com.examenLive.examenesservice.examenes.dominio.SesionExamen;
import com.examenLive.examenesservice.examenes.dominio.puertos.salida.ConsultaSesiones;
import java.util.*;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;
@Component
public class ConsultaSesionesGrpc implements ConsultaSesiones {
 private final SesionesGrpcGrpc.SesionesGrpcBlockingStub stub;
 private final LlamadaRemota llamada=new LlamadaRemota("sesiones-service");
 public ConsultaSesionesGrpc(SesionesGrpcGrpc.SesionesGrpcBlockingStub stub){this.stub=stub;}
 public Optional<SesionExamen> buscarPorId(Long id){
  if(id==null)throw new IllegalArgumentException("El identificador de la sesión es obligatorio.");
  try {var s=llamada.ejecutar(()->stub.withDeadlineAfter(5,TimeUnit.SECONDS).obtenerSesion(Identificador.newBuilder().setId(id).build()));
   return Optional.of(new SesionExamen(s.getId(),s.getExamenId(),new SesionExamen.Ventana(LocalDateTime.parse(s.getInicio()),LocalDateTime.parse(s.getFin()))));
  }catch(NoSuchElementException e){return Optional.empty();}
 }
 public void agregarEstudiante(Long sesion,Long estudiante){llamada.ejecutar(()->stub.withDeadlineAfter(5,TimeUnit.SECONDS).agregarEstudiante(Participante.newBuilder().setSesionId(sesion).setEstudianteId(estudiante).build()));}
}
