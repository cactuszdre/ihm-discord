package main.java.com.ubo.tp.message.ihm;

import main.java.com.ubo.tp.message.core.database.IDatabaseObserver;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Observateur de la base de données qui affiche les modifications dans la
 * console.
 *
 * @author S.Lucas
 */
public class MessageAppDatabaseObserver implements IDatabaseObserver {

    @Override
    public void notifyMessageAdded(Message addedMessage) {
        System.out.println("[DB] Message ajouté : " + addedMessage);
    }

    @Override
    public void notifyMessageDeleted(Message deletedMessage) {
        System.out.println("[DB] Message supprimé : " + deletedMessage);
    }

    @Override
    public void notifyMessageModified(Message modifiedMessage) {
        System.out.println("[DB] Message modifié : " + modifiedMessage);
    }

    @Override
    public void notifyUserAdded(User addedUser) {
        System.out.println("[DB] Utilisateur ajouté : " + addedUser);
    }

    @Override
    public void notifyUserDeleted(User deletedUser) {
        System.out.println("[DB] Utilisateur supprimé : " + deletedUser);
    }

    @Override
    public void notifyUserModified(User modifiedUser) {
        System.out.println("[DB] Utilisateur modifié : " + modifiedUser);
    }

    @Override
    public void notifyChannelAdded(Channel addedChannel) {
        System.out.println("[DB] Canal ajouté : " + addedChannel);
    }

    @Override
    public void notifyChannelDeleted(Channel deletedChannel) {
        System.out.println("[DB] Canal supprimé : " + deletedChannel);
    }

    @Override
    public void notifyChannelModified(Channel modifiedChannel) {
        System.out.println("[DB] Canal modifié : " + modifiedChannel);
    }
}
