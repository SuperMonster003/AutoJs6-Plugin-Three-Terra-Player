******

### Historial de versiones

******

# v1.0.1

###### 2026/08/08

* `Corrección` Enlace de servicio nulo al activar el complemento en el centro de complementos
* `Mejora` Nombre, descripción y documentación de usuario más claros y concisos

# v1.0.0

###### 2026/08/02

* `Función` Plugin Audio Player con ID de plugin `audio-player`, ID de acción `play-audio`, motor `explorer-action` y variante `default`
* `Función` Acción principal de solo lectura del gestor de archivos para las 18 extensiones de audio del host, con compilación 5269 o posterior requerida
* `Función` Reproducción mediante Media3 ExoPlayer y MediaSessionService con foco de audio, gestión de desconexión de salida, modo de activación local, segundo plano, controles multimedia del sistema e interfaz privada
* `Función` Información opcional sobre el permiso de notificaciones de Android 13+ sin bloquear la reproducción si se deniega
* `Función` Compatibilidad Android ACTION_VIEW independiente y de solo lectura para solicitudes de audio con URI `content` y transferencia a otra aplicación compatible si falla el decodificador, con prevención de bucles
* `Función` Validación estricta de protocolo, URI, ClipData, origen, nombre, MIME, tamaño y permisos, sin permisos de almacenamiento ni de red y con transferencia mínima de lectura
* `Función` Metadatos, texto de interfaz, instrucciones, archivos README e historiales localizados en español, francés, ruso, árabe, japonés, coreano, inglés, chino simplificado, chino tradicional de Hong Kong y chino tradicional de Taiwán
* `Dependencia` Añadido AndroidX Media3 ExoPlayer, Session y UI versión 1.10.1
