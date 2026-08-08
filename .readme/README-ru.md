<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="audio-player-ic-launcher" border="0" width="128" />
  </p>

  <p>Плагин файлового менеджера. Воспроизводит аудио с элементами управления в приложении и фоновом режиме</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Audio-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Audio-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Audio-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Языки (Languages)

******

Текущий README.md поддерживает следующие языки:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-en.md)
- [Français [fr]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-fr.md)
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ko.md)
- Русский [ru] # текущий
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ar.md)

******

### Введение

******

Audio Player предоставляет встроенный контроллер и частную службу фонового воспроизведения для аудиофайлов, открытых из файлового менеджера. Он также принимает запросы Android ACTION_VIEW только для чтения для content URI с аудио MIME-типом.

******

### Возможности

******

- Регистрирует основное действие Проводника только для чтения `play-audio` через протокол Explorer Action v2 для 18 расширений аудиофайлов, уже распознаваемых хостом.
- Воспроизводит аудио с помощью Media3 ExoPlayer и MediaSessionService, поддерживая аудиофокус, обработку отключения выхода, локальный режим пробуждения, фоновое воспроизведение и системные элементы управления мультимедиа.
- Предоставляет экран контроллера с метаданными заголовка, элементами воспроизведения, необязательным пояснением разрешения уведомлений Android 13+ и сообщениями об ошибках.
- Принимает независимые запросы Android ACTION_VIEW для URI `content` только для чтения с `audio/*`, удаляя extras вызывающей стороны и широкие разрешения URI.
- При ошибке декодера предлагает открыть файл в другом совместимом приложении и исключает этот плагин, чтобы предотвратить цикл.

******

### Расширения Проводника

******

Каталог Проводника намеренно сопоставляет только следующие расширения и объявляет пустой список MIME-типов:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave
```

Совпадение расширения не гарантирует декодирование. Фактическое воспроизведение зависит от Media3, платформы Android, кодеков устройства и содержимого файла.

******

### Поведение хоста

******

Когда плагин установлен, файловый менеджер показывает Воспроизвести аудио как основное действие для указанных расширений. Выбор открывает контроллер плагина и запускает частную службу с временным доступом только для чтения к выбранному файлу.

Когда плагин отсутствует, это действие не отображается. Хост сохраняет существующий внешний поток ACTION_VIEW только для чтения для аудиофайлов, поэтому другое установленное аудиоприложение может обработать файл. Если совместимого внешнего приложения нет, хост не получает заменяющий интерфейс воспроизведения.

******

### Интерфейс плагина

******

Хост обнаруживает и запускает плагин со следующими идентификаторами:

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
```

Версия 1 предоставляет основное действие только для чтения для одного файла в основном файловом менеджере. Каталог использует только расширения, а независимая точка входа Android продолжает принимать `audio/*`.

Требуется сборка хоста 5269 или новее.

******

### Безопасность

******

Плагин не запрашивает разрешения хранилища или сети. Точка входа файлового менеджера защищена разрешением подписи хоста и строго проверяет протокол v2, целевой и родительский content URI, ClipData, исходный экран, отображаемое имя, MIME-тип, заявленный размер и флаги только для чтения. Частным компонентам воспроизведения передаются только целевой URI и разрешение чтения. Публичная точка входа Android принимает только запросы content audio для чтения, отклоняет разрешения записи, постоянного доступа и префикса и никогда не передает произвольные extras вызывающей стороны.

******

### Ограничения безопасности

******

- Один целевой файл на действие Проводника.
- Запросы Проводника с заявленным размером больше 8 TiB отклоняются.
- Действие Проводника выбирается только по расширению файла и при запуске также проверяет аудио MIME-тип.
- Публичная точка входа Android требует ACTION_VIEW, URI `content`, `audio/*` и разрешение чтения.
- Разрешение уведомлений необязательно. Отказ скрывает элементы управления в панели уведомлений, но не блокирует воспроизведение.
- Воспроизведение безопасно завершается при окончании или ошибке декодера. Внешний резервный вариант передает только новое разрешение чтения и исключает этот плагин.

******

### История выпусков

******

# v1.0.1

###### 2026/08/08

* `Исправление` Пустая привязка службы при включении плагина в центре плагинов
* `Улучшение` Более ясные и краткие название, описание и пользовательская документация

# v1.0.0

###### 2026/08/02

* `Функция` Плагин Audio Player с ID плагина `audio-player`, ID действия `play-audio`, движком `explorer-action` и вариантом `default`
* `Функция` Основное действие файлового менеджера только для чтения для 18 аудиорасширений хоста с обязательной сборкой хоста 5269 или новее
* `Функция` Воспроизведение Media3 ExoPlayer и MediaSessionService с аудиофокусом, обработкой отключения выхода, локальным режимом пробуждения, фоновым режимом, системными элементами управления и частным контроллером
* `Функция` Необязательное пояснение разрешения уведомлений Android 13+ без блокировки воспроизведения при отказе
* `Функция` Независимая поддержка Android ACTION_VIEW только для чтения для аудиозапросов URI `content` и передача другому совместимому приложению при ошибке декодера с предотвращением циклов
* `Функция` Строгая проверка протокола, URI, ClipData, источника, имени, MIME, размера и разрешений, без разрешений хранилища или сети и с передачей минимального права чтения
* `Функция` Локализованные метаданные, текст интерфейса, инструкции, README и журналы изменений на испанском, французском, русском, арабском, японском, корейском, английском, упрощенном китайском, традиционном китайском Гонконга и традиционном китайском Тайваня
* `Зависимость` Добавлена зависимость AndroidX Media3 ExoPlayer, Session и UI версии 1.10.1

##### Другие выпуски

* [CHANGELOG-ru.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/assets/doc/CHANGELOG-ru.md)

******

### Сборка

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Release-сборка:

```powershell
.\gradlew.bat :app:assembleRelease
```

Параметры сборки берутся из `version.properties`. Текущий минимальный SDK равен 24, целевой SDK равен 36.

******

### Структура ресурсов

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` локализует метаданные плагина и текст интерфейса. `plugin_instruction.md` содержит инструкции, показываемые хостом. `.python/generate_markdown.py` создает локализованные README и журналы изменений из источников JSON.

******

### Ссылки

******

- Документация AutoJs6: https://docs.autojs6.com
- Android Media3: https://developer.android.com/media/media3
- Безопасная передача файлов Android: https://developer.android.com/training/secure-file-sharing
