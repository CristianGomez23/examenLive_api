package com.examenLive.resultadosservice.comunicacion;


import com.examenLive.resultadosservice.comunicacion.aplicacion.*;
import com.examenLive.resultadosservice.comunicacion.contratos.*;
import com.examenLive.resultadosservice.resultados.aplicacion.servicios.*;
import com.examenLive.resultadosservice.resultados.aplicacion.ResultadoFactory;
import com.examenLive.resultadosservice.resultados.aplicacion.RepositorioResultadosFake;
import com.examenLive.resultadosservice.resultados.dominio.puertos.salida.VinculosIntentos;
import com.examenLive.resultadosservice.resultados.dominio.PreguntaSinPuntajeException;
import org.junit.jupiter.api.*;
import java.util.*;
import java.math.BigDecimal;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class ProcesadorIntentoTest {
    VinculosIntentos vinculos=mock(VinculosIntentos.class);
    PublicadorEventos outbox=mock(PublicadorEventos.class);
    RegistroMensajes inbox=mock(RegistroMensajes.class);
    ResultadoService resultados=new ResultadoService(new RepositorioResultadosFake(),new ResultadoFactory(()->100L),new CalificacionAutomaticaService());
    ProcesadorIntentoFinalizado procesador=new ProcesadorIntentoFinalizado(resultados,vinculos,outbox,inbox);
    DatosIntento datos=new DatosIntento(1L,10L,700L,List.of(new DatosIntento.Respuesta(101L,Set.of("A"))),
        List.of(new DatosIntento.Pregunta(101L,Set.of("A"),new BigDecimal("2.5"))));
    @BeforeEach void preparar() {when(vinculos.buscarResultado(anyLong())).thenReturn(Optional.empty());when(inbox.registrarSiNuevo(anyString(),any())).thenReturn(true);}
    @Test void calificaYEncolaConfirmacion() {
        var r=procesador.procesar(datos);
        assertTrue(r.creado());
        assertEquals(new BigDecimal("2.5"),r.resultado().calificacion().orElseThrow().puntajeObtenido());
        verify(outbox).guardar(any(),eq("resultados.generados.v1"),eq("1"),isA(ResultadoGeneradoEvento.class));
    }
    @Test void duplicadoDeMensajeNoProduceEfectos() {
        when(inbox.registrarSiNuevo(anyString(),any())).thenReturn(false);
        procesador.consumir(new IntentoFinalizadoEvento(UUID.randomUUID(),1,Instant.now(),datos));
        verifyNoInteractions(outbox);verify(vinculos,never()).guardar(any(),any(),any(),any());
    }
    @Test void nuevoEventIdMismoIntentoTampocoDuplica() {
        procesador.procesar(datos);
        when(vinculos.buscarResultado(1L)).thenReturn(Optional.of(100L));
        assertFalse(procesador.procesar(datos).creado());
        verify(outbox,times(1)).guardar(any(),anyString(),anyString(),any());
    }
    @Test void sinPuntajeNoPublicaConfirmacion() {
        var incompleto=new DatosIntento(2L,10L,700L,datos.respuestas(),List.of(new DatosIntento.Pregunta(101L,Set.of("A"),null)));
        assertThrows(PreguntaSinPuntajeException.class,()->procesador.procesar(incompleto));
        verifyNoInteractions(outbox);
    }
    @Test void versionDesconocidaNoSeMarcaProcesada() {
        assertThrows(IllegalArgumentException.class,()->procesador.consumir(new IntentoFinalizadoEvento(UUID.randomUUID(),2,Instant.now(),datos)));
        verifyNoInteractions(inbox,outbox);
    }
    @Test void respuestasDuplicadasSeRechazan() {
        var invalido=new DatosIntento(1L,10L,700L,List.of(datos.respuestas().getFirst(),datos.respuestas().getFirst()),datos.preguntas());
        assertThrows(IllegalArgumentException.class,()->procesador.procesar(invalido));verifyNoInteractions(outbox);
    }
}
