<!--suppress HtmlDeprecatedAttribute, HttpUrlsUsage -->

<div align="center">
  <p>
    <picture>
      <source srcset="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/app/src/main/res/mipmap-night/ic_launcher.png?raw=true" media="(prefers-color-scheme: dark)" />
      <img src="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/app/src/main/res/mipmap/ic_launcher.png?raw=true" alt="three-terra-player-ic-launcher" border="0" width="128" />
    </picture>
  </p>

  <p>Lecteur audio et application autonome avec lecture en arrière-plan</p>

  <p>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases"><img alt="GitHub release (latest by date)" src="https://img.shields.io/github/v/release/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player?label=Release"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/issues"><img alt="GitHub closed issues" src="https://img.shields.io/github/issues/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player?color=A24232&label=Issues"/></a>
    <a href="https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/LICENSE"><img alt="GitHub License" src="https://img.shields.io/github/license/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player?color=534BAE&label=License"/></a>
  </p>
</div>

******

### Langues (Languages)

******

Le fichier README.md actuel prend en charge les langues suivantes:

- [简体中文 [zh-Hans]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-zh-Hans.md)
- [繁體中文 (香港) [zh-Hant-HK]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-zh-Hant-HK.md)
- [繁體中文 (台灣) [zh-Hant-TW]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-zh-Hant-TW.md)
- [English [en]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-en.md)
- Français [fr] # actuel
- [Español [es]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-es.md)
- [日本語 [ja]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ja.md)
- [한국어 [ko]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ko.md)
- [Русский [ru]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ru.md)
- [العربية [ar]](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/.readme/README-ar.md)

******

### Introduction

******

3-Terra Player (anciennement Audio Player) est à la fois un plugin de lecture audio pour le gestionnaire de fichiers AutoJs6 et un lecteur audio autonome et épuré. Une fois installé et activé, il suffit de toucher un fichier audio dans le gestionnaire de fichiers AutoJs6 pour le lire directement, sans passer par un lecteur tiers; il peut aussi s'ouvrir depuis l'écran d'accueil comme une application ordinaire, pour choisir plusieurs fichiers audio et les lire à la suite.

Une fois installé, le plugin est détecté automatiquement par AutoJs6, sans aucune configuration. La lecture repose sur Media3 (ExoPlayer), le framework multimédia officiel d'Android, avec lecture en arrière-plan et commandes dans la notification multimédia du système; l'accès aux fichiers audio est strictement en lecture seule, aucune autorisation de stockage n'est demandée et les fichiers sources ne sont jamais modifiés ni supprimés.

******

### Points forts

******

- Listes locales: M3U/M3U8, PLS, XSPF, WPL, ASX/WAX/WVX, MPCPL, DPL conservent ordre, titres et doublons. Depuis AutoJs6, les fichiers doivent être dans le même dossier; le lecteur autonome demande le dossier pour les chemins relatifs. Une liste à la fois, 128 éléments maximum. URL réseau, HLS et listes imbriquées non pris en charge.
- Lecture en un appui depuis le gestionnaire de fichiers: touchez n'importe quel fichier audio dans le gestionnaire de fichiers AutoJs6 pour lancer la lecture.
- Enchaînement automatique dans le même dossier: à l'ouverture d'un titre, les autres fichiers audio du dossier sont détectés et lus à la suite dans l'ordre naturel des noms de fichiers (jusqu'à 128 titres, à partir du titre choisi).
- File multisélection: cochez jusqu'à 128 fichiers audio dans le gestionnaire de fichiers et lisez-les d'un seul appui, exactement dans l'ordre de sélection.
- Écran de lecture complet: pochette d'album, étiquettes titre / artiste / album, informations techniques comme la fréquence d'échantillonnage et le débit, et une barre de progression déplaçable.
- Toutes les commandes du quotidien: précédent / suivant, recul et avance réglables à 5 / 10 / 15 / 30 secondes, modes séquentiel / aléatoire / répétition d'un titre, vitesse par défaut mémorisée de 0,5x à 2x et action choisie en fin de file.
- Panneau de file de lecture: consultez à tout moment les titres à venir, touchez pour y sauter ou les retirer, avec le titre en cours clairement indiqué.
- Minuterie de veille: préréglages de 15 / 30 / 60 minutes ou durée personnalisée, avec arrêt possible après le titre en cours et fondu du volume sur les 5 dernières secondes.
- Boucle A-B: réécoutez un passage en boucle, idéal pour travailler la compréhension orale ou relever un morceau à l'oreille.
- Lecture en arrière-plan: la lecture continue après avoir quitté l'écran ou verrouillé l'appareil, avec commandes dans la notification multimédia du système et sur l'écran de verrouillage.
- Restauration de session: rouvrir l'application autonome depuis le lanceur restaure la dernière file, la piste active, la position d'arrêt, l'état répétition / aléatoire et la vitesse; la session restaurée reste en pause.
- Mode application autonome: utilisable sans AutoJs6; choisissez jusqu'à 128 fichiers audio depuis l'écran de démarrage et lisez-les.
- Apparence adaptative: la langue, le mode nuit et la couleur de base peuvent suivre AutoJs6 ou être personnalisés; la pochette génère une palette lisible dans le lecteur bord à bord, avec transitions et retours haptiques respectant le système.

