# Reproductor de audio

El Reproductor de audio añade un controlador dentro de la aplicación y reproducción en segundo plano para las extensiones que ya reconoce el Explorador de AutoJs6:

`aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave`

Cuando el plugin está instalado, elige Reproducir audio entre las acciones principales del Explorador. Cuando falta, AutoJs6 conserva su flujo externo ACTION_VIEW de solo lectura, por lo que otra aplicación de audio instalada todavía puede abrir el archivo.

La reproducción usa Media3 y puede continuar en segundo plano. En Android 13+, el permiso de notificaciones es opcional y solo afecta a los controles de la bandeja de notificaciones. Su rechazo no impide la reproducción.

El plugin requiere la compilación 5269+ de AutoJs6. No contiene bibliotecas nativas y es independiente de la ABI del dispositivo.

Límites de seguridad y privacidad:

- La entrada del Explorador exige el permiso de firma de AutoJs6 y valida el protocolo v2, ambos content URI, ClipData, los metadatos y los permisos de solo lectura.
- La entrada Android independiente solo acepta URI `content` de solo lectura con `audio/*` y no transfiere extras del llamador ni permisos amplios.
- El plugin no solicita permisos de almacenamiento o red y nunca escribe el archivo de origen.
- La coincidencia de la extensión no garantiza la decodificación. La compatibilidad depende de Media3, Android, los codecs del dispositivo y el contenido.
- Tras un error de decodificación, el archivo puede abrirse en otra aplicación compatible y este plugin queda excluido del selector.
