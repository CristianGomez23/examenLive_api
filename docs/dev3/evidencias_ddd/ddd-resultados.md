# Bounded Context de Calificación y Resultados

## Propósito

Este bounded context calcula y conserva el resultado de un estudiante cuando finaliza una sesión de examen. Su modelo responde principalmente a las historias HU-06, HU-07, HU-08 y HU-09, y protege estas reglas del negocio:

- una respuesta no se registra ni se modifica después del cierre de la sesión;
- un examen no se califica mientras exista una pregunta sin puntaje asignado;
- las respuestas enviadas se conservan sin alteraciones;
- el estudiante puede consultar el resultado producido al cerrar la sesión.

## Lenguaje ubicuo

| Concepto del requisito | Nombre en el código | Significado |
| --- | --- | --- |
| Calificación y resultados | `resultados` | Nombre del bounded context. |
| Resultado | `Resultado` | Raíz del agregado que reúne las respuestas y la calificación de un estudiante en una sesión. |
| Respuesta | `Respuesta` | Value Object inmutable que identifica la pregunta, los valores seleccionados y el instante de envío. |
| Calificación automática | `CalificacionAutomaticaService` | Servicio de dominio que valida los puntajes y calcula la calificación. |
| Registrar respuesta | `registrarRespuesta` | Incorpora o reemplaza una respuesta mientras la recepción continúa abierta. |
| Cerrar sesión | `cerrarRecepcionRespuestas` | Cierra el agregado para impedir cambios posteriores. |
| Calificar | `calificar` | Evalúa las respuestas y registra la calificación final. |
| Pregunta con criterios de evaluación | `PreguntaCalificable` | Proyección local de una pregunta, recibida desde el contexto de exámenes únicamente con su id y datos necesarios para calificar. |

Se evitan nombres genéricos como `Data`, `Item`, `Manager`, `Score` o `Answer`, porque no corresponden al vocabulario empleado por docentes y estudiantes en los requisitos.

## Límite del agregado

`Resultado` es la única raíz del agregado. Una operación externa no modifica directamente su colección de respuestas ni su calificación: debe invocar los métodos de la raíz.

El agregado pertenece al contexto de Calificación y Resultados. Los conceptos administrados por otros contextos se referencian solamente mediante identificadores:

| Contexto externo | Referencia conservada |
| --- | --- |
| Sesiones en vivo | `UUID sesionId` |
| Participantes o estudiantes | `UUID estudianteId` |
| Banco de preguntas y exámenes | `UUID preguntaId` |

No se mantienen objetos `Sesion`, `Estudiante`, `Examen` ni `Pregunta` dentro de `Resultado`. Los datos mínimos para calificar se reciben como una proyección local llamada `PreguntaCalificable`, lo que evita acoplar los modelos de los bounded contexts.

## Invariantes

1. Un resultado siempre identifica una sesión y un estudiante.
2. Existe como máximo una respuesta vigente por pregunta dentro del resultado.
3. Las respuestas pueden reemplazarse mientras la recepción esté abierta.
4. Después del cierre no pueden agregarse ni reemplazarse respuestas.
5. Una calificación solo se registra sobre un resultado cerrado.
6. Todas las preguntas deben tener un puntaje positivo antes de calificar.
7. La calificación registrada no puede reemplazarse.