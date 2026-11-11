package com.examenLive.resultadosservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@org.springframework.scheduling.annotation.EnableScheduling
@org.springframework.grpc.client.ImportGrpcClients(target="examenes",types=com.examenLive.contratos.intentos.v1.IntentosGrpcGrpc.IntentosGrpcBlockingStub.class)
@SpringBootApplication
public class ResultadosServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ResultadosServiceApplication.class, args);
	}
}
