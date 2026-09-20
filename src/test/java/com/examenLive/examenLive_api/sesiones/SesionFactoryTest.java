package com.examenLive.examenLive_api.sesiones;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SesionFactoryTest {

    private final SesionFactory factory = new SesionFactory();

    @Test
    void debeCrearUnaSesionValida() {
        VentanaTiempo ventana = new VentanaTiempo(
                LocalDateTime.of(2026, 9, 19, 14, 0),
                LocalDateTime.of(2026, 9, 19, 16, 0)
        );

        Sesion sesion = factory.crear(1L, 25L, ventana);

        assertEquals(1L, sesion.getId());
        assertEquals(25L, sesion.getExamenId());
        assertEquals(ventana, sesion.getVentanaTiempo());
    }

    @Test
    void debeRechazarSesionSinId() {
        VentanaTiempo ventana = new VentanaTiempo(
                LocalDateTime.of(2026, 9, 19, 14, 0),
                LocalDateTime.of(2026, 9, 19, 16, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> factory.crear(null, 25L, ventana)
        );
    }

    @Test
    void debeRechazarSesionSinExamen() {
        VentanaTiempo ventana = new VentanaTiempo(
                LocalDateTime.of(2026, 9, 19, 14, 0),
                LocalDateTime.of(2026, 9, 19, 16, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> factory.crear(1L, null, ventana)
        );
    }

    @Test
    void debeRechazarSesionSinVentana() {
        assertThrows(
                IllegalArgumentException.class,
                () -> factory.crear(1L, 25L, null)
        );
    }
}