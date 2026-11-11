package com.examenLive.examenesservice.comunicacion.aplicacion;


import com.examenLive.examenesservice.comunicacion.contratos.DatosIntento;
import com.examenLive.examenesservice.examenes.dominio.puertos.salida.IntentoExamenRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import java.util.NoSuchElementException;
@Service
public class ConsultaSnapshotService implements ConsultaSnapshotUseCase {
    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;
    private final IntentoExamenRepository intentos;
    public ConsultaSnapshotService(JdbcTemplate jdbc,ObjectMapper mapper,IntentoExamenRepository intentos) {
        this.jdbc=jdbc;this.mapper=mapper;this.intentos=intentos;
    }
    @Transactional(readOnly=true)
    public DatosIntento consultar(Long id) {
        if (id==null || id<=0) throw new IllegalArgumentException("El intentoId debe ser positivo.");
        var snapshots=jdbc.queryForList("SELECT snapshot FROM calificacion_saga WHERE intento_id=?",String.class,id);
        if (!snapshots.isEmpty()) return mapper.readValue(snapshots.getFirst(),DatosIntento.class);
        if (intentos.buscarPorId(id).isEmpty()) throw new NoSuchElementException("Intento no encontrado.");
        throw new IllegalStateException("Intento no finalizado o anterior a la integración.");
    }
}
