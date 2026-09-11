******

### Historique des versions

******

# v1.5.0

###### 2026/09/11

* `Fonctionnalité` La pochette génère désormais en direct une palette lisible pour le dégradé, la barre, les commandes et la file; les écrans s'étendent bord à bord autour des barres système, découpes et zones gestuelles
* `Fonctionnalité` Les réglages de lecture proposent une vitesse par défaut mémorisée, des pas de recul et d'avance de 5 / 10 / 15 / 30 secondes et une action en fin de file: arrêter, revenir au début et mettre en pause ou relire
* `Fonctionnalité` Ajout de transitions discrètes pour lecture / pause et les pochettes, ainsi que de retours haptiques conformes au système pour les actions clés; si les animations système sont coupées, les états changent directement
* `Correctif` Les choix des boîtes de dialogue de langue et de mode nuit utilisent désormais Material Body1 en 16sp au lieu du texte de liste système surdimensionné
* `Amélioration` Le décodage des pochettes et l'échantillonnage couleur limité à 64x64 quittent le thread principal; toute couleur conserve les seuils de contraste existants de 4,5:1 pour le texte et 3:1 pour les contours
* `Amélioration` Étude de visualisation et prototype RMS par blocs terminés: Visualizer reste exclu car il exige l'autorisation d'enregistrement, tandis qu'une dérivation PCM Media3 sans permission est documentée pour de futurs tests de performance
* `Amélioration` Uniformiser la mise en page du README et la gestion des versions de la plateforme Gradle
* `Amélioration` Simplifier la description du plugin et normaliser la ponctuation des ressources multilingues
* `Amélioration` Renommer l'entrée de visualisation externe en External Viewer pour unifier la sémantique de la visionneuse
* `Amélioration` Ouvrir la page intégrée de l'historique des versions depuis le bouton correspondant de la boîte de dialogue de mise à jour
* `Amélioration` La vérification de compilation rejette les dépendances natives involontaires et produit un rapport JSON

# v1.4.1

###### 2026/08/31

* `Fonctionnalité` L'application autonome restaure désormais, lorsqu'elle est rouverte depuis le lanceur, la dernière file d'attente, la piste active, la position d'arrêt, le mode de répétition, l'état aléatoire et la vitesse; la session restaurée reste en pause et seules les files du sélecteur système disposant d'un accès en lecture durable sont enregistrées
* `Correctif` Correction du menu supplémentaire en haut à droite du lecteur qui affichait, avec certains thèmes, du texte blanc sur fond blanc et rendait l'entrée Paramètres illisible
* `Amélioration` La source AutoJs6 du réglage de couleur du thème porte désormais partout le libellé `Suivre AutoJs6`; le sélecteur affiche directement la valeur HEX de la couleur de l'hôte
* `Amélioration` Révision manuelle de base achevée pour les dix traductions du README, du CHANGELOG et des instructions du plugin, avec migration des liens du projet et de mise à jour intégrée vers le dépôt officiel 3-Terra Player

# v1.4.0

###### 2026/08/29

* `Fonctionnalité` Notification multimédia du système entièrement revue: nouveaux boutons précédent / suivant / mode aléatoire / quitter, icône monochrome propre à l'application, et état du mode aléatoire synchronisé en temps réel avec le lecteur
* `Correctif` Correction du plugin parfois marqué automatiquement comme défectueux et désactivé par le centre des plugins d'AutoJs6: les informations du plugin et l'action du gestionnaire de fichiers utilisent désormais des points de terminaison de service distincts
* `Correctif` Vider la file de lecture ne laisse plus d'informations obsolètes: l'écran passe dans un état vide explicite et les commandes de lecture, de saut, de vitesse, de minuterie et A-B sont désactivées ensemble
* `Correctif` Correction de l'ombre du bouton de lecture coupée par la zone inférieure; les boutons radio, cases à cocher, barres de progression et boutons des dialogues de paramètres et de mise à jour suivent désormais la couleur du thème
* `Amélioration` L'application et le plugin sont officiellement renommés 3-Terra Player: l'ID d'application reste inchangé, la mise à niveau s'installe directement par-dessus et les réglages existants sont conservés
* `Amélioration` Le menu en haut à droite du lecteur est allégé pour ne garder que l'entrée Paramètres; le bouton de palette, redondant avec la page des paramètres, est supprimé

