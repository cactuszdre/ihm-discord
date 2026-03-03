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
import javax.swing.JSplitPane;

import main.java.com.ubo.tp.message.ihm.channel.ChannelListView;
import main.java.com.ubo.tp.message.ihm.common.DiscordTheme;
import main.java.com.ubo.tp.message.ihm.login.LoginPanel;
import main.java.com.ubo.tp.message.ihm.login.RegistrationPanel;
import main.java.com.ubo.tp.message.ihm.message.MessageInputView;
import main.java.com.ubo.tp.message.ihm.message.MessageListView;
import main.java.com.ubo.tp.message.ihm.user.UserListView;

/**
 * Classe de la vue principale de l'application.
 * Ne contient aucune logique métier (MVC — View).
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
     * Vue de la liste des canaux.
     */
    private ChannelListView mChannelListView;

    /**
     * Vue de la liste des messages.
     */
    private MessageListView mMessageListView;

    /**
     * Vue de saisie de message.
     */
    private MessageInputView mMessageInputView;

    /**
     * Vue de la liste des utilisateurs.
     */
    private UserListView mUserListView;

    /**
     * Listener de déconnexion.
     */
    private ActionListener mLogoutListener;

    /**
     * Menu item de déconnexion (visible uniquement après connexion).
     */
    private JMenuItem mLogoutItem;

    /**
     * Constructeur.
     */
    public MessageAppMainView() {
        super("MessageApp");
        this.initFrame();
        this.initMenuBar();
        this.initPanels();
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
        this.setPreferredSize(new Dimension(1100, 700));
        this.setLayout(new BorderLayout());
    }

    /**
     * Initialisation de la barre de menu.
     */
    private void initMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // ---- Menu "Fichier" ----
        JMenu fichierMenu = new JMenu("Fichier");

        // Entrée "Déconnexion"
        mLogoutItem = new JMenuItem("Déconnexion");
        mLogoutItem.setToolTipText("Se déconnecter");
        mLogoutItem.setVisible(false);
        mLogoutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (mLogoutListener != null) {
                    mLogoutListener.actionPerformed(
                            new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "logout"));
                }
            }
        });
        fichierMenu.add(mLogoutItem);

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
    private void initPanels() {
        mCardLayout = new CardLayout();
        mContentPanel = new JPanel(mCardLayout);
        mContentPanel.setBackground(DiscordTheme.BACKGROUND_DARK);

        // Panel de login (View pure, sans Controller)
        mLoginPanel = new LoginPanel();
        mLoginPanel.setShowRegistrationListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showRegistrationPanel();
            }
        });

        // Panel d'inscription (View pure, sans Controller)
        mRegistrationPanel = new RegistrationPanel();
        mRegistrationPanel.setShowLoginListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoginPanel();
            }
        });

        // Panel principal avec layout 3 colonnes
        mMainPanel = new JPanel(new BorderLayout());
        mMainPanel.setBackground(DiscordTheme.BACKGROUND_DARK);

        // ---- Sidebar gauche : canaux ----
        mChannelListView = new ChannelListView();

        // ---- Centre : messages ----
        mMessageListView = new MessageListView();
        mMessageInputView = new MessageInputView();

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(DiscordTheme.BACKGROUND_DARK);
        centerPanel.add(mMessageListView, BorderLayout.CENTER);
        centerPanel.add(mMessageInputView, BorderLayout.SOUTH);

        // ---- Sidebar droite : utilisateurs ----
        mUserListView = new UserListView();

        // Assemblage du main panel
        mMainPanel.add(mChannelListView, BorderLayout.WEST);
        mMainPanel.add(centerPanel, BorderLayout.CENTER);
        mMainPanel.add(mUserListView, BorderLayout.EAST);

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
        mLogoutItem.setVisible(false);
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
        mLogoutItem.setVisible(true);
        mCardLayout.show(mContentPanel, CARD_MAIN);
    }

    // ========== Getters pour les Views (utilisés par MessageApp pour le câblage
    // MVC) ==========

    public LoginPanel getLoginPanel() {
        return mLoginPanel;
    }

    public RegistrationPanel getRegistrationPanel() {
        return mRegistrationPanel;
    }

    public ChannelListView getChannelListView() {
        return mChannelListView;
    }

    public MessageListView getMessageListView() {
        return mMessageListView;
    }

    public MessageInputView getMessageInputView() {
        return mMessageInputView;
    }

    public UserListView getUserListView() {
        return mUserListView;
    }

    /**
     * Définit le listener de déconnexion.
     */
    public void setLogoutListener(ActionListener listener) {
        this.mLogoutListener = listener;
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
