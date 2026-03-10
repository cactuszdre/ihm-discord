# Changelog

## [2.1.0] - 2026-03-10

### Ajouté — Modification du nom d'utilisateur (SRS-MAP-USR-009)
- **`IUserActionListener.onEditUserName(String)`** : nouvelle action pour modifier le nom.
- **`UserController.onEditUserName()`** : validation (non vide) + persistance via `DataManager.sendUser()`.
- **`UserListView`** : clic droit sur son propre nom → "Modifier mon nom" (`JOptionPane.showInputDialog`).
- **`IUserView.setCurrentUser(User)`** : permet à la vue de savoir quel utilisateur est connecté (pour n'afficher les options que sur son propre profil).

### Ajouté — Suppression de compte (SRS-MAP-USR-010)
- **`IUserActionListener.onDeleteAccount()`** : nouvelle action pour supprimer son compte.
- **`UserController.onDeleteAccount()`** : suppression du fichier `.usr` + notification du coordinateur.
- **`UserController.IAccountDeletionListener`** : interface de callback pour déclencher la déconnexion après suppression.
- **`DataManager.deleteUser(User)`** : nouvelle méthode de suppression.
- **`EntityManager.deleteUserFile(User)`** : recherche et supprime le fichier utilisateur par UUID dans le répertoire d'échange.
- **`UserListView`** : clic droit sur son propre nom → "Supprimer mon compte" (texte rouge, confirmation via `JOptionPane.showConfirmDialog`).
- **`MessageApp`** : câblage `IAccountDeletionListener` → `mSession.disconnect()` avec `mLastConnectedUser = null` pour éviter la sauvegarde du statut online.

### Ajouté — Notifications visuelles DM / @mention (SRS-MAP-MSG-010)
- **`NotificationManager.java`** : `IDatabaseObserver` qui détecte les DM et les @mentions dans les messages entrants. Ignore les messages envoyés par l'utilisateur lui-même. Utilise un flag `mInitialized` pour ignorer le replay des messages existants lors de l'enregistrement de l'observateur.
- **`ToastNotification.java`** : popup non-modal (`JWindow`) stylisé Discord, positionné en bas à droite de l'écran, auto-fermeture après 4 secondes via `Timer`.
- **`MessageApp`** : instanciation dans `initMainControllers()`, `dispose()` dans `notifyLogout()`.

### Corrigé
- **Statut en ligne en multi-instance** : supprimé `resetAllUsersOffline()` au démarrage qui écrasait le statut online des instances déjà en cours d'exécution.
- **NPE au lancement** : ajout de vérifications `null` sur le chargement des icônes (`logo_20.png`, `exitIcon_20.png`, `logo_50.png`) dans `MessageAppMainView`.

### Amélioré
- **Ouverture automatique du dossier `bdd/`** : l'application utilise automatiquement le dossier `bdd/` comme répertoire d'échange s'il existe, sans afficher le `JFileChooser`. Fallback vers le sélecteur manuel sinon.

---

## [2.0.3] - 2026-03-09

### Ajouté — SonarQube (Docker)
- **`docker-compose.yml`** : serveur SonarQube LTS Community (v9.9.8) lancé via Docker (base H2 embarquée, sans PostgreSQL).
- **`build.gradle`** : plugin `org.sonarqube` (v5.1.0.4882) avec configuration projet (`sonar.projectKey`, `sonar.host.url`, token via `$SONAR_TOKEN`).

### Ajouté — CI/CD GitHub Actions
- **`.github/workflows/ci.yml`** : pipeline CI/CD déclenché sur `push` et `pull_request` vers `main`.
  - Build & compilation avec Gradle (Java 8, Temurin).
  - Génération du JAR et upload comme artifact téléchargeable.

### Commandes disponibles
- `docker compose up -d` — Démarrer SonarQube (http://localhost:9000).
- `docker compose down` — Arrêter SonarQube.
- `$env:SONAR_TOKEN="<token>"; .\gradlew sonar` — Lancer l'analyse de code.

---

## [2.0.2] - 2026-03-09

### Ajouté — Build Gradle
- **`build.gradle`** : configuration Gradle avec plugins `java` et `application`, `sourceSets` adapté au layout non-standard (`src/` comme racine), manifest JAR avec `Main-Class`.
- **`settings.gradle`** : nom du projet (`MessageApp`).
- **Wrapper Gradle 8.12** (`gradlew`, `gradlew.bat`, `gradle/wrapper/`) : permet de builder sans installation globale de Gradle.
- Génération du JAR exécutable dans `build/libs/MessageApp.jar`.

### Commandes disponibles
- `.\gradlew.bat build` — Compiler et générer le JAR.
- `.\gradlew.bat run` — Lancer l'application.
- `.\gradlew.bat jar` — Générer uniquement le JAR.

### Modifié
- **`.gitignore`** : ajout de `.gradle/` et `build/`.

---

## [2.0.1] - 2026-03-04

### Corrigé
- **Statut en ligne disparaît instantanément** : le champ `Online` n'était pas sérialisé dans les fichiers `.usr`. Quand le `WatchableDirectory` relisait le fichier, un nouvel objet `User` était créé avec `online=false` par défaut.
  - `DataFilesManager.writeUserFile()` écrit maintenant `Online=true/false`.
  - `DataFilesManager.readUser()` lit maintenant le champ `Online` et appelle `user.setOnline()`.
- **Badges non lus absents en temps réel** : `ChannelListView.setUnreadChannels()` appelait `repaint()` au lieu de reconstruire les composants. Les badges (JPanel) n'étaient créés que dans `createChannelRow()` et un `repaint()` ne peut pas en ajouter de nouveaux.
  - `setUnreadChannels()` appelle maintenant `rebuildChannelList()` qui reconstruit entièrement la liste des canaux.
  - Les canaux sont stockés dans `mCurrentChannels` pour permettre le rebuild.

### Modifié
- **`DataFilesManager.java`** : ajout constante `PROPERTY_KEY_USER_ONLINE`, lecture/écriture du champ `Online`.
- **`ChannelListView.java`** : nouveau champ `mCurrentChannels`, nouvelle méthode `rebuildChannelList()`, `setUnreadChannels()` corrigé.

---

## [2.0.0] - 2026-03-04

### Ajouté — Gestion des membres de canaux privés
- **`ChannelMembersDialog.java`** : nouveau `JDialog` modal pour gérer les membres d'un canal privé (ajouter/retirer des utilisateurs).
- **Menu contextuel "Gérer les membres"** : clic droit sur un canal privé (visible uniquement pour le créateur).
- **`IChannelActionListener`** : 3 nouvelles méthodes (`onManageMembers`, `onAddUserToChannel`, `onRemoveUserFromChannel`).
- **`IChannelView.showManageMembersDialog()`** : contrat View pour afficher le dialogue de gestion des membres.

### Ajouté — Message privé (DM)
- **`UserController.onSendDirectMessage()`** : implémentation complète remplaçant le stub. Cherche un canal DM existant ou en crée un nouveau (`DM-tag1-tag2`).
- **`UserController.IDirectMessageListener`** : interface pour notifier le coordinateur de la sélection d'un canal DM.
- **Câblage DM dans `MessageApp`** : clic droit sur un utilisateur → "Envoyer un message privé" → crée/sélectionne automatiquement le canal DM.

### Ajouté — Indicateurs graphiques
- **Statut en ligne actif** : `setOnline(true)` à la connexion, `setOnline(false)` à la déconnexion, persisté via `sendUser()`.
- **Messages non lus** : badge rouge (pastille) + nom du canal en **gras** dans la sidebar pour les canaux non lus.
- **Notification sonore** : `Toolkit.beep()` quand un message arrive sur un canal non actif.
- **`IChannelView.setUnreadChannels()`** : contrat View pour les indicateurs de non lu.
- **`ChannelController`** : tracking des timestamps de dernière lecture (`mLastReadTimestamps`) et des canaux non lus (`mUnreadChannelIds`).

### Ajouté — Qualité de code (SKILL.md §4.5, §4.6)
- **`dispose()`** sur les 3 controllers (`ChannelController`, `MessageController`, `UserController`) pour désenregistrer les observers à la déconnexion.
- **`DiscordTheme.BACKGROUND_HOVER`** : nouvelle couleur remplaçant les `new Color(60, 63, 69)` hardcodés.
- **`DiscordTheme.WARNING`** : nouvelle couleur remplaçant le `new Color(250, 166, 26)` hardcodé.
- **`MessageApp.mLastConnectedUser`** : sauvegarde de la référence utilisateur pour le logout (car `Session.disconnect()` nullifie avant notification).

### Modifié
- **`ChannelController.java`** : ajout de la gestion des membres, tracking des messages non lus, `dispose()`.
- **`ChannelListView.java`** : menu "Gérer les membres", badges non lus, utilisation de `DiscordTheme.BACKGROUND_HOVER`.
- **`UserController.java`** : implémentation DM complète, `dispose()`.
- **`MessageApp.java`** : câblage DM, statut en ligne, désenregistrement des observers au logout.
- **`MessageController.java`** : ajout de `dispose()`.
- **`UserListView.java`** : remplacement couleurs hardcodées par `DiscordTheme.BACKGROUND_HOVER`.
- **`MessageInputView.java`** : remplacement couleur hardcodée par `DiscordTheme.WARNING`.
- **`IChannelActionListener.java`** : +3 méthodes.
- **`IChannelView.java`** : +2 méthodes.

---

## [1.0.0] - 2026-03-02

### Ajouté — Infrastructure
- **Observateur de base de données** (`MessageAppDatabaseObserver.java`) : affiche dans la console tous les événements de modification de la base.
- **Fenêtre principale** (`MessageAppMainView.java`) : JFrame avec titre "MessageApp", icône UBO, `CardLayout` pour la navigation.
- **Barre de menu** : "Fichier" (Déconnexion, Quitter) et "?" (A propos).
- **Sélecteur de répertoire** (`JFileChooser`).
- **Look&Feel système**.

### Ajouté — Thème Discord
- **`DiscordTheme`** : palette de couleurs Discord (Dark Theme, Blurple, Green, Red) et polices centralisées.
- **`DiscordButton`** : boutons sans bordure, coins arrondis, effet hover.
- **`DiscordTextField`** / **`DiscordPasswordField`** : champs de saisie sur fond sombre.
- **`DiscordRoundPanel`** : conteneur avec coins arrondis.

### Ajouté — Authentification
- **`LoginPanel`** : interface de connexion centrée dans une "carte", logo UBO, champ tag + mot de passe.
- **`RegistrationPanel`** : formulaire d'inscription (nom, tag, mot de passe), validation des champs obligatoires et unicité du tag.
- **`AccountController`** : logique de connexion (vérification tag + mot de passe) et d'inscription.
- **Gestion de session** : `MessageApp` implémente `ISessionObserver`, navigation Login ↔ Registration ↔ Main.

### Ajouté — Canaux
- **`ChannelListView`** : sidebar gauche affichant les canaux (# public, 🔒 privé) avec recherche en temps réel.
- **`ChannelController`** : création de canaux (public/privé), suppression (propriétaire uniquement), quitter un canal privé, recherche.
- **`IChannelView`** / **`IChannelActionListener`** : contrats MVC.

### Ajouté — Messages
- **`MessageListView`** : affichage des messages avec avatar (initiale), @tag, date, mise en évidence des @mentions.
- **`MessageInputView`** : barre de saisie avec compteur de caractères (max 200), autocomplétion des @mentions.
- **`MessageController`** : envoi de message dans un canal, suppression (auteur uniquement), recherche, filtrage par canal.
- **`IMessageView`** / **`IMessageActionListener`** : contrats MVC.

### Ajouté — Utilisateurs
- **`UserListView`** : sidebar droite affichant les utilisateurs avec avatar, indicateur de présence (pastille verte/grise), recherche.
- **`UserController`** : affichage et recherche des utilisateurs.
- **`IUserView`** / **`IUserActionListener`** : contrats MVC.
- **Menu contextuel** : clic droit → "Envoyer un message privé" (stub).
