<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <h1>3-Terra Player</h1>

  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="three-terra-player-ic-launcher" border="0" width="128" />
    </picture>
  </p>

  <p>Complemento de reproducción de audio y aplicación independiente. Reproduce audio directamente desde el gestor de archivos de AutoJs6 o por sí solo, con reproducción en segundo plano</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/commit/ff025baf3ad619805b2ebec12c738a32861e524b"><img alt="Created" src="https://img.shields.io/date/1785664006?color=2e7d32&label=Created"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Idiomas (Languages)

******

El README.md actual admite los siguientes idiomas:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-fr.md)
- Español [es] # actual
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ar.md)

******

### Introducción

******

3-Terra Player (antes Audio Player) es a la vez un complemento de reproducción de audio para el gestor de archivos de AutoJs6 y un reproductor de audio independiente y sencillo. Una vez instalado y activado, basta con tocar cualquier archivo de audio en el gestor de archivos de AutoJs6 para reproducirlo directamente, sin necesidad de un reproductor de terceros; también puede abrirse desde el lanzador como una aplicación normal para elegir varios archivos de audio y reproducirlos seguidos.

Tras la instalación, AutoJs6 descubre el complemento automáticamente, sin configuración alguna. La reproducción se basa en Media3 (ExoPlayer), el marco multimedia oficial de Android, con reproducción en segundo plano y controles en la notificación multimedia del sistema; el acceso a los archivos de audio es estrictamente de solo lectura, no se solicita permiso de almacenamiento y los archivos originales nunca se modifican ni se eliminan.

******

### Funciones destacadas

******

- Reproducción con un toque en el gestor de archivos: toca cualquier archivo de audio en el gestor de archivos de AutoJs6 para empezar a reproducirlo.
- Reproducción continua de la misma carpeta: al abrir una pista se descubren los demás audios de la carpeta y se ponen en cola por orden natural de nombre de archivo (hasta 128 pistas, empezando por la seleccionada).
- Cola por selección múltiple: marca hasta 128 archivos de audio en el gestor de archivos y reprodúcelos exactamente en el orden en que los marcaste.
- Pantalla de reproducción completa: carátula del álbum, etiquetas de título / artista / álbum, información técnica como frecuencia de muestreo y tasa de bits, y una barra de progreso arrastrable.
- Todos los controles habituales: anterior / siguiente, retroceso y avance de 10 segundos, modos secuencial / aleatorio / repetir una y velocidad de reproducción de 0,5x a 2x.
- Panel de cola de reproducción: consulta las próximas pistas en cualquier momento, toca para saltar a una pista o quitarla, con la pista actual claramente señalada.
- Temporizador de apagado: valores predefinidos de 15 / 30 / 60 minutos o una duración personalizada, con opción de detenerse al terminar la pista actual y un fundido de volumen en los últimos 5 segundos.
- Bucle A-B: repite cualquier fragmento una y otra vez, ideal para practicar la comprensión auditiva o sacar música de oído.
- Reproducción en segundo plano: el audio no se interrumpe al salir de la pantalla o bloquear el dispositivo, y puede controlarse desde la notificación multimedia del sistema y la pantalla de bloqueo.
- Restauración de sesión: al reabrir la aplicación independiente desde el lanzador se restauran la última cola, la pista actual, la posición de parada, el estado de repetición / aleatorio y la velocidad; la sesión restaurada permanece en pausa.
- Modo de aplicación independiente: funciona sin AutoJs6; elige hasta 128 archivos de audio desde la pantalla de inicio y reprodúcelos.
- Apariencia personalizable: el idioma, el modo nocturno y el color del tema siguen a AutoJs6 de forma predeterminada, o elige entre 19 colores predefinidos y RGB personalizado.

******

### Uso

******

1. Descarga el APK más reciente del complemento desde la página de [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases) e instálalo en el dispositivo donde se ejecuta AutoJs6.
2. Abre el centro de complementos de AutoJs6 y comprueba que `3-Terra Player` aparece reconocido y activado.
3. En el gestor de archivos de AutoJs6, toca cualquier archivo de audio y elige `Reproducir audio`, o mantén pulsado para seleccionar varios archivos y elige `Reproducir audios seleccionados` en la barra de herramientas.
4. También puedes abrir `3-Terra Player` directamente desde el lanzador y usar el selector de archivos para elegir varios audios y reproducirlos.

> Si el complemento no aparece en el centro de complementos, actualiza primero AutoJs6 a la versión 6.8.0 (código de versión 5276) o posterior. El complemento es compatible con dispositivos Android 7.0 (API 24) o superiores, y el modo de aplicación independiente no depende de AutoJs6.

