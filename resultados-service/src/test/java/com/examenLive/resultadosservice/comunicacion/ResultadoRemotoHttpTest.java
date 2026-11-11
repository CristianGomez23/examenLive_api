package com.examenLive.resultadosservice.comunicacion;

import com.examenLive.resultadosservice.compartido.*;
import com.examenLive.resultadosservice.resultados.dominio.puertos.entrada.ComunicacionResultadosUseCase;
import com.examenLive.resultadosservice.resultados.infraestructura.entrada.web.ResultadoRemotoController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ResultadoRemotoHttpTest {
    ComunicacionResultadosUseCase casos=mock(ComunicacionResultadosUseCase.class);
    MockMvc mvc;
    @BeforeEach void configurar() {
        mvc=MockMvcBuilders.standaloneSetup(new ResultadoRemotoController(casos))
            .setControllerAdvice(new ManejadorErrores()).build();
    }
    @Test void indisponibilidadEs503() throws Exception {
        when(casos.generarDesdeIntento(1L)).thenThrow(new ServicioNoDisponibleException("Proveedor no disponible",null));
        mvc.perform(post("/api/resultados/desde-intento").contentType("application/json").content("{\"intentoId\":1}"))
            .andExpect(status().isServiceUnavailable()).andExpect(jsonPath("$.estado").value(503));
    }
    @Test void noEncontradoEs404() throws Exception {
        when(casos.generarDesdeIntento(99L)).thenThrow(new RecursoNoEncontradoException("Intento no encontrado"));
        mvc.perform(post("/api/resultados/desde-intento").contentType("application/json").content("{\"intentoId\":99}"))
            .andExpect(status().isNotFound());
    }
    @Test void intentoAbiertoEs409() throws Exception {
        when(casos.generarDesdeIntento(1L)).thenThrow(new IllegalStateException("Intento abierto"));
        mvc.perform(post("/api/resultados/desde-intento").contentType("application/json").content("{\"intentoId\":1}"))
            .andExpect(status().isConflict());
    }
    @Test void jsonInvalidoEs400YNoInvocaCaso() throws Exception {
        mvc.perform(post("/api/resultados/desde-intento").contentType("application/json").content("{"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(casos);
    }
}
