package main.java.com.ubo.tp.message.ihm.login;

import java.awt.Cursor;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.java.com.ubo.tp.message.ihm.common.DiscordButton;
import main.java.com.ubo.tp.message.ihm.common.DiscordPasswordField;
import main.java.com.ubo.tp.message.ihm.common.DiscordRoundPanel;
import main.java.com.ubo.tp.message.ihm.common.DiscordTextField;
import main.java.com.ubo.tp.message.ihm.common.DiscordTheme;

/**
 * Vue de connexion utilisateur (MVC — View pure).
 * Ne contient aucune logique métier. Expose les actions via
 * ILoginActionListener.
 */
public class LoginPanel extends JPanel {

    /**
     * Chemin vers les images.
     */
    private static final String IMAGES_PATH = "/main/resources/images/";

    /**
     * Champ de saisie du tag.
     */
    private DiscordTextField mTagField;

    /**
     * Champ de saisie du mot de passe.
     */
    private DiscordPasswordField mPasswordField;

    /**
     * Listener pour basculer vers l'inscription.
     */
    private ActionListener mShowRegistrationListener;

    /**
     * Listener des actions de login (Controller).
     */
    private ILoginActionListener mLoginActionListener;

    /**
     * Constructeur.
     */
    public LoginPanel() {
        this.initPanel();
    }

    /**
     * Définit le listener pour afficher le panel d'inscription.
     */
    public void setShowRegistrationListener(ActionListener listener) {
        this.mShowRegistrationListener = listener;
    }

    /**
     * Définit le listener des actions de login (Controller MVC).
     */
    public void setLoginActionListener(ILoginActionListener listener) {
        this.mLoginActionListener = listener;
    }

    /**
     * Affiche un message d'erreur à l'utilisateur.
     *
     * @param message le message d'erreur.
     */
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Initialisation du panel.
     */
    private void initPanel() {
        this.setLayout(new GridBagLayout());
        this.setBackground(DiscordTheme.BACKGROUND_DARK);

        // Conteneur "Carte" centrée
        DiscordRoundPanel cardPanel = new DiscordRoundPanel();
        cardPanel.setLayout(new GridBagLayout());
        cardPanel.setBackground(DiscordTheme.BACKGROUND_SECONDARY);

        int row = 0;

        // Logo
        ImageIcon logoIcon = new ImageIcon(getClass().getResource(IMAGES_PATH + "logo_50.png"));
        JLabel logoLabel = new JLabel(logoIcon);
        cardPanel.add(logoLabel, new GridBagConstraints(0, row++, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(30, 40, 20, 40), 0, 0));

        // Titre
        JLabel titleLabel = new JLabel("Connexion");
        titleLabel.setFont(DiscordTheme.FONT_HEADER);
        titleLabel.setForeground(DiscordTheme.TEXT_HEADER);
        cardPanel.add(titleLabel, new GridBagConstraints(0, row++, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 40, 20, 40), 0, 0));

        // Sous-titre
        JLabel subTitleLabel = new JLabel("Heureux de vous revoir !");
        subTitleLabel.setFont(DiscordTheme.FONT_NORMAL);
        subTitleLabel.setForeground(DiscordTheme.TEXT_MUTED);
        cardPanel.add(subTitleLabel, new GridBagConstraints(0, row++, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(-15, 40, 30, 40), 0, 0));

        // Label tag
        JLabel tagLabel = new JLabel("TAG UTILISATEUR (@)");
        tagLabel.setFont(DiscordTheme.FONT_SMALL);
        tagLabel.setForeground(DiscordTheme.TEXT_MUTED);
        cardPanel.add(tagLabel, new GridBagConstraints(0, row++, 1, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(10, 40, 5, 40), 0, 0));

        // Champ tag
        mTagField = new DiscordTextField(20);
        cardPanel.add(mTagField, new GridBagConstraints(0, row++, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 40, 20, 40), 0, 0));

        // Label mot de passe
        JLabel passwordLabel = new JLabel("MOT DE PASSE");
        passwordLabel.setFont(DiscordTheme.FONT_SMALL);
        passwordLabel.setForeground(DiscordTheme.TEXT_MUTED);
        cardPanel.add(passwordLabel, new GridBagConstraints(0, row++, 1, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 40, 5, 40), 0, 0));

        // Champ mot de passe
        mPasswordField = new DiscordPasswordField(20);
        cardPanel.add(mPasswordField, new GridBagConstraints(0, row++, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 40, 20, 40), 0, 0));

        // Bouton connexion
        DiscordButton loginButton = new DiscordButton("Se connecter");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mLoginActionListener != null) {
                    String tag = mTagField.getText().trim();
                    String password = new String(mPasswordField.getPassword());
                    mLoginActionListener.onLoginRequested(tag, password);
                }
            }
        });
        cardPanel.add(loginButton, new GridBagConstraints(0, row++, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(10, 40, 10, 40), 0, 0));

        // Lien vers inscription
        JLabel registerLink = new JLabel("Besoin d'un compte ? S'inscrire");
        registerLink.setFont(DiscordTheme.FONT_SMALL);
        registerLink.setForeground(DiscordTheme.LINK_COLOR);
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (mShowRegistrationListener != null) {
                    mShowRegistrationListener.actionPerformed(
                            new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "showRegistration"));
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                registerLink.setText("<html><u>Besoin d'un compte ? S'inscrire</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                registerLink.setText("Besoin d'un compte ? S'inscrire");
            }
        });
        cardPanel.add(registerLink, new GridBagConstraints(0, row++, 1, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 40, 30, 40), 0, 0));

        // Ajout de la carte au panel principal (centré)
        this.add(cardPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
    }
}
