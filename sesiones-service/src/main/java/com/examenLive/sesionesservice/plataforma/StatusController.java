package com.examenLive.sesionesservice.plataforma;

import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
public class StatusController {
 @GetMapping("/api/status") public Map<String,String> estado(){return Map.of("servicio","sesiones-service","estado","UP");}
}
