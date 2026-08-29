<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <h1>3-Terra Player</h1>

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

3-Terra Player ofrece un controlador dentro de la aplicación y un servicio privado de reproducción en segundo plano para los archivos de audio abiertos desde el gestor de archivos. También puede recibir solicitudes Android ACTION_VIEW de solo lectura para content URI con un tipo MIME de audio.

******

### Funciones

******

- Registra las acciones `play-audio` y `play-audio-selection` mediante Explorer Action v12 para todos los tipos MIME de audio reconocidos por el host y 19 extensiones conocidas.
- Reproduce audio con Media3 ExoPlayer y MediaSessionService, con foco de audio, gestión de desconexión de salida, modo de activación local, reproducción en segundo plano y controles multimedia del sistema.
- Proporciona una pantalla completa con carátula, etiquetas, información técnica, arrastre de progreso, saltos de 10 segundos, modos secuencial / aleatorio / repetir una y velocidad de 0,5x a 2x.
- Reproduce hasta 128 archivos de audio seleccionados explícitamente en el orden proporcionado por el host, con anterior / siguiente y una cola para saltar o eliminar pistas.
- Incluye un temporizador gestionado por el servicio con valores predefinidos, duración personalizada, detener tras la pista actual, fundido final de cinco segundos y bucle A-B.
- Recuerda solo el último archivo abierto y su posición, sustituye el registro al abrir otro y refleja los metadatos y controles actuales en el sistema.
- Ofrece un lanzador para seleccionar hasta 128 documentos y acepta URI `content` ACTION_VIEW de solo lectura, incluidos alias MIME antiguos de WMA.
- Ofrece una alternativa ante errores de decodificación que abre el archivo en otra aplicación compatible y excluye este plugin para evitar un bucle.
- Al abrir un único archivo desde el Explorador descubre, mediante una Host Session por solicitud, una cola acotada y ordenada de forma natural de audios hermanos legibles; la multiselección explícita conserva el orden del host.
- Genera una paleta clara y oscura accesible a partir de un color de origen, sigue AutoJs6 de forma predeterminada y ofrece 19 preajustes Material 500 y colores RGB personalizados; los ajustes propios incluyen idioma, noche, reanudación, actualizaciones, historial e información.

******

### Extensiones del Explorador

******

El catálogo anuncia `audio/*` y usa también estas extensiones para tablas MIME antiguas o incompletas:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave, wma
```

La coincidencia de una extensión no garantiza la decodificación. La reproducción real depende de Media3, la plataforma Android, los codecs del dispositivo y el contenido del archivo.

******

### Comportamiento del host

******

Cuando el plugin está instalado y activado en AutoJs6, el gestor muestra Reproducir audio para un archivo y Reproducir audio seleccionado en la barra de multiselección. La apertura individual comienza en esa pista y puede descubrir audios hermanos directos legibles en orden natural; la multiselección explícita conserva el orden del host.

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
Explorer protocol version: 12 (accepts compatible read-only v4–v12 requests)
Explorer MIME types: [audio/*]
Android VIEW action: android.intent.action.VIEW
Android VIEW MIME type: audio/* plus legacy WMA MIME aliases
required host build: 5276
```

La versión 1.4.0 ofrece acciones v12 de solo lectura, acceso limitado a hermanos, selector independiente de documentos y entradas Android de audio / WMA. Los hosts sin Host Session opcional reproducen solo el archivo seleccionado.

El descubrimiento en la misma carpeta requiere AutoJs6 6.8.0 build 5276 o posterior y Explorer Action v12; este requisito no aumentará con capacidades futuras del plugin.

******

### Seguridad

******

El plugin no solicita almacenamiento ni escribe archivos. Internet se usa solo para comprobaciones de versiones de GitHub manuales o diarias. La entrada firmada valida estrictamente v12, TARGETS, ClipData, metadatos y lectura; Host Session permanece vinculada al UID y no recursiva, y las entradas públicas conservan permisos de solo lectura.

******

### Límites de seguridad

******

