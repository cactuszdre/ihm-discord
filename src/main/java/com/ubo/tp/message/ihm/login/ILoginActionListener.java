package main.java.com.ubo.tp.message.ihm.login;

/**
 * Interface d'écoute des actions de la vue de connexion.
 * Implémentée par le Controller.
 */
public interface ILoginActionListener {

    /**
     * Appelé lorsque l'utilisateur demande à se connecter.
     *
     * @param tag le tag saisi par l'utilisateur.
     */
    void onLoginRequested(String tag);
}
