package main.java.com.ubo.tp.message.ihm.channel;

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

    // ========== IChannelActionListener ==========

    @Override
    public void onChannelSelected(Channel channel) {
        this.mSelectedChannel = channel;
        this.mView.setSelectedChannel(channel);

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

        List<User> members = new ArrayList<>();
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
        // Pour l'instant on retire juste le canal sélectionné
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
        refreshChannels();
    }

    @Override
    public void onSearchChannel(String query) {
        this.mCurrentSearchQuery = query.toLowerCase();
        refreshChannels();
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
        /* Non utilisé */ }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        /* Non utilisé */ }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        /* Non utilisé */ }

    @Override
    public void notifyUserAdded(User addedUser) {
        /* Non utilisé */ }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        /* Non utilisé */ }

    @Override
    public void notifyUserModified(User modifiedUser) {
        /* Non utilisé */ }

    // ========== Méthodes internes ==========

    /**
     * Rafraîchit la liste des canaux dans la vue.
     * Filtre selon la recherche et les droits d'accès.
     */
    private void refreshChannels() {
        Set<Channel> allChannels = mDataManager.getChannels();
        User currentUser = mSession.getConnectedUser();
        List<Channel> filteredChannels = new ArrayList<>();

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
    }

    /**
     * Interface pour notifier de la sélection d'un canal.
     */
    public interface IChannelSelectionListener {
        void onChannelSelected(Channel channel);
    }
}
