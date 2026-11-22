# ATAM-LITE

## Riesgos y respuestas

### Riesgo 1: duplicación de eventos

La entrega repetida de mensajes puede duplicar resultados; se mitiga con inbox
e idempotencia.

### Riesgo 2: dependencia no disponible

Una llamada gRPC puede fallar si el servicio remoto está caído; se mitiga con
deadlines y circuit breaker.

### Riesgo 3: infraestructura no disponible

Kafka o Eureka pueden no estar disponibles durante el arranque; se mitiga con
configuración externa y reintentos.

### Riesgo 4: contrato incompatible

Un cambio de evento puede romper consumidores; se mitiga con versionado.

### Riesgo 5: crecimiento de outbox

Una outbox grande puede degradar el publicador; se mitiga con lotes y limpieza.

| ID | Riesgo arquitectónico | Sensibilidad | Respuesta o mitigación |
|---|---|---|---|
| R1 | Un evento puede procesarse más de una vez por reintentos o reinicios. | Alta | Inbox, claves de negocio, restricciones únicas y operaciones idempotentes. |
| R2 | Un servicio dependiente puede estar caído durante una llamada gRPC. | Alta | Deadlines configurables, circuit breaker de Resilience4j y pruebas de error. |
| R3 | Eureka o Kafka pueden no estar disponibles al iniciar localmente. | Media | Variables de entorno, perfiles de infraestructura y configuración de listener. |
| R4 | La evolución incompatible de eventos rompe consumidores. | Alta | Versionado de topics y mensajes, contratos protobuf versionados y pruebas de comunicación. |
| R5 | El crecimiento de outbox puede degradar el publicador. | Media | Publicación por lotes, limpieza de eventos entregados y métricas de pendientes. |

## Trade-offs

La consistencia eventual reduce el acoplamiento, pero obliga a observar estados
intermedios y reintentos. El descubrimiento simplifica las direcciones de
despliegue, aunque agrega una dependencia operativa. La persistencia separada
protege los límites de contexto, a cambio de no poder resolver una operación
distribuida con una transacción SQL única.
