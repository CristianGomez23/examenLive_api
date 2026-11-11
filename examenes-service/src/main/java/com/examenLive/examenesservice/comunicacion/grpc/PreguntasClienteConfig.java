package com.examenLive.examenesservice.comunicacion.grpc;
@org.springframework.context.annotation.Configuration
@org.springframework.grpc.client.ImportGrpcClients(target="preguntas",types=com.examenLive.contratos.contextos.v1.PreguntasGrpcGrpc.PreguntasGrpcBlockingStub.class)
class PreguntasClienteConfig {}