# v1.3.0

###### 2026/08/29

* `Fonctionnalité` Ajout d'un mode application autonome: sans AutoJs6, choisissez jusqu'à 128 fichiers audio depuis l'écran de démarrage et lisez-les à la suite
* `Fonctionnalité` Ajout d'une page de paramètres: langue, mode nuit, couleur du thème, reprise de lecture, recherche de mises à jour, historique des versions et informations sur l'application réunis au même endroit; la langue et l'apparence suivent AutoJs6 par défaut et reviennent aux valeurs intégrées quand l'hôte est indisponible
* `Fonctionnalité` Ajout de la recherche de mises à jour manuelle et automatique quotidienne, avec possibilité d'ignorer une version donnée et un historique des versions multilingue intégré
* `Correctif` Correction de la couleur du thème signalant toujours la couleur de l'hôte comme indisponible en mode Suivre AutoJs6
* `Correctif` Tous les éléments reconnus comme audio par l'hôte dans le gestionnaire de fichiers (WMA inclus désormais) peuvent maintenant être lus directement par ce plugin
* `Amélioration` La reprise de lecture ne mémorise que le dernier fichier ouvert: ouvrir un autre fichier ou terminer la lecture efface automatiquement l'ancien enregistrement
* `Amélioration` L'écran de lecture conserve une zone d'informations fixe de trois lignes et complète la fréquence d'échantillonnage et le débit; la mise en page ne bouge plus au changement de titre ni pendant le chargement des étiquettes
* `Amélioration` La boucle A-B devient un cycle de trois appuis pour définir le point A, définir le point B puis effacer; l'espacement et l'alignement des icônes des commandes du bas sont également peaufinés

# v1.2.2

###### 2026/08/27

* `Correctif` Correction de tous les fichiers audio signalant une demande audio non valide après une mise à niveau du plugin par-dessus l'ancienne version: l'entrée accepte désormais les anciennes requêtes en lecture seule mises en cache par un hôte en cours d'exécution
* `Correctif` Correction d'extensions prises en charge comme `ogg` et `opus` rejetées à tort sur des appareils aux tables MIME défaillantes: une liste blanche d'extensions fournit désormais des types audio canoniques stables
* `Amélioration` Les requêtes rejetées consignent désormais un code de motif respectueux de la vie privée (sans nom de fichier, chemin ni URI), pour faciliter les retours et le diagnostic

# v1.2.1

###### 2026/08/27

* `Fonctionnalité` Ouvrir un titre depuis le gestionnaire de fichiers découvre désormais les autres fichiers audio du même dossier et les enchaîne dans l'ordre naturel des noms de fichiers: jusqu'à 128 titres, à partir du titre choisi
* `Correctif` Correction de requêtes de lecture légitimes rejetées sur Android 7.0 à cause d'un indicateur de fenêtre ajouté automatiquement par le système
* `Correctif` Correction d'un plantage possible lors du passage automatique à un titre du même dossier
* `Amélioration` La découverte dans le même dossier est strictement en lecture seule et bornée: pas de récursion dans les sous-dossiers, pas d'adresse de fichier devinée, pas d'autorisation de stockage, et les vidéos portant le même nom qu'un fichier audio n'entrent jamais dans la file
* `Amélioration` La session de lecture du dossier est détenue par le service de lecture en arrière-plan et se ferme automatiquement au remplacement de la file, à la fin de la lecture ou à la destruction du service
* `Dépendance` L'interface d'action du gestionnaire de fichiers intégrée passe à la v12 rétrocompatible; la version minimale de l'hôte reste build 5276

