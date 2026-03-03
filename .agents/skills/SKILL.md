---
name: MessageApp Development Guide
description: Architecture patterns, conventions, and best practices for developing the MessageApp Java Swing application.
---

# MessageApp — Guide de développement

## 1. Architecture globale

L'application suit un pattern **MVC strict** (Model-View-Controller) avec une couche d'observation (Observer).

```
MessageAppLauncher (point d'entrée)
  └── MessageApp (coordinateur MVC, ISessionObserver)
        ├── DataManager (Model — façade d'accès aux données)
        │     ├── Database (stockage en mémoire)
        │     ├── EntityManager (sérialisation fichiers)
        │     └── WatchableDirectory (polling répertoire d'échange)
        ├── Session (Model — utilisateur connecté, notifie ISessionObserver)
        └── MessageAppMainView (View — JFrame principale, CardLayout)
              ├── LoginPanel / RegistrationPanel
              ├── ChannelListView (implements IChannelView)
              ├── MessageListView (implements IMessageView)
              ├── MessageInputView
              └── UserListView (implements IUserView)
```

### Flux MVC

1. **View → Controller** : via des interfaces `IXxxActionListener` (ex: `IChannelActionListener`)
2. **Controller → Model** : appels à `DataManager` et `Session`
3. **Model → Controller** : via `IDatabaseObserver` (le Controller s'enregistre comme observateur)
4. **Controller → View** : via des interfaces `IXxxView` (ex: `IChannelView.setChannels(...)`)

---

## 2. Patterns identifiés

### 2.1 Contrat View via interface (`IXxxView`)

Chaque vue implémente une interface qui définit ce que le Controller peut lui demander :

```java
// Contrat View
public interface IChannelView {
    void setChannels(List<Channel> channels);
    void setSelectedChannel(Channel channel);
    void showError(String message);
    void setActionListener(IChannelActionListener listener);
}

// Implémentation Swing
public class ChannelListView extends JPanel implements IChannelView { ... }
```

**Convention** : `I` + nom fonctionnel + `View` (ex: `IMessageView`, `IUserView`).

### 2.2 Contrat Controller via interface (`IXxxActionListener`)

Chaque View communique avec son Controller via une interface d'écoute :

```java
public interface IChannelActionListener {
    void onChannelSelected(Channel channel);
    void onCreatePublicChannel(String name);
    void onDeleteChannel(Channel channel);
    void onSearchChannel(String query);
}
```

**Convention** : `I` + nom fonctionnel + `ActionListener`.  
**Convention de méthodes** : `on` + verbe au passé composé ou action (ex: `onSendMessage`, `onDeleteChannel`).

### 2.3 Observer Pattern (base de données)

Les Controllers s'enregistrent comme `IDatabaseObserver` pour recevoir des notifications temps réel :

```java
public class ChannelController implements IChannelActionListener, IDatabaseObserver {
    public ChannelController(DataManager dm, Session session, IChannelView view) {
        this.mView = view;
        this.mView.setActionListener(this);     // View → Controller
        this.mDataManager.addObserver(this);     // Model → Controller
        this.refreshChannels();                  // chargement initial
    }
}
```

### 2.4 Observer Pattern (session)

`MessageApp` implémente `ISessionObserver` pour réagir aux connexions/déconnexions :

```java
public void notifyLogin(User user) {
    initMainControllers();        // câblage des contrôleurs post-connexion
    mMainView.showMainPanel();    // basculement de la vue
}
```

### 2.5 Design System centralisé (`DiscordTheme`)

Toutes les couleurs et polices sont centralisées dans `DiscordTheme` :

```java
DiscordTheme.BACKGROUND_DARK     // couleur de fond principale
DiscordTheme.BLURPLE              // couleur d'accentuation
DiscordTheme.FONT_NORMAL          // police standard 14px
```

Les composants UI personnalisés (`DiscordButton`, `DiscordTextField`, `DiscordPasswordField`, `DiscordRoundPanel`) appliquent automatiquement ce thème.

### 2.6 Navigation par `CardLayout`

La navigation entre Login, Registration et Main se fait via un `CardLayout` dans `MessageAppMainView` :

```java
private static final String CARD_LOGIN = "LOGIN";
private static final String CARD_REGISTER = "REGISTER";
private static final String CARD_MAIN = "MAIN";

mCardLayout.show(mContentPanel, CARD_LOGIN);
```

---

## 3. Conventions de code

### Nommage

| Élément | Convention | Exemple |
|---|---|---|
| Champs d'instance | Préfixe `m` (notation hongroise) | `mDataManager`, `mSession` |
| Champs statiques | Préfixe `UPPER_CASE` | `MAX_MESSAGE_LENGTH`, `IMAGES_PATH` |
| Interfaces | Préfixe `I` | `IChannelView`, `ILoginActionListener` |
| Méthodes listener | Préfixe `on` | `onSendMessage()`, `onChannelSelected()` |
| Méthodes init | Préfixe `init` | `initPanel()`, `initGui()` |
| Méthodes refresh | Préfixe `refresh` | `refreshChannels()`, `refreshMessages()` |

### Structure de package

```
com.ubo.tp.message
├── common/          # Utilitaires (Constants, DataFilesManager, PropertiesManager)
├── core/            # Couche métier & infrastructure
│   ├── database/    # Database, EntityManager, DbConnector, interfaces Observer
│   ├── directory/   # WatchableDirectory (surveillance de fichiers)
│   └── session/     # Session, ISession, ISessionObserver
├── datamodel/       # Entités métier (User, Message, Channel)
└── ihm/             # Couche présentation (MVC)
    ├── common/      # Composants UI réutilisables (DiscordButton, DiscordTheme...)
    ├── login/       # Login & Registration (Panel + Controller + Interfaces)
    ├── channel/     # Canaux (View + Controller + Interfaces)
    ├── message/     # Messages (View + Controller + Interfaces)
    └── user/        # Utilisateurs (View + Controller + Interfaces)
```

### Pattern d'un nouveau module

Pour ajouter une nouvelle fonctionnalité (ex: "Paramètres utilisateur") :

1. **Créer les interfaces** :
   - `ISettingsView` — contrat View
   - `ISettingsActionListener` — contrat Controller

2. **Créer la View** :
   - `SettingsPanel extends JPanel implements ISettingsView`
   - Utiliser `DiscordTheme` pour les couleurs/polices
   - Utiliser les composants `Discord*` pour les champs de saisie
   - Déléguer TOUTES les actions au listener (jamais de logique métier dans la View)

3. **Créer le Controller** :
   - `SettingsController implements ISettingsActionListener`
   - Si des données changent en temps réel : implémenter aussi `IDatabaseObserver`
   - Recevoir `DataManager` et `Session` en paramètres du constructeur
   - Appeler `mView.setActionListener(this)` dans le constructeur
   - Implémenter un `refresh*()` pour actualiser la vue

4. **Câbler dans `MessageApp`** :
   - Créer la vue dans `MessageAppMainView`
   - Instancier le Controller dans `MessageApp.initMainControllers()`

### Pattern d'un Controller

```java
public class XxxController implements IXxxActionListener, IDatabaseObserver {
    private DataManager mDataManager;
    private Session mSession;
    private IXxxView mView;

    public XxxController(DataManager dataManager, Session session, IXxxView view) {
        this.mDataManager = dataManager;
        this.mSession = session;
        this.mView = view;

        // Câblage MVC
        this.mView.setActionListener(this);
        this.mDataManager.addObserver(this);

        // Chargement initial
        this.refreshXxx();
    }

    // IXxxActionListener — actions utilisateur
    @Override
    public void onDoSomething(...) {
        // validation → appel Model → refresh
    }

    // IDatabaseObserver — notifications DB (implémenter les méthodes qu'on utilise,
    // laisser vide les autres avec /* Non utilisé */)
    @Override
    public void notifyXxxAdded(Xxx xxx) { refreshXxx(); }

    // Méthode interne de rafraîchissement
    private void refreshXxx() {
        // récupérer données depuis mDataManager
        // filtrer si nécessaire
        // mView.setXxx(filteredData);
    }
}
```

---

## 4. ⚠️ Anti-patterns et mauvaises pratiques identifiés

### 4.1 🔴 `IDatabaseObserver` oblige à implémenter 9 méthodes vides

**Problème** : L'interface `IDatabaseObserver` a 9 méthodes (`notifyMessageAdded`, `notifyMessageDeleted`, `notifyMessageModified`, + idem pour User et Channel). Chaque Controller doit toutes les implémenter, même celles qu'il n'utilise pas, ce qui produit du code inutile :

```java
// ChannelController — 6 méthodes vides sur 9
@Override public void notifyMessageAdded(Message m) { /* Non utilisé */ }
@Override public void notifyMessageDeleted(Message m) { /* Non utilisé */ }
@Override public void notifyMessageModified(Message m) { /* Non utilisé */ }
@Override public void notifyUserAdded(User u) { /* Non utilisé */ }
@Override public void notifyUserDeleted(User u) { /* Non utilisé */ }
@Override public void notifyUserModified(User u) { /* Non utilisé */ }
```

**Solution** : Créer un `DatabaseObserverAdapter` (classe abstraite avec implémentations vides par défaut) et n'override que les méthodes nécessaires :

```java
public abstract class DatabaseObserverAdapter implements IDatabaseObserver {
    @Override public void notifyMessageAdded(Message m) {}
    @Override public void notifyMessageDeleted(Message m) {}
    // ... toutes les méthodes avec un corps vide
}

// Utilisation :
public class ChannelController extends DatabaseObserverAdapter implements IChannelActionListener {
    // Ne redéfinit que notifyChannelAdded, notifyChannelDeleted, notifyChannelModified
}
```

### 4.2 🔴 `LoginPanel` n'a PAS d'interface `ILoginView`

**Problème** : Tous les modules (channel, message, user) ont une interface `IXxxView`, sauf le login. `AccountController` utilise directement la classe concrète `LoginPanel` et `RegistrationPanel`. Cela casse la cohérence MVC et empêche les tests unitaires.

**Solution** : Créer `ILoginView` et `IRegistrationView` comme pour les autres vues.

### 4.3 🟡 Constante `MAX_MESSAGE_LENGTH` dupliquée

**Problème** : La constante `MAX_MESSAGE_LENGTH = 200` est définie à la fois dans `MessageController` (ligne 26) ET dans `MessageInputView` (ligne 32). Si la valeur change, il faut penser à modifier les deux fichiers.

**Solution** : La déplacer dans une classe de constantes centralisée (ex: `Constants.java` qui existe déjà) ou ne la garder que dans le Controller (la View ne devrait pas connaître les règles métier).

### 4.4 🟡 Validation métier dans la View (`MessageInputView.doSend()`)

**Problème** : `MessageInputView.doSend()` vérifie `text.length() > MAX_MESSAGE_LENGTH` avant d'appeler le Controller. Dans un MVC strict, la View ne devrait faire AUCUNE validation métier — seulement un affichage visuel (le compteur orange/rouge est OK, mais bloquer l'envoi est une logique métier).

