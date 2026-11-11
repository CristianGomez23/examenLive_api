package com.examenLive.examenesservice.comunicacion;

import com.examenLive.contratos.contextos.v1.*;
import com.examenLive.examenesservice.comunicacion.grpc.*;
import com.examenLive.examenesservice.compartido.ServicioNoDisponibleException;
import io.grpc.*;
import io.grpc.inprocess.*;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDateTime;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

class ContextosGrpcTest {
    Server servidor; ManagedChannel canal;
    BancoPreguntasGrpc preguntas; ConsultaSesionesGrpc sesiones;
    Status error;
    AtomicInteger consultas=new AtomicInteger(), usos=new AtomicInteger();
    long sesionRegistrada, estudianteRegistrado;
    @BeforeEach void iniciar() throws Exception {
        String nombre=InProcessServerBuilder.generateName();
        servidor=InProcessServerBuilder.forName(nombre).directExecutor()
            .addService(new PreguntasGrpcGrpc.PreguntasGrpcImplBase(){
                public void obtenerPregunta(Identificador r,StreamObserver<PreguntaVista> o){
                    consultas.incrementAndGet();
                    if(error!=null){o.onError(error.asRuntimeException());return;}
                    o.onNext(PreguntaVista.newBuilder().setId(r.getId()).setEnunciado("A y B").setPuntaje("2.50").setActiva(true)
                        .addOpciones(OpcionVista.newBuilder().setTexto("A").setCorrecta(true))
                        .addOpciones(OpcionVista.newBuilder().setTexto("B").setCorrecta(true))
                        .addOpciones(OpcionVista.newBuilder().setTexto("C"))
                        .addOpciones(OpcionVista.newBuilder().setTexto("D")).build());o.onCompleted();
                }
                public void registrarUso(Identificador r,StreamObserver<Vacio> o){usos.incrementAndGet();o.onNext(Vacio.getDefaultInstance());o.onCompleted();}
            })
            .addService(new SesionesGrpcGrpc.SesionesGrpcImplBase(){
                public void obtenerSesion(Identificador r,StreamObserver<SesionVista> o){
                    if(error!=null){o.onError(error.asRuntimeException());return;}
                    o.onNext(SesionVista.newBuilder().setId(r.getId()).setExamenId(8).setInicio("2026-11-05T10:00:00").setFin("2026-11-05T11:00:00").build());o.onCompleted();
                }
                public void agregarEstudiante(Participante r,StreamObserver<Vacio> o){sesionRegistrada=r.getSesionId();estudianteRegistrado=r.getEstudianteId();o.onNext(Vacio.getDefaultInstance());o.onCompleted();}
            }).build().start();
        canal=InProcessChannelBuilder.forName(nombre).directExecutor().build();
        preguntas=new BancoPreguntasGrpc(PreguntasGrpcGrpc.newBlockingStub(canal));
        sesiones=new ConsultaSesionesGrpc(SesionesGrpcGrpc.newBlockingStub(canal));
    }
    @AfterEach void cerrar(){canal.shutdownNow();servidor.shutdownNow();}
    @Test void conservaPuntajeOpcionesYEstado(){
        var p=preguntas.buscarPorId(1L).orElseThrow();
        assertEquals("2.50",p.puntaje().toPlainString());assertEquals(Set.of("A","B"),p.respuestasCorrectas());
        assertTrue(p.estaActiva());assertTrue(p.tieneOpcionesIncorrectasSuficientes());
    }
    @Test void noEncontradoEsOptionalVacioYNoAbreCircuito(){
        error=Status.NOT_FOUND;for(int i=0;i<7;i++)assertTrue(preguntas.buscarPorId(9L).isEmpty());
        assertEquals(7,consultas.get());error=null;assertTrue(preguntas.buscarPorId(1L).isPresent());
    }
    @Test void falloDeRedAbreCircuito(){
        error=Status.UNAVAILABLE;for(int i=0;i<6;i++)assertThrows(ServicioNoDisponibleException.class,()->preguntas.buscarPorId(1L));
        assertEquals(5,consultas.get());
    }
    @Test void errorDeNegocioNoSeConvierteEnDisponibilidad(){
        error=Status.INVALID_ARGUMENT;assertThrows(IllegalArgumentException.class,()->preguntas.buscarPorId(0L));
        error=Status.FAILED_PRECONDITION;assertThrows(IllegalStateException.class,()->preguntas.buscarPorId(1L));
    }
    @Test void registrarUsoLlamaAlProveedor(){preguntas.registrarUso(1L);assertEquals(1,usos.get());}
    @Test void ventanaConservaLimitesOriginales(){
        var s=sesiones.buscarPorId(2L).orElseThrow();assertEquals(8L,s.getExamenId());
        assertTrue(s.getVentanaTiempo().estaActiva(LocalDateTime.parse("2026-11-05T10:00:00")));
        assertFalse(s.getVentanaTiempo().estaActiva(LocalDateTime.parse("2026-11-05T11:00:00")));
    }
    @Test void participanteConservaIdentificadores(){sesiones.agregarEstudiante(2L,700L);assertEquals(2,sesionRegistrada);assertEquals(700,estudianteRegistrado);}
    @Test void sesionInexistenteYCaidaSeDistinguen(){
        error=Status.NOT_FOUND;assertTrue(sesiones.buscarPorId(2L).isEmpty());
        error=Status.UNAVAILABLE;assertThrows(ServicioNoDisponibleException.class,()->sesiones.buscarPorId(2L));
    }
}
