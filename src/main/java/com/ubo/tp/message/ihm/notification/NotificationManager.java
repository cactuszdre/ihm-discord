package main.java.com.ubo.tp.message.ihm.notification;

import java.util.List;
import java.util.Set;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Gestionnaire de notifications (SRS-MAP-MSG-010).
 * Observe la base de données et affiche des notifications toast
 * lorsque l'utilisateur connecté reçoit un DM ou est mentionné.
 */
public class NotificationManager implements IDatabaseObserver {

    /**
     * Gestionnaire de données (Model).
     */
    private DataManager mDataManager;

    /**
     * Session de l'application (Model).
     */
    private Session mSession;

    /**
     * Indique si l'initialisation est terminée (après le replay des messages
     * existants).
     */
    private boolean mInitialized = false;

    /**
     * Constructeur.
     *
     * @param dataManager gestionnaire de données.
     * @param session     session de l'application.
     */
    public NotificationManager(DataManager dataManager, Session session) {
        this.mDataManager = dataManager;
        this.mSession = session;

        // Enregistrement comme observateur de la base de données
        // Note: addObserver() re-joue tous les messages existants en synchrone.
        // Le flag mInitialized empêche les fausses notifications lors du replay.
        this.mDataManager.addObserver(this);
        this.mInitialized = true;
    }

    /**
     * Désenregistre cet observateur (SKILL.md §4.6).
     */
    public void dispose() {
        mDataManager.removeObserver(this);
    }

    // ========== IDatabaseObserver ==========

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        // Ignorer les messages rejoués lors de l'enregistrement de l'observateur
        if (!mInitialized) {
            return;
        }

        User currentUser = mSession.getConnectedUser();
        if (currentUser == null) {
            return;
        }

        // Ignorer les messages envoyés par l'utilisateur lui-même
        if (addedMessage.getSender().getUuid().equals(currentUser.getUuid())) {
            return;
        }

        // Vérifier si c'est un DM (canal privé dont le nom commence par "DM-")
        boolean isDm = isDmForCurrentUser(addedMessage, currentUser);

        // Vérifier si l'utilisateur est mentionné (@tag)
        boolean isMentioned = addedMessage.getText().contains("@" + currentUser.getUserTag());

        if (isDm) {
            String senderName = addedMessage.getSender().getName();
            ToastNotification.show(
                    "\uD83D\uDCE9 Message de " + senderName,
                    addedMessage.getText());
        } else if (isMentioned) {
            String senderName = addedMessage.getSender().getName();
            ToastNotification.show(
                    "\uD83D\uDD14 " + senderName + " vous a mentionné",
                    addedMessage.getText());
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

    // ========== Méthodes utilitaires ==========

    /**
     * Vérifie si le message est un DM adressé à l'utilisateur connecté.
     *
     * @param message     le message à vérifier.
     * @param currentUser l'utilisateur connecté.
     * @return true si c'est un DM pour l'utilisateur connecté.
     */
    private boolean isDmForCurrentUser(Message message, User currentUser) {
        Set<Channel> allChannels = mDataManager.getChannels();

        for (Channel channel : allChannels) {
            if (!channel.isPrivate() || !channel.getName().startsWith("DM-")) {
                continue;
            }

            // Vérifier que le message appartient à ce canal
            if (!message.getRecipient().equals(channel.getUuid())) {
                continue;
            }

            // Vérifier que l'utilisateur connecté est membre
            if (channel.getCreator().getUuid().equals(currentUser.getUuid())) {
                return true;
            }
            List<User> members = channel.getUsers();
            for (User member : members) {
                if (member.getUuid().equals(currentUser.getUuid())) {
                    return true;
                }
            }
        }

        return false;
    }
}