******

### Formatos de audio compatibles

******

La entrada del gestor de archivos declara el tipo de audio genérico `audio/*` y cubre explícitamente las siguientes 19 extensiones para mantener la compatibilidad con tablas MIME incompletas o antiguas de algunos dispositivos:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave, wma
```

Que una extensión sea compatible no garantiza su decodificación: la reproducción real depende de Media3, la versión de Android, los códecs del dispositivo y el contenido del archivo. Cuando un archivo no se puede reproducir, el panel de error ofrece abrirlo con otra aplicación.

******

### Preguntas frecuentes

******

#### No aparece el botón `Reproducir audio` en el gestor de archivos?

Comprueba tres cosas en este orden: que el complemento esté instalado, que esté activado en el centro de complementos de AutoJs6 y que AutoJs6 sea la versión 6.8.0 (código de versión 5276) o posterior. Cuando se cumplen las tres, los archivos de audio del gestor de archivos muestran la entrada de reproducción.

#### Solo toqué una canción, por qué aparecen en la cola otras canciones de la misma carpeta?

Es la reproducción continua de la misma carpeta: al abrir un solo audio, el complemento descubre los demás audios de la carpeta a través de una sesión gestionada por el host y los pone en cola por orden natural de nombre de archivo para escucharlos seguidos. El descubrimiento es totalmente de solo lectura y nunca entra en subcarpetas; si la versión del host no admite esta capacidad, solo se reproduce la pista seleccionada.

#### Qué orden sigue la reproducción con selección múltiple?

Exactamente el orden en que marcaste los archivos. Para escuchar en un orden concreto, marca los archivos en ese orden; ya en el reproductor también puedes saltar a una pista o quitarla desde el panel de cola.

#### La reproducción continúa al salir de la interfaz o apagar la pantalla?

Sí. La reproducción corre a cargo de un servicio en segundo plano y puede controlarse desde la notificación multimedia del sistema y la pantalla de bloqueo. En Android 13 y versiones posteriores el permiso de notificaciones es opcional: si se rechaza, solo se ocultan los controles de la notificación, sin afectar a la reproducción.

#### El complemento modifica o sube mis archivos de audio?

No. El complemento no solicita permiso de almacenamiento y accede a los archivos de audio estrictamente en modo de solo lectura; la conexión a Internet se usa únicamente para buscar nuevas versiones en GitHub (a petición del usuario o como máximo una vez al día) y nunca se suben archivos ni datos personales.

#### Por qué algunos archivos no suenan o indican un error de decodificación?

Que una extensión sea compatible no significa que el dispositivo pueda decodificar el archivo; algunos códecs poco comunes o archivos dañados pueden fallar. Ante un error, el panel muestra el código exacto y ofrece abrir el archivo con otra aplicación (excluyendo este complemento para evitar bucles).

#### Cómo hago que la interfaz del complemento siga el idioma y el tema de AutoJs6?

Los sigue de forma predeterminada: el idioma, el modo nocturno y el color del tema se sincronizan automáticamente a través de la interfaz oficial de ajustes de solo lectura de AutoJs6. También puedes fijar un idioma concreto o elegir tus propios colores en Ajustes; sin AutoJs6 instalado, se recurre a la apariencia del sistema y a los valores predeterminados integrados.

#### Dónde están el temporizador de apagado y el bucle A-B?

Ambos están en la barra de herramientas inferior de la pantalla de reproducción. El icono del temporizador ofrece valores predefinidos o una duración personalizada; el botón A-B alterna entre 'fijar punto A, fijar punto B, borrar' con toques sucesivos.

******

### Permisos y seguridad

******

Los archivos de audio pueden proceder de fuentes no confiables, por lo que el flujo de reproducción cuenta desde el diseño con varias líneas de defensa:

- Cero permisos de almacenamiento: el complemento no solicita (ni puede obtener) acceso de lectura o escritura al almacenamiento del dispositivo; solo puede acceder a los archivos concretos autorizados explícitamente por el host o el sistema.
- Nunca escribe: los archivos de audio se abren en modo de solo lectura y nunca se modifican, se mueven ni se eliminan.
- Validación estricta: la entrada del gestor de archivos está protegida por el permiso de firma de AutoJs6, y en cada solicitud de reproducción se verifican uno por uno la versión del protocolo, la lista de destinos y las autorizaciones de solo lectura; las solicitudes no conformes se rechazan directamente.
- Descubrimiento acotado: la reproducción continua de la misma carpeta solo lee a través de una sesión gestionada por el host y limitada a la solicitud, no entra en subcarpetas, no adivina ubicaciones de archivos, y la sesión se cierra al terminar la reproducción.
- Solo sesión local: al activar la reanudación, el almacenamiento privado de la aplicación conserva únicamente la última cola independiente del selector y su estado; solo admite URI del selector del sistema con acceso de lectura duradero, nunca guarda rutas Host Session y al desactivar el ajuste borra el registro de inmediato.
- Red mínima: el permiso de Internet se usa solo para las comprobaciones de versiones en GitHub iniciadas por el usuario o una vez al día, nunca para contenido de audio ni datos de uso.
- Notificaciones opcionales: en Android 13+ el permiso de notificaciones es opcional; si se rechaza, solo se ocultan los controles de la notificación y la reproducción no se ve afectada.

Instala el complemento únicamente desde la página oficial de [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases) u otros canales de confianza; un paquete de origen desconocido puede haber sido manipulado aunque su nombre y versión parezcan idénticos.

******

### Interfaz del complemento

******

La siguiente información está dirigida a los desarrolladores del host AutoJs6 y de complementos; el host usa estos identificadores para descubrir el complemento y negociar capacidades:

```text
application id: io.github.supermonster003.autojs6.plugin.audioplayer
service action: org.autojs.plugin.EXPLORER_ACTION
execute action: org.autojs.plugin.EXPLORER_ACTION_EXECUTE
plugin id: audio-player
engine: explorer-action
variant: default
Explorer action ids: play-audio (single) / play-audio-selection (multiple, up to 128)
Explorer protocol version: 12 (accepts compatible read-only v4-v12 requests)
Explorer MIME types: [audio/*]
Android VIEW action: android.intent.action.VIEW
Android VIEW MIME type: audio/* plus legacy WMA MIME aliases
required host build: 5276
```

La versión actual ofrece acciones de solo lectura del protocolo v12 para un solo archivo y para selección múltiple ordenada, descubrimiento en la misma carpeta limitado a cada solicitud, un selector de documentos independiente y una entrada de audio Android pública de solo lectura (incluidos los alias MIME antiguos de WMA). Los hosts sin la capacidad opcional Host Session siguen reproduciendo solo el archivo seleccionado.

El descubrimiento en la misma carpeta requiere AutoJs6 6.8.0 (código de versión 5276) o posterior con Explorer Action v12; las futuras capacidades del complemento no elevarán este requisito. Desde la v1.4.0 el producto se llama 3-Terra Player, mientras que el ID de aplicación se mantiene como `io.github.supermonster003.autojs6.plugin.audioplayer`, por lo que la actualización se instala sobre la versión existente.

******

### Hoja de ruta

******

Las capacidades planificadas y su progreso se mantienen como una lista de casillas marcables en Roadmap.md, organizada por hitos y con criterios de aceptación, que abarca letras de canciones, ecualizador, colores basados en la carátula, interfaz de borde a borde y calidad de ingeniería. Los elementos sin marcar son intenciones de planificación y no funciones de la versión actual; puedes sumarte a la conversación a través de Issues.

- [Ver Roadmap.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/Roadmap.md)

******

### Historial de versiones

******

#### v1.4.1

_2026/08/31_

- `Función` La aplicación independiente ahora restaura la última cola, la pista actual, la posición de parada, el modo de repetición, el estado aleatorio y la velocidad al volver a abrirla desde el lanzador; la sesión restaurada permanece en pausa y solo se guardan colas del selector del sistema con acceso de lectura duradero
- `Corrección` Se corrigió el menú adicional de la esquina superior derecha del reproductor, que con algunos temas mostraba texto blanco sobre fondo blanco e impedía leer la opción Ajustes
- `Mejora` La fuente AutoJs6 del ajuste de color del tema ahora se denomina siempre `Seguir AutoJs6`; el selector muestra directamente el valor HEX del color del host
- `Mejora` Se completó una revisión manual básica de las diez traducciones del README, CHANGELOG e instrucciones del complemento, y se migraron los enlaces del proyecto y de actualización integrada al repositorio oficial de 3-Terra Player

#### v1.4.0

_2026/08/29_

- `Función` Notificación multimedia del sistema renovada por completo: nuevos botones de anterior / siguiente / alternar aleatorio y salir, con el icono monocromo propio de la aplicación, y el estado de aleatorio sincronizado en tiempo real con el reproductor
- `Corrección` Corregido que el complemento pudiera marcarse automáticamente como defectuoso y desactivarse en el centro de complementos de AutoJs6: la información del complemento y la acción del gestor de archivos ahora usan servicios independientes
- `Corrección` Vaciar la cola de reproducción ya no deja información obsoleta: la pantalla pasa a un estado vacío explícito y los controles de reproducción, búsqueda, velocidad, temporizador y A-B se desactivan a la vez
- `Corrección` Corregido el recorte de la sombra del botón de reproducción por la zona inferior; los botones de opción, las casillas, las barras de progreso y los botones de los diálogos de ajustes y de actualización ahora siguen el color del tema
- `Mejora` La aplicación y el complemento pasan a llamarse oficialmente 3-Terra Player: el ID de aplicación no cambia, la actualización se instala directamente sobre la versión anterior y los ajustes existentes se conservan
- `Mejora` El menú de la esquina superior derecha del reproductor se reduce a una sola entrada de Ajustes, eliminando el botón de paleta que duplicaba la página de ajustes

#### v1.3.0

_2026/08/29_

- `Función` Nuevo modo de aplicación independiente: sin necesidad de AutoJs6, elige de una vez hasta 128 archivos de audio desde la pantalla de inicio y reprodúcelos seguidos
- `Función` Nueva página de ajustes: idioma, modo nocturno, color del tema, reanudación de la reproducción, comprobación de actualizaciones, historial de versiones e información de la aplicación en un solo lugar; el idioma y la apariencia siguen a AutoJs6 de forma predeterminada y recurren a los valores integrados cuando el host no está disponible
- `Función` Nuevas comprobaciones de actualizaciones manuales y automáticas una vez al día, con opción de ignorar versiones concretas y un historial de versiones multilingüe integrado
- `Corrección` Corregido que, al seguir el color del tema de AutoJs6, siempre se indicara que el color del host no estaba disponible
- `Corrección` Todos los elementos que el host reconoce como audio en el gestor de archivos (ahora también WMA) pueden reproducirse directamente con este complemento
- `Mejora` La reanudación de la reproducción recuerda solo el último archivo abierto: al abrir otro archivo o terminar la reproducción, el registro anterior se borra automáticamente
- `Mejora` La pantalla de reproducción reserva un área fija de tres líneas de información y completa la frecuencia de muestreo y la tasa de bits, de modo que el diseño ya no salta al cambiar de pista ni durante la carga asíncrona
- `Mejora` El bucle A-B ahora se completa con tres toques sucesivos: fijar punto A, fijar punto B y borrar; también se mejoran el espaciado y la alineación de los iconos de los controles inferiores

##### Para más historial de versiones, consulta

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/app/src/main/assets/doc/CHANGELOG-es.md)

******

### Compilación

******

Esta sección está dirigida a los desarrolladores que deseen compilar el complemento a partir del código fuente.

Compilar un APK debug:

```powershell
.\gradlew.bat :app:assembleDebug
```

Compilar un APK release (se firma automáticamente una vez configurada la firma en `sign.properties`, un archivo que no entra en el control de versiones):

```powershell
.\gradlew.bat :app:assembleRelease
```

Para archivar una publicación, ejecuta la tarea `:app:appendDigestToReleasedFiles`, que copia los APK firmados en `releases/` añadiendo al nombre del archivo la versión y un resumen CRC32.

Los parámetros de compilación se centralizan en `version.properties`: SDK mínimo 24 (Android 7.0), SDK de destino 36, versión actual 1.4.1.

******

### Localización y generación de documentación

******

```text
.readme/common.json
.readme/lang_*.json
.readme/template_readme.md
.readme/template_plugin_instruction.md
.changelog/lang_*.json
.changelog/template_changelog.md
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` localiza los metadatos del complemento y el texto de la interfaz, y `plugin_instruction.md` proporciona las instrucciones de uso que muestra el centro de complementos del host. El README, el historial de cambios y las instrucciones se generan a partir de fuentes JSON: edita las fuentes de `.readme/` y `.changelog/` y ejecuta `py .python/generate_markdown.py` para regenerar todos los artefactos (los archivos generados no se editan a mano); ejecuta `py .python/generate_markdown.py --check` para verificar que fuentes y artefactos están sincronizados.

******

### Licencia

******

El código del proyecto es de código abierto bajo la [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/LICENSE). La reproducción de audio se basa en [AndroidX Media3](https://developer.android.com/media/media3) (Apache License 2.0).

******

### Enlaces

******

- Documentación de AutoJs6: https://docs.autojs6.com
- Android Media3: https://developer.android.com/media/media3
- Uso compartido seguro de archivos en Android: https://developer.android.com/training/secure-file-sharing
