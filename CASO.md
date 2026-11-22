# Caso de uso arquitectónico

Cuando un estudiante finaliza un intento, `examenes-service` registra el
estado y deja un evento en su outbox dentro de la misma transacción. El
publicador entrega el evento versionado a Kafka. `resultados-service` lo
consume, verifica su inbox y genera el resultado una sola vez. Si el mensaje
se reintenta, la clave de idempotencia evita duplicar la calificación. El
cliente consulta el resultado por el gateway, que enruta la petición al
servicio responsable. Así se desacoplan la finalización del intento y la
disponibilidad temporal del servicio de resultados.
