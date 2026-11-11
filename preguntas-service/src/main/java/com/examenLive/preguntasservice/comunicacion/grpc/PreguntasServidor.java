package com.examenLive.preguntasservice.comunicacion.grpc;
import com.examenLive.contratos.contextos.v1.*;
import com.examenLive.preguntasservice.preguntas.dominio.puertos.entrada.PreguntaUseCase;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;
@Service
public class PreguntasServidor extends PreguntasGrpcGrpc.PreguntasGrpcImplBase {
 private final PreguntaUseCase servicio;
 public PreguntasServidor(PreguntaUseCase servicio){this.servicio=servicio;}
 public void obtenerPregunta(Identificador r,StreamObserver<PreguntaVista> o){RespuestaGrpc.enviar(o,()->{
  RespuestaGrpc.validar(r.getId());
  com.examenLive.preguntasservice.preguntas.dominio.Pregunta p;
  try {p=servicio.consultar(r.getId());}catch(IllegalArgumentException e){throw new java.util.NoSuchElementException(e.getMessage());}
  return PreguntaVista.newBuilder().setId(p.preguntaId()).setEnunciado(p.enunciado()).setPuntaje(p.puntaje().toPlainString()).setActiva(p.estaActiva())
   .addAllOpciones(p.opciones().stream().map(x->OpcionVista.newBuilder().setTexto(x.texto()).setCorrecta(x.correcta()).build()).toList()).build();
 });}
 public void registrarUso(Identificador r,StreamObserver<Vacio> o){RespuestaGrpc.enviar(o,()->{
  RespuestaGrpc.validar(r.getId());servicio.registrarUsoEnSesionAplicada(r.getId());return Vacio.getDefaultInstance();
 });}
}
