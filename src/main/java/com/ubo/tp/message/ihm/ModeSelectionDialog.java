package main.java.com.ubo.tp.message.ihm;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import main.java.com.ubo.tp.message.ihm.common.DiscordTheme;

/**
 * Fenêtre de sélection du mode d'interface (Swing ou JavaFX).
 * S'affiche au lancement de l'application.
 */
public class ModeSelectionDialog {

    /**
     * Listener pour le choix de mode.
     */
    public interface ModeSelectionListener {
        void onSwingSelected();

        void onJavaFXSelected();
    }

    /**
     * Affiche la fenêtre de sélection de mode.
     *
     * @param listener le listener appelé selon le choix de l'utilisateur.
     */
    public static void show(ModeSelectionListener listener) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowDialog(listener);
            }
        });
    }

    /**
     * Crée et affiche la fenêtre de sélection.
     */
    private static void createAndShowDialog(ModeSelectionListener listener) {
        JFrame frame = new JFrame("MessageApp — Choix du mode");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);

        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(DiscordTheme.BACKGROUND_DARK);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));

        int row = 0;

        // Titre
        JLabel titleLabel = new JLabel("MessageApp");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(DiscordTheme.TEXT_HEADER);
        mainPanel.add(titleLabel, new GridBagConstraints(0, row++, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 10, 0), 0, 0));

        // Sous-titre
        JLabel subtitleLabel = new JLabel("Choisissez le mode d'interface");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(DiscordTheme.TEXT_MUTED);
        mainPanel.add(subtitleLabel, new GridBagConstraints(0, row++, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 30, 0), 0, 0));

        // Panel des boutons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(DiscordTheme.BACKGROUND_DARK);

        // Bouton Swing
        JButton swingButton = createModeButton("Mode Swing", DiscordTheme.BLURPLE);
        swingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                listener.onSwingSelected();
            }
        });
        buttonPanel.add(swingButton);

        // Bouton JavaFX
        JButton javafxButton = createModeButton("Mode JavaFX", new Color(87, 242, 135));
        javafxButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
                listener.onJavaFXSelected();
            }
        });
        buttonPanel.add(javafxButton);

        mainPanel.add(buttonPanel, new GridBagConstraints(0, row++, 2, 1, 1, 0,
                GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));

        frame.setContentPane(mainPanel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /**
     * Crée un bouton stylisé pour le choix de mode.
     */
    private static JButton createModeButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 16));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(180, 50));
        return button;
    }
}
