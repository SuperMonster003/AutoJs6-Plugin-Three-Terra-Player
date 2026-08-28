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

- Enregistre l'action fichier unique `play-audio` et l'action de sélection multiple ordonnée `play-audio-selection` via le protocole Explorer Action v12 pour les 18 extensions audio reconnues par l'hôte.
- Lit les fichiers avec Media3 ExoPlayer et MediaSessionService, avec gestion du focus audio, de la déconnexion de sortie, du mode de réveil local, de la lecture en arrière-plan et des commandes multimédias du système.
- Fournit un écran complet avec pochette, étiquettes, informations techniques, déplacement de la progression, sauts de 10 secondes, modes séquentiel / aléatoire / répétition d'un titre et vitesse de 0,5x à 2x.
- Lit jusqu'à 128 fichiers audio explicitement sélectionnés dans l'ordre fourni par l'hôte, avec précédent / suivant et une file permettant de choisir ou retirer un titre.
- Inclut une minuterie gérée par le service avec préréglages, durée personnalisée, arrêt après le titre courant, fondu des cinq dernières secondes et boucle A-B.
- Mémorise la position de chaque titre et reflète dans les surfaces multimédias système le titre, la pochette, précédent / suivant et les sauts de 10 secondes.
- Accepte des requêtes Android ACTION_VIEW indépendantes pour des URI `content` en lecture seule avec `audio/*`, tout en supprimant les extras de l'appelant et les autorisations URI étendues.
- Propose un recours en cas d'échec du décodeur qui ouvre le fichier dans une autre application compatible et exclut ce plugin afin d'éviter une boucle.
- L'ouverture d'un fichier depuis l'explorateur découvre, via une Host Session par requête, une file bornée et naturellement triée des fichiers audio frères lisibles ; la sélection multiple explicite conserve l'ordre de l'hôte.
- Génère une palette claire et sombre lisible depuis une couleur source, suit AutoJs6 par défaut et propose 19 préréglages Material 500 localisés ainsi que des couleurs RGB personnalisées avec aperçu en direct.

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

