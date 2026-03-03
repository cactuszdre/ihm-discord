package main.java.com.ubo.tp.message.ihm.channel;

import java.util.List;

import main.java.com.ubo.tp.message.datamodel.Channel;

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
}
