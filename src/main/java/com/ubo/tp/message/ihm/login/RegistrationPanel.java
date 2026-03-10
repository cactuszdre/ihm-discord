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
 * Vue de création de compte utilisateur (MVC — View pure).
 * Ne contient aucune logique métier. Expose les actions via
 * IRegistrationActionListener.
 */
public class RegistrationPanel extends JPanel {

    /**
     * Chemin vers les images.
     */
    private static final String IMAGES_PATH = "/images/";

    /**
     * Champ de saisie du nom.
     */
    private DiscordTextField mNameField;

    /**
     * Champ de saisie du tag.
     */
    private DiscordTextField mTagField;

    /**
     * Champ de saisie du mot de passe.
     */
    private DiscordPasswordField mPasswordField;

    /**
     * Listener pour basculer vers le login.
     */
    private ActionListener mShowLoginListener;

    /**
     * Listener des actions d'inscription (Controller).
     */
    private IRegistrationActionListener mRegistrationActionListener;

    /**
     * Constructeur.
     */
    public RegistrationPanel() {
        this.initPanel();
    }

    /**
     * Définit le listener pour afficher le panel de login.
     */
    public void setShowLoginListener(ActionListener listener) {
        this.mShowLoginListener = listener;
    }

    /**
     * Définit le listener des actions d'inscription (Controller MVC).
     */
    public void setRegistrationActionListener(IRegistrationActionListener listener) {
        this.mRegistrationActionListener = listener;
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
     * Affiche un message de succès à l'utilisateur.
     *
     * @param message le message de succès.
     */
    public void showSuccess(String message) {
        JOptionPane.showMessageDialog(this, message, "Succès", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Efface les champs de saisie.
     */
    public void clearFields() {
        mNameField.setText("");
        mTagField.setText("");
        mPasswordField.setText("");
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
                new Insets(30, 40, 10, 40), 0, 0));

        // Titre
        JLabel titleLabel = new JLabel("Créer un compte");
        titleLabel.setFont(DiscordTheme.FONT_HEADER);
        titleLabel.setForeground(DiscordTheme.TEXT_HEADER);
        cardPanel.add(titleLabel, new GridBagConstraints(0, row++, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 40, 20, 40), 0, 0));

        // Label nom
        JLabel nameLabel = new JLabel("NOM");
        nameLabel.setFont(DiscordTheme.FONT_SMALL);
        nameLabel.setForeground(DiscordTheme.TEXT_MUTED);
        cardPanel.add(nameLabel, new GridBagConstraints(0, row++, 1, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(10, 40, 5, 40), 0, 0));

        // Champ nom
        mNameField = new DiscordTextField(20);
        cardPanel.add(mNameField, new GridBagConstraints(0, row++, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 40, 0, 40), 0, 0));

        // Label tag
        JLabel tagLabel = new JLabel("TAG (@)");
        tagLabel.setFont(DiscordTheme.FONT_SMALL);
        tagLabel.setForeground(DiscordTheme.TEXT_MUTED);
        cardPanel.add(tagLabel, new GridBagConstraints(0, row++, 1, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(15, 40, 5, 40), 0, 0));

        // Champ tag
        mTagField = new DiscordTextField(20);
        cardPanel.add(mTagField, new GridBagConstraints(0, row++, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 40, 0, 40), 0, 0));

        // Label mot de passe
        JLabel passwordLabel = new JLabel("MOT DE PASSE");
        passwordLabel.setFont(DiscordTheme.FONT_SMALL);
        passwordLabel.setForeground(DiscordTheme.TEXT_MUTED);
        cardPanel.add(passwordLabel, new GridBagConstraints(0, row++, 1, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(15, 40, 5, 40), 0, 0));

        // Champ mot de passe
        mPasswordField = new DiscordPasswordField(20);
        cardPanel.add(mPasswordField, new GridBagConstraints(0, row++, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(0, 40, 0, 40), 0, 0));

        // Bouton créer
        DiscordButton registerButton = new DiscordButton("Continuer");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mRegistrationActionListener != null) {
                    mRegistrationActionListener.onRegisterRequested(
                            mNameField.getText().trim(),
                            mTagField.getText().trim(),
                            new String(mPasswordField.getPassword()));
                }
            }
        });
        cardPanel.add(registerButton, new GridBagConstraints(0, row++, 1, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
                new Insets(30, 40, 10, 40), 0, 0));

        // Lien vers login
        JLabel loginLink = new JLabel("Tu as déjà un compte ?");
        loginLink.setFont(DiscordTheme.FONT_SMALL);
        loginLink.setForeground(DiscordTheme.LINK_COLOR);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (mShowLoginListener != null) {
                    mShowLoginListener.actionPerformed(
                            new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "showLogin"));
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                loginLink.setText("<html><u>Tu as déjà un compte ?</u></html>");
            }

            @Override
            public void mouseExited(MouseEvent e) {
                loginLink.setText("Tu as déjà un compte ?");
            }
        });
        cardPanel.add(loginLink, new GridBagConstraints(0, row++, 1, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 40, 30, 40), 0, 0));

        // Ajout au panel principal
        this.add(cardPanel, new GridBagConstraints(0, 0, 1, 1, 1, 1,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
    }
}