**Solution** : Laisser la View **toujours** appeler `mActionListener.onSendMessage(text)` et laisser le Controller décider de rejeter ou non. La View ne garde que le retour visuel (compteur de couleur).

### 4.5 🟡 Couleurs hardcodées dans les Views

**Problème** : Certaines couleurs sont codées en dur dans les Views au lieu d'utiliser `DiscordTheme` :

```java
// ChannelListView.java:300
row.setBackground(new Color(60, 63, 69));

// UserListView.java:229
row.setBackground(new Color(60, 63, 69));

// MessageInputView.java:166
mCharCounter.setForeground(new java.awt.Color(250, 166, 26));
```

**Solution** : Ajouter ces couleurs à `DiscordTheme` :

```java
public static final Color BACKGROUND_HOVER = new Color(60, 63, 69);
public static final Color WARNING = new Color(250, 166, 26);
```

### 4.6 🟡 Pas de désenregistrement des observers

**Problème** : Les Controllers s'enregistrent comme observateurs (`mDataManager.addObserver(this)`) mais ne se désenregistrent jamais. Lors d'une déconnexion, `MessageApp.notifyLogout()` met les contrôleurs à `null` mais ne les retire pas de la liste des observers dans la Database. Cela provoque des **fuites mémoire** et potentiellement des **NullPointerException** si la DB notifie un ancien contrôleur.

