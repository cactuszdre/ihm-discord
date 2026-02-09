package main.java.com.ubo.tp.message.ihm;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

/**
 * Classe de la vue principale de l'application.
 */
public class MessageAppMainView extends JFrame {

    /**
     * Chemin vers les images.
     */
    private static final String IMAGES_PATH = "/main/resources/images/";

    /**
     * Constructeur.
     */
    public MessageAppMainView() {
        super("MessageApp");
        this.initFrame();
        this.initMenuBar();
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
