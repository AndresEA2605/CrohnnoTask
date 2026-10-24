# CrohnnoTask

Aplicación nativa Android para organizar tareas con entrada por voz. Este repositorio inicia la entrega universitaria con Kotlin, Jetpack Compose y Material 3.

## Estado actual

El dashboard en español muestra tareas de ejemplo y permite marcarlas como completadas. La pantalla consume estado desde un ViewModel; el dominio define el modelo, el repositorio y los casos de uso, y la capa de datos ofrece un repositorio en memoria. Las tareas todavía no persisten al cerrar la app.

## Tecnologías

- Kotlin
- Jetpack Compose y Material 3
- MVVM con `StateFlow` para el estado de presentación
- Capas separadas para dominio, datos y presentación
- Room y reconocimiento de voz planificados

## Abrir el proyecto

Abre esta carpeta desde Android Studio. Se requiere Android SDK 35 y JDK 17. Android Studio descargará el Gradle Wrapper y las dependencias al sincronizar el proyecto.

## Próximas iteraciones

1. Persistir las tareas con Room y ordenarlas con una estrategia de prioridad definida.
2. Añadir formulario de alta/edición y entrada por voz con permisos y manejo del ciclo de vida.
3. Incorporar navegación, preferencias, accesibilidad y pantallas restantes.

## Identidad visual

Nombre provisional: CrohnnoTask. La pantalla usa una paleta violeta, superficies claras y tarjetas Material 3. El saludo y las tareas son datos demostrativos que se reemplazarán por información configurable.