******

### Utilisation

******

1. Téléchargez le dernier APK du plugin depuis la page [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases) et installez-le sur l'appareil qui exécute AutoJs6.
2. Ouvrez le centre des plugins d'AutoJs6 et vérifiez que `3-Terra Player` est bien reconnu et activé.
3. Dans le gestionnaire de fichiers AutoJs6, touchez un fichier audio et choisissez `Lire l'audio`; ou faites un appui long pour en sélectionner plusieurs, puis choisissez `Lire les fichiers audio sélectionnés` dans la barre d'outils.
4. Vous pouvez aussi ouvrir `3-Terra Player` directement depuis l'écran d'accueil et choisir plusieurs fichiers audio via le bouton de sélection de fichiers pour lancer la lecture.

> Si le plugin n'apparaît pas dans le centre des plugins, mettez d'abord AutoJs6 à niveau vers 6.8.0 (code de version 5276) ou une version ultérieure. Le plugin lui-même prend en charge Android 7.0 (API 24) et versions ultérieures, et le mode application autonome ne dépend pas d'AutoJs6.

******

### Formats audio pris en charge

******

L'entrée du gestionnaire de fichiers déclare le type audio générique `audio/*` et couvre explicitement les 19 extensions suivantes, pour rester compatible avec les tables MIME incomplètes ou anciennes de certains appareils:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave, wma
```

Une extension prise en charge ne garantit pas le décodage: la lecture réelle dépend de Media3, de la version d'Android, des codecs de l'appareil et du contenu du fichier. Si un fichier ne peut pas être lu, le panneau d'erreur propose de l'ouvrir avec une autre application.

******

### Questions fréquentes

******

#### Le bouton `Lire l'audio` n'apparaît pas dans le gestionnaire de fichiers?

Vérifiez trois points dans l'ordre: le plugin est installé; il est activé dans le centre des plugins d'AutoJs6; et AutoJs6 est en version 6.8.0 (code de version 5276) ou ultérieure. Une fois ces trois conditions réunies, l'action de lecture apparaît sur les fichiers audio du gestionnaire de fichiers.

#### Je n'ai ouvert qu'un seul titre, pourquoi d'autres morceaux du même dossier apparaissent-ils dans la file?

C'est l'enchaînement automatique dans le même dossier: à l'ouverture d'un seul fichier audio, le plugin découvre les autres fichiers audio du dossier via une session gérée par l'hôte et les place dans la file dans l'ordre naturel des noms de fichiers, pour une écoute continue. La découverte est strictement en lecture seule et n'entre jamais dans les sous-dossiers; si la version de l'hôte ne prend pas en charge cette capacité, seul le titre choisi est lu.

#### Dans quel ordre la lecture multisélection se fait-elle?

Exactement dans l'ordre de sélection. Pour écouter dans un ordre précis, cochez les fichiers dans cet ordre; une fois dans le lecteur, vous pouvez aussi sauter vers un titre ou le retirer depuis le panneau de file.

#### La lecture continue-t-elle après avoir quitté l'écran ou verrouillé l'appareil?

Oui. La lecture est portée par un service en arrière-plan et se contrôle depuis la notification multimédia du système et l'écran de verrouillage. Sur Android 13 et ultérieur, l'autorisation de notifications est facultative: la refuser masque seulement les commandes de notification, sans affecter la lecture elle-même.

