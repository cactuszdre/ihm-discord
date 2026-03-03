package main.java.com.ubo.tp.message.ihm.login;

/**
 * Interface d'écoute des actions de la vue d'inscription.
 * Implémentée par le Controller.
 */
public interface IRegistrationActionListener {

    /**
     * Appelé lorsque l'utilisateur demande à créer un compte.
     *
     * @param name     le nom saisi.
     * @param tag      le tag saisi.
     * @param password le mot de passe saisi.
     */
    void onRegisterRequested(String name, String tag, String password);
}
