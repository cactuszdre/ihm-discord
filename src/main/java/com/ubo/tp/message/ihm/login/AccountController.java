package main.java.com.ubo.tp.message.ihm.login;

import java.util.Set;
import java.util.UUID;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Contrôleur pour la gestion des comptes utilisateurs (MVC — Controller).
 * Implémente les listeners des vues Login et Registration.
 * Contient toute la logique métier (validation, appels au Model).
 */
public class AccountController implements ILoginActionListener, IRegistrationActionListener {

    /**
     * Gestionnaire de données (Model).
     */
    private DataManager mDataManager;

    /**
     * Session de l'application (Model).
     */
    private Session mSession;

    /**
     * Vue de connexion.
     */
    private LoginPanel mLoginView;

    /**
     * Vue d'inscription.
     */
    private RegistrationPanel mRegistrationView;

    /**
     * Constructeur.
     *
     * @param dataManager gestionnaire de données
     * @param session     session de l'application
     */
    public AccountController(DataManager dataManager, Session session) {
        this.mDataManager = dataManager;
        this.mSession = session;
    }

    /**
     * Associe la vue de connexion et s'enregistre comme listener.
     *
     * @param loginView la vue de connexion.
     */
    public void setLoginView(LoginPanel loginView) {
        this.mLoginView = loginView;
        this.mLoginView.setLoginActionListener(this);
    }

    /**
     * Associe la vue d'inscription et s'enregistre comme listener.
     *
     * @param registrationView la vue d'inscription.
     */
    public void setRegistrationView(RegistrationPanel registrationView) {
        this.mRegistrationView = registrationView;
        this.mRegistrationView.setRegistrationActionListener(this);
    }

    // ========== ILoginActionListener ==========

    @Override
    public void onLoginRequested(String tag, String password) {
        // Validation du tag
        if (tag.isEmpty()) {
            mLoginView.showError("Veuillez saisir votre tag utilisateur.");
            return;
        }

        // Validation du mot de passe
        if (password.isEmpty()) {
            mLoginView.showError("Veuillez saisir votre mot de passe.");
            return;
        }

        // Recherche de l'utilisateur par tag
        User user = findUserByTag(tag);
        if (user == null) {
            mLoginView.showError("Aucun utilisateur trouvé avec ce tag.");
            return;
        }

        // Vérification du mot de passe
        if (!user.getUserPassword().equals(password)) {
            mLoginView.showError("Mot de passe incorrect.");
            return;
        }

        mSession.connect(user);
    }

    // ========== IRegistrationActionListener ==========

    @Override
    public void onRegisterRequested(String name, String tag, String password) {
        // Validation des champs obligatoires (SRS-MAP-USR-002)
        if (name.isEmpty()) {
            mRegistrationView.showError("Le nom est obligatoire.");
            return;
        }

        if (tag.isEmpty()) {
            mRegistrationView.showError("Le tag est obligatoire.");
            return;
        }

        // Vérification unicité du tag (SRS-MAP-USR-003)
        if (!isTagUnique(tag)) {
            mRegistrationView.showError("Ce tag est déjà utilisé par un autre utilisateur.");
            return;
        }

        // Création de l'utilisateur
        User newUser = new User(UUID.randomUUID(), tag, password, name);
        mDataManager.sendUser(newUser);

        // Notification de succès et retour au login
        mRegistrationView.showSuccess("Compte créé avec succès !\nVous pouvez maintenant vous connecter.");
        mRegistrationView.clearFields();
    }

    // ========== Méthodes utilitaires ==========

    /**
     * Vérifie si un tag est unique dans le système.
     *
     * @param tag tag à vérifier
     * @return true si le tag n'existe pas encore
     */
    public boolean isTagUnique(String tag) {
        return findUserByTag(tag) == null;
    }

    /**
     * Recherche un utilisateur par son tag.
     *
     * @param tag tag à rechercher
     * @return l'utilisateur trouvé ou null
     */
    private User findUserByTag(String tag) {
        Set<User> users = mDataManager.getUsers();
        for (User user : users) {
            if (user.getUserTag().equalsIgnoreCase(tag)) {
                return user;
            }
        }
        return null;
    }
}
