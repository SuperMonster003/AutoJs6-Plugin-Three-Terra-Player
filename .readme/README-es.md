<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="autojs6-plugin-audio-player-ic-launcher" border="0" width="128" />
  </p>

  <p>Reproducción de audio de solo lectura para el Explorador de AutoJs6 con controles Media3 en segundo plano</p>

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

El plugin AutoJs6 Audio Player añade un controlador de audio dentro de la aplicación y un servicio privado de reproducción en segundo plano para los archivos abiertos desde el Explorador de AutoJs6. También puede recibir solicitudes Android ACTION_VIEW de solo lectura para content URI con un tipo MIME de audio.

******

### Funciones

******

- Registra la acción principal de solo lectura del Explorador `play-audio` mediante el protocolo Explorer Action v2 para las 18 extensiones de audio que el host ya reconoce.
- Reproduce audio con Media3 ExoPlayer y MediaSessionService, con foco de audio, gestión de desconexión de salida, modo de activación local, reproducción en segundo plano y controles multimedia del sistema.
- Proporciona una pantalla de control con metadatos del título, controles de reproducción, información opcional sobre el permiso de notificaciones en Android 13+ y avisos de error.
- Acepta solicitudes Android ACTION_VIEW independientes para URI `content` de solo lectura con `audio/*` y descarta los extras del llamador y los permisos URI amplios.
- Ofrece una alternativa ante errores de decodificación que abre el archivo en otra aplicación compatible y excluye este plugin para evitar un bucle.

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

Cuando el plugin está instalado, el Explorador de AutoJs6 muestra Reproducir audio como acción principal para las extensiones indicadas. Al seleccionarla, se abre el controlador del plugin y se inicia el servicio privado con acceso temporal de solo lectura al archivo seleccionado.

Cuando falta el plugin, esta acción no aparece. AutoJs6 conserva su flujo externo ACTION_VIEW de solo lectura para archivos de audio, por lo que otra aplicación de audio instalada puede gestionar el archivo. Si no hay una aplicación externa compatible, el host no obtiene una interfaz de reproducción alternativa.

******

### Interfaz del plugin

******

AutoJs6 descubre y ejecuta el plugin con las siguientes identidades:

```text
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: audio-player
engine: explorer-action
variant: default
Explorer action id: play-audio
Explorer protocol version: 2
Explorer MIME types: []
Android VIEW action: android.intent.action.VIEW
Android VIEW MIME type: audio/*
required host build: 5269
supported ABIs: []
```

La versión 1 proporciona una acción principal de solo lectura para un único archivo en el Explorador principal de AutoJs6. El catálogo usa solo las extensiones, mientras que la entrada Android independiente sigue aceptando `audio/*`.

El plugin no contiene bibliotecas nativas. Declara `supportedAbis = emptyArray()` y se publica como un único APK independiente de ABI. Requiere la compilación 5269 o posterior del host AutoJs6.

******

### Seguridad

******

El plugin no solicita permisos de almacenamiento ni de red. Su entrada del Explorador está protegida por el permiso de firma de AutoJs6 y valida estrictamente el protocolo v2, los content URI de destino y padre, ClipData, la superficie de origen, el nombre visible, el tipo MIME, el tamaño declarado y los indicadores de solo lectura. Solo transfiere el URI de destino y un permiso de lectura a los componentes privados. La entrada Android pública solo acepta solicitudes content de audio de solo lectura, rechaza permisos de escritura, persistentes y de prefijo, y nunca transfiere extras arbitrarios.

******

### Límites de seguridad

******

- Un archivo de destino por acción del Explorador.
- Se rechazan las solicitudes del Explorador con un tamaño declarado superior a 8 TiB.
- La acción del Explorador se selecciona solo por la extensión y valida un tipo MIME de audio al ejecutarse.
- La entrada Android pública requiere ACTION_VIEW, un URI `content`, `audio/*` y permiso de lectura.
- El permiso de notificaciones es opcional. Si se deniega, se ocultan los controles de la bandeja de notificaciones pero no se bloquea la reproducción.
- La reproducción finaliza de forma segura al completarse o producirse un error de decodificación. La alternativa externa transfiere solo un nuevo permiso de lectura y excluye este plugin.

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
