package com.examenLive.resultadosservice.resultados;

import com.examenLive.resultadosservice.resultados.dominio.puertos.salida.GeneradorId;

import java.util.concurrent.atomic.AtomicLong;

public class GeneradorIdNumericoFake implements GeneradorId {

	private final AtomicLong siguiente;

	public GeneradorIdNumericoFake(long primerId) {
		siguiente = new AtomicLong(primerId);
	}

	@Override
	public long siguienteId() {
		return siguiente.getAndIncrement();
	}
}