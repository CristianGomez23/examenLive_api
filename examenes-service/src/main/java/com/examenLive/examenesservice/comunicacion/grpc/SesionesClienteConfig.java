package com.examenLive.examenesservice.comunicacion.grpc;
@org.springframework.context.annotation.Configuration
@org.springframework.grpc.client.ImportGrpcClients(target="sesiones",types=com.examenLive.contratos.contextos.v1.SesionesGrpcGrpc.SesionesGrpcBlockingStub.class)
class SesionesClienteConfig {}
