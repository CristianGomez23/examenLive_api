package com.examenLive.sesionesservice.identidad;

import com.examenLive.sesionesservice.identidad.dominio.puertos.salida.GeneradorId;

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
