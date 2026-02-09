package main.java.com.ubo.tp.message.ihm;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import main.java.com.ubo.tp.message.ihm.common.DiscordTheme;
import main.java.com.ubo.tp.message.ihm.login.AccountController;
import main.java.com.ubo.tp.message.ihm.login.LoginPanel;
import main.java.com.ubo.tp.message.ihm.login.RegistrationPanel;

/**
 * Classe de la vue principale de l'application.
 */
public class MessageAppMainView extends JFrame {

    /**
     * Chemin vers les images.
     */
    private static final String IMAGES_PATH = "/main/resources/images/";

    /**
     * Constantes pour les noms de cartes.
     */
    private static final String CARD_LOGIN = "LOGIN";
    private static final String CARD_REGISTER = "REGISTER";
    private static final String CARD_MAIN = "MAIN";

    /**
     * Layout pour basculer entre les panels.
     */
    private CardLayout mCardLayout;

    /**
     * Panel conteneur principal.
     */
    private JPanel mContentPanel;

    /**
     * Panel de login.
     */
    private LoginPanel mLoginPanel;

    /**
     * Panel d'inscription.
     */
    private RegistrationPanel mRegistrationPanel;

    /**
     * Panel principal (après connexion).
     */
    private JPanel mMainPanel;

    /**
     * Constructeur.
     *
     * @param accountController contrôleur des comptes
     */
    public MessageAppMainView(AccountController accountController) {
        super("MessageApp");
        this.initFrame();
        this.initMenuBar();
        this.initPanels(accountController);
    }

    /**
     * Initialisation de la fenêtre.
     */
    private void initFrame() {
        // Icône de la fenêtre
        ImageIcon logoIcon = new ImageIcon(getClass().getResource(IMAGES_PATH + "logo_20.png"));
        this.setIconImage(logoIcon.getImage());

        // Configuration de la fenêtre
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setPreferredSize(new Dimension(800, 600));
        this.setLayout(new BorderLayout());
    }

    /**
     * Initialisation de la barre de menu.
     */
    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // ---- Menu "Fichier" ----
        JMenu fichierMenu = new JMenu("Fichier");

        // Entrée "Quitter"
        JMenuItem quitterItem = new JMenuItem("Quitter");
        ImageIcon exitIcon = new ImageIcon(getClass().getResource(IMAGES_PATH + "exitIcon_20.png"));
        quitterItem.setIcon(exitIcon);
        quitterItem.setToolTipText("Fermer l'application");
        quitterItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fichierMenu.add(quitterItem);

        // ---- Menu "?" ----
        JMenu helpMenu = new JMenu("?");

        // Entrée "A propos"
        JMenuItem aProposItem = new JMenuItem("A propos");
        aProposItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAboutDialog();
            }
        });
        helpMenu.add(aProposItem);

        // Ajout des menus à la barre
        menuBar.add(fichierMenu);
        menuBar.add(helpMenu);

        this.setJMenuBar(menuBar);
    }

    /**
     * Initialisation des panels avec CardLayout.
     */
    private void initPanels(AccountController accountController) {
        mCardLayout = new CardLayout();
        mContentPanel = new JPanel(mCardLayout);
        mContentPanel.setBackground(DiscordTheme.BACKGROUND_DARK); // Fond général

        // Panel de login
        mLoginPanel = new LoginPanel(accountController);
        mLoginPanel.setShowRegistrationListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRegistrationPanel();
            }
        });

        // Panel d'inscription
        mRegistrationPanel = new RegistrationPanel(accountController);
        mRegistrationPanel.setShowLoginListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoginPanel();
            }
        });
        mRegistrationPanel.setRegistrationSuccessListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoginPanel();
            }
        });

        // Panel principal (placeholder pour l'instant)
        mMainPanel = new JPanel(new BorderLayout());
        mMainPanel.setBackground(DiscordTheme.BACKGROUND_DARK);

        // Ajout des cartes
        mContentPanel.add(mLoginPanel, CARD_LOGIN);
        mContentPanel.add(mRegistrationPanel, CARD_REGISTER);
        mContentPanel.add(mMainPanel, CARD_MAIN);

        this.add(mContentPanel, BorderLayout.CENTER);

        // Afficher login par défaut
        showLoginPanel();
    }

    /**
     * Affiche le panel de login.
     */
    public void showLoginPanel() {
        mCardLayout.show(mContentPanel, CARD_LOGIN);
    }

    /**
     * Affiche le panel d'inscription.
     */
    public void showRegistrationPanel() {
        mCardLayout.show(mContentPanel, CARD_REGISTER);
    }

    /**
     * Affiche le panel principal.
     */
    public void showMainPanel() {
        mCardLayout.show(mContentPanel, CARD_MAIN);
    }

    /**
     * Retourne le panel principal pour y ajouter du contenu.
     */
    public JPanel getMainPanel() {
        return mMainPanel;
    }

    /**
     * Définit le listener de succès de connexion sur le LoginPanel.
     */
    public void setLoginSuccessListener(ActionListener listener) {
        mLoginPanel.setLoginSuccessListener(listener);
    }

    /**
     * Affiche la boite de dialogue "A propos".
     */
    private void showAboutDialog() {
        ImageIcon logoIcon = new ImageIcon(getClass().getResource(IMAGES_PATH + "logo_50.png"));
        JOptionPane.showMessageDialog(
                this,
                "UBO M2-TIIL\nDépartement Informatique",
                "A propos",
                JOptionPane.INFORMATION_MESSAGE,
                logoIcon);
    }
}
