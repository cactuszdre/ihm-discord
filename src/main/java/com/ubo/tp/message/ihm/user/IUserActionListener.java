package main.java.com.ubo.tp.message.ihm.user;

import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Interface d'écoute des actions utilisateur sur la liste des utilisateurs.
 * Implémentée par le UserController.
 */
public interface IUserActionListener {

    /**
     * Appelé lorsque l'utilisateur recherche un utilisateur.
     */
    void onSearchUser(String query);

    /**
     * Appelé lorsque l'utilisateur envoie un message direct à un autre utilisateur.
     */
    void onSendDirectMessage(User user);
}
