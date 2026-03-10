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

    /**
     * Appelé lorsque l'utilisateur connecté modifie son nom (SRS-MAP-USR-009).
     *
     * @param newName le nouveau nom de l'utilisateur.
     */
    void onEditUserName(String newName);

    /**
     * Appelé lorsque l'utilisateur connecté supprime son compte (SRS-MAP-USR-010).
     */
    void onDeleteAccount();
}
