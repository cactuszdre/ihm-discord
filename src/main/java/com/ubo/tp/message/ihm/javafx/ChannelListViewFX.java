package main.java.com.ubo.tp.message.ihm.javafx;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.channel.IChannelActionListener;
import main.java.com.ubo.tp.message.ihm.channel.IChannelView;

/**
 * Vue JavaFX de la liste des canaux (impl IChannelView).
 * Réutilise le ChannelController existant via l'interface.
 */
public class ChannelListViewFX extends VBox implements IChannelView {

    private IChannelActionListener mActionListener;
    private ListView<Channel> mChannelList;
    private TextField mSearchField;
    private Set<UUID> mUnreadChannelIds;
    private User mConnectedUser;

    public ChannelListViewFX() {
        initPanel();
    }

    public void setConnectedUser(User user) {
        mConnectedUser = user;
    }

    private void initPanel() {
        setSpacing(8);
        setPadding(new Insets(10));
        setPrefWidth(230);
        setStyle("-fx-background-color: #2f3136;");

        Label title = new Label("CANAUX");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        title.setStyle("-fx-text-fill: #8e9297;");

        mSearchField = FXHelper.styledTextField("Rechercher un canal...");
        mSearchField.setMaxWidth(Double.MAX_VALUE);
        mSearchField.textProperty().addListener((obs, o, n) -> {
            if (mActionListener != null)
                mActionListener.onSearchChannel(n);
        });

        mChannelList = new ListView<>();
        mChannelList.setStyle("-fx-background-color: #2f3136; -fx-control-inner-background: #2f3136;");
        mChannelList.setCellFactory(param -> new ChannelCell());
        mChannelList.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null && mActionListener != null) {
                        mActionListener.onChannelSelected(newVal);
                    }
                });
        VBox.setVgrow(mChannelList, Priority.ALWAYS);

        // Boutons création
        HBox createBtns = new HBox(5);
        Button publicBtn = FXHelper.styledButton("+ Public", "#5865f2");
        publicBtn.setStyle(publicBtn.getStyle() + "-fx-font-size: 11px; -fx-padding: 5 10;");
        publicBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Nouveau canal public");
            dialog.setHeaderText("Créer un canal public");
            dialog.setContentText("Nom du canal :");
            dialog.showAndWait().ifPresent(name -> {
                if (!name.trim().isEmpty() && mActionListener != null) {
                    mActionListener.onCreatePublicChannel(name.trim());
                }
            });
        });

        Button privateBtn = FXHelper.styledButton("+ Privé", "#57f287");
        privateBtn.setStyle(privateBtn.getStyle() + "-fx-font-size: 11px; -fx-padding: 5 10; -fx-text-fill: #000;");
        privateBtn.setOnAction(e -> {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Nouveau canal privé");
            dialog.setHeaderText("Créer un canal privé");
            dialog.setContentText("Nom du canal :");
            dialog.showAndWait().ifPresent(name -> {
                if (!name.trim().isEmpty() && mActionListener != null) {
                    mActionListener.onCreatePrivateChannel(name.trim());
                }
            });
        });
        createBtns.getChildren().addAll(publicBtn, privateBtn);

        getChildren().addAll(title, mSearchField, mChannelList, createBtns);
    }

    // ========== IChannelView ==========

    @Override
    public void setChannels(List<Channel> channels) {
        Platform.runLater(() -> {
            Channel selected = mChannelList.getSelectionModel().getSelectedItem();
            mChannelList.getItems().setAll(channels);
            if (selected != null && channels.contains(selected)) {
                mChannelList.getSelectionModel().select(selected);
            }
        });
    }

    @Override
    public void setSelectedChannel(Channel channel) {
        Platform.runLater(() -> mChannelList.getSelectionModel().select(channel));
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
    public void setActionListener(IChannelActionListener listener) {
        mActionListener = listener;
    }

    @Override
    public void showManageMembersDialog(Channel channel, List<User> currentMembers, List<User> availableUsers) {
        Platform.runLater(() -> {
            Alert dialog = new Alert(Alert.AlertType.INFORMATION);
            dialog.setTitle("Gérer les membres — " + channel.getName());
            dialog.setHeaderText("Membres du canal privé");

            VBox content = new VBox(8);
            content.setPrefWidth(350);

            Label membersLabel = new Label("Membres actuels :");
            membersLabel.setStyle("-fx-font-weight: bold;");
            VBox membersList = new VBox(4);
            for (User u : currentMembers) {
                HBox row = new HBox(8);
                row.setAlignment(Pos.CENTER_LEFT);
                Label name = new Label(u.getName() + " @" + u.getUserTag());
                if (channel.getCreator() != null && !u.getUuid().equals(channel.getCreator().getUuid())) {
                    Button removeBtn = new Button("Retirer");
                    removeBtn.setStyle("-fx-background-color: #ed4245; -fx-text-fill: white; -fx-font-size: 11px;");
                    removeBtn.setOnAction(e -> {
                        if (mActionListener != null)
                            mActionListener.onRemoveUserFromChannel(channel, u);
                        dialog.close();
                    });
                    row.getChildren().addAll(name, removeBtn);
                } else {
                    Label badge = new Label("(créateur)");
                    badge.setStyle("-fx-text-fill: #57f287; -fx-font-size: 11px;");
                    row.getChildren().addAll(name, badge);
                }
                membersList.getChildren().add(row);
            }

            Label addLabel = new Label("Ajouter un membre :");
            addLabel.setStyle("-fx-font-weight: bold;");
            VBox addList = new VBox(4);
            for (User u : availableUsers) {
                HBox row = new HBox(8);
                row.setAlignment(Pos.CENTER_LEFT);
                Label name = new Label(u.getName() + " @" + u.getUserTag());
                Button addBtn = new Button("Ajouter");
                addBtn.setStyle("-fx-background-color: #57f287; -fx-text-fill: black; -fx-font-size: 11px;");
                addBtn.setOnAction(e -> {
                    if (mActionListener != null)
                        mActionListener.onAddUserToChannel(channel, u);
                    dialog.close();
                });
                row.getChildren().addAll(name, addBtn);
                addList.getChildren().add(row);
            }

            ScrollPane sp = new ScrollPane(new VBox(8, membersLabel, membersList, addLabel, addList));
            sp.setFitToWidth(true);
            sp.setPrefHeight(280);
            content.getChildren().add(sp);
            dialog.getDialogPane().setContent(content);
            dialog.showAndWait();
        });
    }

    @Override
    public void setUnreadChannels(Set<UUID> unreadChannelIds) {
        mUnreadChannelIds = unreadChannelIds;
        Platform.runLater(() -> mChannelList.refresh());
    }

    // ========== Cell ==========

    private class ChannelCell extends ListCell<Channel> {
        @Override
        protected void updateItem(Channel channel, boolean empty) {
            super.updateItem(channel, empty);
            if (empty || channel == null) {
                setText(null);
                setContextMenu(null);
            } else {
                String prefix = channel.isPrivate() ? "\uD83D\uDD12 " : "# ";
                boolean unread = mUnreadChannelIds != null && mUnreadChannelIds.contains(channel.getUuid());
                String badge = unread ? " \u25CF" : "";
                setText(prefix + channel.getName() + badge);
                setStyle("-fx-text-fill: " + (unread ? "#ffffff" : "#dcddde")
                        + "; -fx-background-color: transparent; -fx-font-size: 13px;");

                // Menu contextuel
                ContextMenu ctx = new ContextMenu();
                if (mConnectedUser != null && channel.getCreator() != null
                        && channel.getCreator().getUuid().equals(mConnectedUser.getUuid())) {
                    MenuItem deleteItem = new MenuItem("Supprimer le canal");
                    deleteItem.setOnAction(e -> {
                        if (mActionListener != null)
                            mActionListener.onDeleteChannel(channel);
                    });
                    ctx.getItems().add(deleteItem);
                    if (channel.isPrivate()) {
                        MenuItem manageItem = new MenuItem("Gérer les membres");
                        manageItem.setOnAction(e -> {
                            if (mActionListener != null)
                                mActionListener.onManageMembers(channel);
                        });
                        ctx.getItems().add(manageItem);
                    }
                } else if (channel.isPrivate()) {
                    MenuItem leaveItem = new MenuItem("Quitter le canal");
                    leaveItem.setOnAction(e -> {
                        if (mActionListener != null)
                            mActionListener.onLeaveChannel(channel);
                    });
                    ctx.getItems().add(leaveItem);
                }
                if (!ctx.getItems().isEmpty())
                    setContextMenu(ctx);
            }
        }
    }
}
