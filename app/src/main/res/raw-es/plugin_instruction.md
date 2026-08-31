3-Terra Player (antes Audio Player) es a la vez un complemento de reproducción de audio para el gestor de archivos de AutoJs6 y un reproductor de audio independiente y sencillo. Una vez instalado y activado, basta con tocar cualquier archivo de audio en el gestor de archivos de AutoJs6 para reproducirlo directamente, sin necesidad de un reproductor de terceros; también puede abrirse desde el lanzador como una aplicación normal para elegir varios archivos de audio y reproducirlos seguidos.

### Uso

1. Descarga el APK más reciente del complemento desde la página de [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases) e instálalo en el dispositivo donde se ejecuta AutoJs6.
2. Abre el centro de complementos de AutoJs6 y comprueba que `3-Terra Player` aparece reconocido y activado.
3. En el gestor de archivos de AutoJs6, toca cualquier archivo de audio y elige `Reproducir audio`, o mantén pulsado para seleccionar varios archivos y elige `Reproducir audios seleccionados` en la barra de herramientas.
4. También puedes abrir `3-Terra Player` directamente desde el lanzador y usar el selector de archivos para elegir varios audios y reproducirlos.

Si el complemento no aparece en el centro de complementos, actualiza primero AutoJs6 a la versión 6.8.0 (código de versión 5276) o posterior. El complemento es compatible con dispositivos Android 7.0 (API 24) o superiores, y el modo de aplicación independiente no depende de AutoJs6.

### Formatos de audio compatibles

La entrada del gestor de archivos declara el tipo de audio genérico `audio/*` y cubre explícitamente las siguientes 19 extensiones para mantener la compatibilidad con tablas MIME incompletas o antiguas de algunos dispositivos:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave, wma
```

Que una extensión sea compatible no garantiza su decodificación: la reproducción real depende de Media3, la versión de Android, los códecs del dispositivo y el contenido del archivo. Cuando un archivo no se puede reproducir, el panel de error ofrece abrirlo con otra aplicación.

### Permisos y seguridad

El complemento no solicita permiso de almacenamiento, accede a los archivos de audio estrictamente en modo de solo lectura y nunca escribe en los archivos originales; la conexión a Internet se usa únicamente para comprobaciones de actualizaciones iniciadas por el usuario o una vez al día. En Android 13+ el permiso de notificaciones es opcional; si se rechaza, solo se ocultan los controles de la notificación, sin afectar a la reproducción.

Para más explicaciones y la documentación completa, consulta la [página principal del proyecto](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player) y la [documentación de AutoJs6](https://docs.autojs6.com).
