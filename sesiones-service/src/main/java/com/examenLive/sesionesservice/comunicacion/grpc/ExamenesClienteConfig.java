package com.examenLive.sesionesservice.comunicacion.grpc;
@org.springframework.context.annotation.Configuration
@org.springframework.grpc.client.ImportGrpcClients(target="examenes",types=com.examenLive.contratos.contextos.v1.ExamenesGrpcGrpc.ExamenesGrpcBlockingStub.class)
class ExamenesClienteConfig {}
