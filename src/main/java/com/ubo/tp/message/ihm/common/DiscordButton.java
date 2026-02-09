package main.java.com.ubo.tp.message.ihm.common;

import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;

/**
 * Bouton personnalisé avec le style Discord (arrondi, couleur Blurple).
 */
public class DiscordButton extends JButton {

    private boolean isHovered = false;

    public DiscordButton(String text) {
        super(text);
        super.setContentAreaFilled(false);
        this.setFocusPainted(false);
        this.setBorderPainted(false);
        this.setForeground(DiscordTheme.TEXT_HEADER);
        this.setFont(DiscordTheme.FONT_NORMAL);
        this.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isHovered) {
            g2.setColor(DiscordTheme.BLURPLE_HOVER);
        } else {
            g2.setColor(DiscordTheme.BLURPLE);
        }

        // Fond arrondi
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);

        // Texte
        super.paintComponent(g);

        g2.dispose();
    }
}
