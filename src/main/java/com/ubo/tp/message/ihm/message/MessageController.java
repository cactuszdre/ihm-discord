package main.java.com.ubo.tp.message.ihm.message;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Contrôleur des messages (MVC — Controller).
 * Gère l'envoi, la suppression et la recherche de messages.
 * Écoute les événements de la base de données pour les mises à jour temps réel.
 */
public class MessageController implements IMessageActionListener, IDatabaseObserver {

    /**
     * Taille maximale d'un message (SRS-MAP-MSG-008).
     */
    private static final int MAX_MESSAGE_LENGTH = 200;

    /**
     * Gestionnaire de données (Model).
     */
    private DataManager mDataManager;

    /**
     * Session de l'application (Model).
     */
    private Session mSession;

    /**
     * Vue de la liste des messages.
     */
    private IMessageView mMessageListView;

    /**
     * Vue de saisie de message.
     */
    private IMessageInputView mMessageInputView;

    /**
     * Canal actuellement sélectionné.
     */
    private Channel mCurrentChannel;

    /**
     * Requête de recherche courante.
     */
    private String mCurrentSearchQuery = "";

    /**
     * Constructeur.
     *
     * @param dataManager      gestionnaire de données
     * @param session          session de l'application
     * @param messageListView  vue de la liste des messages
     * @param messageInputView vue de saisie de message
     */
    public MessageController(DataManager dataManager, Session session,
            IMessageView messageListView, IMessageInputView messageInputView) {
        this.mDataManager = dataManager;
        this.mSession = session;
        this.mMessageListView = messageListView;
        this.mMessageInputView = messageInputView;

        // Câblage MVC
        this.mMessageListView.setActionListener(this);
        this.mMessageInputView.setActionListener(this);
        this.mMessageListView.setCurrentUser(session.getConnectedUser());
        this.mDataManager.addObserver(this);
    }

    /**
     * Désenregistre cet observateur (SKILL.md §4.6).
     */
    public void dispose() {
        mDataManager.removeObserver(this);
    }

    /**
     * Change le canal courant et rafraîchit les messages.
     */
    public void setCurrentChannel(Channel channel) {
        this.mCurrentChannel = channel;
        this.mCurrentSearchQuery = "";

        if (channel != null) {
            this.mMessageListView.setChannelName(channel.getName());
            // Mettre à jour les utilisateurs disponibles pour @mention
            updateAvailableUsers();
        } else {
            this.mMessageListView.setChannelName("Sélectionnez un canal");
        }

        refreshMessages();
    }

    // ========== IMessageActionListener ==========

    @Override
    public void onSendMessage(String text) {
        if (mCurrentChannel == null) {
            mMessageListView.showError("Veuillez sélectionner un canal.");
            return;
        }

        if (text.isEmpty())
            return;

        // Validation taille (SRS-MAP-MSG-008)
        if (text.length() > MAX_MESSAGE_LENGTH) {
            mMessageListView.showError("Le message ne peut pas dépasser " + MAX_MESSAGE_LENGTH + " caractères.");
            return;
        }

        User currentUser = mSession.getConnectedUser();
        if (currentUser == null)
            return;

        Message message = new Message(currentUser, mCurrentChannel.getUuid(), text);
        mDataManager.sendMessage(message);
    }

    @Override
    public void onDeleteMessage(Message message) {
        User currentUser = mSession.getConnectedUser();
        if (currentUser == null)
            return;

        // Seul l'auteur peut supprimer (SRS-MAP-MSG-006)
        if (!message.getSender().getUuid().equals(currentUser.getUuid())) {
            mMessageListView.showError("Vous ne pouvez supprimer que vos propres messages.");
            return;
        }

        // Suppression via le DataManager (le refresh sera déclenché par
        // notifyMessageDeleted)
        mDataManager.deleteMessage(message);
    }

    @Override
    public void onSearchMessage(String query) {
        this.mCurrentSearchQuery = query.toLowerCase();
        refreshMessages();
    }

    // ========== IDatabaseObserver ==========

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        refreshMessages();
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        refreshMessages();
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        refreshMessages();
    }

    @Override
    public void notifyUserAdded(User addedUser) {
        updateAvailableUsers();
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        updateAvailableUsers();
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
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
     * Rafraîchit la liste des messages dans la vue.
     */
    private void refreshMessages() {
        if (mCurrentChannel == null) {
            mMessageListView.setMessages(new ArrayList<Message>());
            return;
        }

        Set<Message> allMessages = mDataManager.getMessages();
        List<Message> channelMessages = new ArrayList<>();

        for (Message message : allMessages) {
            // Filtrer par canal
            if (!message.getRecipient().equals(mCurrentChannel.getUuid())) {
                continue;
            }

            // Filtrer par recherche
            if (!mCurrentSearchQuery.isEmpty()
                    && !message.getText().toLowerCase().contains(mCurrentSearchQuery)) {
                continue;
            }

            channelMessages.add(message);
        }

        // Tri par date d'émission
        Collections.sort(channelMessages, new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                return Long.compare(m1.getEmissionDate(), m2.getEmissionDate());
            }
        });

        mMessageListView.setMessages(channelMessages);
    }

    /**
     * Met à jour la liste des utilisateurs disponibles pour @mention.
     */
    private void updateAvailableUsers() {
        Set<User> allUsers = mDataManager.getUsers();
        List<User> userList = new ArrayList<>(allUsers);
        mMessageInputView.setAvailableUsers(userList);
    }
}
