package com.examenLive.examenLive_api.resultados.aplicacion;

import com.examenLive.examenLive_api.resultados.dominio.Resultado;
import com.examenLive.examenLive_api.resultados.dominio.puertos.salida.RepositorioResultados;
import java.util.*;

class RepositorioResultadosFake implements RepositorioResultados {
    private final Map<Long, Resultado> datos = new HashMap<>();

    public Resultado guardar(Resultado resultado){
        datos.put(resultado.resultadoId(), resultado);
        return resultado;
    }

    public Optional<Resultado> buscarPorId(Long id){
        return Optional.ofNullable(datos.get(id));
    }

    public Optional<Resultado> buscarPorSesionYEstudiante(Long sesionId, Long estudianteId){
        return datos.values().stream()
            .filter(r -> r.sesionId().equals(sesionId) && r.estudianteId().equals(estudianteId))
            .findFirst();
    }

    public List<Resultado> buscarPorEstudiante(Long estudianteId){
        return datos.values().stream().filter(r -> r.estudianteId().equals(estudianteId)).toList();
    }
}
