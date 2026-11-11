package com.examenLive.examenLive_api.preguntas.dominio.puertos.entrada;

import com.examenLive.examenLive_api.preguntas.dominio.Pregunta;
import com.examenLive.examenLive_api.preguntas.dominio.TipoPregunta;
import com.examenLive.examenLive_api.preguntas.dominio.OpcionPregunta;

import java.math.BigDecimal;
import java.util.List;

public interface PreguntaUseCase {

	Pregunta registrar(TipoPregunta tipo, String enunciado, List<OpcionPregunta> opciones, BigDecimal puntaje);

	List<Pregunta> consultarActivas();

	Pregunta consultar(Long preguntaId);

	void registrarUsoEnSesionAplicada(Long preguntaId);

	void desactivar(Long preguntaId);

	void eliminar(Long preguntaId);
}