- La acción individual parte exactamente de un archivo seleccionado y puede formar una cola acotada de audios hermanos directos legibles; la acción de selección acepta de 1 a 128 archivos únicos y conserva su orden.
- Se rechazan las solicitudes del Explorador con un tamaño declarado superior a 8 TiB.
- Las acciones coinciden por MIME de audio reconocido o por la lista de extensiones y normalizan y validan cada objetivo.
- El descubrimiento de hermanos no es recursivo y solo está disponible mediante la sesión por solicitud del host; el plugin nunca adivina un URI hermano ni recibe una ruta del sistema de archivos.
- La entrada Android pública requiere ACTION_VIEW, un URI `content`, un MIME de audio o WMA compatible y permiso de lectura.
- El permiso de notificaciones es opcional. Si se deniega, se ocultan los controles de la bandeja de notificaciones pero no se bloquea la reproducción.
- La reproducción finaliza de forma segura al completarse o producirse un error de decodificación. La alternativa externa transfiere solo un nuevo permiso de lectura y excluye este plugin.

******

### Historial de versiones

******

# v1.4.0

###### 2026/08/29

* `Función` Las notificaciones multimedia ahora usan acciones dedicadas para anterior, siguiente, alternar aleatorio y salir, además del icono monocromático transparente de la aplicación; la iluminación de aleatorio se mantiene sincronizada con la reproducción
* `Corrección` AutoJs6 ya no marca ni desactiva el plugin como erróneo al descubrir la información del plugin y Explorer Action, porque los dos protocolos Binder ahora usan servicios independientes
* `Corrección` Vaciar la cola ahora elimina los metadatos obsoletos y la sesión en segundo plano, desactiva reproducción, búsqueda, cola, velocidad, temporizador y A-B, y muestra un estado vacío explícito en vez de aceptar pulsaciones sin efecto
* `Corrección` La sombra del botón principal ya no queda recortada por la zona inferior y el estado desactivado usa colores claramente atenuados; los controles de radio, selección múltiple, progreso y botones de los diálogos ahora siguen el tema dinámico
* `Mejora` La aplicación y el plugin pasan a llamarse con el texto no traducible 3-Terra Player, y los espacios de nombres y símbolos del código usan las formas three / Three, mientras se conserva el ID de aplicación publicado para actualizaciones y ajustes existentes
* `Mejora` El acceso directo de paleta de la barra del reproductor se sustituyó por un menú de tres puntos con solo Ajustes, manteniendo el espaciado establecido bajo los controles de reproducción

# v1.3.0

###### 2026/08/29

* `Función` Se añadió una pantalla de inicio y un reproductor independiente para varios archivos, además de ajustes propios de idioma, modo nocturno, color, reanudación, actualizaciones, historial e información de la aplicación y el desarrollador
* `Función` El idioma, el modo nocturno y el color siguen AutoJs6 de forma predeterminada mediante su contrato oficial de solo lectura; si el host no está disponible, las opciones siguen visibles pero deshabilitadas y usan los valores predeterminados
* `Función` Se añadieron comprobaciones manuales y automáticas diarias, gestión de versiones ignoradas e historial localizado integrado
* `Corrección` Se corrigió el aviso permanente Host color unavailable al exponer la entrada protegida de información del plugin que requiere el proveedor de ajustes del host
* `Corrección` Las acciones del Explorador anuncian tipos MIME de audio además de 19 extensiones conocidas, incluida WMA, para abrir directamente en el plugin todo audio reconocido por el host
* `Mejora` La reanudación recuerda exactamente el último archivo abierto, descarta de inmediato el anterior al abrir otro y nunca conserva una reproducción terminada
* `Mejora` El área de metadatos reserva tres líneas estables y completa frecuencia y tasa de bits con la pista seleccionada, en el orden 44.1 kHz · MP3 · 128 kbps
* `Mejora` A-B usa ahora un ciclo claro de tres pulsaciones para fijar A, fijar B y borrar; los controles inferiores tienen más espacio e iconos centrados y del mismo tamaño

# v1.2.2

###### 2026/08/27

* `Corrección` La reproducción desde el explorador ya no falla tras actualizar el complemento cuando el gestor de archivos AutoJs6 en ejecución aún envía una acción de protocolo v4 almacenada en caché; la puerta de enlace acepta la envolvente compatible de solo lectura v4–v12 y sigue anunciando v12
* `Corrección` Las extensiones de audio anunciadas ya no se rechazan cuando una tabla MIME de Android o del fabricante devuelve un comodín o un tipo application; la lista permitida de extensiones proporciona ahora un tipo MIME de audio canónico y estable
* `Mejora` Las solicitudes del explorador rechazadas registran ahora un código de motivo respetuoso con la privacidad, sin nombres de archivo, rutas visibles ni URI, para diagnosticar directamente futuras diferencias de contrato

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