#### Le plugin peut-il modifier mes fichiers audio ou les envoyer en ligne?

Non. Le plugin ne demande aucune autorisation de stockage et n'accède aux fichiers audio qu'en lecture seule; la connexion réseau sert uniquement à vérifier les nouvelles versions sur GitHub (déclenchée manuellement ou au plus une fois par jour), et rien n'est envoyé, ni fichiers ni données personnelles.

#### Pourquoi certains fichiers restent-ils muets ou signalent-ils une erreur de décodage?

Une extension prise en charge ne signifie pas que l'appareil sait forcément décoder le fichier; certains codecs rares ou des fichiers endommagés peuvent échouer. En cas d'erreur, le panneau d'erreur affiche le code exact et propose d'ouvrir le fichier avec une autre application (ce plugin est automatiquement exclu pour éviter une boucle).

#### Comment faire suivre au plugin la langue et le thème d'AutoJs6?

C'est le comportement par défaut: la langue, le mode nuit et la couleur du thème se synchronisent automatiquement via l'interface officielle de paramètres en lecture seule d'AutoJs6. Vous pouvez aussi fixer une langue ou choisir vos propres couleurs dans les paramètres; sans AutoJs6 installé, le plugin revient à l'apparence du système et aux valeurs par défaut intégrées.

#### Où se trouvent la minuterie de veille et la boucle A-B?

Toutes deux se trouvent dans la barre d'outils en bas de l'écran de lecture. L'icône de minuterie propose des préréglages ou une durée personnalisée; le bouton A-B suit le cycle 'définir le point A, définir le point B, effacer' au fil des appuis pour poser ou annuler la boucle.

******

### Autorisations et sécurité

******

Les fichiers audio peuvent provenir de sources non fiables, aussi le flux de lecture est-il protégé dès la conception par plusieurs lignes de défense:

- Zéro autorisation de stockage: le plugin ne demande pas (et ne peut pas obtenir) l'accès en lecture ou en écriture au stockage de l'appareil; il n'accède qu'aux fichiers individuels explicitement accordés par l'hôte ou le système.
- Jamais d'écriture: les fichiers audio sont ouverts en lecture seule et ne sont jamais modifiés, déplacés ni supprimés.
- Validation stricte: l'entrée du gestionnaire de fichiers est protégée par l'autorisation de signature d'AutoJs6, et chaque requête de lecture est vérifiée point par point, y compris la version du protocole, la liste des cibles et les droits en lecture seule; toute requête non conforme est rejetée d'emblée.
- Découverte bornée: l'enchaînement dans le même dossier lit uniquement via une session gérée par l'hôte et limitée à la requête, sans récursion dans les sous-dossiers ni adresse de fichier devinée, et la session se ferme à la fin de la lecture.
- Session locale uniquement: lorsque la reprise de lecture est activée, le stockage privé de l'application conserve seulement la dernière file autonome du sélecteur et son état de lecture; seuls les URI du sélecteur système avec un accès durable sont admis, les routes Host Session ne sont jamais enregistrées et désactiver ce réglage efface aussitôt l'enregistrement.
- Réseau minimal: l'autorisation Internet ne sert qu'aux vérifications de versions GitHub déclenchées par l'utilisateur ou une fois par jour, jamais au contenu audio ni aux données d'usage.
- Notifications facultatives: sur Android 13+, l'autorisation de notifications est facultative; la refuser masque seulement les commandes de notification et la lecture n'est pas affectée.

Installez le plugin uniquement depuis la page officielle [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases) ou d'autres canaux de confiance; un paquet d'origine inconnue peut avoir été altéré, même si son nom et sa version semblent identiques.

******

### Interface du plugin

******

Les informations suivantes s'adressent aux développeurs de l'hôte AutoJs6 et de plugins; l'hôte utilise ces identifiants pour découvrir le plugin et négocier les capacités:

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

La version actuelle fournit les actions en lecture seule du protocole v12 pour un fichier unique et pour la multisélection ordonnée, la découverte dans le même dossier limitée à la requête, un sélecteur de documents autonome et une entrée audio Android publique en lecture seule (y compris les anciens alias MIME WMA). Les hôtes sans la capacité facultative Host Session continuent de lire uniquement le fichier choisi.

