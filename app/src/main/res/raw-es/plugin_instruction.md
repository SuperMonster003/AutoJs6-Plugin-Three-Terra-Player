3-Terra Player es el plugin de reproducción de audio del gestor de archivos de AutoJs6 y también funciona como reproductor de audio local independiente. Toca un archivo de audio en el gestor de archivos para reproducirlo; admite la reproducción continua de una carpeta, listas de reproducción locales, un temporizador de apagado, bucles A-B y reproducción en segundo plano, sin modificar nunca los archivos de origen.

### Primeros pasos

1. Comprueba que este plugin esté activado en el "Centro de plugins" de AutoJs6.
2. En el gestor de archivos de AutoJs6, toca un archivo de audio y elige "Reproducir audio"; el reproductor se abre y empieza a reproducir.
3. Para reproducir varios archivos a la vez, mantén pulsado para seleccionarlos y elige "Reproducir audios seleccionados" en la barra de herramientas.
4. Para usarlo sin AutoJs6, abre este plugin desde su icono y toca "Abrir archivos de audio".

### Funciones principales

- **Reproducción continua**: Al abrir un archivo de audio, los demás audios de la misma carpeta se ponen en cola por nombre de archivo, hasta 128; salta a una pista o quítala en "Cola de reproducción".
- **Reproducir seleccionados**: Selecciona varios audios en el gestor de archivos y elige "Reproducir audios seleccionados" para reproducir hasta 128 en el orden de selección.
- **Listas de reproducción locales**: Abre listas M3U/M3U8, PLS, XSPF y otras de hasta 128 elementos; guarda los audios en la carpeta de la lista y, al abrir desde el icono, elige esa carpeta cuando se te pida.
- **Controles de reproducción**: Pista anterior y siguiente, desplazamiento, retroceso y avance; alterna entre "Reproducción secuencial", "Reproducción aleatoria" y "Repetir pista actual", y elige de 0,5x a 2x en "Velocidad de reproducción".
- **Temporizador de apagado**: En "Temporizador de apagado", elige una duración o "Detener al terminar la pista actual"; el volumen baja poco a poco justo antes de parar.
- **Bucle A-B**: Toca "Bucle A-B" una vez para fijar el inicio y otra para fijar el final, y ese tramo se repite; un tercer toque lo borra.
- **Reproducción en segundo plano**: La reproducción sigue al salir del reproductor o bloquear la pantalla, con controles en la notificación multimedia y en la pantalla de bloqueo.
- **Reanudar reproducción**: Al volver a abrir desde el icono se restauran la última cola, la pista y la posición, en pausa; puedes desactivar "Reanudar reproducción" en "Ajustes".
- **Apariencia y ajustes**: El idioma, el modo nocturno y el "Color del tema" siguen a AutoJs6 de forma predeterminada; en "Ajustes" también puedes cambiar el "Icono del lanzador", "Buscar actualizaciones" y ver "Historial de versiones".

### Requisitos

- Android 7.0 o posterior.
- Reproducir desde el gestor de archivos requiere AutoJs6 6.8.0 o posterior; el reproductor independiente abierto desde su icono no necesita AutoJs6.
- En Android 13 o posterior, la notificación multimedia y los controles de la pantalla de bloqueo necesitan el permiso de notificaciones; la reproducción funciona aunque se deniegue.

### Preguntas frecuentes

**Motivos por los que "Reproducir audio" no aparece en el gestor de archivos**

Comprueba que AutoJs6 cumple el requisito de versión y que este plugin está activado en el "Centro de plugins"; cuando se cumplen ambos, los archivos de audio ofrecen "Reproducir audio".

**Casos en que un archivo de audio no se puede reproducir**

La decodificación depende del dispositivo y del archivo, así que códecs poco comunes o archivos dañados pueden no reproducirse. Si falla, toca "Abrir con otra aplicación" para usar otro reproductor.

**Motivos por los que se ponen en cola otras pistas de la misma carpeta**

Es la reproducción continua: al abrir un solo audio, los demás audios de la misma carpeta se ponen en cola por nombre, sin subcarpetas. Quita las pistas que no quieras en "Cola de reproducción".

**Contenido del registro de reanudación**

Solo la última cola abierta desde el icono y su estado de reproducción, guardados únicamente en este dispositivo; desactivar "Reanudar reproducción" los borra al instante.

Más detalles en la [página del proyecto](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player).
