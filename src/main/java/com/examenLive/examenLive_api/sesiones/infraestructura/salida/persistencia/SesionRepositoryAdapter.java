package com.examenLive.examenLive_api.sesiones.infraestructura.salida.persistencia;

import com.examenLive.examenLive_api.sesiones.dominio.puertos.salida.SesionRepository;
import com.examenLive.examenLive_api.sesiones.dominio.Sesion;
import com.examenLive.examenLive_api.sesiones.dominio.VentanaTiempo;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SesionRepositoryAdapter implements SesionRepository {

    private final SesionJpaRepository dataRepository;

    public SesionRepositoryAdapter(SesionJpaRepository dataRepository) {
        this.dataRepository = dataRepository;
    }

    @Override
    public Optional<Sesion> buscarPorId(Long sesionId) {
        return dataRepository.findById(sesionId).map(entity -> Sesion.reconstruir(
            entity.getId(),
            entity.getExamenId(),
            new VentanaTiempo(entity.getInicio(), entity.getFin()),
            entity.getEstudiantesIds()
        ));
    }

    @Override
    public void guardar(Sesion sesion) {
        dataRepository.save(new SesionEntity(
            sesion.getId(), sesion.getExamenId(),
            sesion.getVentanaTiempo().inicio(), sesion.getVentanaTiempo().fin(),
            sesion.getEstudiantesIds()
        ));
    }
}