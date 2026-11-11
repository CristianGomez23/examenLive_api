# Arquitectura Hexagonal - Dev 3 Resultados

Puerto primario: ResultadoUseCase. Expone los casos de uso que pueden ser llamados por controladores o tests.

Puerto secundario: RepositorioResultados. Define únicamente guardar y buscar por id, las operaciones requeridas por la aplicación.

Adaptador secundario: ResultadoRepositoryJpaAdapter. Traduce el puerto de aplicación hacia Spring Data JPA.

El núcleo (dominio y aplicación) no conoce la implementación JPA.
