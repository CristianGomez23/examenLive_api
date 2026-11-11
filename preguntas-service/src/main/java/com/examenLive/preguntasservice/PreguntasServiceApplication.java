package com.examenLive.preguntasservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication
@EnableScheduling
public class PreguntasServiceApplication {
    public static void main(String[] args) { SpringApplication.run(PreguntasServiceApplication.class,args); }
}
