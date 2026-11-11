package com.examenLive.examenesservice.examenes.dominio.puertos.salida;

import com.examenLive.examenesservice.examenes.dominio.SesionExamen;
import java.util.Optional;
public interface ConsultaSesiones {Optional<SesionExamen> buscarPorId(Long id);void agregarEstudiante(Long sesionId,Long estudianteId);}
