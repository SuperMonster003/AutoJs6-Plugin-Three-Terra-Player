3-Terra Player est le plugin de lecture audio du gestionnaire de fichiers d'AutoJs6 et sert aussi de lecteur audio local autonome. Touchez un fichier audio dans le gestionnaire de fichiers pour le lire; il prend en charge la lecture continue d'un dossier, les listes de lecture locales, une minuterie de veille, les boucles A-B et la lecture en arrière-plan, sans jamais modifier les fichiers source.

### Prise en main

1. Vérifiez que ce plugin est activé dans le "Centre des plugins" d'AutoJs6.
2. Dans le gestionnaire de fichiers d'AutoJs6, touchez un fichier audio et choisissez "Lire l'audio"; le lecteur s'ouvre et lance la lecture.
3. Pour lire plusieurs fichiers d'un coup, sélectionnez-les par un appui long puis choisissez "Lire les fichiers audio sélectionnés" dans la barre d'outils.
4. Pour l'utiliser sans AutoJs6, ouvrez ce plugin depuis son icône et touchez "Ouvrir des fichiers audio".

### Fonctionnalités principales

- **Lecture continue**: Après l'ouverture d'un fichier audio, les autres fichiers audio du même dossier sont mis en file par nom, jusqu'à 128; passez à une piste ou retirez-la dans "File de lecture".
- **Lecture de la sélection**: Sélectionnez plusieurs fichiers audio dans le gestionnaire de fichiers et choisissez "Lire les fichiers audio sélectionnés" pour en lire jusqu'à 128 dans l'ordre de sélection.
- **Listes de lecture locales**: Ouvrez des listes M3U/M3U8, PLS, XSPF et autres, jusqu'à 128 éléments; gardez les fichiers audio dans le dossier de la liste et choisissez ce dossier quand c'est demandé après une ouverture depuis l'icône.
- **Commandes de lecture**: Piste précédente ou suivante, déplacement dans la piste, retour et avance rapides; basculez entre "Lecture séquentielle", "Lecture aléatoire" et "Répéter la piste actuelle", et choisissez de 0,5x à 2x dans "Vitesse de lecture".
- **Minuterie de veille**: Dans "Minuterie de veille", choisissez une durée ou "Arrêter après la piste actuelle"; le volume baisse progressivement juste avant l'arrêt.
- **Boucle A-B**: Touchez "Boucle A-B" une fois pour fixer le début, puis une deuxième fois pour fixer la fin, et ce passage se répète; une troisième touche l'efface.
- **Lecture en arrière-plan**: La lecture continue après avoir quitté le lecteur ou verrouillé l'écran, avec des commandes dans la notification multimédia et sur l'écran de verrouillage.
- **Reprise de la lecture**: Une nouvelle ouverture depuis l'icône restaure la dernière file, la piste et la position, en pause; vous pouvez désactiver "Reprendre la lecture" dans "Paramètres".
- **Apparence et paramètres**: La langue, le mode nuit et la "Couleur du thème" suivent AutoJs6 par défaut; dans "Paramètres", vous pouvez aussi choisir une icône dans "Icône du lanceur", "Rechercher des mises à jour" et consulter "Historique des versions".

### Prérequis

- Android 7.0 ou version ultérieure.
- La lecture depuis le gestionnaire de fichiers nécessite AutoJs6 6.8.0 ou version ultérieure; le lecteur autonome ouvert depuis son icône n'a pas besoin d'AutoJs6.
- Sur Android 13 ou version ultérieure, la notification multimédia et les commandes de l'écran de verrouillage nécessitent l'autorisation des notifications; la lecture fonctionne même si elle est refusée.

### Questions fréquentes

**Raisons pour lesquelles "Lire l'audio" n'apparaît pas dans le gestionnaire de fichiers**

Vérifiez qu'AutoJs6 remplit la condition de version et que ce plugin est activé dans le "Centre des plugins"; une fois les deux réunies, les fichiers audio proposent "Lire l'audio".

**Cas où un fichier audio ne peut pas être lu**

Le décodage dépend de l'appareil et du fichier: des codecs rares ou des fichiers endommagés peuvent ne pas être lus. En cas d'échec, touchez "Ouvrir avec une autre application" pour utiliser un autre lecteur.

**Raisons pour lesquelles d'autres pistes du même dossier sont mises en file**

C'est la lecture continue: l'ouverture d'un seul fichier audio met en file les autres fichiers audio du même dossier par nom, sans les sous-dossiers. Retirez les pistes inutiles dans "File de lecture".

**Contenu de l'historique de reprise**

Seulement la dernière file ouverte depuis l'icône et son état de lecture, conservés sur cet appareil uniquement; désactiver "Reprendre la lecture" les efface aussitôt.

Plus de détails sur la [page du projet](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player).
