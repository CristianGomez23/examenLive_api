package com.examenLive.examenLive_api.sesiones;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class VentanaTiempoTest {

    @Test
    void debeCrearUnaVentanaValida() {
        LocalDateTime inicio = LocalDateTime.of(2026, 9, 19, 14, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 9, 19, 16, 0);

        VentanaTiempo ventana = new VentanaTiempo(inicio, fin);

        assertEquals(inicio, ventana.inicio());
        assertEquals(fin, ventana.fin());
    }

    @Test
    void debeRechazarInicioPosteriorAlFin() {
        LocalDateTime inicio = LocalDateTime.of(2026, 9, 19, 16, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 9, 19, 14, 0);

        assertThrows(
                IllegalArgumentException.class,
                () -> new VentanaTiempo(inicio, fin)
        );
    }

    @Test
    void debeRechazarInicioIgualAlFin() {
        LocalDateTime momento = LocalDateTime.of(2026, 9, 19, 14, 0);

        assertThrows(
                IllegalArgumentException.class,
                () -> new VentanaTiempo(momento, momento)
        );
    }

    @Test
    void debeIndicarSiUnMomentoEstaDentroDeLaVentana() {
        LocalDateTime inicio = LocalDateTime.of(2026, 9, 19, 14, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 9, 19, 16, 0);

        VentanaTiempo ventana = new VentanaTiempo(inicio, fin);

        assertTrue(
                ventana.estaActiva(
                        LocalDateTime.of(2026, 9, 19, 15, 0)
                )
        );

        assertFalse(
                ventana.estaActiva(
                        LocalDateTime.of(2026, 9, 19, 16, 0)
                )
        );
    }
}