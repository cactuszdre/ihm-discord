package main.java.com.ubo.tp.message.ihm.login;

import java.util.Set;
import java.util.UUID;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Contrôleur pour la gestion des comptes utilisateurs (login/register).
 */
public class AccountController {

    /**
     * Gestionnaire de données.
     */
    private DataManager mDataManager;

    /**
     * Session de l'application.
     */
    private Session mSession;

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
     * Tente de connecter un utilisateur par son tag.
     *
     * @param tag tag de l'utilisateur (sans @)
     * @return true si la connexion a réussi
     */
    public boolean login(String tag) {
        User user = findUserByTag(tag);
        if (user != null) {
            mSession.connect(user);
            return true;
        }
        return false;
    }

    /**
     * Enregistre un nouvel utilisateur.
     *
     * @param name     nom de l'utilisateur
     * @param tag      tag unique de l'utilisateur
     * @param password mot de passe
     * @return true si l'enregistrement a réussi
     */
    public boolean register(String name, String tag, String password) {
        // Vérification tag unique
        if (!isTagUnique(tag)) {
            return false;
        }

        // Création de l'utilisateur
        User newUser = new User(UUID.randomUUID(), tag, password, name);

        // Sauvegarde via DataManager (écrit le fichier utilisateur)
        mDataManager.sendUser(newUser);

        return true;
    }

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
