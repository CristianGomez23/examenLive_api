package com.examenLive.examenesservice.comunicacion.infraestructura;


import com.examenLive.examenesservice.comunicacion.aplicacion.ConsultaSnapshotUseCase;
import com.examenLive.contratos.intentos.v1.*;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.springframework.stereotype.Service;
import java.util.NoSuchElementException;
@Service
public class IntentosGrpcService extends IntentosGrpcGrpc.IntentosGrpcImplBase {
    private final ConsultaSnapshotUseCase consulta;
    public IntentosGrpcService(ConsultaSnapshotUseCase consulta) {this.consulta=consulta;}
    @Override
    public void obtenerIntentoFinalizado(SolicitudIntento request,StreamObserver<IntentoFinalizado> observer) {
        try {
            var datos=consulta.consultar(request.getIntentoId());
            var b=IntentoFinalizado.newBuilder().setIntentoId(datos.intentoId()).setSesionId(datos.sesionId()).setEstudianteId(datos.estudianteId());
            datos.respuestas().forEach(r -> b.addRespuestas(RespuestaIntento.newBuilder().setPreguntaId(r.preguntaId()).addAllOpcionesSeleccionadas(r.opcionesSeleccionadas())));
            datos.preguntas().forEach(p -> {
                var pregunta=PreguntaIntento.newBuilder().setPreguntaId(p.preguntaId()).addAllRespuestasCorrectas(p.respuestasCorrectas());
                if (p.puntaje()!=null) pregunta.setPuntaje(p.puntaje().toPlainString());
                b.addPreguntas(pregunta);
            });
            observer.onNext(b.build()); observer.onCompleted();
        } catch (NoSuchElementException e) {observer.onError(Status.NOT_FOUND.withDescription(e.getMessage()).asRuntimeException());}
          catch (IllegalArgumentException e) {observer.onError(Status.INVALID_ARGUMENT.withDescription(e.getMessage()).asRuntimeException());}
          catch (IllegalStateException e) {observer.onError(Status.FAILED_PRECONDITION.withDescription(e.getMessage()).asRuntimeException());}
          catch (Exception e) {observer.onError(Status.INTERNAL.withDescription("No se pudo consultar el intento.").asRuntimeException());}
    }
}
