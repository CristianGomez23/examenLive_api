# Matriz de decisiones y riesgos

| Elemento | Decisión | Beneficio | Costo o riesgo | Evidencia |
|---|---|---|---|---|
| Límites | Un servicio por subdominio | Bajo acoplamiento | Más despliegues | `ARQUITECTURA.md` |
| Persistencia | Base PostgreSQL por servicio | Autonomía de datos | Operación de varias bases | Migraciones Flyway |
| Entrada | Gateway HTTP | Punto único para clientes | Debe mantenerse disponible | `gateway/application.yaml` |
| Descubrimiento | Eureka | Direcciones configurables | Dependencia adicional | `eureka-server` |
| Síncrono | gRPC interno | Contratos tipados y baja latencia | Gestionar deadlines | archivos `.proto` |
| Asíncrono | Kafka con outbox | Resistencia a fallos entre DB y broker | Consistencia eventual | tablas `outbox_event` |
| Duplicados | Inbox e idempotencia | Reintentos seguros | Claves y limpieza | servicios de comunicación |
| Contratos | Versionado de eventos | Evolución controlada | Mantener versiones | topics `.v1` |
