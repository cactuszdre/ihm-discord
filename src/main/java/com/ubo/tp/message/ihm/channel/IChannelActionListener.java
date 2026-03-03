package main.java.com.ubo.tp.message.ihm.channel;

import main.java.com.ubo.tp.message.datamodel.Channel;

/**
 * Interface d'écoute des actions utilisateur sur les canaux.
 * Implémentée par le ChannelController.
 */
public interface IChannelActionListener {

    /**
     * Appelé lorsque l'utilisateur sélectionne un canal.
     */
    void onChannelSelected(Channel channel);

    /**
     * Appelé lorsque l'utilisateur demande à créer un canal public.
     */
    void onCreatePublicChannel(String name);

    /**
     * Appelé lorsque l'utilisateur demande à créer un canal privé.
     */
    void onCreatePrivateChannel(String name);

    /**
     * Appelé lorsque l'utilisateur demande à supprimer un canal.
     */
    void onDeleteChannel(Channel channel);

    /**
     * Appelé lorsque l'utilisateur quitte un canal privé.
     */
    void onLeaveChannel(Channel channel);

    /**
     * Appelé lorsque l'utilisateur recherche un canal.
     */
    void onSearchChannel(String query);
}
