package com.examenLive.sesionesservice.comunicacion.grpc;
import com.examenLive.contratos.contextos.v1.*;
import com.examenLive.sesionesservice.sesiones.dominio.puertos.salida.VerificadorExamen;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;
@Component
public class VerificadorExamenGrpc implements VerificadorExamen {
 private final ExamenesGrpcGrpc.ExamenesGrpcBlockingStub stub;
 private final LlamadaRemota llamada=new LlamadaRemota("examenes-service");
 public VerificadorExamenGrpc(ExamenesGrpcGrpc.ExamenesGrpcBlockingStub stub){this.stub=stub;}
 public boolean existe(Long id){
  if(id==null)throw new IllegalArgumentException("El identificador del examen es obligatorio.");
  return llamada.ejecutar(()->stub.withDeadlineAfter(5,TimeUnit.SECONDS).existeExamen(Identificador.newBuilder().setId(id).build())).getExiste();
 }
}
