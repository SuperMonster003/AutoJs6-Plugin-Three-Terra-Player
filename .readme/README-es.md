<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="audio-player-ic-launcher" border="0" width="128" />
  </p>

  <p>Complemento del gestor de archivos. Reproduce audio con controles integrados y en segundo plano</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Audio-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Audio-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Audio-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Idiomas (Languages)

******

El README.md actual admite los siguientes idiomas:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-fr.md)
- Español [es] # actual
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ar.md)

******

### Introducción

******

Audio Player ofrece un controlador dentro de la aplicación y un servicio privado de reproducción en segundo plano para los archivos de audio abiertos desde el gestor de archivos. También puede recibir solicitudes Android ACTION_VIEW de solo lectura para content URI con un tipo MIME de audio.

******

### Funciones

******

- Registra la acción de archivo único `play-audio` y la acción multiselección ordenada `play-audio-selection` mediante el protocolo Explorer Action v12 para las 18 extensiones de audio reconocidas por el host.
- Reproduce audio con Media3 ExoPlayer y MediaSessionService, con foco de audio, gestión de desconexión de salida, modo de activación local, reproducción en segundo plano y controles multimedia del sistema.
- Proporciona una pantalla completa con carátula, etiquetas, información técnica, arrastre de progreso, saltos de 10 segundos, modos secuencial / aleatorio / repetir una y velocidad de 0,5x a 2x.
- Reproduce hasta 128 archivos de audio seleccionados explícitamente en el orden proporcionado por el host, con anterior / siguiente y una cola para saltar o eliminar pistas.
- Incluye un temporizador gestionado por el servicio con valores predefinidos, duración personalizada, detener tras la pista actual, fundido final de cinco segundos y bucle A-B.
- Recuerda la posición de cada pista y refleja en las superficies multimedia del sistema el título, la carátula, anterior / siguiente y los saltos de 10 segundos.
- Acepta solicitudes Android ACTION_VIEW independientes para URI `content` de solo lectura con `audio/*` y descarta los extras del llamador y los permisos URI amplios.
- Ofrece una alternativa ante errores de decodificación que abre el archivo en otra aplicación compatible y excluye este plugin para evitar un bucle.
- Al abrir un único archivo desde el Explorador descubre, mediante una Host Session por solicitud, una cola acotada y ordenada de forma natural de audios hermanos legibles; la multiselección explícita conserva el orden del host.

******

### Extensiones del Explorador

******

El catálogo del Explorador solo coincide intencionadamente con estas extensiones y declara una lista de tipos MIME vacía:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave
```

La coincidencia de una extensión no garantiza la decodificación. La reproducción real depende de Media3, la plataforma Android, los codecs del dispositivo y el contenido del archivo.

******

### Comportamiento del host

******

Cuando el plugin está instalado, el gestor muestra Reproducir audio para un archivo y Reproducir audio seleccionado en la barra de multiselección. La apertura individual comienza en esa pista y puede descubrir audios hermanos directos legibles en orden natural; la multiselección explícita conserva el orden del host.

Cuando falta el plugin, esta acción no aparece. El host conserva su flujo externo ACTION_VIEW de solo lectura para archivos de audio, por lo que otra aplicación de audio instalada puede gestionar el archivo. Si no hay una aplicación externa compatible, el host no obtiene una interfaz de reproducción alternativa.

******

### Interfaz del plugin

******

El host descubre y ejecuta el plugin con las siguientes identidades:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: audio-player
engine: explorer-action
variant: default
Explorer action ids: play-audio (single) / play-audio-selection (multiple, up to 128)
Explorer protocol version: 4
Explorer MIME types: []
Android VIEW action: android.intent.action.VIEW
Android VIEW MIME type: audio/*
required host build: 5276
```

La versión 1.2.1 proporciona una acción principal de solo lectura con protocolo v12 que puede recibir una capacidad por solicitud para hermanos directos, además de una acción multiselección ordenada de solo lectura. La entrada Android independiente sigue siendo de un archivo y acepta `audio/*`. Los hosts sin la Host Session opcional conservan la reproducción del archivo seleccionado.

El descubrimiento en la misma carpeta requiere AutoJs6 6.8.0 build 5276 o posterior y Explorer Action v12; este requisito no aumentará con capacidades futuras del plugin.

******

### Seguridad

******

El plugin no solicita permisos de almacenamiento ni de acceso a Internet. Su entrada protegida por firma valida estrictamente el protocolo v12, TARGETS y ClipData ordenados, identificadores, relación con el padre, metadatos y flags de solo lectura. Una Host Session opcional queda vinculada por el host al UID del plugin y solo puede listar el padre directo del archivo seleccionado y abrir este o un hermano directo legible; los componentes de reproducción reciben rutas sintéticas opacas, nunca rutas del sistema de archivos. La entrada Android pública sigue siendo de un archivo y solo lectura.

******

### Límites de seguridad

******

