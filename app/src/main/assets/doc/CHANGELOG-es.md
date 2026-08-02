******

### Historial de versiones

******

# v1.0.0

###### 2026/08/02

* `Función` Plugin Audio Player con ID de plugin `audio-player`, ID de acción `play-audio`, motor `explorer-action` y variante `default`
* `Función` Entrada principal de solo lectura del protocolo Explorer Action v2 para las 18 extensiones de audio existentes del host, catálogo MIME del Explorador vacío y compilación de host AutoJs6 5269 requerida
* `Función` Reproducción mediante Media3 ExoPlayer y MediaSessionService con foco de audio, gestión de desconexión de salida, modo de activación local, segundo plano, controles multimedia del sistema e interfaz privada
* `Función` Información opcional sobre el permiso de notificaciones de Android 13+ sin bloquear la reproducción si se deniega
* `Función` Compatibilidad Android ACTION_VIEW independiente y de solo lectura para solicitudes de audio con URI `content` y transferencia a otra aplicación compatible si falla el decodificador, con prevención de bucles
* `Función` Validación estricta de protocolo, URI, ClipData, origen, nombre, MIME, tamaño y permisos, sin permisos de almacenamiento ni de red y con transferencia mínima de lectura
* `Función` Implementación JVM pura sin bibliotecas nativas, ABI sin restricciones mediante `supportedAbis = emptyArray()` y un APK independiente de ABI
* `Función` Metadatos, texto de interfaz, instrucciones, archivos README e historiales localizados en español, francés, ruso, árabe, japonés, coreano, inglés, chino simplificado, chino tradicional de Hong Kong y chino tradicional de Taiwán
* `Dependencia` Añadido AndroidX Media3 ExoPlayer, Session y UI versión 1.10.1
