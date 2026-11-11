package com.examenLive.sesionesservice.sesiones;

import com.examenLive.sesionesservice.identidad.GeneradorIdNumericoFake;
import com.examenLive.sesionesservice.sesiones.dominio.Sesion;
import com.examenLive.sesionesservice.sesiones.dominio.SesionFactory;
import com.examenLive.sesionesservice.sesiones.dominio.VentanaTiempo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SesionFactoryTest {

        private final SesionFactory factory = new SesionFactory(new GeneradorIdNumericoFake(1));

    @Test
    void debeCrearUnaSesionValida() {
        VentanaTiempo ventana = new VentanaTiempo(
                LocalDateTime.of(2026, 9, 19, 14, 0),
                LocalDateTime.of(2026, 9, 19, 16, 0)
        );

        Sesion sesion = factory.crear(25L, ventana);

        assertEquals(1L, sesion.getId());
        assertEquals(25L, sesion.getExamenId());
        assertEquals(ventana, sesion.getVentanaTiempo());
    }

    @Test
        void debeGenerarIdsNumericosPositivos() {
        VentanaTiempo ventana = new VentanaTiempo(
                LocalDateTime.of(2026, 9, 19, 14, 0),
                LocalDateTime.of(2026, 9, 19, 16, 0)
        );

        assertTrue(factory.crear(25L, ventana).getId() > 0);
    }

    @Test
    void debeRechazarSesionSinExamen() {
        VentanaTiempo ventana = new VentanaTiempo(
                LocalDateTime.of(2026, 9, 19, 14, 0),
                LocalDateTime.of(2026, 9, 19, 16, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> factory.crear(null, ventana)
        );
    }

    @Test
    void debeRechazarSesionSinVentana() {
        assertThrows(
                IllegalArgumentException.class,
                () -> factory.crear(25L, null)
        );
    }
}
