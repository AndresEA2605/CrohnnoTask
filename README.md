# CrohnnoTask

Aplicación nativa Android para organizar tareas con entrada por voz. Este repositorio inicia la entrega universitaria con Kotlin, Jetpack Compose y Material 3.

## Estado actual

La app solicita iniciar sesión o crear una cuenta local. El registro valida nombre, correo, longitud y confirmación de contraseña; las contraseñas se almacenan con PBKDF2-HMAC-SHA256, sal aleatoria y costo configurable, nunca en texto plano. La sesión se conserva en DataStore y se puede cerrar desde Configuración. Cada cuenta ve sus propias tareas. En Hoy, el botón de voz permite dictar una frase y la app la guarda directamente. El parser extrae fecha, hora, prioridad y descripción de expresiones comunes en español. Las tareas anteriores se conservan y se asignan a la primera cuenta creada al migrar la base de datos.

## Tecnologías

- Kotlin
- Jetpack Compose y Material 3
- MVVM con `StateFlow` para el estado de presentación
- Capas separadas para dominio, datos y presentación
- Room con KSP para persistencia local
- Preferences DataStore para guardar el tema
- Reconocimiento de voz de Android con creación automática de tareas
- Parser local de frases habituales en español; no requiere un modelo generativo
- Registro e inicio de sesión local con contraseñas derivadas y sesión persistente

## Abrir el proyecto

Abre esta carpeta desde Android Studio. Se requiere Android SDK 35 y JDK 17. Android Studio descargará el Gradle Wrapper y las dependencias al sincronizar el proyecto.

## Próximas iteraciones

1. Completar planificación por días y campos de tiempo estimado/real.
2. Añadir recuperación local de cuenta y personalizar Acerca de con los datos del estudiante.

## Identidad visual

Nombre provisional: CrohnnoTask. La pantalla usa una paleta violeta, superficies claras y tarjetas Material 3. El saludo y las tareas son datos demostrativos que se reemplazarán por información configurable.
