# CrohnnoTask

Aplicación nativa Android para organizar tareas con entrada por voz. Este repositorio inicia la entrega universitaria con Kotlin, Jetpack Compose y Material 3.

## Estado actual

El dashboard en español muestra tareas y permite marcarlas como completadas. La UI consume estado desde un ViewModel; el dominio define el modelo y los casos de uso, y Room guarda las tareas localmente. La consulta muestra primero las pendientes, luego ordena por prioridad alta, media y baja, y usa horario y título para desempatar. En una instalación nueva se insertan tres tareas de ejemplo.

## Tecnologías

- Kotlin
- Jetpack Compose y Material 3
- MVVM con `StateFlow` para el estado de presentación
- Capas separadas para dominio, datos y presentación
- Room con KSP para persistencia local
- Reconocimiento de voz planificado

## Abrir el proyecto

Abre esta carpeta desde Android Studio. Se requiere Android SDK 35 y JDK 17. Android Studio descargará el Gradle Wrapper y las dependencias al sincronizar el proyecto.

## Próximas iteraciones

1. Añadir formulario de alta/edición y entrada por voz con permisos y manejo del ciclo de vida.
2. Incorporar navegación, preferencias, accesibilidad y pantallas restantes.

## Identidad visual

Nombre provisional: CrohnnoTask. La pantalla usa una paleta violeta, superficies claras y tarjetas Material 3. El saludo y las tareas son datos demostrativos que se reemplazarán por información configurable.
