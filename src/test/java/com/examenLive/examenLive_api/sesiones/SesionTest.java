package com.examenLive.examenLive_api.sesiones;

import com.examenLive.examenLive_api.sesiones.dominio.Sesion;
import com.examenLive.examenLive_api.sesiones.dominio.VentanaTiempo;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class SesionTest {

    @Test
    void debeCrearUnaSesionConReferenciaAlExamenPorId() {
        Long sesionId = 1L;
        Long examenId = 25L;
        VentanaTiempo ventana = new VentanaTiempo(
                LocalDateTime.of(2026, 9, 19, 14, 0),
                LocalDateTime.of(2026, 9, 19, 16, 0)
        );

        Sesion sesion = new Sesion(sesionId, examenId, ventana);

        assertEquals(sesionId, sesion.getId());
        assertEquals(examenId, sesion.getExamenId());
        assertEquals(ventana, sesion.getVentanaTiempo());
    }

    @Test
    void debePermitirAgregarEstudiantePorId() {
        Long sesionId = 1L;
        Long examenId = 25L;
        Long primerEstudianteId = 100L;
        Long segundoEstudianteId = 200L;
        VentanaTiempo ventana = new VentanaTiempo(
                LocalDateTime.of(2026, 9, 19, 14, 0),
                LocalDateTime.of(2026, 9, 19, 16, 0)
        );

        Sesion sesion = new Sesion(sesionId, examenId, ventana);

        sesion.agregarEstudiante(primerEstudianteId);
        sesion.agregarEstudiante(segundoEstudianteId);

        assertEquals(2, sesion.getEstudiantesIds().size());
        assertTrue(sesion.getEstudiantesIds().contains(primerEstudianteId));
        assertTrue(sesion.getEstudiantesIds().contains(segundoEstudianteId));
    }


}