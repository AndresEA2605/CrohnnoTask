# VocalFlow

Aplicación nativa Android para organizar tareas con entrada por voz. Este repositorio inicia la entrega universitaria con Kotlin, Jetpack Compose y Material 3.

## Estado inicial

La primera versión presenta un dashboard en español con tareas de ejemplo, niveles de prioridad y una interacción para marcar tareas como completadas. Los datos son temporales mientras se construyen las capas de persistencia y dominio.

## Tecnologías

- Kotlin
- Jetpack Compose y Material 3
- MVVM/Clean Architecture como dirección para las siguientes iteraciones
- Room y reconocimiento de voz planificados

## Abrir el proyecto

Abre esta carpeta desde Android Studio. Se requiere Android SDK 35 y JDK 17. Android Studio descargará el Gradle Wrapper y las dependencias al sincronizar el proyecto.

## Próximas iteraciones

1. Separar dominio, datos y presentación; incorporar ViewModel y estado observable.
2. Persistir las tareas con Room y ordenarlas con una estrategia de prioridad definida.
3. Añadir formulario de alta/edición y entrada por voz con permisos y manejo del ciclo de vida.
4. Incorporar navegación, preferencias, accesibilidad y pantallas restantes.

## Identidad visual

Nombre provisional: VocalFlow. La pantalla usa una paleta violeta, superficies claras y tarjetas Material 3. El saludo y las tareas son datos demostrativos que se reemplazarán por información configurable.
