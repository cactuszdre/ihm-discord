package main.java.com.ubo.tp.message.ihm.common;

import java.awt.Color;
import java.awt.Font;

/**
 * Définit la palette de couleurs et les polices pour le thème Discord.
 */
public class DiscordTheme {

    // Couleurs de fond
    public static final Color BACKGROUND_DARK = new Color(54, 57, 63); // #36393f (Main)
    public static final Color BACKGROUND_SECONDARY = new Color(47, 49, 54); // #2f3136 (Card)
    public static final Color BACKGROUND_TERTIARY = new Color(32, 34, 37); // #202225 (Inputs)

    // Couleurs de texte
    public static final Color TEXT_HEADER = new Color(255, 255, 255); // #ffffff
    public static final Color TEXT_NORMAL = new Color(220, 221, 222); // #dcddde
    public static final Color TEXT_MUTED = new Color(114, 118, 125); // #72767d (Placeholder)
    public static final Color LINK_COLOR = new Color(0, 176, 244); // #00b0f4

    // Couleurs d'accentuation
    public static final Color BLURPLE = new Color(88, 101, 242); // #5865F2 (Brand)
    public static final Color BLURPLE_HOVER = new Color(71, 82, 196); // #4752c4
    public static final Color GREEN = new Color(87, 242, 135); // #57F287
    public static final Color RED = new Color(237, 66, 69); // #ED4245
    public static final Color BACKGROUND_HOVER = new Color(60, 63, 69); // Hover sur les lignes
    public static final Color WARNING = new Color(250, 166, 26); // Orange avertissement

    // Polices
    public static final Font FONT_HEADER = new Font("SansSerif", Font.BOLD, 24);
    public static final Font FONT_SUBHEADER = new Font("SansSerif", Font.BOLD, 16);
    public static final Font FONT_NORMAL = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font FONT_SMALL = new Font("SansSerif", Font.PLAIN, 12);
}
