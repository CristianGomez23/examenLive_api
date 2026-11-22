# Resumen ejecutivo

ExamenLive implementa una arquitectura de microservicios para administrar
preguntas, exámenes, sesiones en vivo y resultados. Gateway y Eureka ofrecen
entrada y descubrimiento; HTTP y gRPC cubren las interacciones síncronas, y
Kafka con outbox e inbox cubre la propagación confiable de eventos. Cada
servicio conserva su propia base PostgreSQL y aplica una separación de
dominio, aplicación e infraestructura. La documentación registra decisiones,
riesgos, gobernanza, contratos y el caso de generación idempotente de
resultados.
