# Reproductor de audio

El Reproductor de audio añade un controlador dentro de la aplicación y reproducción en segundo plano para las extensiones que ya reconoce el gestor de archivos:

`aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave`

Cuando el plugin está instalado, Reproducir audio en un archivo comienza en esa pista y puede descubrir una cola acotada y ordenada de forma natural de audios hermanos directos legibles. La selección explícita de 1 a 128 archivos conserva el orden del host. Cuando falta, el host mantiene su flujo ACTION_VIEW externo de solo lectura para un archivo.

La reproducción usa Media3 y puede continuar en segundo plano. Incluye anterior / siguiente, cola editable, modos secuencial / aleatorio / repetir una, temporizador, bucle A-B, velocidad y reanudación por pista. En Android 13+, el permiso de notificaciones es opcional.

El descubrimiento en la misma carpeta requiere AutoJs6 6.8.0 build 5276+ y Explorer Action v12; las capacidades futuras no elevarán este requisito. Los hosts sin la sesión opcional conservan la reproducción del archivo seleccionado.

Límites de seguridad y privacidad:

- La entrada del gestor exige la firma del host y valida el protocolo v12, destinos y ClipData ordenados, identificadores, relación con el padre, metadatos, capacidades declaradas y permisos de solo lectura.
- La entrada Android independiente solo acepta URI `content` de solo lectura con `audio/*` y no transfiere extras del llamador ni permisos amplios.
- El plugin no solicita permisos de almacenamiento ni de acceso a Internet y nunca escribe el archivo de origen. Media3 aporta el permiso normal `ACCESS_NETWORK_STATE`, pero el APK no tiene `INTERNET`.
- Una Host Session por solicitud queda vinculada al UID del plugin y solo permite listar sin recursión el padre directo del archivo seleccionado y abrir este o un audio hermano directo legible. La reproducción recibe rutas sintéticas opacas, nunca rutas del sistema de archivos, y no adivina URI hermanos.
- La coincidencia de la extensión no garantiza la decodificación. La compatibilidad depende de Media3, Android, los codecs del dispositivo y el contenido.
- Tras un error de decodificación, el archivo puede abrirse en otra aplicación compatible y este plugin queda excluido del selector.
