<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <h1>3-Terra Player</h1>

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

3-Terra Player fournit un contrôleur intégré et un service privé de lecture en arrière-plan pour les fichiers audio ouverts depuis le gestionnaire de fichiers. Il peut également recevoir des requêtes Android ACTION_VIEW en lecture seule pour des content URI avec un type MIME audio.

******

### Fonctionnalités

******

- Enregistre `play-audio` et `play-audio-selection` via Explorer Action v12 pour tous les types MIME audio reconnus par l’hôte et 19 extensions connues.
- Lit les fichiers avec Media3 ExoPlayer et MediaSessionService, avec gestion du focus audio, de la déconnexion de sortie, du mode de réveil local, de la lecture en arrière-plan et des commandes multimédias du système.
- Fournit un écran complet avec pochette, étiquettes, informations techniques, déplacement de la progression, sauts de 10 secondes, modes séquentiel / aléatoire / répétition d'un titre et vitesse de 0,5x à 2x.
- Lit jusqu'à 128 fichiers audio explicitement sélectionnés dans l'ordre fourni par l'hôte, avec précédent / suivant et une file permettant de choisir ou retirer un titre.
- Inclut une minuterie gérée par le service avec préréglages, durée personnalisée, arrêt après le titre courant, fondu des cinq dernières secondes et boucle A-B.
- Mémorise uniquement le dernier fichier ouvert et sa position, remplace l’ancienne entrée à l’ouverture d’un autre et synchronise métadonnées et commandes avec le système.
- Fournit un lanceur permettant de choisir jusqu’à 128 documents et accepte des URI `content` ACTION_VIEW en lecture seule, y compris les anciens alias MIME WMA.
- Propose un recours en cas d'échec du décodeur qui ouvre le fichier dans une autre application compatible et exclut ce plugin afin d'éviter une boucle.
- L'ouverture d'un fichier depuis l'explorateur découvre, via une Host Session par requête, une file bornée et naturellement triée des fichiers audio frères lisibles ; la sélection multiple explicite conserve l'ordre de l'hôte.
- Génère une palette claire et sombre lisible depuis une couleur source, suit AutoJs6 par défaut et propose 19 couleurs Material 500 et des RGB personnalisées ; les paramètres couvrent langue, nuit, reprise, mises à jour, historique et informations.

******

### Extensions de l'explorateur

******

Le catalogue annonce `audio/*` et utilise aussi ces extensions pour les tables MIME anciennes ou incomplètes:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave, wma
```

Une extension reconnue ne garantit pas le décodage. La lecture dépend de Media3, de la plateforme Android, des codecs de l'appareil et du contenu du fichier.

******

### Comportement de l'hôte

******

Lorsque le plugin est installé et activé dans AutoJs6, le gestionnaire affiche Lire l'audio pour un fichier et Lire les fichiers audio sélectionnés dans la barre de sélection multiple. L'ouverture simple démarre sur cette piste et peut découvrir les fichiers audio frères directs lisibles dans l'ordre naturel ; la sélection multiple explicite conserve l'ordre de l'hôte.

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
Explorer action ids: play-audio (single) / play-audio-selection (multiple, up to 128)
Explorer protocol version: 12 (accepts compatible read-only v4–v12 requests)
Explorer MIME types: [audio/*]
Android VIEW action: android.intent.action.VIEW
Android VIEW MIME type: audio/* plus legacy WMA MIME aliases
required host build: 5276
```

La version 1.4.0 fournit des actions v12 en lecture seule, un accès limité aux frères, un sélecteur autonome de documents et des entrées Android audio / WMA. Sans Host Session facultative, seul le fichier choisi est lu.

La découverte dans le même dossier nécessite AutoJs6 6.8.0 build 5276 ou ultérieur et Explorer Action v12 ; cette exigence ne sera pas relevée par les capacités futures du plugin.

******

### Sécurité

******

Le plugin ne demande aucun stockage et n’écrit jamais les fichiers sources. Internet sert uniquement aux vérifications manuelles ou quotidiennes des versions GitHub. L’entrée signée valide strictement v12, TARGETS, ClipData, les métadonnées et la lecture ; la Host Session reste liée à l’UID et non récursive, et les entrées publiques restent en lecture seule.

******

### Limites de sécurité

******

