<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="audio-player-ic-launcher" border="0" width="128" />
  </p>

  <p>Plugin de gestionnaire de fichiers. Lit des fichiers audio avec des commandes intégrées et en arrière-plan</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Audio-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Audio-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Audio-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Langues (Languages)

******

Le fichier README.md actuel prend en charge les langues suivantes:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-en.md)
- Français [fr] # actuel
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/.readme/README-ar.md)

******

### Introduction

******

Audio Player fournit un contrôleur intégré et un service privé de lecture en arrière-plan pour les fichiers audio ouverts depuis le gestionnaire de fichiers. Il peut également recevoir des requêtes Android ACTION_VIEW en lecture seule pour des content URI avec un type MIME audio.

******

### Fonctionnalités

******

- Enregistre l'action principale de l'explorateur en lecture seule `play-audio` via le protocole Explorer Action v2 pour les 18 extensions audio déjà reconnues par l'hôte.
- Lit les fichiers avec Media3 ExoPlayer et MediaSessionService, avec gestion du focus audio, de la déconnexion de sortie, du mode de réveil local, de la lecture en arrière-plan et des commandes multimédias du système.
- Fournit un écran de contrôle avec métadonnées du titre, commandes de lecture, explication facultative de l'autorisation de notifications sur Android 13+ et retour sur les erreurs.
- Accepte des requêtes Android ACTION_VIEW indépendantes pour des URI `content` en lecture seule avec `audio/*`, tout en supprimant les extras de l'appelant et les autorisations URI étendues.
- Propose un recours en cas d'échec du décodeur qui ouvre le fichier dans une autre application compatible et exclut ce plugin afin d'éviter une boucle.

******

### Extensions de l'explorateur

******

Le catalogue de l'explorateur utilise volontairement uniquement ces extensions et déclare une liste de types MIME vide:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave
```

Une extension reconnue ne garantit pas le décodage. La lecture dépend de Media3, de la plateforme Android, des codecs de l'appareil et du contenu du fichier.

******

### Comportement de l'hôte

******

Lorsque le plugin est installé, le gestionnaire de fichiers affiche Lire l'audio comme action principale pour les extensions indiquées. La sélection ouvre le contrôleur du plugin et démarre le service privé avec un accès temporaire en lecture seule au fichier choisi.

Lorsque le plugin est absent, cette action ne figure pas dans la liste. L'hôte conserve son flux ACTION_VIEW externe en lecture seule pour les fichiers audio, de sorte qu'une autre application audio installée peut traiter le fichier. Sans application externe compatible, l'hôte ne dispose pas d'une interface de lecture de remplacement.

******

### Interface du plugin

******

L'hôte découvre et exécute le plugin avec les identités suivantes:

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

La version 1 fournit une action principale en lecture seule pour un seul fichier dans le gestionnaire de fichiers principal. Le catalogue utilise uniquement les extensions, tandis que l'entrée Android indépendante continue d'accepter `audio/*`.

La version 5269 ou ultérieure de l'hôte est requise.

******

### Sécurité

******

Le plugin ne demande aucune autorisation de stockage ou de réseau. Son entrée du gestionnaire de fichiers est protégée par l'autorisation de signature de l'hôte et valide strictement le protocole v2, les content URI cible et parent, ClipData, la surface source, le nom affiché, le type MIME, la taille déclarée et les indicateurs de lecture seule. Seuls l'URI cible et un droit de lecture sont transmis aux composants privés. L'entrée Android publique accepte uniquement les requêtes content audio en lecture seule, refuse les droits d'écriture, persistants et de préfixe, et ne transmet jamais les extras arbitraires de l'appelant.

******

### Limites de sécurité

******

- Un fichier cible par action de l'explorateur.
- Les requêtes de l'explorateur dont la taille déclarée dépasse 8 TiB sont refusées.
- L'action de l'explorateur est sélectionnée uniquement par extension et valide encore un type MIME audio lors de l'exécution.
- L'entrée Android publique exige ACTION_VIEW, un URI `content`, `audio/*` et un droit de lecture.
- L'autorisation de notifications est facultative. Son refus masque les commandes dans le volet de notifications mais ne bloque pas la lecture.
- La lecture se termine correctement à la fin du fichier ou en cas d'erreur de décodage. Le recours externe transmet uniquement un nouveau droit de lecture et exclut ce plugin.

******

### Historique des versions

******

# v1.0.1

###### 2026/08/08

* `Correctif` Liaison de service nulle lors de l'activation du plugin dans le centre des plugins
* `Amélioration` Nom, description et documentation utilisateur plus clairs et concis

# v1.0.0

###### 2026/08/02

* `Fonctionnalité` Plugin Audio Player avec l'ID de plugin `audio-player`, l'ID d'action `play-audio`, le moteur `explorer-action` et la variante `default`
* `Fonctionnalité` Action principale en lecture seule du gestionnaire de fichiers pour les 18 extensions audio de l'hôte, avec version 5269 ou ultérieure requise
* `Fonctionnalité` Lecture Media3 ExoPlayer et MediaSessionService avec focus audio, gestion de la déconnexion de sortie, mode de réveil local, arrière-plan, commandes multimédias du système et interface privée
* `Fonctionnalité` Explication facultative de l'autorisation de notifications Android 13+ sans bloquer la lecture en cas de refus
* `Fonctionnalité` Prise en charge Android ACTION_VIEW indépendante en lecture seule pour les requêtes audio URI `content` et transfert vers une autre application compatible en cas d'échec du décodeur, avec prévention des boucles
* `Fonctionnalité` Validation stricte du protocole, des URI, de ClipData, de la source, du nom, du MIME, de la taille et des droits, sans autorisation de stockage ou de réseau et avec transfert minimal en lecture
* `Fonctionnalité` Métadonnées, textes de l'interface, instructions, fichiers README et historiques localisés en espagnol, français, russe, arabe, japonais, coréen, anglais, chinois simplifié, chinois traditionnel de Hong Kong et chinois traditionnel de Taïwan
* `Dépendance` Ajout de AndroidX Media3 ExoPlayer, Session et UI version 1.10.1

##### Pour consulter davantage de versions

* [CHANGELOG-fr.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Audio-Player/blob/master/app/src/main/assets/doc/CHANGELOG-fr.md)

******

### Compilation

******

```powershell
.\gradlew.bat :app:assembleDebug
```

Compilation Release:

```powershell
.\gradlew.bat :app:assembleRelease
```

Les paramètres de compilation proviennent de `version.properties`. Le SDK minimal actuel est 24 et le SDK cible est 36.

******

### Structure des ressources

******

```text
.readme/lang_*.json
.changelog/lang_*.json
.python/generate_markdown.py
app/src/main/assets/doc/CHANGELOG-*.md
app/src/main/res/values-*/strings.xml
app/src/main/res/raw-*/plugin_instruction.md
```

`strings.xml` localise les métadonnées du plugin et le texte de l'interface. `plugin_instruction.md` fournit les instructions visibles depuis l'hôte. `.python/generate_markdown.py` génère les fichiers README et les historiques localisés depuis les sources JSON.

******

### Liens

******

- Documentation AutoJs6: https://docs.autojs6.com
- Android Media3: https://developer.android.com/media/media3
- Partage sécurisé de fichiers Android: https://developer.android.com/training/secure-file-sharing
