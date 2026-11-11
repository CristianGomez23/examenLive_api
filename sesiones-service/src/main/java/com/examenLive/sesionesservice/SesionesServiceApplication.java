package com.examenLive.sesionesservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication
@EnableScheduling
public class SesionesServiceApplication {
    public static void main(String[] args) { SpringApplication.run(SesionesServiceApplication.class,args); }
}
