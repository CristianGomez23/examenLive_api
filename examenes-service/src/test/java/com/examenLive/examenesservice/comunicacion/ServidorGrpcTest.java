package com.examenLive.examenesservice.comunicacion;


import com.examenLive.examenesservice.comunicacion.aplicacion.ConsultaSnapshotUseCase;
import com.examenLive.examenesservice.comunicacion.contratos.DatosIntento;
import com.examenLive.examenesservice.comunicacion.infraestructura.IntentosGrpcService;
import com.examenLive.contratos.intentos.v1.*;
import io.grpc.*;
import io.grpc.inprocess.*;
import org.junit.jupiter.api.*;
import java.util.*;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class ServidorGrpcTest {
    ConsultaSnapshotUseCase consulta=mock(ConsultaSnapshotUseCase.class);
    Server server;ManagedChannel channel;IntentosGrpcGrpc.IntentosGrpcBlockingStub cliente;
    @BeforeEach void iniciar() throws Exception {
        String nombre=InProcessServerBuilder.generateName();
        server=InProcessServerBuilder.forName(nombre).directExecutor().addService(new IntentosGrpcService(consulta)).build().start();
        channel=InProcessChannelBuilder.forName(nombre).directExecutor().build();cliente=IntentosGrpcGrpc.newBlockingStub(channel);
    }
    @AfterEach void cerrar() {channel.shutdownNow();server.shutdownNow();}
    @Test void entregaSnapshotConDecimal() {
        when(consulta.consultar(1L)).thenReturn(new DatosIntento(1L,10L,700L,List.of(),List.of(new DatosIntento.Pregunta(101L,Set.of("A"),new BigDecimal("2.50")))));
        var r=cliente.obtenerIntentoFinalizado(SolicitudIntento.newBuilder().setIntentoId(1).build());
        assertEquals("2.50",r.getPreguntas(0).getPuntaje());assertEquals(10,r.getSesionId());
    }
    @Test void noExisteDevuelveNotFound() {
        when(consulta.consultar(99L)).thenThrow(new NoSuchElementException());
        var e=assertThrows(StatusRuntimeException.class,()->cliente.obtenerIntentoFinalizado(SolicitudIntento.newBuilder().setIntentoId(99).build()));
        assertEquals(Status.Code.NOT_FOUND,e.getStatus().getCode());
    }
    @Test void abiertoDevuelveFailedPrecondition() {
        when(consulta.consultar(1L)).thenThrow(new IllegalStateException());
        var e=assertThrows(StatusRuntimeException.class,()->cliente.obtenerIntentoFinalizado(SolicitudIntento.newBuilder().setIntentoId(1).build()));
        assertEquals(Status.Code.FAILED_PRECONDITION,e.getStatus().getCode());
    }
}
