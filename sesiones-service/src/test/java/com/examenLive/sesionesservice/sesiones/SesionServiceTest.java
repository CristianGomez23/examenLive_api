package com.examenLive.sesionesservice.sesiones;
import com.examenLive.sesionesservice.sesiones.aplicacion.servicios.SesionService;
import com.examenLive.sesionesservice.sesiones.dominio.puertos.salida.*;
import com.examenLive.sesionesservice.compartido.ServicioNoDisponibleException;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class SesionServiceTest {
    SesionRepository repositorio=mock(SesionRepository.class);
    VerificadorExamen remoto=mock(VerificadorExamen.class);
    SesionService servicio=new SesionService(repositorio,remoto,()->10L);
    LocalDateTime inicio=LocalDateTime.of(2026,11,5,10,0);
    @Test void noProgramaSiExamenNoExiste(){when(remoto.existe(1L)).thenReturn(false);assertThrows(IllegalArgumentException.class,()->servicio.programar(1L,inicio,inicio.plusHours(1)));verifyNoInteractions(repositorio);}
    @Test void noFabricaValidacionSiProveedorCae(){when(remoto.existe(1L)).thenThrow(new ServicioNoDisponibleException("caído",null));assertThrows(ServicioNoDisponibleException.class,()->servicio.programar(1L,inicio,inicio.plusHours(1)));verifyNoInteractions(repositorio);}
    @Test void programaSiExamenExiste(){when(remoto.existe(1L)).thenReturn(true);var s=servicio.programar(1L,inicio,inicio.plusHours(1));assertEquals(1L,s.getExamenId());verify(repositorio).guardar(s);}
}
