package com.examenLive.examenLive_api.resultados.aplicacion;

import com.examenLive.examenLive_api.resultados.dominio.Resultado;
import java.util.*;

class RepositorioResultadosFake implements RepositorioResultados {
    private final Map<UUID, Resultado> datos = new HashMap<>();

    public Resultado guardar(Resultado resultado){
        datos.put(resultado.resultadoId(), resultado);
        return resultado;
    }

    public Optional<Resultado> buscarPorId(UUID id){
        return Optional.ofNullable(datos.get(id));
    }
}
