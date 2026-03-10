package main.java.com.ubo.tp.message.ihm.user;

import java.util.List;

import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Interface de la vue des utilisateurs (MVC — contrat View).
 * Implémentée par UserListView, appelée par UserController.
 */
public interface IUserView {

    /**
     * Met à jour la liste des utilisateurs affichés.
     */
    void setUsers(List<User> users);

    /**
     * Affiche un message d'erreur.
     */
    void showError(String message);

    /**
     * Définit le listener des actions utilisateur.
     */
    void setActionListener(IUserActionListener listener);

    /**
     * Définit l'utilisateur actuellement connecté.
     *
     * @param user l'utilisateur connecté.
     */
    void setCurrentUser(User user);
}
