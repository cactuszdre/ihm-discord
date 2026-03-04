package main.java.com.ubo.tp.message.ihm.channel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Contrôleur des canaux (MVC — Controller).
 * Gère la logique métier des canaux et met à jour la vue.
 * Écoute les événements de la base de données pour les mises à jour temps réel.
 */
public class ChannelController implements IChannelActionListener, IDatabaseObserver {

    /**
     * Gestionnaire de données (Model).
     */
    private DataManager mDataManager;

    /**
     * Session de l'application (Model).
     */
    private Session mSession;

    /**
     * Vue des canaux.
     */
    private IChannelView mView;

    /**
     * Canal actuellement sélectionné.
     */
    private Channel mSelectedChannel;

    /**
     * Listener de sélection de canal (pour notifier la vue messages).
     */
    private IChannelSelectionListener mSelectionListener;

    /**
     * Requête de recherche courante.
     */
    private String mCurrentSearchQuery = "";

    /**
     * Timestamps de la dernière lecture par canal (pour messages non lus).
     */
    private Map<UUID, Long> mLastReadTimestamps = new HashMap<UUID, Long>();

    /**
     * Identifiants des canaux ayant des messages non lus.
     */
    private Set<UUID> mUnreadChannelIds = new HashSet<UUID>();

    /**
     * Constructeur.
     *
     * @param dataManager gestionnaire de données
     * @param session     session de l'application
     * @param view        vue des canaux
     */
    public ChannelController(DataManager dataManager, Session session, IChannelView view) {
        this.mDataManager = dataManager;
        this.mSession = session;
        this.mView = view;

        // Câblage MVC
        this.mView.setActionListener(this);
        this.mDataManager.addObserver(this);

        // Chargement initial des canaux
        this.refreshChannels();
    }

    /**
     * Définit le listener de sélection de canal.
     */
    public void setChannelSelectionListener(IChannelSelectionListener listener) {
        this.mSelectionListener = listener;
    }

    /**
     * Retourne le canal actuellement sélectionné.
     */
    public Channel getSelectedChannel() {
        return this.mSelectedChannel;
    }

    /**
     * Désenregistre cet observateur (SKILL.md §4.6).
     */
    public void dispose() {
        mDataManager.removeObserver(this);
    }

    // ========== IChannelActionListener ==========

    @Override
    public void onChannelSelected(Channel channel) {
        this.mSelectedChannel = channel;
        this.mView.setSelectedChannel(channel);

        // Marquer comme lu
        if (channel != null) {
            mLastReadTimestamps.put(channel.getUuid(), System.currentTimeMillis());
            mUnreadChannelIds.remove(channel.getUuid());
            mView.setUnreadChannels(mUnreadChannelIds);
        }

        if (mSelectionListener != null) {
            mSelectionListener.onChannelSelected(channel);
        }
    }

    @Override
    public void onCreatePublicChannel(String name) {
        User currentUser = mSession.getConnectedUser();
        if (currentUser == null)
            return;

        Channel channel = new Channel(currentUser, name);
        mDataManager.sendChannel(channel);
    }

    @Override
    public void onCreatePrivateChannel(String name) {
        User currentUser = mSession.getConnectedUser();
        if (currentUser == null)
            return;

        List<User> members = new ArrayList<User>();
        members.add(currentUser);
        Channel channel = new Channel(currentUser, name, members);
        mDataManager.sendChannel(channel);
    }

    @Override
    public void onDeleteChannel(Channel channel) {
        User currentUser = mSession.getConnectedUser();
        if (currentUser == null)
            return;

        // Seul le propriétaire peut supprimer (SRS-MAP-CHN-006)
        if (!channel.getCreator().getUuid().equals(currentUser.getUuid())) {
            mView.showError("Seul le propriétaire peut supprimer ce canal.");
            return;
        }

        // Note: La suppression se fait via le système de fichiers / DataManager
        if (mSelectedChannel != null && mSelectedChannel.getUuid().equals(channel.getUuid())) {
            mSelectedChannel = null;
            mView.setSelectedChannel(null);
        }
    }

    @Override
    public void onLeaveChannel(Channel channel) {
        User currentUser = mSession.getConnectedUser();
        if (currentUser == null)
            return;

        // Ne peut pas quitter si propriétaire (SRS-MAP-CHN-005)
        if (channel.getCreator().getUuid().equals(currentUser.getUuid())) {
            mView.showError("Le propriétaire ne peut pas quitter son propre canal.\nSupprimez-le à la place.");
            return;
        }

        channel.removeUser(currentUser);
        mDataManager.sendChannel(channel);
        refreshChannels();
    }

