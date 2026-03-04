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
     * Listener de message direct (pour notifier le coordinateur).
     */
    private IDirectMessageListener mDirectMessageListener;

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

    /**
     * Définit le listener pour les messages directs.
     */
    public void setDirectMessageListener(IDirectMessageListener listener) {
        this.mDirectMessageListener = listener;
    }

    /**
     * Désenregistre cet observateur (SKILL.md §4.6).
     */
    public void dispose() {
        mDataManager.removeObserver(this);
    }

    // ========== IUserActionListener ==========

    @Override
    public void onSearchUser(String query) {
        this.mCurrentSearchQuery = query.toLowerCase();
        refreshUsers();
    }

    @Override
    public void onSendDirectMessage(User targetUser) {
        User currentUser = mSession.getConnectedUser();
        if (currentUser == null)
            return;

        // Ne pas envoyer un DM à soi-même
        if (currentUser.getUuid().equals(targetUser.getUuid())) {
            mView.showError("Vous ne pouvez pas vous envoyer un message privé.");
            return;
        }

        // Chercher un canal DM existant entre les deux utilisateurs
        Channel dmChannel = findExistingDmChannel(currentUser, targetUser);

        if (dmChannel == null) {
            // Créer un nouveau canal DM privé
            String dmName = "DM-" + currentUser.getUserTag() + "-" + targetUser.getUserTag();
            List<User> members = new ArrayList<User>();
            members.add(currentUser);
            members.add(targetUser);
            dmChannel = new Channel(currentUser, dmName, members);
            mDataManager.sendChannel(dmChannel);
        }

        // Notifier le coordinateur pour sélectionner ce canal
        if (mDirectMessageListener != null) {
            mDirectMessageListener.onDirectMessageChannelReady(dmChannel);
        }
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
        /* Non utilisé */
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        /* Non utilisé */
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        /* Non utilisé */
    }

    @Override
    public void notifyChannelAdded(Channel addedChannel) {
        /* Non utilisé */
    }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        /* Non utilisé */
    }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {
        /* Non utilisé */
    }

    // ========== Méthodes internes ==========

    /**
     * Rafraîchit la liste des utilisateurs dans la vue.
     */
    private void refreshUsers() {
        Set<User> allUsers = mDataManager.getUsers();
        List<User> filteredUsers = new ArrayList<User>();

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

    /**
     * Recherche un canal DM existant entre deux utilisateurs.
     * Un canal DM est un canal privé dont le nom commence par "DM-"
     * et qui contient exactement les deux utilisateurs.
     *
     * @param user1 premier utilisateur
     * @param user2 deuxième utilisateur
     * @return le canal DM existant ou null
     */
    private Channel findExistingDmChannel(User user1, User user2) {
        Set<Channel> allChannels = mDataManager.getChannels();

        for (Channel channel : allChannels) {
            if (!channel.isPrivate() || !channel.getName().startsWith("DM-")) {
                continue;
            }

            // Vérifier que les deux utilisateurs sont membres
            List<User> members = channel.getUsers();
            boolean hasUser1 = false;
            boolean hasUser2 = false;

            // Vérifier le créateur
            if (channel.getCreator().getUuid().equals(user1.getUuid())) {
                hasUser1 = true;
            }
            if (channel.getCreator().getUuid().equals(user2.getUuid())) {
                hasUser2 = true;
            }

            // Vérifier les membres
            for (User member : members) {
                if (member.getUuid().equals(user1.getUuid())) {
                    hasUser1 = true;
                }
                if (member.getUuid().equals(user2.getUuid())) {
                    hasUser2 = true;
                }
            }

            if (hasUser1 && hasUser2) {
                return channel;
            }
        }

        return null;
    }

    /**
     * Interface pour notifier de la création/sélection d'un canal DM.
     */
    public interface IDirectMessageListener {
        /**
         * Appelé quand un canal DM est prêt (existant ou nouvellement créé).
         */
        void onDirectMessageChannelReady(Channel dmChannel);
    }
}
