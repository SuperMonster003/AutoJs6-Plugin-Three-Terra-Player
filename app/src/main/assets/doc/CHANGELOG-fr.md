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
