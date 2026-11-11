package com.examenLive.examenesservice.comunicacion.grpc;
import io.grpc.*;
import io.github.resilience4j.circuitbreaker.*;
import java.time.Duration;
import java.util.Set;
import java.util.function.Supplier;
import com.examenLive.examenesservice.compartido.ServicioNoDisponibleException;
final class LlamadaRemota {
 private final CircuitBreaker circuito;private final String nombre;
 LlamadaRemota(String nombre){this.nombre=nombre;circuito=CircuitBreaker.of(nombre,CircuitBreakerConfig.custom()
  .slidingWindowSize(10).minimumNumberOfCalls(5).failureRateThreshold(50).waitDurationInOpenState(Duration.ofSeconds(10)).permittedNumberOfCallsInHalfOpenState(2)
  .ignoreException(e->e instanceof StatusRuntimeException s && Set.of(Status.Code.NOT_FOUND,Status.Code.INVALID_ARGUMENT,Status.Code.FAILED_PRECONDITION).contains(s.getStatus().getCode())).build());
  circuito.getEventPublisher().onStateTransition(e->org.slf4j.LoggerFactory.getLogger(getClass()).warn("Circuito {}: {}",nombre,e.getStateTransition()));
 }
 <T> T ejecutar(Supplier<T> accion){try{return circuito.executeSupplier(accion);}
  catch(CallNotPermittedException e){throw new ServicioNoDisponibleException(nombre+": circuito abierto.",e);}
  catch(StatusRuntimeException e){switch(e.getStatus().getCode()){
   case NOT_FOUND -> throw new java.util.NoSuchElementException("Recurso remoto no encontrado.");
   case INVALID_ARGUMENT -> throw new IllegalArgumentException(e.getStatus().getDescription());
   case FAILED_PRECONDITION -> throw new IllegalStateException(e.getStatus().getDescription());
   default -> throw new ServicioNoDisponibleException(nombre+" no disponible: "+e.getStatus().getCode(),e);
  }}
 }
}
