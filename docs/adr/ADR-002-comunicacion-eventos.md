# ADR-002: Eventos y patrón outbox

## Estado

Aceptada.

## Contexto

La generación de resultados depende de eventos producidos al finalizar
intentos. Una publicación directa a Kafka después de confirmar la base de
datos puede perder el evento si el proceso falla entre ambas operaciones.

## Decisión

Los eventos se guardan en una tabla outbox dentro de la transacción de
negocio y un publicador los entrega a Kafka. Los consumidores usan inbox y
claves de negocio para procesar mensajes de forma idempotente.

## Alternativa considerada

Publicar directamente a Kafka desde el servicio después de guardar la entidad.

## Consecuencias

Se mejora la consistencia entre persistencia y mensajería y se toleran
reintentos. Se agregan tablas, un proceso de publicación y la responsabilidad
de monitorear mensajes pendientes o fallidos.
