package com.examenLive.examenLive_api.sesiones.infraestructura.persistencia;

import com.examenLive.examenLive_api.sesiones.Sesion;
import com.examenLive.examenLive_api.sesiones.aplicacion.puertos.salida.SesionRepository;

import java.util.Optional;

public class SesionRepositoryAdapter implements SesionRepository {

    private final SesionDataRepository dataRepository;

    public SesionRepositoryAdapter(SesionDataRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public Optional<Sesion> buscarPorId(Long sesionId) {
        return dataRepository.findById(sesionId);
    }

    @Override
    public void guardar(Sesion sesion) {
        dataRepository.save(sesion);
    }
}