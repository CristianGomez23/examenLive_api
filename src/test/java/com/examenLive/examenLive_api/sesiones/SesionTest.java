package com.examenLive.examenLive_api.sesiones;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SesionTest {

    @Test
    void debeCrearUnaSesionConReferenciaAlExamenPorId() {
        VentanaTiempo ventana = new VentanaTiempo(
                LocalDateTime.of(2026, 9, 19, 14, 0),
                LocalDateTime.of(2026, 9, 19, 16, 0)
        );

        Sesion sesion = new Sesion(1L, 25L, ventana);

        assertEquals(1L, sesion.getId());
        assertEquals(25L, sesion.getExamenId());
        assertEquals(ventana, sesion.getVentanaTiempo());
    }

    @Test
    void debePermitirAgregarEstudiantePorId() {
        VentanaTiempo ventana = new VentanaTiempo(
                LocalDateTime.of(2026, 9, 19, 14, 0),
                LocalDateTime.of(2026, 9, 19, 16, 0)
        );

        Sesion sesion = new Sesion(1L, 25L, ventana);

        sesion.agregarEstudiante(100L);
        sesion.agregarEstudiante(200L);

        assertEquals(2, sesion.getEstudiantesIds().size());
        assertTrue(sesion.getEstudiantesIds().contains(100L));
        assertTrue(sesion.getEstudiantesIds().contains(200L));
    }

    @Test
    void noDebePermitirSesionSinExamen() {
        VentanaTiempo ventana = new VentanaTiempo(
                LocalDateTime.of(2026, 9, 19, 14, 0),
                LocalDateTime.of(2026, 9, 19, 16, 0)
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> new Sesion(1L, null, ventana)
        );
    }
}