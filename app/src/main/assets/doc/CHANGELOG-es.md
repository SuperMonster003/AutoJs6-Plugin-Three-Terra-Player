******

### Historial de versiones

******

# v1.5.0

###### 2026/09/01

* `Función` La carátula del álbum ahora genera una paleta legible en tiempo real para el degradado, la barra, los controles y la cola; las pantallas se extienden de borde a borde alrededor de barras, recortes y gestos
* `Función` Los ajustes de reproducción incluyen velocidad predeterminada guardada, intervalos de retroceso y avance de 5 / 10 / 15 / 30 segundos y una acción al terminar la cola: detener, volver al principio y pausar o repetir
* `Función` Se añadieron transiciones discretas para reproducción / pausa y carátulas, además de respuesta háptica para acciones clave que respeta el sistema; al desactivar las animaciones, los estados cambian directamente
* `Corrección` Las opciones de los diálogos de idioma y modo nocturno ahora usan Material Body1 a 16sp en lugar del texto de lista del sistema, demasiado grande
* `Mejora` La decodificación de carátulas y la muestra de color de hasta 64x64 se ejecutan fuera del hilo principal; cualquier color mantiene los límites de contraste de 4,5:1 para texto y 3:1 para contornos
* `Mejora` Se completaron el estudio de visualización y un prototipo RMS por bloques: Visualizer sigue excluido por requerir permiso de grabación y se documenta una toma PCM de Media3 sin permisos para futuras pruebas de rendimiento
* `Mejora` Unificar el diseño del README y la gestión de versiones de la plataforma Gradle
* `Mejora` Simplificar la descripción del complemento y normalizar la puntuación de los recursos multilingües
* `Mejora` Renombrar la entrada de visualización externa como External Viewer para unificar la semántica del visor
* `Mejora` Abrir la página integrada del historial de versiones desde el botón correspondiente del diálogo de actualización

# v1.4.1

###### 2026/08/31

* `Función` La aplicación independiente ahora restaura la última cola, la pista actual, la posición de parada, el modo de repetición, el estado aleatorio y la velocidad al volver a abrirla desde el lanzador; la sesión restaurada permanece en pausa y solo se guardan colas del selector del sistema con acceso de lectura duradero
* `Corrección` Se corrigió el menú adicional de la esquina superior derecha del reproductor, que con algunos temas mostraba texto blanco sobre fondo blanco e impedía leer la opción Ajustes
* `Mejora` La fuente AutoJs6 del ajuste de color del tema ahora se denomina siempre `Seguir AutoJs6`; el selector muestra directamente el valor HEX del color del host
* `Mejora` Se completó una revisión manual básica de las diez traducciones del README, CHANGELOG e instrucciones del complemento, y se migraron los enlaces del proyecto y de actualización integrada al repositorio oficial de 3-Terra Player

# v1.4.0

###### 2026/08/29

* `Función` Notificación multimedia del sistema renovada por completo: nuevos botones de anterior / siguiente / alternar aleatorio y salir, con el icono monocromo propio de la aplicación, y el estado de aleatorio sincronizado en tiempo real con el reproductor
* `Corrección` Corregido que el complemento pudiera marcarse automáticamente como defectuoso y desactivarse en el centro de complementos de AutoJs6: la información del complemento y la acción del gestor de archivos ahora usan servicios independientes
* `Corrección` Vaciar la cola de reproducción ya no deja información obsoleta: la pantalla pasa a un estado vacío explícito y los controles de reproducción, búsqueda, velocidad, temporizador y A-B se desactivan a la vez
* `Corrección` Corregido el recorte de la sombra del botón de reproducción por la zona inferior; los botones de opción, las casillas, las barras de progreso y los botones de los diálogos de ajustes y de actualización ahora siguen el color del tema
* `Mejora` La aplicación y el complemento pasan a llamarse oficialmente 3-Terra Player: el ID de aplicación no cambia, la actualización se instala directamente sobre la versión anterior y los ajustes existentes se conservan
* `Mejora` El menú de la esquina superior derecha del reproductor se reduce a una sola entrada de Ajustes, eliminando el botón de paleta que duplicaba la página de ajustes

# v1.3.0

###### 2026/08/29

* `Función` Nuevo modo de aplicación independiente: sin necesidad de AutoJs6, elige de una vez hasta 128 archivos de audio desde la pantalla de inicio y reprodúcelos seguidos
* `Función` Nueva página de ajustes: idioma, modo nocturno, color del tema, reanudación de la reproducción, comprobación de actualizaciones, historial de versiones e información de la aplicación en un solo lugar; el idioma y la apariencia siguen a AutoJs6 de forma predeterminada y recurren a los valores integrados cuando el host no está disponible
* `Función` Nuevas comprobaciones de actualizaciones manuales y automáticas una vez al día, con opción de ignorar versiones concretas y un historial de versiones multilingüe integrado
* `Corrección` Corregido que, al seguir el color del tema de AutoJs6, siempre se indicara que el color del host no estaba disponible
* `Corrección` Todos los elementos que el host reconoce como audio en el gestor de archivos (ahora también WMA) pueden reproducirse directamente con este complemento
* `Mejora` La reanudación de la reproducción recuerda solo el último archivo abierto: al abrir otro archivo o terminar la reproducción, el registro anterior se borra automáticamente
* `Mejora` La pantalla de reproducción reserva un área fija de tres líneas de información y completa la frecuencia de muestreo y la tasa de bits, de modo que el diseño ya no salta al cambiar de pista ni durante la carga asíncrona
* `Mejora` El bucle A-B ahora se completa con tres toques sucesivos: fijar punto A, fijar punto B y borrar; también se mejoran el espaciado y la alineación de los iconos de los controles inferiores

