package com.examenLive.examenesservice.plataforma;

import org.springframework.web.bind.annotation.*;
import java.util.Map;
@RestController
public class StatusController {
 @GetMapping("/api/status") public Map<String,String> estado(){return Map.of("servicio","examenes-service","estado","UP");}
}