**Solution** : Ajouter une méthode `dispose()` aux Controllers :

```java
public void dispose() {
    mDataManager.removeObserver(this);
}
```

Et l'appeler dans `MessageApp.notifyLogout()` :
```java
if (mChannelController != null) mChannelController.dispose();
if (mMessageController != null) mMessageController.dispose();
if (mUserController != null) mUserController.dispose();
```

### 4.7 🟢 Mot de passe non vérifié au login

**Problème** : `AccountController.onLoginRequested(String tag)` ne vérifie que le tag. Le mot de passe stocké dans `User.mUserPassword` est ignoré lors de la connexion — n'importe qui peut se connecter en connaissant un tag.

**Solution** : Ajouter un champ mot de passe au `LoginPanel` et vérifier dans le Controller.

### 4.8 🟢 `MessageInputView` n'a pas d'interface View

**Problème** : `MessageInputView` est utilisée en type concret dans `MessageController`. Contrairement à `MessageListView` qui implémente `IMessageView`, `MessageInputView` n'a pas d'interface.

**Solution** : Créer `IMessageInputView` pour la cohérence.

### 4.9 🟢 Bug dans `DataManager.getMessagesTo()`

**Problème** : La méthode `getMessagesTo(User user)` est censée retourner les messages **adressés à** un utilisateur, mais le code filtre sur `message.getSender().equals(user)` (les messages **envoyés par**). C'est la même logique que `getMessagesFrom()`, c'est un **copier-coller bugué**.

---

## 5. Checklist pour chaque nouvelle fonctionnalité

- [ ] Créer `IXxxView` (contrat View)
- [ ] Créer `IXxxActionListener` (contrat Controller)
- [ ] Créer `XxxView extends JPanel implements IXxxView`
- [ ] Créer `XxxController implements IXxxActionListener`
- [ ] Si données temps réel : implémenter `IDatabaseObserver` dans le Controller
- [ ] Utiliser exclusivement `DiscordTheme` pour les couleurs et polices
- [ ] Utiliser les composants `Discord*` (`DiscordButton`, `DiscordTextField`, etc.)
- [ ] Câbler View + Controller dans `MessageApp.initGui()` ou `initMainControllers()`
- [ ] Ajouter la vue dans `MessageAppMainView`
- [ ] Documenter les Javadoc en français (patron existant)
- [ ] Vérifier que la View ne contient **aucune logique métier**