    @Override
    public void onSearchChannel(String query) {
        this.mCurrentSearchQuery = query.toLowerCase();
        refreshChannels();
    }

    @Override
    public void onManageMembers(Channel channel) {
        User currentUser = mSession.getConnectedUser();
        if (currentUser == null)
            return;

        // Seul le propriétaire peut gérer les membres
        if (!channel.getCreator().getUuid().equals(currentUser.getUuid())) {
            mView.showError("Seul le propriétaire peut gérer les membres de ce canal.");
            return;
        }

        // Récupérer les membres actuels et les utilisateurs disponibles
        List<User> currentMembers = new ArrayList<User>(channel.getUsers());
        // Ajouter le créateur s'il n'est pas déjà dans la liste
        boolean creatorFound = false;
        for (User u : currentMembers) {
            if (u.getUuid().equals(channel.getCreator().getUuid())) {
                creatorFound = true;
                break;
            }
        }
        if (!creatorFound) {
            currentMembers.add(0, channel.getCreator());
        }

        // Utilisateurs non membres
        Set<User> allUsers = mDataManager.getUsers();
        List<User> availableUsers = new ArrayList<User>();
        for (User user : allUsers) {
            boolean isMember = false;
            for (User member : currentMembers) {
                if (member.getUuid().equals(user.getUuid())) {
                    isMember = true;
                    break;
                }
            }
            if (!isMember) {
                availableUsers.add(user);
            }
        }

        mView.showManageMembersDialog(channel, currentMembers, availableUsers);
    }

    @Override
    public void onAddUserToChannel(Channel channel, User user) {
        channel.addUser(user);
        mDataManager.sendChannel(channel);
    }

    @Override
    public void onRemoveUserFromChannel(Channel channel, User user) {
        // Le créateur ne peut pas être retiré
        if (user.getUuid().equals(channel.getCreator().getUuid())) {
            mView.showError("Le créateur ne peut pas être retiré du canal.");
            return;
        }

        channel.removeUser(user);
        mDataManager.sendChannel(channel);
    }

    // ========== IDatabaseObserver ==========

    @Override
    public void notifyChannelAdded(Channel addedChannel) {
        refreshChannels();
    }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        refreshChannels();
    }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {
        refreshChannels();
    }

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        // Vérifier si le message concerne un canal non sélectionné → marquer comme non
        // lu
        UUID recipientId = addedMessage.getRecipient();
        User currentUser = mSession.getConnectedUser();

        if (currentUser != null
                && !addedMessage.getSender().getUuid().equals(currentUser.getUuid())) {
            // Si le canal du message n'est pas le canal sélectionné
            if (mSelectedChannel == null || !mSelectedChannel.getUuid().equals(recipientId)) {
                Long lastRead = mLastReadTimestamps.get(recipientId);
                if (lastRead == null || addedMessage.getEmissionDate() > lastRead) {
                    mUnreadChannelIds.add(recipientId);
                    mView.setUnreadChannels(mUnreadChannelIds);
                }
            }
        }
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
    public void notifyUserAdded(User addedUser) {
        /* Non utilisé */
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        /* Non utilisé */
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        /* Non utilisé */
    }

    // ========== Méthodes internes ==========

    /**
     * Rafraîchit la liste des canaux dans la vue.
     * Filtre selon la recherche et les droits d'accès.
     */
    private void refreshChannels() {
        Set<Channel> allChannels = mDataManager.getChannels();
        User currentUser = mSession.getConnectedUser();
        List<Channel> filteredChannels = new ArrayList<Channel>();

        for (Channel channel : allChannels) {
            // Filtrer les canaux privés dont l'utilisateur n'est pas membre
            if (channel.isPrivate() && currentUser != null) {
                boolean isMember = false;
                if (channel.getCreator().getUuid().equals(currentUser.getUuid())) {
                    isMember = true;
                } else {
                    for (User member : channel.getUsers()) {
                        if (member.getUuid().equals(currentUser.getUuid())) {
                            isMember = true;
                            break;
                        }
                    }
                }
                if (!isMember)
                    continue;
            }

            // Filtrer par recherche
            if (!mCurrentSearchQuery.isEmpty()
                    && !channel.getName().toLowerCase().contains(mCurrentSearchQuery)) {
                continue;
            }

            filteredChannels.add(channel);
        }

        mView.setChannels(filteredChannels);
        mView.setUnreadChannels(mUnreadChannelIds);
    }

    /**
     * Interface pour notifier de la sélection d'un canal.
     */
    public interface IChannelSelectionListener {
        void onChannelSelected(Channel channel);
    }
}