# v1.2.0

###### 2026/08/27

* `Fonctionnalité` Ajout de la lecture multisélection: cochez jusqu'à 128 fichiers audio dans le gestionnaire de fichiers et ajoutez-les à la file d'un seul appui, exactement dans l'ordre de sélection
* `Fonctionnalité` Ajout des commandes précédent / suivant, d'un panneau de file permettant de sauter vers un titre ou de le retirer, et des modes séquentiel / aléatoire / répétition d'un titre
* `Fonctionnalité` Ajout d'une minuterie de veille: préréglages de 15 / 30 / 60 minutes ou durée personnalisée, avec arrêt après le titre en cours et fondu sur les 5 dernières secondes
* `Fonctionnalité` Ajout de la boucle A-B pour réécouter en boucle un passage choisi
* `Amélioration` Les commandes multimédias du système gagnent précédent, suivant et les sauts de 10 secondes; les métadonnées de la notification se mettent à jour au fil de la file
* `Amélioration` La validation des requêtes du gestionnaire de fichiers s'étend aux cibles multisélection ordonnées, tout en conservant des droits minimaux en lecture seule
* `Dépendance` L'interface d'action du gestionnaire de fichiers passe de la v2 à la v4; la version minimale de l'hôte monte à build 5276
* `Dépendance` Ajout d'AndroidX RecyclerView 1.4.0

# v1.1.0

###### 2026/08/27

* `Fonctionnalité` Nouvel écran de lecture: pochette d'album, étiquettes titre / artiste / album, informations techniques et barre de progression déplaçable
* `Fonctionnalité` Ajout du recul et de l'avance de 10 secondes, d'un bouton de répétition d'un titre et de la vitesse de lecture de 0,5x à 2x
* `Fonctionnalité` Lecture automatique des étiquettes et de la pochette intégrées, synchronisées avec la notification multimédia du système
* `Fonctionnalité` Ajout de la reprise de lecture: rouvrir le même fichier reprend à la dernière position, effacée automatiquement une fois la lecture terminée
* `Amélioration` Après la fin de la lecture ou un échec de décodage, le titre peut être relancé directement depuis le lecteur, sans revenir au gestionnaire de fichiers
* `Amélioration` Les erreurs de lecture indiquent le code d'erreur exact, pour faciliter le diagnostic et les signalements
* `Dépendance` Ajout d'AndroidX ConstraintLayout 2.2.1
* `Dépendance` Suppression de la dépendance AndroidX Media3 UI devenue inutile

# v1.0.1

###### 2026/08/08

* `Correctif` Correction d'une liaison de service vide lors de l'activation du plugin dans le centre des plugins
* `Amélioration` Nom, description et instructions du plugin plus simples et naturels

# v1.0.0

###### 2026/08/02

* `Fonctionnalité` Première version stable: une action de lecture audio pour le gestionnaire de fichiers AutoJs6 couvrant 18 extensions audio courantes, un appui suffit pour lire
* `Fonctionnalité` Lecture stable fondée sur Media3 ExoPlayer et MediaSessionService: lecture en arrière-plan, gestion du focus audio, pause automatique à la déconnexion du périphérique de sortie et commandes multimédias du système
* `Fonctionnalité` Accepte les demandes d'ouverture audio en lecture seule venant d'autres applications; en cas d'échec de décodage, le fichier peut être confié à une autre application compatible tout en évitant les boucles
* `Fonctionnalité` Base de sécurité stricte: aucune autorisation de stockage ni d'accès à Internet, seuls des droits minimaux en lecture seule sont acceptés et transmis
* `Fonctionnalité` Informations du plugin, instructions, README et journal des modifications disponibles en 10 langues
* `Dépendance` Ajout d'AndroidX Media3 ExoPlayer, Session et UI 1.10.1
