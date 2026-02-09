# Changelog

## [1.0.0] - 2026-02-09

### Ajouté
- **Observateur de base de données** (`MessageAppDatabaseObserver.java`) : affiche dans la console tous les événements de modification de la base (ajout, suppression, modification d'utilisateurs, messages et canaux).
- **Fenêtre principale** (`MessageAppMainView.java`) : JFrame avec titre "MessageApp" et icône UBO (`logo_20.png`).
- **Barre de menu** :
  - Menu **"Fichier"** avec entrée **"Quitter"** (icône `exitIcon_20.png`, tooltip "Fermer l'application").
  - Menu **"?"** avec entrée **"A propos"** (icône `editIcon_20.png`).
- **Boîte de dialogue "A propos"** : affiche le logo UBO (`logo_50.png`), "UBO M2-TIIL" et "Département Informatique".
- **Sélecteur de répertoire** (`JFileChooser`) : s'affiche au lancement pour choisir le répertoire d'échange (mode répertoires uniquement, boucle tant que la sélection n'est pas valide).
- **Look&Feel système** : utilisation du Look&Feel natif Windows via `UIManager.setLookAndFeel()`.

### Modifié
- **`DataManager.java`** : passage en `public` des méthodes `addObserver()` et `removeObserver()` (anciennement `protected`) pour permettre l'enregistrement d'observateurs depuis l'extérieur du package.
- **`MessageApp.java`** : implémentation des méthodes `initLookAndFeel()`, `initGui()`, `initDirectory()` et `show()`.
