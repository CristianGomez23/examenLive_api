package com.examenLive.preguntasservice.comunicacion.grpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import java.util.function.Supplier;
final class RespuestaGrpc {
 private RespuestaGrpc(){}
 static <T> void enviar(StreamObserver<T> salida,Supplier<T> operacion) {
  try {salida.onNext(operacion.get());salida.onCompleted();}
  catch(java.util.NoSuchElementException e){salida.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());}
  catch(IllegalArgumentException e){salida.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());}
  catch(IllegalStateException e){salida.onError(Status.FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException());}
  catch(Exception e){org.slf4j.LoggerFactory.getLogger(RespuestaGrpc.class).error("Error gRPC",e);salida.onError(Status.INTERNAL.withDescription("Error interno del proveedor").asRuntimeException());}
 }
 static void validar(long id){if(id<=0)throw new IllegalArgumentException("El identificador debe ser positivo.");}
}
