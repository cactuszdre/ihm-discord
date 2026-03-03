package main.java.com.ubo.tp.message.ihm.message;

import main.java.com.ubo.tp.message.datamodel.Message;

/**
 * Interface d'écoute des actions utilisateur sur les messages.
 * Implémentée par le MessageController.
 */
public interface IMessageActionListener {

    /**
     * Appelé lorsque l'utilisateur envoie un message.
     */
    void onSendMessage(String text);

    /**
     * Appelé lorsque l'utilisateur supprime un message.
     */
    void onDeleteMessage(Message message);

    /**
     * Appelé lorsque l'utilisateur recherche dans les messages.
     */
    void onSearchMessage(String query);
}
