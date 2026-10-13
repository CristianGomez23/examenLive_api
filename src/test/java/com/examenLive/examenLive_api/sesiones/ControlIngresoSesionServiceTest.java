package com.examenLive.examenLive_api.sesiones;

import com.examenLive.examenLive_api.sesiones.aplicacion.servicios.ControlIngresoSesionService;
import com.examenLive.examenLive_api.sesiones.dominio.Sesion;
import com.examenLive.examenLive_api.sesiones.dominio.VentanaTiempo;
import com.examenLive.examenLive_api.sesiones.dominio.puertos.entrada.ControlIngresoSesionUseCase;
import com.examenLive.examenLive_api.sesiones.dominio.puertos.salida.SesionRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ControlIngresoSesionServiceTest {

    @Test
    void debePermitirIngresarDentroDeLaVentana() {
        assertTrue(ingresarEn(LocalDateTime.of(2026, 9, 19, 15, 0), false));
    }

    @Test
    void debeRechazarIngresoFueraDeLaVentana() {
        assertFalse(ingresarEn(LocalDateTime.of(2026, 9, 19, 16, 0), false));
    }

    @Test
    void debePermitirReingresarSiLaSesionSigueActiva() {
        assertTrue(ingresarEn(LocalDateTime.of(2026, 9, 19, 15, 30), true));
    }

    @Test
    void debeRechazarReingresoDespuesDelCierre() {
        assertFalse(ingresarEn(LocalDateTime.of(2026, 9, 19, 16, 1), true));
    }

        @Test
        void debeRegistrarIngresoDePrincipioAFinConUnFake() {
                Long sesionId = 1L;
                Long examenId = 25L;
                Long estudianteId = 7L;
                VentanaTiempo ventana = new VentanaTiempo(
                                LocalDateTime.of(2026, 9, 19, 14, 0),
                                LocalDateTime.of(2026, 9, 19, 16, 0)
                );
                Sesion sesion = new Sesion(sesionId, examenId, ventana);
                FakeSesionRepository repositorio = new FakeSesionRepository(sesion);
                ControlIngresoSesionUseCase casoDeUso = new ControlIngresoSesionService(repositorio);

                boolean ingresoPermitido = casoDeUso.ingresar(
                                sesionId,
                                estudianteId,
                                LocalDateTime.of(2026, 9, 19, 15, 0)
                );

                assertTrue(ingresoPermitido);
                assertTrue(repositorio.buscarPorId(sesionId).orElseThrow()
                                .getEstudiantesIds().contains(estudianteId));
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

        private boolean ingresarEn(LocalDateTime momento, boolean reingreso) {
                VentanaTiempo ventana = new VentanaTiempo(
                                LocalDateTime.of(2026, 9, 19, 14, 0),
                                LocalDateTime.of(2026, 9, 19, 16, 0)
                );
                Sesion sesion = new Sesion(1L, 25L, ventana);
                FakeSesionRepository repositorio = new FakeSesionRepository(sesion);
                ControlIngresoSesionUseCase casoDeUso = new ControlIngresoSesionService(repositorio);
                return reingreso
                                ? casoDeUso.reingresar(1L, 7L, momento)
                                : casoDeUso.ingresar(1L, 7L, momento);
        }
}