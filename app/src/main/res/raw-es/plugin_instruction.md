# Reproductor de audio

El Reproductor de audio funciona como plugin de AutoJs6 y como aplicación independiente. Añade un controlador y reproducción en segundo plano para todos los tipos MIME de audio que reconoce el gestor, con cobertura explícita de estas extensiones:

`aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave, wma`

Cuando el plugin está instalado, Reproducir audio en un archivo comienza en esa pista y puede descubrir una cola acotada y ordenada de forma natural de audios hermanos directos legibles. La selección explícita de 1 a 128 archivos conserva el orden del host. Cuando falta, el host mantiene su flujo ACTION_VIEW externo de solo lectura para un archivo.

El iniciador permite abrir hasta 128 documentos de audio sin AutoJs6. Los ajustes independientes incluyen idioma, modo nocturno, color del tema, reanudación, actualizaciones, versiones ignoradas, historial de versiones e información de la aplicación y el desarrollador. El idioma, el modo nocturno y el color siguen a AutoJs6 de forma predeterminada cuando su contrato protegido está disponible.

La reproducción usa Media3 y puede continuar en segundo plano. Incluye anterior / siguiente, cola editable, modos secuencial / aleatorio / repetir una, temporizador, bucle A-B y velocidad. La reanudación recuerda solo el archivo abierto más recientemente, borra esa posición al abrir otro y nunca guarda una reproducción completada. En Android 13+, el permiso de notificaciones es opcional.

El descubrimiento en la misma carpeta requiere AutoJs6 6.8.0 build 5276+ y Explorer Action v12; las capacidades futuras no elevarán este requisito. Los hosts sin la sesión opcional conservan la reproducción del archivo seleccionado.

Límites de seguridad y privacidad:

- La entrada del gestor exige la firma del host y valida el protocolo v12, destinos y ClipData ordenados, identificadores, relación con el padre, metadatos, capacidades declaradas y permisos de solo lectura.
- La entrada Android independiente solo acepta URI `content` de lectura con un tipo MIME de audio o WMA heredado y no transfiere extras del llamador ni permisos amplios.
- La aplicación no solicita almacenamiento y nunca escribe el archivo de origen. Internet se usa solo para comprobaciones de versiones de GitHub iniciadas por el usuario o diarias.
- Una Host Session por solicitud queda vinculada al UID del plugin y solo permite listar sin recursión el padre directo del archivo seleccionado y abrir este o un audio hermano directo legible. La reproducción recibe rutas sintéticas opacas, nunca rutas del sistema de archivos, y no adivina URI hermanos.
- La coincidencia de la extensión no garantiza la decodificación. La compatibilidad depende de Media3, Android, los codecs del dispositivo y el contenido.
- Tras un error de decodificación, el archivo puede abrirse en otra aplicación compatible y este plugin queda excluido del selector.
