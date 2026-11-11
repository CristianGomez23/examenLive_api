package com.examenLive.sesionesservice.compartido;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import java.time.Instant;
import java.util.NoSuchElementException;
@RestControllerAdvice
public class ManejadorErrores {
 record Error(Instant fecha,int estado,String mensaje){}
 private ResponseEntity<Error> error(int estado,String texto){return ResponseEntity.status(estado).body(new Error(Instant.now(),estado,texto));}
 @ExceptionHandler({IllegalArgumentException.class,HttpMessageNotReadableException.class,MethodArgumentTypeMismatchException.class})
 public ResponseEntity<Error> invalido(Exception e){return error(400,e.getMessage());}
 @ExceptionHandler({IllegalStateException.class,DataIntegrityViolationException.class})
 public ResponseEntity<Error> conflicto(Exception e){return error(409,e.getMessage());}
 @ExceptionHandler(NoSuchElementException.class) public ResponseEntity<Error> inexistente(Exception e){return error(404,e.getMessage());}
 @ExceptionHandler(ServicioNoDisponibleException.class) public ResponseEntity<Error> noDisponible(Exception e){return error(503,e.getMessage());}
}