- L'action individuelle part exactement d'un fichier sélectionné et peut former une file bornée de fichiers audio frères directs lisibles ; l'action de sélection accepte de 1 à 128 fichiers uniques et conserve leur ordre.
- Les requêtes de l'explorateur dont la taille déclarée dépasse 8 TiB sont refusées.
- Les actions correspondent au MIME audio reconnu ou à la liste d’extensions, puis normalisent et valident chaque cible.
- La découverte des frères n'est pas récursive et n'est disponible que via la session par requête gérée par l'hôte ; le plugin ne devine jamais un URI frère et ne reçoit aucun chemin de système de fichiers.
- L’entrée Android publique exige ACTION_VIEW, un URI `content`, un MIME audio ou WMA pris en charge et un droit de lecture.
- L'autorisation de notifications est facultative. Son refus masque les commandes dans le volet de notifications mais ne bloque pas la lecture.
- La lecture se termine correctement à la fin du fichier ou en cas d'erreur de décodage. Le recours externe transmet uniquement un nouveau droit de lecture et exclut ce plugin.

******

### Historique des versions

******

# v1.4.0

###### 2026/08/29

* `Fonctionnalité` Les notifications multimédias utilisent désormais des actions dédiées précédent, suivant, activation du mode aléatoire et quitter, ainsi que l'icône monochrome transparente de l'application; l'état lumineux du mode aléatoire reste synchronisé avec la lecture
* `Correctif` AutoJs6 ne marque et ne désactive plus le plugin comme erroné lors de la découverte des informations du plugin et d'Explorer Action, car les deux protocoles Binder utilisent désormais des services distincts
* `Correctif` Vider la file efface maintenant les anciennes métadonnées et la session en arrière-plan, désactive la lecture, la recherche, la file, la vitesse, le minuteur et A-B, puis affiche un état vide explicite au lieu d'accepter des appuis sans effet
* `Correctif` L'ombre du bouton principal n'est plus coupée par la zone inférieure et son état désactivé utilise des couleurs clairement atténuées; les boutons radio, choix multiples, progressions et boutons des dialogues suivent désormais le thème dynamique
* `Amélioration` L'application et le plugin portent désormais le nom non traduisible 3-Terra Player, avec les espaces de noms et symboles source migrés vers les formes three / Three, tout en conservant l'ID d'application publié pour les mises à niveau et réglages existants
* `Amélioration` Le raccourci de palette de la barre du lecteur est remplacé par un menu à trois points contenant uniquement Paramètres, sans modifier l'espacement établi sous les commandes de lecture

# v1.3.0

###### 2026/08/29

* `Fonctionnalité` Ajout d’un écran de lancement et d’un lecteur autonome multifichier, ainsi que de paramètres dédiés à la langue, au mode nuit, à la couleur, à la reprise, aux mises à jour, à l’historique et aux informations sur l’application et le développeur
* `Fonctionnalité` La langue, le mode nuit et la couleur suivent AutoJs6 par défaut via son contrat officiel en lecture seule ; si l’hôte est indisponible, les choix restent visibles mais désactivés et reviennent aux valeurs par défaut
* `Fonctionnalité` Ajout de la vérification manuelle et automatique quotidienne, de la gestion des versions ignorées et d’un historique localisé intégré
* `Correctif` Correction du message permanent Host color unavailable en exposant l’entrée protégée d’informations du plugin requise par le fournisseur de paramètres de l’hôte
* `Correctif` Les actions de l’explorateur annoncent les types MIME audio en plus de 19 extensions connues, dont WMA, afin que tout audio reconnu par l’hôte s’ouvre directement dans le plugin
* `Amélioration` La reprise mémorise exactement le dernier fichier ouvert, efface immédiatement l’ancien à l’ouverture d’un autre et ne conserve jamais une lecture terminée
* `Amélioration` La zone de métadonnées réserve trois lignes stables et complète fréquence et débit depuis la piste sélectionnée, dans l’ordre 44.1 kHz · MP3 · 128 kbps
* `Amélioration` A-B suit désormais un cycle clair en trois pressions pour définir A, définir B et effacer ; les commandes inférieures sont espacées et leurs icônes centrées et uniformes

# v1.2.2

###### 2026/08/27

* `Correctif` La lecture depuis l’explorateur n’échoue plus après une mise à niveau du plugin lorsque le gestionnaire de fichiers AutoJs6 en cours d’exécution envoie encore une action de protocole v4 mise en cache ; la passerelle accepte l’enveloppe compatible en lecture seule v4–v12 tout en continuant d’annoncer v12
* `Correctif` Les extensions audio annoncées ne sont plus rejetées lorsqu’une table MIME Android ou constructeur renvoie un joker ou un type application ; la liste d’extensions autorisées fournit désormais un type MIME audio canonique stable
* `Amélioration` Les requêtes Explorer rejetées consignent désormais un code de motif respectueux de la vie privée, sans nom de fichier, chemin affiché ni URI, afin de diagnostiquer directement les futurs écarts de contrat

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
