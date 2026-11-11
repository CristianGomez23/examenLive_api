package com.examenLive.resultadosservice.resultados.infraestructura.salida.grpc;


import com.examenLive.resultadosservice.comunicacion.contratos.DatosIntento;
import com.examenLive.resultadosservice.compartido.*;
import com.examenLive.resultadosservice.resultados.dominio.puertos.salida.ConsultaIntentos;
import com.examenLive.contratos.intentos.v1.*;
import io.grpc.*;
import io.github.resilience4j.circuitbreaker.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.Set;
import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;
@Component
public class ConsultaIntentosGrpc implements ConsultaIntentos {
    private final IntentosGrpcGrpc.IntentosGrpcBlockingStub stub;
    private final CircuitBreaker circuito;
    private final long deadlineMs;
    public ConsultaIntentosGrpc(IntentosGrpcGrpc.IntentosGrpcBlockingStub stub,
            @Value("${comunicacion.grpc.deadline-ms:5000}") long deadlineMs) {
        this.stub=stub; this.deadlineMs=deadlineMs;
        circuito=CircuitBreaker.of("examenes-service",CircuitBreakerConfig.custom()
            .slidingWindowSize(10).minimumNumberOfCalls(5).failureRateThreshold(50)
            .waitDurationInOpenState(Duration.ofSeconds(10)).permittedNumberOfCallsInHalfOpenState(2)
            .ignoreException(e -> e instanceof StatusRuntimeException s &&
                Set.of(Status.Code.NOT_FOUND,Status.Code.FAILED_PRECONDITION,Status.Code.INVALID_ARGUMENT).contains(s.getStatus().getCode()))
            .build());
        circuito.getEventPublisher().onStateTransition(e -> org.slf4j.LoggerFactory.getLogger(getClass())
            .warn("Circuit Breaker examenes: {}",e.getStateTransition()));
    }
    public DatosIntento obtenerFinalizado(Long id) {
        try {
            var r=circuito.executeSupplier(() -> stub.withDeadlineAfter(deadlineMs,TimeUnit.MILLISECONDS)
                .obtenerIntentoFinalizado(SolicitudIntento.newBuilder().setIntentoId(id).build()));
            return new DatosIntento(r.getIntentoId(),r.getSesionId(),r.getEstudianteId(),
                r.getRespuestasList().stream().map(x -> new DatosIntento.Respuesta(x.getPreguntaId(),Set.copyOf(x.getOpcionesSeleccionadasList()))).toList(),
                r.getPreguntasList().stream().map(x -> new DatosIntento.Pregunta(x.getPreguntaId(),Set.copyOf(x.getRespuestasCorrectasList()),
                    x.hasPuntaje()?new BigDecimal(x.getPuntaje()):null)).toList());
        } catch (CallNotPermittedException e) {
            throw new ServicioNoDisponibleException("Exámenes no disponible: circuito abierto.",e);
        } catch (StatusRuntimeException e) {
            switch(e.getStatus().getCode()) {
                case NOT_FOUND -> throw new RecursoNoEncontradoException("Intento no encontrado.");
                case FAILED_PRECONDITION -> throw new IllegalStateException("El intento aún no está finalizado o no tiene snapshot de integración.");
                case INVALID_ARGUMENT -> throw new IllegalArgumentException("Identificador de intento inválido.");
                default -> throw new ServicioNoDisponibleException("No se pudo consultar Exámenes: "+e.getStatus().getCode(),e);
            }
        }
    }
}
