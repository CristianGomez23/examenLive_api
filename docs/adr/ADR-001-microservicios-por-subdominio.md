# ADR-001: Microservicios por subdominio

## Estado

Aceptada.

## Contexto

La plataforma tiene responsabilidades diferenciadas para preguntas, exámenes,
sesiones y resultados. Compartir un único modelo y una única base de datos
haría que los cambios de un contexto afectaran a todos los demás.

## Decisión

Se mantiene un servicio Spring Boot por subdominio, con persistencia propia y
contratos explícitos para la comunicación.

## Alternativa considerada

Un monolito modular con una base de datos compartida.

## Consecuencias

Cada equipo puede desplegar y probar su contexto de forma independiente.
Aumentan la configuración operativa, la necesidad de contratos entre servicios
y el costo de observar comunicaciones distribuidas.
