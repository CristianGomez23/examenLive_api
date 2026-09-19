package com.examenLive.examenLive_api.sesiones;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ControlIngresoSesionServiceTest {

    @Test
    void debePermitirIngresarDentroDeLaVentana() {
        LocalDateTime inicio = LocalDateTime.of(2026, 9, 19, 14, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 9, 19, 16, 0);

        VentanaTiempo ventana = new VentanaTiempo(inicio, fin);
        ControlIngresoSesionService servicio = new ControlIngresoSesionService();

        assertTrue(
                servicio.puedeIngresar(
                        ventana,
                        LocalDateTime.of(2026, 9, 19, 15, 0)
                )
        );
    }

    @Test
    void debeRechazarIngresoFueraDeLaVentana() {
        LocalDateTime inicio = LocalDateTime.of(2026, 9, 19, 14, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 9, 19, 16, 0);

        VentanaTiempo ventana = new VentanaTiempo(inicio, fin);
        ControlIngresoSesionService servicio = new ControlIngresoSesionService();

        assertFalse(
                servicio.puedeIngresar(
                        ventana,
                        LocalDateTime.of(2026, 9, 19, 16, 0)
                )
        );
    }

    @Test
    void debePermitirReingresarSiLaSesionSigueActiva() {
        LocalDateTime inicio = LocalDateTime.of(2026, 9, 19, 14, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 9, 19, 16, 0);

        VentanaTiempo ventana = new VentanaTiempo(inicio, fin);
        ControlIngresoSesionService servicio = new ControlIngresoSesionService();

        assertTrue(
                servicio.puedeReingresar(
                        ventana,
                        LocalDateTime.of(2026, 9, 19, 15, 30)
                )
        );
    }

    @Test
    void debeRechazarReingresoDespuesDelCierre() {
        LocalDateTime inicio = LocalDateTime.of(2026, 9, 19, 14, 0);
        LocalDateTime fin = LocalDateTime.of(2026, 9, 19, 16, 0);

        VentanaTiempo ventana = new VentanaTiempo(inicio, fin);
        ControlIngresoSesionService servicio = new ControlIngresoSesionService();

        assertFalse(
                servicio.puedeReingresar(
                        ventana,
                        LocalDateTime.of(2026, 9, 19, 16, 1)
                )
        );
    }
}