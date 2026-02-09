# Changelog

## [1.2.0] - 2026-02-09

### Ajouté
- **Thème Discord** : refonte graphique complète de l'interface de connexion et d'inscription.
  - **Couleurs** : utilisation de la palette Discord (Dark Theme `#36393f`, Blurple `#5865F2`, Green `#57F287`, Red `#ED4245`).
  - **Composants personnalisés** :
    - `DiscordButton` : boutons sans bordure, coins arrondis, effet hover.
    - `DiscordTextField` / `DiscordPasswordField` : champs de saisie sur fond sombre avec padding.
    - `DiscordRoundPanel` : conteneur avec coins arrondis pour les formulaires.
    - `DiscordTheme` : classe utilitaire centralisant les constantes de style.

### Modifié
- **`LoginPanel`** : interface centrée dans une "carte", logo UBO, champs et boutons stylisés.
- **`RegistrationPanel`** : formulaire d'inscription cohérent avec le nouveau thème.
- **`MessageAppMainView`** : fond de fenêtre adapté au thème sombre.

---

## [1.1.0] - 2026-02-09

### Ajouté
- **Composant de connexion** (`LoginPanel.java`) : panel avec logo, champ tag, bouton connexion, lien vers inscription.
- **Composant d'inscription** (`RegistrationPanel.java`) : panel avec champs nom/tag/mot de passe, bouton créer, validation des champs obligatoires et unicité du tag.
- **Contrôleur des comptes** (`AccountController.java`) : logique de connexion et d'inscription, recherche d'utilisateur par tag.
- **Gestion de session** : `MessageApp` implémente `ISessionObserver` pour réagir aux connexions/déconnexions.
- **Navigation CardLayout** : bascule entre les panels Login, Registration et Main.

### Modifié
- **`MessageAppMainView.java`** : ajout de `CardLayout` pour la navigation entre panels, intégration de `LoginPanel` et `RegistrationPanel`.
- **`MessageApp.java`** : ajout de `Session`, `AccountController`, implémentation de `ISessionObserver`.

---

## [1.0.0] - 2026-02-09

### Ajouté
- **Observateur de base de données** (`MessageAppDatabaseObserver.java`) : affiche dans la console tous les événements de modification de la base.
- **Fenêtre principale** (`MessageAppMainView.java`) : JFrame avec titre "MessageApp" et icône UBO.
- **Barre de menu** : "Fichier" (Quitter) et "?" (A propos).
- **Sélecteur de répertoire** (`JFileChooser`).
- **Look&Feel système**.
