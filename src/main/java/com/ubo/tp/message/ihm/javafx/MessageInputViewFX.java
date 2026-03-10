package main.java.com.ubo.tp.message.ihm.javafx;

import java.util.List;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.message.IMessageActionListener;
import main.java.com.ubo.tp.message.ihm.message.IMessageInputView;

/**
 * Vue JavaFX de saisie de message.
 * Barre de saisie avec champ texte, bouton envoi, compteur et @mention.
 */
public class MessageInputViewFX extends HBox implements IMessageInputView {

    private static final int MAX_LENGTH = 200;
    private IMessageActionListener mActionListener;
    private TextField mTextField;
    private Label mCharCounter;
    private List<User> mAvailableUsers;

    public MessageInputViewFX() {
        initPanel();
    }

    public void setActionListener(IMessageActionListener listener) {
        mActionListener = listener;
    }

    public void setAvailableUsers(List<User> users) {
        mAvailableUsers = users;
    }

    private void initPanel() {
        setSpacing(8);
        setAlignment(javafx.geometry.Pos.CENTER);
        setPadding(new Insets(8, 10, 8, 10));

        mTextField = FXHelper.styledTextField("Envoyer un message...");
        HBox.setHgrow(mTextField, Priority.ALWAYS);
        mTextField.setMaxWidth(Double.MAX_VALUE);

        mTextField.setOnAction(e -> doSend());
        mTextField.textProperty().addListener((obs, o, n) -> {
            updateCounter();
            // Autocomplétion @mention
            if (n.length() > 0 && n.charAt(n.length() - 1) == '@') {
                showMentionPopup();
            }
        });

        mCharCounter = new Label("0/" + MAX_LENGTH);
        mCharCounter.setStyle("-fx-text-fill: #72767d; -fx-font-size: 11px;");

        Button sendBtn = FXHelper.styledButton("Envoyer", "#5865f2");
        sendBtn.setOnAction(e -> doSend());

        getChildren().addAll(mTextField, mCharCounter, sendBtn);
    }

    private void doSend() {
        String text = mTextField.getText().trim();
        if (text.isEmpty())
            return;
        if (text.length() > MAX_LENGTH) {
            mCharCounter.setStyle("-fx-text-fill: #ed4245; -fx-font-size: 11px;");
            return;
        }
        if (mActionListener != null) {
            mActionListener.onSendMessage(text);
        }
        mTextField.clear();
        updateCounter();
    }

    private void updateCounter() {
        int len = mTextField.getText().length();
        mCharCounter.setText(len + "/" + MAX_LENGTH);
        if (len > MAX_LENGTH) {
            mCharCounter.setStyle("-fx-text-fill: #ed4245; -fx-font-size: 11px;");
        } else if (len > MAX_LENGTH * 0.9) {
            mCharCounter.setStyle("-fx-text-fill: #faa61a; -fx-font-size: 11px;");
        } else {
            mCharCounter.setStyle("-fx-text-fill: #72767d; -fx-font-size: 11px;");
        }
    }

    private void showMentionPopup() {
        if (mAvailableUsers == null || mAvailableUsers.isEmpty())
            return;
        ContextMenu popup = new ContextMenu();
        for (User user : mAvailableUsers) {
            MenuItem item = new MenuItem(user.getName() + " (@" + user.getUserTag() + ")");
            item.setOnAction(e -> {
                String current = mTextField.getText();
                int lastAt = current.lastIndexOf('@');
                if (lastAt >= 0) {
                    mTextField.setText(current.substring(0, lastAt) + "@" + user.getUserTag() + " ");
                    mTextField.positionCaret(mTextField.getText().length());
                }
                updateCounter();
            });
            popup.getItems().add(item);
        }
        popup.show(mTextField, javafx.geometry.Side.TOP, 0, 0);
    }
}
