3-Terra Player (anciennement Audio Player) est à la fois un plugin de lecture audio pour le gestionnaire de fichiers AutoJs6 et un lecteur audio autonome et épuré. Une fois installé et activé, il suffit de toucher un fichier audio dans le gestionnaire de fichiers AutoJs6 pour le lire directement, sans passer par un lecteur tiers; il peut aussi s'ouvrir depuis l'écran d'accueil comme une application ordinaire, pour choisir plusieurs fichiers audio et les lire à la suite.

### Utilisation

1. Téléchargez le dernier APK du plugin depuis la page [Releases](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player/releases) et installez-le sur l'appareil qui exécute AutoJs6.
2. Ouvrez le centre des plugins d'AutoJs6 et vérifiez que `3-Terra Player` est bien reconnu et activé.
3. Dans le gestionnaire de fichiers AutoJs6, touchez un fichier audio et choisissez `Lire l'audio`; ou faites un appui long pour en sélectionner plusieurs, puis choisissez `Lire les fichiers audio sélectionnés` dans la barre d'outils.
4. Vous pouvez aussi ouvrir `3-Terra Player` directement depuis l'écran d'accueil et choisir plusieurs fichiers audio via le bouton de sélection de fichiers pour lancer la lecture.

Si le plugin n'apparaît pas dans le centre des plugins, mettez d'abord AutoJs6 à niveau vers 6.8.0 (code de version 5276) ou une version ultérieure. Le plugin lui-même prend en charge Android 7.0 (API 24) et versions ultérieures, et le mode application autonome ne dépend pas d'AutoJs6.

### Formats audio pris en charge

L'entrée du gestionnaire de fichiers déclare le type audio générique `audio/*` et couvre explicitement les 19 extensions suivantes, pour rester compatible avec les tables MIME incomplètes ou anciennes de certains appareils:

```text
aac, ac3, amr, awb, flac, m4a, m4b, m4r, mka, mp1, mp2, mp3, mpga, oga, ogg, opus, wav, wave, wma
```

Une extension prise en charge ne garantit pas le décodage: la lecture réelle dépend de Media3, de la version d'Android, des codecs de l'appareil et du contenu du fichier. Si un fichier ne peut pas être lu, le panneau d'erreur propose de l'ouvrir avec une autre application.

### Autorisations et sécurité

Le plugin ne demande aucune autorisation de stockage, n'accède aux fichiers audio qu'en lecture seule et n'écrit jamais dans les fichiers sources; le réseau ne sert qu'aux vérifications de mise à jour déclenchées par l'utilisateur ou une fois par jour. Sur Android 13+, l'autorisation de notifications est facultative; la refuser masque seulement les commandes de notification, sans affecter la lecture.

Pour plus de détails, consultez la [page du projet](https://github.com/SuperMonster003/AutoJs6-Plugin-Three-Terra-Player) et la [documentation AutoJs6](https://docs.autojs6.com).
