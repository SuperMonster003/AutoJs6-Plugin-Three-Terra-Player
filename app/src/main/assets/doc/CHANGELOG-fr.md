******

### Historique des versions

******

# v1.0.0

###### 2026/08/02

* `Fonctionnalité` Plugin Audio Player avec l'ID de plugin `audio-player`, l'ID d'action `play-audio`, le moteur `explorer-action` et la variante `default`
* `Fonctionnalité` Entrée principale en lecture seule du protocole Explorer Action v2 pour les 18 extensions audio existantes de l'hôte, catalogue MIME de l'explorateur vide et version 5269 de l'hôte AutoJs6 requise
* `Fonctionnalité` Lecture Media3 ExoPlayer et MediaSessionService avec focus audio, gestion de la déconnexion de sortie, mode de réveil local, arrière-plan, commandes multimédias du système et interface privée
* `Fonctionnalité` Explication facultative de l'autorisation de notifications Android 13+ sans bloquer la lecture en cas de refus
* `Fonctionnalité` Prise en charge Android ACTION_VIEW indépendante en lecture seule pour les requêtes audio URI `content` et transfert vers une autre application compatible en cas d'échec du décodeur, avec prévention des boucles
* `Fonctionnalité` Validation stricte du protocole, des URI, de ClipData, de la source, du nom, du MIME, de la taille et des droits, sans autorisation de stockage ou de réseau et avec transfert minimal en lecture
* `Fonctionnalité` Implémentation JVM pure sans bibliothèque native, ABI sans restriction via `supportedAbis = emptyArray()` et un APK indépendant de l'ABI
* `Fonctionnalité` Métadonnées, textes de l'interface, instructions, fichiers README et historiques localisés en espagnol, français, russe, arabe, japonais, coréen, anglais, chinois simplifié, chinois traditionnel de Hong Kong et chinois traditionnel de Taïwan
* `Dépendance` Ajout de AndroidX Media3 ExoPlayer, Session et UI version 1.10.1
