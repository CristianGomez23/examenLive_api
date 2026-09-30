package com.examenLive.examenLive_api.sesiones;

import com.examenLive.examenLive_api.sesiones.aplicacion.ControlIngresoSesionUseCase;
import com.examenLive.examenLive_api.sesiones.aplicacion.puertos.salida.SesionRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

        @Test
        void debeRegistrarIngresoDePrincipioAFinConUnFake() {
                VentanaTiempo ventana = new VentanaTiempo(
                                LocalDateTime.of(2026, 9, 19, 14, 0),
                                LocalDateTime.of(2026, 9, 19, 16, 0)
                );
                Sesion sesion = new Sesion(1L, 25L, ventana);
                FakeSesionRepository repositorio = new FakeSesionRepository(sesion);
                ControlIngresoSesionUseCase casoDeUso =
                                new ControlIngresoSesionService(repositorio);

                boolean ingresoPermitido = casoDeUso.ingresar(
                                1L,
                                7L,
                                LocalDateTime.of(2026, 9, 19, 15, 0)
                );

                assertTrue(ingresoPermitido);
                assertTrue(repositorio.buscarPorId(1L).orElseThrow()
                                .getEstudiantesIds().contains(7L));
                assertEquals(1, repositorio.getCantidadGuardados());
        }

        private static class FakeSesionRepository implements SesionRepository {

                private final Map<Long, Sesion> sesiones = new HashMap<>();
                private int cantidadGuardados;

                private FakeSesionRepository(Sesion sesionInicial) {
                        sesiones.put(sesionInicial.getId(), sesionInicial);
                }

                @Override
                public Optional<Sesion> buscarPorId(Long sesionId) {
                        return Optional.ofNullable(sesiones.get(sesionId));
                }

                @Override
                public void guardar(Sesion sesion) {
                        sesiones.put(sesion.getId(), sesion);
                        cantidadGuardados++;
                }

                private int getCantidadGuardados() {
                        return cantidadGuardados;
                }
        }
}