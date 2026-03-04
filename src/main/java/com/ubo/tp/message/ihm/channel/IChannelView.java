package main.java.com.ubo.tp.message.ihm.channel;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Interface de la vue des canaux (MVC — contrat View).
 * Implémentée par ChannelListView, appelée par ChannelController.
 */
public interface IChannelView {

    /**
     * Met à jour la liste des canaux affichés.
     */
    void setChannels(List<Channel> channels);

    /**
     * Définit le canal actuellement sélectionné.
     */
    void setSelectedChannel(Channel channel);

    /**
     * Affiche un message d'erreur.
     */
    void showError(String message);

    /**
     * Définit le listener des actions utilisateur.
     */
    void setActionListener(IChannelActionListener listener);

    /**
     * Affiche le dialogue de gestion des membres d'un canal.
     */
    void showManageMembersDialog(Channel channel, List<User> currentMembers, List<User> availableUsers);

    /**
     * Définit les identifiants des canaux ayant des messages non lus.
     */
    void setUnreadChannels(Set<UUID> unreadChannelIds);
}
