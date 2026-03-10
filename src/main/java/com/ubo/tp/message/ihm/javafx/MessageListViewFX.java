package main.java.com.ubo.tp.message.ihm.javafx;

import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.message.IMessageActionListener;
import main.java.com.ubo.tp.message.ihm.message.IMessageView;

/**
 * Vue JavaFX de la liste des messages (impl IMessageView).
 * Réutilise le MessageController existant via l'interface.
 */
public class MessageListViewFX extends VBox implements IMessageView {

    private IMessageActionListener mActionListener;
    private VBox mMessagesBox;
    private Label mChannelNameLabel;
    private TextField mSearchField;
    private User mCurrentUser;

    public MessageListViewFX() {
        initPanel();
    }

    private void initPanel() {
        setSpacing(8);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: #36393f;");

        // Header canal
        mChannelNameLabel = new Label("Sélectionnez un canal");
        mChannelNameLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 16));
        mChannelNameLabel.setStyle("-fx-text-fill: #ffffff;");

        // Recherche
        mSearchField = FXHelper.styledTextField("Rechercher dans les messages...");
        mSearchField.setMaxWidth(Double.MAX_VALUE);
        mSearchField.textProperty().addListener((obs, o, n) -> {
            if (mActionListener != null)
                mActionListener.onSearchMessage(n);
        });

        // Liste messages
        mMessagesBox = new VBox(3);
        mMessagesBox.setStyle("-fx-background-color: #36393f;");

        ScrollPane scrollPane = new ScrollPane(mMessagesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #36393f; -fx-background-color: #36393f;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        getChildren().addAll(mChannelNameLabel, mSearchField, scrollPane);
    }

    // ========== IMessageView ==========

    @Override
    public void setMessages(List<Message> messages) {
        Platform.runLater(() -> {
            mMessagesBox.getChildren().clear();
            for (Message msg : messages) {
                HBox row = new HBox(8);
                row.setPadding(new Insets(4, 10, 4, 10));
                row.setAlignment(Pos.CENTER_LEFT);

                VBox bubble = new VBox(2);
                HBox.setHgrow(bubble, Priority.ALWAYS);

                Label author = new Label(msg.getSender().getName());
                author.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 13px;");

                Label text = new Label(msg.getText());
                text.setWrapText(true);
                text.setStyle("-fx-text-fill: #dcddde; -fx-font-size: 14px;");

                bubble.getChildren().addAll(author, text);
                row.getChildren().add(bubble);

                // Bouton supprimer si auteur
                if (mCurrentUser != null && msg.getSender().getUuid().equals(mCurrentUser.getUuid())) {
                    Button delBtn = new Button("\u2715");
                    delBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #ed4245; "
                            + "-fx-font-size: 12px; -fx-cursor: hand; -fx-padding: 2 6;");
                    delBtn.setOnAction(e -> {
                        if (mActionListener != null)
                            mActionListener.onDeleteMessage(msg);
                    });
                    row.getChildren().add(delBtn);
                }

                mMessagesBox.getChildren().add(row);
            }
        });
    }

    @Override
    public void setChannelName(String name) {
        Platform.runLater(() -> mChannelNameLabel.setText("# " + name));
    }

    @Override
    public void showError(String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setContentText(message);
            alert.showAndWait();
        });
    }

    @Override
    public void setActionListener(IMessageActionListener listener) {
        mActionListener = listener;
    }

    @Override
    public void setCurrentUser(User user) {
        mCurrentUser = user;
    }
}
