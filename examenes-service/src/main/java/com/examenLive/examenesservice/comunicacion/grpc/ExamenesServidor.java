package com.examenLive.examenesservice.comunicacion.grpc;
import com.examenLive.contratos.contextos.v1.*;
import com.examenLive.examenesservice.examenes.dominio.puertos.salida.ExamenRepository;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
@Service
public class ExamenesServidor extends ExamenesGrpcGrpc.ExamenesGrpcImplBase {
 private final ExamenRepository examenes;
 public ExamenesServidor(ExamenRepository examenes){this.examenes=examenes;}
 @Transactional(readOnly=true)
 public void existeExamen(Identificador r,StreamObserver<Existencia> o){RespuestaGrpc.enviar(o,()->{
  RespuestaGrpc.validar(r.getId());return Existencia.newBuilder().setExiste(examenes.buscarPorId(r.getId()).isPresent()).build();
 });}
}