Lorsque le plugin est installé, le gestionnaire affiche Lire l'audio pour un fichier et Lire les fichiers audio sélectionnés dans la barre de sélection multiple. L'ouverture simple démarre sur cette piste et peut découvrir les fichiers audio frères directs lisibles dans l'ordre naturel ; la sélection multiple explicite conserve l'ordre de l'hôte.

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
Explorer protocol version: 4
Explorer MIME types: []
Android VIEW action: android.intent.action.VIEW
Android VIEW MIME type: audio/*
required host build: 5276
```

La version 1.2.1 fournit une action principale en lecture seule selon le protocole v12, pouvant recevoir une capacité par requête pour les frères directs, ainsi qu'une action de sélection multiple ordonnée en lecture seule. L'entrée Android indépendante reste limitée à un fichier et accepte `audio/*`. Les hôtes sans Host Session facultative conservent la lecture du seul fichier sélectionné.

La découverte dans le même dossier nécessite AutoJs6 6.8.0 build 5276 ou ultérieur et Explorer Action v12 ; cette exigence ne sera pas relevée par les capacités futures du plugin.

******

### Sécurité

******

Le plugin ne demande aucune autorisation de stockage ou d'accès à Internet. Son entrée protégée par signature valide strictement le protocole v12, TARGETS et ClipData ordonnés, les identifiants, la relation au parent, les métadonnées et les indicateurs de lecture seule. Une Host Session facultative est liée par l'hôte à l'UID du plugin et permet seulement de lister le parent direct du fichier sélectionné et d'ouvrir celui-ci ou un frère direct lisible ; les composants de lecture reçoivent des routes synthétiques opaques, jamais des chemins de système de fichiers. L'entrée Android publique reste limitée à un fichier en lecture seule.

******

### Limites de sécurité

******

- L'action individuelle part exactement d'un fichier sélectionné et peut former une file bornée de fichiers audio frères directs lisibles ; l'action de sélection accepte de 1 à 128 fichiers uniques et conserve leur ordre.
- Les requêtes de l'explorateur dont la taille déclarée dépasse 8 TiB sont refusées.
- L'action de l'explorateur est sélectionnée uniquement par extension et valide encore un type MIME audio lors de l'exécution.
- La découverte des frères n'est pas récursive et n'est disponible que via la session par requête gérée par l'hôte ; le plugin ne devine jamais un URI frère et ne reçoit aucun chemin de système de fichiers.
- L'entrée Android publique exige ACTION_VIEW, un URI `content`, `audio/*` et un droit de lecture.
- L'autorisation de notifications est facultative. Son refus masque les commandes dans le volet de notifications mais ne bloque pas la lecture.
- La lecture se termine correctement à la fin du fichier ou en cas d'erreur de décodage. Le recours externe transmet uniquement un nouveau droit de lecture et exclut ce plugin.

******

### Historique des versions

******

# v1.2.2

###### 2026/08/27

* `Correctif` La lecture depuis l’explorateur n’échoue plus après une mise à niveau du plugin lorsque le gestionnaire de fichiers AutoJs6 en cours d’exécution envoie encore une action de protocole v4 mise en cache ; la passerelle accepte l’enveloppe compatible en lecture seule v4–v12 tout en continuant d’annoncer v12
* `Correctif` Les extensions audio annoncées ne sont plus rejetées lorsqu’une table MIME Android ou constructeur renvoie un joker ou un type application ; la liste d’extensions autorisées fournit désormais un type MIME audio canonique stable
* `Amélioration` Les requêtes Explorer rejetées consignent désormais un code de motif respectueux de la vie privée, sans nom de fichier, chemin affiché ni URI, afin de diagnostiquer directement les futurs écarts de contrat

# v1.2.1

###### 2026/08/27

* `Fonctionnalité` L'action Lire l'audio sur un seul fichier peut maintenant découvrir jusqu'à 128 fichiers audio lisibles dans le même dossier via Explorer Action v12 et créer une file triée naturellement à partir de la piste sélectionnée
* `Correctif` API 24 ne rejette plus une requête Explorer valide lorsqu'Android ajoute depuis le manifeste de l'activité passerelle l'indicateur sans permission FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS
* `Correctif` Le passage automatique à une piste sœur ne plante plus lors de la reconstruction de l'Intent de retour MediaSession ; la cible autorisée à l'origine reste l'ancre de la Host Session indépendamment de la piste active
* `Amélioration` La piste sélectionnée conserve son content URI d'origine tandis que les pistes sœurs sont diffusées uniquement via les descripteurs d'une Host Session limitée à la requête ; aucun URI voisin n'est deviné et aucun accès récursif, en écriture, au stockage ou persistant n'est ajouté
* `Amélioration` Le filtrage des extensions audio exclut de la file les vidéos .mp4 portant le même nom que des fichiers audio .m4a, tandis que la file multisélection explicite existante reste inchangée
* `Amélioration` La propriété de la Host Session est transférée au service de lecture en arrière-plan, puis fermée lors du remplacement de la file, d'un échec de démarrage, de la fin de lecture ou de la destruction du service
* `Dépendance` Mise à niveau de l'API Explorer Action intégrée du protocole v4 vers l'extension rétrocompatible v12 de lecture des fichiers frères, tout en conservant la version hôte minimale 5276

# v1.2.0

###### 2026/08/27

* `Fonctionnalité` Prise en charge du protocole Explorer Action v4 et nouvelle action de sélection multiple ordonnée créant une file avec jusqu'à 128 fichiers audio explicitement choisis
* `Fonctionnalité` Liste Media3 native avec précédent / suivant, panneau pour choisir ou retirer des titres et modes séquentiel / aléatoire / répétition d'un titre
* `Fonctionnalité` Minuterie gérée par le service avec 15 / 30 / 60 minutes, durée personnalisée, arrêt après le titre courant et fondu des cinq dernières secondes
* `Fonctionnalité` Boucle d'intervalle A-B pour répéter une section sélectionnée
* `Amélioration` Les commandes multimédias système affichent désormais précédent, suivant et les sauts de 10 secondes; métadonnées et reprise suivent le titre courant
* `Amélioration` La validation Explorer couvre TARGETS et ClipData ordonnés, identifiants uniques, session hôte et chaque fichier choisi sans élargir les droits de lecture
* `Amélioration` Documentation de l'impossibilité d'énumérer les enfants via l'URI parent FileProvider; découverte des voisins et déduction d'URI restent désactivées, la sélection multiple étant la voie sûre
* `Dépendance` API Explorer Action intégrée mise à niveau de v2 à v4 et version hôte minimale portée à 5276
* `Dépendance` Ajout d'AndroidX RecyclerView 1.4.0

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
