package main.java.com.ubo.tp.message.ihm.common;

import javax.swing.BorderFactory;
import javax.swing.JPasswordField;

/**
 * Champ mot de passe personnalisé style Discord.
 */
public class DiscordPasswordField extends JPasswordField {

    public DiscordPasswordField(int columns) {
        super(columns);
        this.initStyle();
    }

    private void initStyle() {
        this.setBackground(DiscordTheme.BACKGROUND_TERTIARY);
        this.setForeground(DiscordTheme.TEXT_NORMAL);
        this.setCaretColor(DiscordTheme.TEXT_NORMAL);
        this.setFont(DiscordTheme.FONT_NORMAL);
        this.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    }
}
