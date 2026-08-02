# Lecteur audio

Le Lecteur audio ajoute un contrôleur intégré et une lecture en arrière-plan pour les extensions déjà reconnues par l'explorateur AutoJs6:

`aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave`

Lorsque le plugin est installé, choisissez Lire l'audio parmi les actions principales de l'explorateur. Lorsqu'il est absent, AutoJs6 conserve son flux ACTION_VIEW externe en lecture seule, de sorte qu'une autre application audio installée peut encore ouvrir le fichier.

La lecture utilise Media3 et peut continuer en arrière-plan. Sur Android 13+, l'autorisation de notifications est facultative et affecte uniquement les commandes du volet de notifications. Son refus n'empêche pas la lecture.

Le plugin nécessite la version 5269+ d'AutoJs6. Il ne contient aucune bibliothèque native et ne dépend pas de l'ABI de l'appareil.

Limites de sécurité et de confidentialité:

- L'entrée de l'explorateur exige l'autorisation de signature AutoJs6 et valide le protocole v2, les deux content URI, ClipData, les métadonnées et les droits de lecture seule.
- L'entrée Android indépendante accepte uniquement des URI `content` en lecture seule avec `audio/*` et ne transmet ni les extras de l'appelant ni les droits étendus.
- Le plugin ne demande aucune autorisation de stockage ou de réseau et ne modifie jamais le fichier source.
- La correspondance de l'extension ne garantit pas le décodage. La compatibilité dépend de Media3, Android, des codecs de l'appareil et du contenu.
- Après une erreur de décodage, le fichier peut être ouvert dans une autre application compatible et ce plugin est exclu du sélecteur.
