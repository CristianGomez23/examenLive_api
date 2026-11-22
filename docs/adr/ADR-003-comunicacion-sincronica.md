# ADR-003: HTTP y gRPC según el caso de uso

## Estado

Aceptada.

## Contexto

El gateway necesita exponer rutas HTTP para clientes, mientras que algunos
servicios requieren consultar datos de otro contexto con baja latencia y un
contrato tipado.

## Decisión

Se usa HTTP para la API pública y las consultas remotas orientadas a cliente.
Se usa gRPC para llamadas internas entre servicios que requieren contratos
protobuf y deadlines explícitos. Los eventos Kafka se reservan para procesos
asíncronos.

## Alternativa considerada

Usar únicamente REST entre todos los servicios.

## Consecuencias

Los clientes reciben una interfaz familiar y los servicios internos tienen
contratos eficientes y tipados. El proyecto debe mantener archivos protobuf,
configuración de canales y pruebas de compatibilidad además de la API HTTP.
