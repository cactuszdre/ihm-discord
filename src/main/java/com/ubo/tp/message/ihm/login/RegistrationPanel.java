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
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * Panel de création de compte utilisateur.
 */
public class RegistrationPanel extends JPanel {

    /**
     * Chemin vers les images.
     */
    private static final String IMAGES_PATH = "/main/resources/images/";

    /**
     * Champ de saisie du nom.
     */
    private JTextField mNameField;

    /**
     * Champ de saisie du tag.
     */
    private JTextField mTagField;

    /**
     * Champ de saisie du mot de passe.
     */
    private JPasswordField mPasswordField;

    /**
     * Contrôleur des comptes.
     */
    private AccountController mAccountController;

    /**
     * Listener pour basculer vers le login.
     */
    private ActionListener mShowLoginListener;

    /**
     * Listener appelé après inscription réussie.
     */
    private ActionListener mRegistrationSuccessListener;

    /**
     * Constructeur.
     *
     * @param accountController contrôleur des comptes
     */
    public RegistrationPanel(AccountController accountController) {
        this.mAccountController = accountController;
        this.initPanel();
    }

    /**
     * Définit le listener pour afficher le panel de login.
     */
    public void setShowLoginListener(ActionListener listener) {
        this.mShowLoginListener = listener;
    }

    /**
     * Définit le listener appelé après inscription réussie.
     */
    public void setRegistrationSuccessListener(ActionListener listener) {
        this.mRegistrationSuccessListener = listener;
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
        JLabel titleLabel = new JLabel("Créer un compte");
        titleLabel.setFont(titleLabel.getFont().deriveFont(18f));
        this.add(titleLabel, new GridBagConstraints(0, row++, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 10, 20, 10), 0, 0));

        // Label nom
        JLabel nameLabel = new JLabel("Nom :");
        this.add(nameLabel, new GridBagConstraints(0, row, 1, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(5, 10, 5, 5), 0, 0));

        // Champ nom
        mNameField = new JTextField(20);
        this.add(mNameField, new GridBagConstraints(1, row++, 1, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 10), 0, 0));

        // Label tag
        JLabel tagLabel = new JLabel("Tag (@) :");
        this.add(tagLabel, new GridBagConstraints(0, row, 1, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(5, 10, 5, 5), 0, 0));

        // Champ tag
        mTagField = new JTextField(20);
        this.add(mTagField, new GridBagConstraints(1, row++, 1, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 10), 0, 0));

        // Label mot de passe
        JLabel passwordLabel = new JLabel("Mot de passe :");
        this.add(passwordLabel, new GridBagConstraints(0, row, 1, 1, 0, 0,
                GridBagConstraints.EAST, GridBagConstraints.NONE,
                new Insets(5, 10, 5, 5), 0, 0));

        // Champ mot de passe
        mPasswordField = new JPasswordField(20);
        this.add(mPasswordField, new GridBagConstraints(1, row++, 1, 1, 1, 0,
                GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL,
                new Insets(5, 5, 5, 10), 0, 0));

        // Bouton créer
        JButton registerButton = new JButton("Créer le compte");
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doRegister();
            }
        });
        this.add(registerButton, new GridBagConstraints(0, row++, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(20, 10, 10, 10), 0, 0));

        // Lien vers login
        JLabel loginLink = new JLabel("<html><u>J'ai déjà un compte</u></html>");
        loginLink.setForeground(java.awt.Color.BLUE);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLink.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (mShowLoginListener != null) {
                    mShowLoginListener.actionPerformed(
                            new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "showLogin"));
                }
            }
        });
        this.add(loginLink, new GridBagConstraints(0, row++, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(10, 10, 20, 10), 0, 0));
    }

    /**
     * Effectue l'inscription.
     */
    private void doRegister() {
        String name = mNameField.getText().trim();
        String tag = mTagField.getText().trim();
        String password = new String(mPasswordField.getPassword());

        // Validation des champs obligatoires (SRS-MAP-USR-002)
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Le nom est obligatoire.",
                    "Champ requis",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (tag.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Le tag est obligatoire.",
                    "Champ requis",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Vérification unicité du tag (SRS-MAP-USR-003)
        if (!mAccountController.isTagUnique(tag)) {
            JOptionPane.showMessageDialog(this,
                    "Ce tag est déjà utilisé par un autre utilisateur.",
                    "Tag non disponible",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Enregistrement
        boolean success = mAccountController.register(name, tag, password);

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Compte créé avec succès !\nVous pouvez maintenant vous connecter.",
                    "Inscription réussie",
                    JOptionPane.INFORMATION_MESSAGE);

            // Effacer les champs
            mNameField.setText("");
            mTagField.setText("");
            mPasswordField.setText("");

            if (mRegistrationSuccessListener != null) {
                mRegistrationSuccessListener.actionPerformed(
                        new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "registrationSuccess"));
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Erreur lors de la création du compte.",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
