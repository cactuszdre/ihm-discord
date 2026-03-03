package main.java.com.ubo.tp.message.ihm.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Contrôleur des utilisateurs (MVC — Controller).
 * Gère l'affichage et la recherche des utilisateurs.
 * Écoute les événements de la base de données pour les mises à jour temps réel.
 */
public class UserController implements IUserActionListener, IDatabaseObserver {

    /**
     * Gestionnaire de données (Model).
     */
    private DataManager mDataManager;

    /**
     * Session de l'application (Model).
     */
    private Session mSession;

    /**
     * Vue des utilisateurs.
     */
    private IUserView mView;

    /**
     * Requête de recherche courante.
     */
    private String mCurrentSearchQuery = "";

    /**
     * Constructeur.
     *
     * @param dataManager gestionnaire de données
     * @param session     session de l'application
     * @param view        vue des utilisateurs
     */
    public UserController(DataManager dataManager, Session session, IUserView view) {
        this.mDataManager = dataManager;
        this.mSession = session;
        this.mView = view;

        // Câblage MVC
        this.mView.setActionListener(this);
        this.mDataManager.addObserver(this);

        // Chargement initial
        this.refreshUsers();
    }

    // ========== IUserActionListener ==========

    @Override
    public void onSearchUser(String query) {
        this.mCurrentSearchQuery = query.toLowerCase();
        refreshUsers();
    }

    @Override
    public void onSendDirectMessage(User user) {
        // Note: pour l'instant, afficher un message. L'implémentation complète
        // nécessiterait la création d'un canal privé dédié.
        mView.showError("Fonctionnalité \"Message direct à @" + user.getUserTag() + "\" à implémenter.");
    }

    // ========== IDatabaseObserver ==========

    @Override
    public void notifyUserAdded(User addedUser) {
        refreshUsers();
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        refreshUsers();
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        refreshUsers();
    }

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        /* Non utilisé */ }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        /* Non utilisé */ }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        /* Non utilisé */ }

    @Override
    public void notifyChannelAdded(Channel addedChannel) {
        /* Non utilisé */ }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        /* Non utilisé */ }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {
        /* Non utilisé */ }

    // ========== Méthodes internes ==========

    /**
     * Rafraîchit la liste des utilisateurs dans la vue.
     */
    private void refreshUsers() {
        Set<User> allUsers = mDataManager.getUsers();
        List<User> filteredUsers = new ArrayList<>();

        for (User user : allUsers) {
            // Filtrer par recherche
            if (!mCurrentSearchQuery.isEmpty()
                    && !user.getName().toLowerCase().contains(mCurrentSearchQuery)
                    && !user.getUserTag().toLowerCase().contains(mCurrentSearchQuery)) {
                continue;
            }
            filteredUsers.add(user);
        }

        mView.setUsers(filteredUsers);
    }
}
