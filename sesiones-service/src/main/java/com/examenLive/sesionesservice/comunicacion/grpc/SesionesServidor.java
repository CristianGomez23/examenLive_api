package com.examenLive.sesionesservice.comunicacion.grpc;
import com.examenLive.contratos.contextos.v1.*;
import com.examenLive.sesionesservice.sesiones.dominio.puertos.entrada.SesionUseCase;
import com.examenLive.sesionesservice.sesiones.aplicacion.servicios.RegistroParticipantesService;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;
@Service
public class SesionesServidor extends SesionesGrpcGrpc.SesionesGrpcImplBase {
 private final SesionUseCase sesiones;private final RegistroParticipantesService participantes;
 public SesionesServidor(SesionUseCase sesiones,RegistroParticipantesService participantes){this.sesiones=sesiones;this.participantes=participantes;}
 public void obtenerSesion(Identificador r,StreamObserver<SesionVista> o){RespuestaGrpc.enviar(o,()->{
  RespuestaGrpc.validar(r.getId());
  com.examenLive.sesionesservice.sesiones.dominio.Sesion s;
  try{s=sesiones.consultar(r.getId());}catch(IllegalArgumentException e){throw new java.util.NoSuchElementException(e.getMessage());}
  return SesionVista.newBuilder().setId(s.getId()).setExamenId(s.getExamenId()).setInicio(s.getVentanaTiempo().inicio().toString()).setFin(s.getVentanaTiempo().fin().toString()).build();
 });}
 public void agregarEstudiante(Participante r,StreamObserver<Vacio> o){RespuestaGrpc.enviar(o,()->{
  RespuestaGrpc.validar(r.getSesionId());RespuestaGrpc.validar(r.getEstudianteId());
  participantes.agregar(r.getSesionId(),r.getEstudianteId());return Vacio.getDefaultInstance();
 });}
}
