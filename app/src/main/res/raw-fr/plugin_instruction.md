# 3-Terra Player

Le Lecteur audio fonctionne à la fois comme plugin AutoJs6 et comme application autonome. Il ajoute un contrôleur et une lecture en arrière-plan pour tous les types MIME audio reconnus par le gestionnaire, avec une couverture explicite des extensions suivantes:

`aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave, wma`

Lorsque le plugin est installé, Lire l'audio sur un fichier démarre sur cette piste et peut découvrir une file bornée et naturellement triée de fichiers audio frères directs lisibles. La sélection explicite de 1 à 128 fichiers conserve l'ordre de l'hôte. Sans le plugin, l'hôte garde son flux ACTION_VIEW externe en lecture seule pour un fichier.

Le lanceur peut ouvrir jusqu'à 128 documents audio sans AutoJs6. Les réglages autonomes proposent langue, mode nuit, couleur du thème, reprise, mises à jour, versions ignorées, historique des versions et informations sur l'application et le développeur. La langue, le mode nuit et la couleur suivent AutoJs6 par défaut lorsque son contrat protégé est disponible.

La lecture utilise Media3 et continue en arrière-plan. Elle propose précédent / suivant, file modifiable, modes séquentiel / aléatoire / répétition d'un titre, minuterie, boucle A-B et vitesse. La reprise ne mémorise que le fichier ouvert le plus récemment, efface cette position dès qu'un autre fichier est ouvert et ne conserve jamais une lecture terminée. Sur Android 13+, l'autorisation de notifications est facultative.

La découverte dans le même dossier nécessite AutoJs6 6.8.0 build 5276+ et Explorer Action v12 ; les capacités futures ne relèveront pas ce seuil. Les hôtes sans session facultative conservent la lecture du fichier sélectionné.

Limites de sécurité et de confidentialité:

- L'entrée exige la signature de l'hôte et valide le protocole v12, les cibles et ClipData ordonnés, les identifiants, la relation au parent, les métadonnées, les capacités déclarées et les droits de lecture seule.
- L'entrée Android indépendante accepte uniquement des URI `content` en lecture seule avec un type MIME audio ou WMA hérité et ne transmet ni les extras de l'appelant ni les droits étendus.
- L'application ne demande aucun stockage et ne modifie jamais le fichier source. Internet sert uniquement aux vérifications de versions GitHub lancées par l'utilisateur ou quotidiennes.
- Une Host Session par requête est liée à l'UID du plugin et permet seulement de lister sans récursion le parent direct du fichier sélectionné et d'ouvrir celui-ci ou un fichier audio frère direct lisible. La lecture reçoit des routes synthétiques opaques, jamais de chemins de système de fichiers, et ne devine aucun URI frère.
- La correspondance de l'extension ne garantit pas le décodage. La compatibilité dépend de Media3, Android, des codecs de l'appareil et du contenu.
- Après une erreur de décodage, le fichier peut être ouvert dans une autre application compatible et ce plugin est exclu du sélecteur.
