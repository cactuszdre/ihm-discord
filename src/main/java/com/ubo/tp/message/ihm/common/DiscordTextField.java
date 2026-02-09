package main.java.com.ubo.tp.message.ihm.common;

import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JTextField;

/**
 * Champ de texte personnalisé style Discord.
 */
public class DiscordTextField extends JTextField {

    public DiscordTextField(int columns) {
        super(columns);
        this.initStyle();
    }

    public DiscordTextField() {
        super();
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
