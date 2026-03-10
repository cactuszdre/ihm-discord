package main.java.com.ubo.tp.message.ihm.javafx;

import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.control.Label;

/**
 * Utilitaires de style pour les composants JavaFX (thème Discord).
 */
public final class FXHelper {

    private FXHelper() {
    }

    public static Label styledLabel(String text, double size, boolean bold, String color) {
        Label label = new Label(text);
        label.setFont(Font.font("Segoe UI", bold ? FontWeight.BOLD : FontWeight.NORMAL, size));
        label.setStyle("-fx-text-fill: " + color + ";");
        return label;
    }

    public static TextField styledTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setMaxWidth(300);
        field.setStyle("-fx-background-color: #202225; -fx-text-fill: #dcddde; "
                + "-fx-prompt-text-fill: #72767d; -fx-border-color: #202225; "
                + "-fx-padding: 8; -fx-font-size: 13px;");
        return field;
    }

    public static PasswordField styledPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.setMaxWidth(300);
        field.setStyle("-fx-background-color: #202225; -fx-text-fill: #dcddde; "
                + "-fx-prompt-text-fill: #72767d; -fx-border-color: #202225; "
                + "-fx-padding: 8; -fx-font-size: 13px;");
        return field;
    }

    public static Button styledButton(String text, String bgColor) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: " + bgColor + "; -fx-text-fill: white; "
                + "-fx-font-size: 13px; -fx-font-weight: bold; "
                + "-fx-padding: 8 16; -fx-cursor: hand;");
        return btn;
    }
}
