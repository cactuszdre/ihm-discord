package main.java.com.ubo.tp.message.ihm.message;

import java.util.List;

import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Interface de la vue de saisie de message (MVC — contrat View).
 * Implémentée par MessageInputView (Swing) et MessageInputViewFX (JavaFX).
 */
public interface IMessageInputView {

    /**
     * Définit le listener des actions (Controller).
     */
    void setActionListener(IMessageActionListener listener);

    /**
     * Met à jour la liste des utilisateurs disponibles pour @mention.
     */
    void setAvailableUsers(List<User> users);
}
