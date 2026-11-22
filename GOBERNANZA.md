# Gobernanza técnica

El equipo mantiene los límites de cada subdominio y evita compartir entidades
de dominio entre servicios. Los cambios que modifiquen contratos HTTP, gRPC,
protobuf, eventos Kafka o esquemas de base de datos deben describir su impacto
en un ADR o en la documentación de arquitectura. Cada cambio debe incluir
pruebas enfocadas y conservar la configuración por variables de entorno.

Las revisiones verifican responsabilidades, nombres del lenguaje ubicuo,
idempotencia de consumidores y compatibilidad de migraciones. Los documentos
de decisión se mantienen en la raíz y las evidencias específicas de cada
integrante se conservan bajo `docs/`.
