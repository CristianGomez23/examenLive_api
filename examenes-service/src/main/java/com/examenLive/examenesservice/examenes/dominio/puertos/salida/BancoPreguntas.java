package com.examenLive.examenesservice.examenes.dominio.puertos.salida;

import com.examenLive.examenesservice.examenes.dominio.PreguntaExamen;
import java.util.Optional;
public interface BancoPreguntas {Optional<PreguntaExamen> buscarPorId(Long id);void registrarUso(Long id);}
