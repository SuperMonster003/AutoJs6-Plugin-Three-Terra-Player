******

### Historial de versiones

******

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

# v1.1.0

###### 2026/08/27

* `Función` Pantalla de reproducción rediseñada con carátula del álbum, etiquetas de título / artista / álbum, barra de progreso arrastrable y tiempos de reproducción
* `Función` Controles de reproducción mejorados: retroceso y avance de 10 segundos, repetición de pista y velocidad de 0,5x a 2x
* `Función` Extracción automática de etiquetas integradas, carátula y propiedades técnicas (códec / frecuencia de muestreo / tasa de bits), sincronizadas con las notificaciones multimedia del sistema
* `Función` Memoria de posición de reproducción: al reabrir el mismo archivo se reanuda desde la última posición y la reproducción completada borra el registro
* `Mejora` La reproducción puede reiniciarse directamente en la pantalla del reproductor tras finalizar o fallar la decodificación, sin volver al gestor de archivos
* `Mejora` Los avisos de error de reproducción ahora incluyen el código de error específico para facilitar los reportes
* `Dependencia` Se añade AndroidX ConstraintLayout versión 2.2.1
* `Dependencia` Se elimina la dependencia AndroidX Media3 UI sin uso

# v1.0.1

###### 2026/08/08

* `Corrección` Enlace de servicio nulo al activar el complemento en el centro de complementos
* `Mejora` Nombre, descripción y documentación de usuario más claros y concisos

# v1.0.0

###### 2026/08/02

* `Función` Plugin Audio Player con ID de plugin `audio-player`, ID de acción `play-audio`, motor `explorer-action` y variante `default`
* `Función` Acción principal de solo lectura del gestor de archivos para las 18 extensiones de audio del host, con compilación 5269 o posterior requerida
* `Función` Reproducción mediante Media3 ExoPlayer y MediaSessionService con foco de audio, gestión de desconexión de salida, modo de activación local, segundo plano, controles multimedia del sistema e interfaz privada
* `Función` Información opcional sobre el permiso de notificaciones de Android 13+ sin bloquear la reproducción si se deniega
* `Función` Compatibilidad Android ACTION_VIEW independiente y de solo lectura para solicitudes de audio con URI `content` y transferencia a otra aplicación compatible si falla el decodificador, con prevención de bucles
* `Función` Validación estricta de protocolo, URI, ClipData, origen, nombre, MIME, tamaño y permisos, sin permisos de almacenamiento ni de acceso a Internet y con transferencia mínima de lectura
* `Función` Metadatos, texto de interfaz, instrucciones, archivos README e historiales localizados en español, francés, ruso, árabe, japonés, coreano, inglés, chino simplificado, chino tradicional de Hong Kong y chino tradicional de Taiwán
* `Dependencia` Añadido AndroidX Media3 ExoPlayer, Session y UI versión 1.10.1