La découverte dans le même dossier nécessite AutoJs6 6.8.0 (code de version 5276) ou ultérieur avec Explorer Action v12; les capacités futures du plugin ne relèveront pas cette exigence. Depuis la v1.4.0, le produit s'appelle 3-Terra Player tandis que l'ID d'application reste `io.github.supermonster003.autojs6.plugin.audioplayer`, de sorte que la mise à niveau s'installe par-dessus la version existante.

******

### Feuille de route

******

Les capacités prévues et leur avancement sont tenus à jour sous forme de liste cochable dans Roadmap.md, organisée par jalons avec des critères d'acceptation, couvrant les paroles, un égaliseur, des couleurs tirées de la pochette, une interface bord à bord et la qualité d'ingénierie. Les éléments non cochés sont des intentions et non des capacités de la version actuelle; n'hésitez pas à rejoindre la discussion via les Issues.

- [Voir Roadmap.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/Roadmap.md)

******

### Historique des versions

******

#### v1.6.2

_2026/09/16_

- `Amélioration` Après compileSdk, targetSdk passe à 37 (Android 17) ; le comportement du plugin ne dépend pas de la nouvelle cible

#### v1.6.1

_2026/09/15_

- `Amélioration` compileSdk passe à 37 (Android 17) ; targetSdk reste à 36 jusqu'à la vérification du comportement dépendant de la cible

#### v1.6.0

_2026/09/13_

- `Fonctionnalité` Listes locales: M3U/M3U8, PLS, XSPF, WPL, ASX/WAX/WVX, MPCPL, DPL conservent ordre, titres et doublons. Depuis AutoJs6, les fichiers doivent être dans le même dossier; le lecteur autonome demande le dossier pour les chemins relatifs. Une liste à la fois, 128 éléments maximum. URL réseau, HLS et listes imbriquées non pris en charge
- `Correctif` Les choix des boîtes de dialogue de langue et de mode nuit utilisent désormais Material Body1 en 16sp au lieu du texte de liste système surdimensionné
- `Correctif` Conserver la date de version du plugin en anglais quelle que soit la langue de la machine de compilation
- `Amélioration` Ressources traduites cohérentes, activation explicite du plugin et validation des paquets de publication

##### Pour un historique des versions plus complet, voir

* [CHANGELOG.md](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/app/src/main/assets/doc/CHANGELOG-fr.md)

******

### Compilation

******

Cette section s'adresse aux développeurs qui souhaitent compiler le plugin depuis les sources.

Compiler un APK debug:

```powershell
.\gradlew.bat :app:assembleDebug
```

Compiler un APK release (signé automatiquement une fois la signature configurée dans le fichier non versionné `sign.properties`):

```powershell
.\gradlew.bat :app:assembleRelease
```

Pour l'archivage des publications, exécutez la tâche `:app:appendDigestToReleasedFiles`, qui copie les APK signés dans `releases/` en ajoutant au nom de fichier la version et une somme de contrôle CRC32.

Les paramètres de compilation sont centralisés dans `version.properties`: SDK minimal 24 (Android 7.0), SDK cible 37, version actuelle 1.6.2.

******

### Localisation et génération des documents

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

`strings.xml` localise les métadonnées du plugin et le texte de l'interface, et `plugin_instruction.md` fournit les instructions affichées dans le centre des plugins de l'hôte. Le README, le journal des modifications et les instructions sont tous générés depuis des sources JSON: modifiez les sources sous `.readme/` et `.changelog/`, puis exécutez `py .python/generate_markdown.py` pour régénérer tous les artefacts (ne modifiez jamais les fichiers générés à la main); exécutez `py .python/generate_markdown.py --check` pour vérifier que sources et artefacts sont synchronisés.

******

### Licence

******

Le code du projet est open source sous [Mozilla Public License 2.0](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/LICENSE). La lecture audio repose sur [AndroidX Media3](https://developer.android.com/media/media3) (Apache License 2.0).

******

### Liens

******

- Documentation AutoJs6: https://docs.autojs6.com
- Android Media3: https://developer.android.com/media/media3
- Partage sécurisé de fichiers Android: https://developer.android.com/training/secure-file-sharing


[16 KB page alignment and build verification](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/blob/master/docs/16kb.md)
