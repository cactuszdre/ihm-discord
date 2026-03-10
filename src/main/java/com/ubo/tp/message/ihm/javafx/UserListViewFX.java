package main.java.com.ubo.tp.message.ihm.javafx;

import java.util.List;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.user.IUserActionListener;
import main.java.com.ubo.tp.message.ihm.user.IUserView;

/**
 * Vue JavaFX de la liste des utilisateurs (impl IUserView).
 * Réutilise le UserController existant via l'interface.
 */
public class UserListViewFX extends VBox implements IUserView {

    private IUserActionListener mActionListener;
    private ListView<User> mUserList;
    private TextField mSearchField;
    private User mCurrentUser;

    public UserListViewFX() {
        initPanel();
    }

    private void initPanel() {
        setSpacing(8);
        setPadding(new Insets(10));
        setPrefWidth(200);
        setStyle("-fx-background-color: #2f3136;");

        Label title = new Label("UTILISATEURS");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        title.setStyle("-fx-text-fill: #8e9297;");

        mSearchField = FXHelper.styledTextField("Rechercher...");
        mSearchField.setMaxWidth(Double.MAX_VALUE);
        mSearchField.textProperty().addListener((obs, o, n) -> {
            if (mActionListener != null)
                mActionListener.onSearchUser(n);
        });

        mUserList = new ListView<>();
        mUserList.setStyle("-fx-background-color: #2f3136; -fx-control-inner-background: #2f3136;");
        mUserList.setCellFactory(param -> new UserCell());
        VBox.setVgrow(mUserList, Priority.ALWAYS);

        getChildren().addAll(title, mSearchField, mUserList);
    }

    // ========== IUserView ==========

    @Override
    public void setUsers(List<User> users) {
        Platform.runLater(() -> mUserList.getItems().setAll(users));
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
    public void setActionListener(IUserActionListener listener) {
        mActionListener = listener;
    }

    @Override
    public void setCurrentUser(User user) {
        mCurrentUser = user;
        Platform.runLater(() -> mUserList.refresh());
    }

    // ========== Cell ==========

    private class UserCell extends ListCell<User> {
        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);
            if (empty || user == null) {
                setText(null);
                setGraphic(null);
                setContextMenu(null);
            } else {
                HBox box = new HBox(6);
                box.setAlignment(Pos.CENTER_LEFT);

                Circle dot = new Circle(5);
                dot.setFill(user.isOnline() ? Color.web("#57f287") : Color.web("#747f8d"));

                Label nameLabel = new Label(user.getName() + " @" + user.getUserTag());
                nameLabel.setStyle("-fx-text-fill: #dcddde; -fx-font-size: 12px;");

                box.getChildren().addAll(dot, nameLabel);
                setGraphic(box);
                setText(null);
                setStyle("-fx-background-color: transparent;");

                // Menu contextuel
                ContextMenu ctx = new ContextMenu();

                MenuItem dmItem = new MenuItem("Envoyer un message direct");
                dmItem.setOnAction(e -> {
                    if (mActionListener != null)
                        mActionListener.onSendDirectMessage(user);
                });
                ctx.getItems().add(dmItem);

                if (mCurrentUser != null && user.getUuid().equals(mCurrentUser.getUuid())) {
                    MenuItem renameItem = new MenuItem("Modifier mon nom");
                    renameItem.setOnAction(e -> {
                        TextInputDialog dialog = new TextInputDialog(mCurrentUser.getName());
                        dialog.setTitle("Modifier le nom");
                        dialog.setHeaderText("Modifier votre nom d'utilisateur");
                        dialog.setContentText("Nouveau nom :");
                        dialog.showAndWait().ifPresent(newName -> {
                            if (!newName.trim().isEmpty() && mActionListener != null) {
                                mActionListener.onEditUserName(newName.trim());
                            }
                        });
                    });
                    ctx.getItems().add(renameItem);

                    MenuItem deleteItem = new MenuItem("Supprimer mon compte");
                    deleteItem.setStyle("-fx-text-fill: #ed4245;");
                    deleteItem.setOnAction(e -> {
                        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                        confirm.setTitle("Supprimer le compte");
                        confirm.setHeaderText("Êtes-vous sûr ?");
                        confirm.setContentText("Cette action est irréversible.");
                        confirm.showAndWait().ifPresent(btn -> {
                            if (btn.getText().equals("OK") && mActionListener != null) {
                                mActionListener.onDeleteAccount();
                            }
                        });
                    });
                    ctx.getItems().add(deleteItem);
                }
                setContextMenu(ctx);
            }
        }
    }
}
