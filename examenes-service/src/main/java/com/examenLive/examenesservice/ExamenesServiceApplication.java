package com.examenLive.examenesservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@SpringBootApplication
@EnableScheduling
public class ExamenesServiceApplication {
    public static void main(String[] args) { SpringApplication.run(ExamenesServiceApplication.class,args); }
}