# v1.2.2

###### 2026/08/27

* `Corrección` Corregido que, tras actualizar el complemento sobre la versión anterior, todos los audios del gestor de archivos indicaran una solicitud de audio no válida: la entrada ahora acepta las solicitudes antiguas de solo lectura almacenadas en caché por un host en ejecución
* `Corrección` Corregido el rechazo indebido de extensiones compatibles como `ogg` y `opus` en dispositivos con tablas MIME defectuosas: la lista de extensiones permitidas proporciona ahora tipos de audio canónicos y estables
* `Mejora` Las solicitudes rechazadas registran ahora un código de motivo respetuoso con la privacidad (sin nombres de archivo, rutas ni URI), lo que facilita informar y localizar problemas

# v1.2.1

###### 2026/08/27

* `Función` Al abrir una canción desde el gestor de archivos, se descubren automáticamente los demás audios de la misma carpeta y se reproducen seguidos por orden natural de nombre de archivo: hasta 128 pistas, empezando por la seleccionada
* `Corrección` Corregido el rechazo indebido de solicitudes de reproducción legítimas en dispositivos con Android 7.0 debido a una marca de ventana que el sistema añade automáticamente
* `Corrección` Corregido un posible cierre inesperado al pasar automáticamente a una pista de la misma carpeta
* `Mejora` El descubrimiento en la misma carpeta es siempre de solo lectura y acotado: sin recursión en subcarpetas, sin adivinar ubicaciones de archivos, sin permiso de almacenamiento, y los vídeos con el mismo nombre que un audio nunca entran en la cola
* `Mejora` La sesión de lectura de la misma carpeta pasa a manos del servicio de reproducción en segundo plano y se cierra automáticamente al sustituir la cola, terminar la reproducción o destruirse el servicio
* `Dependencia` La interfaz de acciones del gestor de archivos integrada se actualiza a la v12 retrocompatible; la versión mínima del host se mantiene en build 5276

# v1.2.0

###### 2026/08/27

* `Función` Nueva reproducción con selección múltiple: marca hasta 128 audios en el gestor de archivos y añádelos a la cola con un toque, manteniendo estrictamente el orden en que se marcaron
* `Función` Nuevos controles de anterior / siguiente, un panel de cola para saltar a pistas o quitarlas, y modos secuencial / aleatorio / repetir una
* `Función` Nuevo temporizador de apagado: valores predefinidos de 15 / 30 / 60 minutos o duración personalizada, con opción de detenerse al terminar la pista actual y fundido en los últimos 5 segundos
* `Función` Nuevo bucle de intervalo A-B para escuchar repetidamente un fragmento elegido
* `Mejora` Los controles multimedia del sistema añaden los comandos de anterior, siguiente y retroceso / avance de 10 segundos, y los metadatos de la notificación se actualizan en tiempo real al avanzar la cola
* `Mejora` La validación de solicitudes del gestor de archivos se extiende a los destinos de selección múltiple ordenada, manteniendo autorizaciones mínimas de solo lectura
* `Dependencia` La interfaz de acciones del gestor de archivos se actualiza de v2 a v4; la versión mínima del host sube a build 5276
* `Dependencia` Se añade AndroidX RecyclerView 1.4.0

# v1.1.0

###### 2026/08/27

* `Función` Pantalla de reproducción totalmente nueva: carátula del álbum, etiquetas de título / artista / álbum, información técnica y barra de progreso arrastrable
* `Función` Se añaden el retroceso y avance de 10 segundos, un interruptor de repetir la pista actual y velocidad de reproducción de 0,5x a 2x
* `Función` Las etiquetas y la carátula integradas en el audio se leen automáticamente y se sincronizan con la notificación multimedia del sistema
* `Función` Nueva reanudación de la reproducción: al reabrir el mismo archivo se continúa desde la última posición, y el registro se borra automáticamente al completarse la reproducción
* `Mejora` Tras terminar la reproducción o fallar la decodificación, la pista puede reiniciarse directamente en la pantalla del reproductor, sin volver al gestor de archivos
* `Mejora` Los avisos de error de reproducción incluyen el código de error concreto, lo que facilita diagnosticar e informar
* `Dependencia` Se añade AndroidX ConstraintLayout 2.2.1
* `Dependencia` Se elimina la dependencia AndroidX Media3 UI, que ya no se usa

# v1.0.1

###### 2026/08/08

* `Corrección` Corregido un enlace de servicio vacío al activar el complemento en el centro de complementos
* `Mejora` El nombre, la descripción y las instrucciones del complemento son ahora más concisos y naturales

# v1.0.0

###### 2026/08/02

* `Función` Primera versión estable: la acción Reproducir audio para el gestor de archivos de AutoJs6, con 18 extensiones de audio comunes y reproducción con un solo toque
* `Función` Reproducción sólida basada en Media3 ExoPlayer y MediaSessionService: reproducción en segundo plano, gestión del foco de audio, pausa automática al desconectarse el dispositivo de salida y controles multimedia del sistema
* `Función` Acepta solicitudes de solo lectura de otras aplicaciones para abrir audio; si falla la decodificación, el archivo puede pasarse a otra aplicación compatible evitando bucles
* `Función` Base de seguridad estricta: sin permisos de almacenamiento ni de Internet, aceptando y transmitiendo solo autorizaciones mínimas de solo lectura
* `Función` Información del complemento, instrucciones, README e historial de cambios disponibles en 10 idiomas
* `Dependencia` Se añaden AndroidX Media3 ExoPlayer, Session y UI 1.10.1
