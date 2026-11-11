package com.examenLive.resultadosservice.resultados.aplicacion.servicios;


import com.examenLive.resultadosservice.comunicacion.contratos.IntentoFinalizadoEvento;
import com.examenLive.resultadosservice.resultados.dominio.puertos.entrada.*;
import com.examenLive.resultadosservice.resultados.dominio.puertos.salida.ConsultaIntentos;
import org.springframework.stereotype.Service;
@Service
public class ComunicacionResultadosService implements ComunicacionResultadosUseCase {
    private final ConsultaIntentos intentos;
    private final ProcesadorIntentoFinalizado procesador;
    public ComunicacionResultadosService(ConsultaIntentos intentos,ProcesadorIntentoFinalizado procesador) {
        this.intentos=intentos; this.procesador=procesador;
    }
    public ResultadoGenerado generarDesdeIntento(Long id) {
        if (id==null || id<=0) throw new IllegalArgumentException("El intentoId debe ser positivo.");
        var previo=procesador.buscar(id);
        if (previo.isPresent()) return previo.get();
        // Llamada remota fuera de la transacción de escritura.
        var datos=intentos.obtenerFinalizado(id);
        if (!id.equals(datos.intentoId())) throw new IllegalStateException("El proveedor devolvió otro intento.");
        return procesador.procesar(datos);
    }
    public void alFinalizarIntento(IntentoFinalizadoEvento evento) { procesador.consumir(evento); }
}