- La acción individual parte exactamente de un archivo seleccionado y puede formar una cola acotada de audios hermanos directos legibles; la acción de selección acepta de 1 a 128 archivos únicos y conserva su orden.
- Se rechazan las solicitudes del Explorador con un tamaño declarado superior a 8 TiB.
- La acción del Explorador se selecciona solo por la extensión y valida un tipo MIME de audio al ejecutarse.
- El descubrimiento de hermanos no es recursivo y solo está disponible mediante la sesión por solicitud del host; el plugin nunca adivina un URI hermano ni recibe una ruta del sistema de archivos.
- La entrada Android pública requiere ACTION_VIEW, un URI `content`, `audio/*` y permiso de lectura.
- El permiso de notificaciones es opcional. Si se deniega, se ocultan los controles de la bandeja de notificaciones pero no se bloquea la reproducción.
- La reproducción finaliza de forma segura al completarse o producirse un error de decodificación. La alternativa externa transfiere solo un nuevo permiso de lectura y excluye este plugin.

******

### Historial de versiones

******

# v1.2.2

###### 2026/08/27

* `Corrección` La reproducción desde el explorador ya no falla tras actualizar el complemento cuando el gestor de archivos AutoJs6 en ejecución aún envía una acción de protocolo v4 almacenada en caché; la puerta de enlace acepta la envolvente compatible de solo lectura v4–v12 y sigue anunciando v12
* `Corrección` Las extensiones de audio anunciadas ya no se rechazan cuando una tabla MIME de Android o del fabricante devuelve un comodín o un tipo application; la lista permitida de extensiones proporciona ahora un tipo MIME de audio canónico y estable
* `Mejora` Las solicitudes del explorador rechazadas registran ahora un código de motivo respetuoso con la privacidad, sin nombres de archivo, rutas visibles ni URI, para diagnosticar directamente futuras diferencias de contrato

# v1.2.1

###### 2026/08/27

* `Función` La acción Reproducir audio para un solo archivo ahora puede descubrir hasta 128 archivos de audio legibles en la misma carpeta mediante Explorer Action v12 y crear una cola con orden natural que comienza en la pista seleccionada
* `Corrección` API 24 ya no rechaza una solicitud válida de Explorer cuando Android añade desde el manifiesto de la actividad puente la marca sin permisos FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
* `Corrección` El avance automático a una pista hermana ya no se bloquea al reconstruir el Intent de retorno de MediaSession; el objetivo autorizado originalmente permanece como ancla de la Host Session independientemente de la pista activa
* `Mejora` La pista seleccionada conserva su content URI original y las pistas hermanas solo se transmiten mediante descriptores de archivo de una Host Session limitada a la solicitud; no se adivinan URI ni se añade acceso recursivo, de escritura, almacenamiento o persistente
* `Mejora` El filtro de extensiones de audio excluye de la cola los vídeos .mp4 con el mismo nombre que audios .m4a, mientras la cola de selección múltiple explícita existente no cambia
* `Mejora` La propiedad de la Host Session se transfiere al servicio de reproducción en segundo plano y se cierra al sustituir la cola, fallar el inicio, terminar la reproducción o destruir el servicio
* `Dependencia` Se actualizó la API Explorer Action incluida del protocolo v4 a la extensión de lectura de hermanos v12 retrocompatible, manteniendo la compilación mínima 5276 del host

# v1.2.0

###### 2026/08/27

* `Función` Compatibilidad con Explorer Action v4 y una nueva acción multiselección ordenada que crea una cola con hasta 128 archivos de audio elegidos explícitamente
* `Función` Lista nativa de Media3 con anterior / siguiente, panel para saltar o eliminar pistas y modos secuencial / aleatorio / repetir una
* `Función` Temporizador gestionado por el servicio con 15 / 30 / 60 minutos, duración personalizada, detener tras la pista actual y fundido final de cinco segundos
* `Función` Bucle de intervalo A-B para repetir una sección seleccionada
* `Mejora` Los controles multimedia del sistema ahora muestran anterior, siguiente y saltos de 10 segundos, mientras metadatos y reanudación siguen la pista actual
* `Mejora` La validación del Explorador cubre TARGETS y ClipData ordenados, identificadores únicos, sesión del host y cada archivo seleccionado sin ampliar permisos de lectura
* `Mejora` Se documenta que el URI padre FileProvider no enumera hijos; la búsqueda de archivos hermanos y la conjetura de URI siguen desactivadas y la multiselección es la vía segura
* `Dependencia` API Explorer Action integrada actualizada de v2 a v4 y compilación mínima del host elevada a 5276
* `Dependencia` Añadido AndroidX RecyclerView 1.4.0

##### Para consultar más versiones

* [CHANGELOG-es.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/assets/doc/CHANGELOG-es.md)

******

### Compilación

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Compilación Release:

```powershell
.\gradlew.bat :app:assembleRelease
```

Los parámetros de compilación proceden de `version.properties`. El SDK mínimo actual es 24 y el SDK de destino es 36.

******

### Estructura de recursos

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` localiza los metadatos del plugin y el texto de la interfaz. `plugin_instruction.md` proporciona instrucciones visibles desde el host. `.python/generate_markdown.py` genera archivos README y de cambios localizados a partir de fuentes JSON.

******

### Enlaces

******

- Documentación de AutoJs6: https://docs.autojs6.com
- Android Media3: https://developer.android.com/media/media3
- Uso compartido seguro de archivos en Android: https://developer.android.com/training/secure-file-sharing
