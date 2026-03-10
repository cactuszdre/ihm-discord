package main.java.com.ubo.tp.message.ihm.notification;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import main.java.com.ubo.tp.message.ihm.common.DiscordTheme;

/**
 * Composant de notification toast (popup non-modal).
 * Affiche un message temporaire en bas à droite de l'écran
 * puis disparaît automatiquement après quelques secondes.
 */
public class ToastNotification {

    /**
     * Durée d'affichage du toast en millisecondes.
     */
    private static final int DISPLAY_DURATION_MS = 4000;

    /**
     * Affiche une notification toast.
     *
     * @param title   titre de la notification (ex: nom de l'expéditeur).
     * @param message contenu du message (aperçu).
     */
    public static void show(final String title, final String message) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                final JWindow window = new JWindow();
                window.setAlwaysOnTop(true);

                JPanel panel = new JPanel(new BorderLayout(8, 4));
                panel.setBackground(DiscordTheme.BACKGROUND_TERTIARY);
                panel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(DiscordTheme.BLURPLE, 2),
                        BorderFactory.createEmptyBorder(12, 16, 12, 16)));

                // Titre (nom de l'expéditeur)
                JLabel titleLabel = new JLabel(title);
                titleLabel.setFont(DiscordTheme.FONT_HEADER);
                titleLabel.setForeground(DiscordTheme.TEXT_HEADER);
                panel.add(titleLabel, BorderLayout.NORTH);

                // Message (aperçu)
                String truncatedMessage = message.length() > 80
                        ? message.substring(0, 80) + "..."
                        : message;
                JLabel messageLabel = new JLabel(truncatedMessage);
                messageLabel.setFont(DiscordTheme.FONT_NORMAL);
                messageLabel.setForeground(DiscordTheme.TEXT_NORMAL);
                panel.add(messageLabel, BorderLayout.CENTER);

                window.getContentPane().add(panel);
                window.setPreferredSize(new Dimension(350, 80));
                window.pack();

                // Positionner en bas à droite de l'écran
                Dimension screenSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                window.setLocation(
                        screenSize.width - window.getWidth() - 20,
                        screenSize.height - window.getHeight() - 60);

                window.setVisible(true);

                // Timer de fermeture automatique
                Timer timer = new Timer(DISPLAY_DURATION_MS, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        window.dispose();
                    }
                });
                timer.setRepeats(false);
                timer.start();
            }
        });
    }
}
