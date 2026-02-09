# Changelog

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
- **Observateur de base de données** (`MessageAppDatabaseObserver.java`) : affiche dans la console tous les événements de modification de la base (ajout, suppression, modification d'utilisateurs, messages et canaux).
- **Fenêtre principale** (`MessageAppMainView.java`) : JFrame avec titre "MessageApp" et icône UBO (`logo_20.png`).
- **Barre de menu** :
  - Menu **"Fichier"** avec entrée **"Quitter"** (icône `exitIcon_20.png`, tooltip "Fermer l'application").
  - Menu **"?"** avec entrée **"A propos"**.
- **Boîte de dialogue "A propos"** : affiche le logo UBO (`logo_50.png`), "UBO M2-TIIL" et "Département Informatique".
- **Sélecteur de répertoire** (`JFileChooser`) : s'affiche au lancement pour choisir le répertoire d'échange.
- **Look&Feel système** : utilisation du Look&Feel natif Windows via `UIManager.setLookAndFeel()`.

### Modifié
- **`DataManager.java`** : passage en `public` des méthodes `addObserver()` et `removeObserver()`.
- **`MessageApp.java`** : implémentation des méthodes `initLookAndFeel()`, `initGui()`, `initDirectory()` et `show()`.
