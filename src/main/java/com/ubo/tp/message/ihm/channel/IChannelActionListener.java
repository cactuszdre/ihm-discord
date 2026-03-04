package main.java.com.ubo.tp.message.ihm.channel;

import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.User;

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

    /**
     * Appelé lorsque l'utilisateur demande à gérer les membres d'un canal privé.
     */
    void onManageMembers(Channel channel);

    /**
     * Appelé lorsque l'utilisateur ajoute un membre à un canal privé.
     */
    void onAddUserToChannel(Channel channel, User user);

    /**
     * Appelé lorsque l'utilisateur retire un membre d'un canal privé.
     */
    void onRemoveUserFromChannel(Channel channel, User user);
}
