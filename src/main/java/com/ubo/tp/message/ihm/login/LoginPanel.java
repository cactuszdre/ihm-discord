package main.java.com.ubo.tp.message.ihm.login;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Panel de connexion utilisateur.
 */
public class LoginPanel extends JPanel {

    /**
     * Chemin vers les images.
     */
    private static final String IMAGES_PATH = "/main/resources/images/";

    /**
     * Champ de saisie du tag.
     */
    private JTextField mTagField;

    /**
     * Contrôleur des comptes.
     */
    private AccountController mAccountController;

    /**
     * Listener pour basculer vers l'inscription.
     */
    private ActionListener mShowRegistrationListener;

    /**
     * Listener appelé après une connexion réussie.
     */
    private ActionListener mLoginSuccessListener;

    /**
     * Constructeur.
     *
     * @param accountController contrôleur des comptes
     */
    public LoginPanel(AccountController accountController) {
        this.mAccountController = accountController;
        this.initPanel();
    }

    /**
     * Définit le listener pour afficher le panel d'inscription.
     */
    public void setShowRegistrationListener(ActionListener listener) {
        this.mShowRegistrationListener = listener;
    }

    /**
     * Définit le listener appelé après connexion réussie.
     */
    public void setLoginSuccessListener(ActionListener listener) {
        this.mLoginSuccessListener = listener;
    }

    /**
     * Initialisation du panel.
     */
    private void initPanel() {
        this.setLayout(new GridBagLayout());

        int row = 0;

        // Logo
        ImageIcon logoIcon = new ImageIcon(getClass().getResource(IMAGES_PATH + "logo_50.png"));
        JLabel logoLabel = new JLabel(logoIcon);
        this.add(logoLabel, new GridBagConstraints(0, row++, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(20, 10, 20, 10), 0, 0));

        // Titre
        JLabel titleLabel = new JLabel("Connexion");
        titleLabel.setFont(titleLabel.getFont().deriveFont(18f));
        this.add(titleLabel, new GridBagConstraints(0, row++, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 10, 20, 10), 0, 0));

        // Label tag
        JLabel tagLabel = new JLabel("Tag utilisateur (@) :");
        this.add(tagLabel, new GridBagConstraints(0, row, 1, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(5, 10, 5, 5), 0, 0));

        // Champ tag
        mTagField = new JTextField(20);
        this.add(mTagField, new GridBagConstraints(1, row++, 1, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 10), 0, 0));

        // Bouton connexion
        JButton loginButton = new JButton("Se connecter");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doLogin();
            }
        });
        this.add(loginButton, new GridBagConstraints(0, row++, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(20, 10, 10, 10), 0, 0));

        // Lien vers inscription
        JLabel registerLink = new JLabel("<html><u>Créer un compte</u></html>");
        registerLink.setForeground(java.awt.Color.BLUE);
        registerLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        registerLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (mShowRegistrationListener != null) {
                    mShowRegistrationListener.actionPerformed(
                            new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "showRegistration"));
                }
            }
        });
        this.add(registerLink, new GridBagConstraints(0, row++, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 10, 20, 10), 0, 0));
    }

    /**
     * Effectue la connexion.
     */
    private void doLogin() {
        String tag = mTagField.getText().trim();

        if (tag.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez saisir votre tag utilisateur.",
                    "Champ requis",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = mAccountController.login(tag);

        if (success) {
            if (mLoginSuccessListener != null) {
                mLoginSuccessListener.actionPerformed(
                        new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "loginSuccess"));
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Aucun utilisateur trouvé avec ce tag.",
                    "Erreur de connexion",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
