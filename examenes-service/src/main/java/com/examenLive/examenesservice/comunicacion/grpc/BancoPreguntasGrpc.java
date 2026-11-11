package com.examenLive.examenesservice.comunicacion.grpc;
import com.examenLive.contratos.contextos.v1.*;
import com.examenLive.examenesservice.examenes.dominio.PreguntaExamen;
import com.examenLive.examenesservice.examenes.dominio.puertos.salida.BancoPreguntas;
import java.util.*;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;
@Component
public class BancoPreguntasGrpc implements BancoPreguntas {
 private final PreguntasGrpcGrpc.PreguntasGrpcBlockingStub stub;
 private final LlamadaRemota llamada=new LlamadaRemota("preguntas-service");
 public BancoPreguntasGrpc(PreguntasGrpcGrpc.PreguntasGrpcBlockingStub stub){this.stub=stub;}
 public Optional<PreguntaExamen> buscarPorId(Long id){
  if(id==null)throw new IllegalArgumentException("El identificador de la pregunta es obligatorio.");
  try {var p=llamada.ejecutar(()->stub.withDeadlineAfter(5,TimeUnit.SECONDS).obtenerPregunta(Identificador.newBuilder().setId(id).build()));
   return Optional.of(new PreguntaExamen(p.getId(),p.getEnunciado(),p.getOpcionesList().stream().map(x->new PreguntaExamen.Opcion(x.getTexto(),x.getCorrecta())).toList(),new BigDecimal(p.getPuntaje()),p.getActiva()));
  }catch(NoSuchElementException e){return Optional.empty();}
 }
 public void registrarUso(Long id){llamada.ejecutar(()->stub.withDeadlineAfter(5,TimeUnit.SECONDS).registrarUso(Identificador.newBuilder().setId(id).build()));}
}
