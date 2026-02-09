package main.java.com.ubo.tp.message.ihm.common;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

/**
 * Panel avec un fond arrondi.
 */
public class DiscordRoundPanel extends JPanel {

    public DiscordRoundPanel() {
        super();
        this.setOpaque(false); // Important pour la transparence des coins
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

        super.paintComponent(g);
        g2.dispose();
    }
}
