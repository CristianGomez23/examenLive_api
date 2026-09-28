package com.examenLive.examenLive_api.resultados.infraestructura.salida.persistencia;

import com.examenLive.examenLive_api.resultados.aplicacion.RepositorioResultados;
import com.examenLive.examenLive_api.resultados.dominio.Resultado;
import org.springframework.stereotype.Component;
import java.util.Optional;
import java.util.UUID;

@Component
public class ResultadoRepositoryJpaAdapter implements RepositorioResultados {

    private final ResultadoRepository resultadoRepository;

    public ResultadoRepositoryJpaAdapter(ResultadoRepository resultadoRepository){
        this.resultadoRepository = resultadoRepository;
    }

    @Override
    public Resultado guardar(Resultado resultado){
        resultadoRepository.save(new ResultadoEntity(
            resultado.resultadoId(),
            resultado.sesionId(),
            resultado.estudianteId(),
            resultado.creadoEn(),
            resultado.estado().name()
        ));
        return resultado;
    }

    @Override
    public Optional<Resultado> buscarPorId(UUID id){
        return resultadoRepository.findById(id)
            .map(e -> new Resultado(
                e.getId(),
                e.getSesionId(),
                e.getEstudianteId(),
                e.getCreadoEn()
            ));
    }
}
