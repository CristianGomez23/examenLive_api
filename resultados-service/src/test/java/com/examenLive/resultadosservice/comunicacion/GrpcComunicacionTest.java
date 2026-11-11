package com.examenLive.resultadosservice.comunicacion;


import com.examenLive.resultadosservice.resultados.infraestructura.salida.grpc.ConsultaIntentosGrpc;
import com.examenLive.resultadosservice.compartido.*;
import com.examenLive.contratos.intentos.v1.*;
import io.grpc.*;
import io.grpc.inprocess.*;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.*;
import java.util.concurrent.atomic.AtomicInteger;
import static org.junit.jupiter.api.Assertions.*;
class GrpcComunicacionTest {
    Server server; ManagedChannel channel; ConsultaIntentosGrpc cliente;
    AtomicInteger llamadas=new AtomicInteger();
    Status estado; boolean lento;
    @BeforeEach void iniciar() throws Exception {
        String nombre=InProcessServerBuilder.generateName();
        server=InProcessServerBuilder.forName(nombre).directExecutor().addService(new IntentosGrpcGrpc.IntentosGrpcImplBase() {
            public void obtenerIntentoFinalizado(SolicitudIntento req,StreamObserver<IntentoFinalizado> o) {
                llamadas.incrementAndGet();
                if (lento) return;
                if (estado!=null) {o.onError(estado.asRuntimeException());return;}
                o.onNext(IntentoFinalizado.newBuilder().setIntentoId(req.getIntentoId()).setSesionId(10).setEstudianteId(700)
                    .addPreguntas(PreguntaIntento.newBuilder().setPreguntaId(101).addRespuestasCorrectas("A").setPuntaje("2.50"))
                    .addRespuestas(RespuestaIntento.newBuilder().setPreguntaId(101).addOpcionesSeleccionadas("A")).build());o.onCompleted();
            }
        }).build().start();
        channel=InProcessChannelBuilder.forName(nombre).directExecutor().build();
        cliente=new ConsultaIntentosGrpc(IntentosGrpcGrpc.newBlockingStub(channel),5000);
    }
    @AfterEach void cerrar() {channel.shutdownNow();server.shutdownNow();}
    @Test void contratoRealConservaDecimalYRespuestas() {
        var r=cliente.obtenerFinalizado(1L);
        assertEquals("2.50",r.preguntas().getFirst().puntaje().toPlainString());
        assertTrue(r.respuestas().getFirst().opcionesSeleccionadas().contains("A"));
    }
    @Test void cincoFallosAbrenCircuitoYSextaLlamadaNoTocaRed() {
        estado=Status.UNAVAILABLE;
        for(int i=0;i<6;i++) assertThrows(ServicioNoDisponibleException.class,()->cliente.obtenerFinalizado(1L));
        assertEquals(5,llamadas.get());
    }
    @Test void noEncontradoNoAbreCircuito() {
        estado=Status.NOT_FOUND;
        for(int i=0;i<7;i++) assertThrows(RecursoNoEncontradoException.class,()->cliente.obtenerFinalizado(99L));
        assertEquals(7,llamadas.get());
        estado=null;assertEquals(1L,cliente.obtenerFinalizado(1L).intentoId());
    }
    @Test void intentoAbiertoEsConflicto() {
        estado=Status.FAILED_PRECONDITION;
        assertThrows(IllegalStateException.class,()->cliente.obtenerFinalizado(1L));
    }
    @Test void deadlineImpideEsperaIndefinida() {
        lento=true;
        cliente=new ConsultaIntentosGrpc(IntentosGrpcGrpc.newBlockingStub(channel),100);
        var e=assertThrows(ServicioNoDisponibleException.class,()->cliente.obtenerFinalizado(1L));
        assertTrue(e.getMessage().contains("DEADLINE_EXCEEDED"));
    }
}
