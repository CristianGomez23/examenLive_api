package com.examenLive.sesionesservice.sesiones.infraestructura.salida.persistencia;
import com.examenLive.sesionesservice.sesiones.dominio.puertos.salida.Participantes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
@Repository
public class ParticipantesJdbc implements Participantes {
 private final JdbcTemplate jdbc;
 public ParticipantesJdbc(JdbcTemplate jdbc){this.jdbc=jdbc;}
 public void agregar(Long sesion,Long estudiante){jdbc.update("INSERT INTO sesion_estudiantes(sesion_id,estudiante_id) VALUES (?,?) ON CONFLICT DO NOTHING",sesion,estudiante);}
}
