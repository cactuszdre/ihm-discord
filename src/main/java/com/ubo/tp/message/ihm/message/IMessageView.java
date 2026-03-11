package main.java.com.ubo.tp.message.ihm.message;

import java.util.List;

import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Interface de la vue des messages (MVC — contrat View).
 * Implémentée par MessageListView, appelée par MessageController.
 */
public interface IMessageView {

    /**
     * Met à jour la liste des messages affichés.
     */
    void setMessages(List<Message> messages);

    /**
     * Définit le nom du canal affiché dans le header.
     */
    void setChannelName(String name);

    /**
     * Affiche un message d'erreur.
     */
    void showError(String message);

    /**
     * Définit le listener des actions utilisateur.
     */
    void setActionListener(IMessageActionListener listener);

    /**
     * Définit l'utilisateur connecté (pour identifier les messages de l'auteur).
     */
    void setCurrentUser(User user);

    /**
     * Déclenche un easter egg dans l'interface.
     *
     * @param command la commande easter egg ("/party", "/flip", "/earthquake")
     */
    void triggerEasterEgg(String command);
}
