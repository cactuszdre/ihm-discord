package main.java.com.ubo.tp.message.ihm.javafx;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldListCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Application JavaFX principale.
 * Équivalent JavaFX de MessageApp (Swing).
 */
public class MessageAppFX extends Application implements ISessionObserver {

    private static DataManager sDataManager;
    private Session mSession;
    private Stage mPrimaryStage;
    private User mConnectedUser;

    // Vues JavaFX
    private ListView<Channel> mChannelListView;
    private VBox mMessagesBox;
    private TextField mMessageInput;
    private Channel mCurrentChannel;

    public static void launch(DataManager dataManager) {
        sDataManager = dataManager;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Application.launch(MessageAppFX.class);
            }
        }).start();
    }

    @Override
    public void start(Stage primaryStage) {
        mPrimaryStage = primaryStage;
        mPrimaryStage.setTitle("MessageApp — JavaFX");

        mSession = new Session();
        mSession.addObserver(this);

        // Initialiser le répertoire d'échange
        initDirectory();

        // Afficher le login
        showLoginScene();

        mPrimaryStage.setOnCloseRequest(event -> {
            if (mConnectedUser != null) {
                mConnectedUser.setOnline(false);
                sDataManager.sendUser(mConnectedUser);
            }
            Platform.exit();
        });

        mPrimaryStage.show();
    }

    // ========== Initialisation ==========

    private void initDirectory() {
        File defaultDir = new File("bdd");
        if (defaultDir.exists() && defaultDir.isDirectory()
                && defaultDir.canRead() && defaultDir.canWrite()) {
            sDataManager.setExchangeDirectory(defaultDir.getAbsolutePath());
        }
    }

    // ========== Scènes ==========

    private void showLoginScene() {
        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #36393f;");

        Label titleLabel = new Label("MessageApp");
        titleLabel.setFont(Font.font("Segoe UI", 28));
        titleLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold;");

        Label subtitleLabel = new Label("Mode JavaFX — Connexion");
        subtitleLabel.setFont(Font.font("Segoe UI", 14));
        subtitleLabel.setStyle("-fx-text-fill: #b9bbbe;");

        Label tagLabel = new Label("TAG UTILISATEUR (@)");
        tagLabel.setStyle("-fx-text-fill: #b9bbbe; -fx-font-size: 11px;");

        TextField tagField = new TextField();
        tagField.setPromptText("Entrez votre tag...");
        tagField.setMaxWidth(300);
        tagField.setStyle(
                "-fx-background-color: #202225; -fx-text-fill: #dcddde; "
                        + "-fx-prompt-text-fill: #72767d; -fx-border-color: #202225; "
                        + "-fx-padding: 10; -fx-font-size: 14px;");

        Label passwordLabel = new Label("MOT DE PASSE");
        passwordLabel.setStyle("-fx-text-fill: #b9bbbe; -fx-font-size: 11px;");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Entrez votre mot de passe...");
        passwordField.setMaxWidth(300);
        passwordField.setStyle(
                "-fx-background-color: #202225; -fx-text-fill: #dcddde; "
                        + "-fx-prompt-text-fill: #72767d; -fx-border-color: #202225; "
                        + "-fx-padding: 10; -fx-font-size: 14px;");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ed4245; -fx-font-size: 12px;");

        Button loginButton = new Button("Se connecter");
        loginButton.setMaxWidth(300);
        loginButton.setStyle(
                "-fx-background-color: #5865f2; -fx-text-fill: white; "
                        + "-fx-font-size: 14px; -fx-font-weight: bold; "
                        + "-fx-padding: 10 20; -fx-cursor: hand;");

        loginButton.setOnAction(event -> {
            String tag = tagField.getText().trim();
            String password = passwordField.getText();

            if (tag.isEmpty()) {
                errorLabel.setText("Veuillez saisir votre tag utilisateur.");
                return;
            }
            if (password.isEmpty()) {
                errorLabel.setText("Veuillez saisir votre mot de passe.");
                return;
            }

            User user = findUserByTag(tag);
            if (user == null) {
                errorLabel.setText("Aucun utilisateur trouvé avec ce tag.");
                return;
            }
            if (!user.getUserPassword().equals(password)) {
                errorLabel.setText("Mot de passe incorrect.");
                return;
            }

            mSession.connect(user);
        });

        root.getChildren().addAll(
                titleLabel, subtitleLabel,
                tagLabel, tagField,
                passwordLabel, passwordField,
                errorLabel, loginButton);

        Scene scene = new Scene(root, 450, 500);
        mPrimaryStage.setScene(scene);
    }

    private void showMainScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #36393f;");

        // === Sidebar gauche : canaux ===
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));
        sidebar.setPrefWidth(220);
        sidebar.setStyle("-fx-background-color: #2f3136;");

        Label channelsTitle = new Label("Canaux");
        channelsTitle.setStyle("-fx-text-fill: #b9bbbe; -fx-font-size: 12px; -fx-font-weight: bold;");

        mChannelListView = new ListView<>();
        mChannelListView.setStyle("-fx-background-color: #2f3136; -fx-control-inner-background: #2f3136;");
        mChannelListView.setCellFactory(param -> {
            TextFieldListCell<Channel> cell = new TextFieldListCell<>(new StringConverter<Channel>() {
                @Override
                public String toString(Channel channel) {
                    return channel == null ? "" : "# " + channel.getName();
                }

                @Override
                public Channel fromString(String string) {
                    return null;
                }
            });
            cell.setStyle("-fx-text-fill: #dcddde; -fx-background-color: transparent;");
            return cell;
        });

        mChannelListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldVal, newVal) -> {
                    if (newVal != null) {
                        mCurrentChannel = newVal;
                        refreshMessages();
                    }
                });

        // Charger les canaux
        ObservableList<Channel> channels = FXCollections.observableArrayList(sDataManager.getChannels());
        mChannelListView.setItems(channels);

        // Bouton déconnexion
        Button logoutBtn = new Button("Déconnexion");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle(
                "-fx-background-color: #ed4245; -fx-text-fill: white; "
                        + "-fx-font-size: 12px; -fx-cursor: hand;");
        logoutBtn.setOnAction(event -> mSession.disconnect());

        Label userLabel = new Label("Connecté : " + mConnectedUser.getName());
        userLabel.setStyle("-fx-text-fill: #57f287; -fx-font-size: 11px;");

        sidebar.getChildren().addAll(channelsTitle, mChannelListView, userLabel, logoutBtn);
        VBox.setVgrow(mChannelListView, Priority.ALWAYS);

        // === Zone centrale : messages ===
        VBox center = new VBox(10);
        center.setPadding(new Insets(10));
        center.setStyle("-fx-background-color: #36393f;");

        mMessagesBox = new VBox(5);
        mMessagesBox.setStyle("-fx-background-color: #36393f;");

        javafx.scene.control.ScrollPane scrollPane = new javafx.scene.control.ScrollPane(mMessagesBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: #36393f; -fx-background-color: #36393f;");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        // Barre d'envoi
        HBox inputBar = new HBox(10);
        inputBar.setAlignment(Pos.CENTER);

        mMessageInput = new TextField();
        mMessageInput.setPromptText("Envoyer un message...");
        mMessageInput.setStyle(
                "-fx-background-color: #40444b; -fx-text-fill: #dcddde; "
                        + "-fx-prompt-text-fill: #72767d; -fx-padding: 10; -fx-font-size: 14px;");
        HBox.setHgrow(mMessageInput, Priority.ALWAYS);

        Button sendBtn = new Button("Envoyer");
        sendBtn.setStyle(
                "-fx-background-color: #5865f2; -fx-text-fill: white; "
                        + "-fx-font-size: 13px; -fx-padding: 10 20; -fx-cursor: hand;");
        sendBtn.setOnAction(event -> sendMessage());
        mMessageInput.setOnAction(event -> sendMessage());

        inputBar.getChildren().addAll(mMessageInput, sendBtn);

        center.getChildren().addAll(scrollPane, inputBar);

        root.setLeft(sidebar);
        root.setCenter(center);

        Scene scene = new Scene(root, 900, 600);
        mPrimaryStage.setScene(scene);
    }

    // ========== Actions ==========

    private void sendMessage() {
        if (mCurrentChannel == null || mConnectedUser == null)
            return;

        String text = mMessageInput.getText().trim();
        if (text.isEmpty())
            return;
        if (text.length() > 200) {
            showAlert("Le message ne peut pas dépasser 200 caractères.");
            return;
        }

        Message message = new Message(mConnectedUser, mCurrentChannel.getUuid(), text);
        sDataManager.sendMessage(message);
        mMessageInput.clear();

        // Rafraîchir après un court délai pour laisser le WatchableDirectory détecter
        // le fichier
        new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                /* ignore */ }
            Platform.runLater(() -> refreshMessages());
        }).start();
    }

    private void refreshMessages() {
        if (mCurrentChannel == null || mMessagesBox == null)
            return;

        mMessagesBox.getChildren().clear();

        Set<Message> allMessages = sDataManager.getMessages();
        List<Message> channelMessages = new ArrayList<>();

        for (Message message : allMessages) {
            if (message.getRecipient().equals(mCurrentChannel.getUuid())) {
                channelMessages.add(message);
            }
        }

        Collections.sort(channelMessages, new Comparator<Message>() {
            @Override
            public int compare(Message m1, Message m2) {
                return Long.compare(m1.getEmissionDate(), m2.getEmissionDate());
            }
        });

        for (Message msg : channelMessages) {
            VBox bubble = new VBox(2);
            bubble.setPadding(new Insets(5, 10, 5, 10));

            Label authorLabel = new Label(msg.getSender().getName());
            authorLabel.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 13px;");

            Label textLabel = new Label(msg.getText());
            textLabel.setWrapText(true);
            textLabel.setStyle("-fx-text-fill: #dcddde; -fx-font-size: 14px;");

            bubble.getChildren().addAll(authorLabel, textLabel);
            mMessagesBox.getChildren().add(bubble);
        }
    }

    // ========== Utilitaires ==========

    private User findUserByTag(String tag) {
        Set<User> users = sDataManager.getUsers();
        for (User user : users) {
            if (user.getUserTag().equalsIgnoreCase(tag)) {
                return user;
            }
        }
        return null;
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // ========== ISessionObserver ==========

    @Override
    public void notifyLogin(User connectedUser) {
        mConnectedUser = connectedUser;
        connectedUser.setOnline(true);
        sDataManager.sendUser(connectedUser);
        System.out.println("[SESSION-FX] Connecté : " + connectedUser.getName());

        Platform.runLater(() -> showMainScene());
    }

    @Override
    public void notifyLogout() {
        if (mConnectedUser != null) {
            mConnectedUser.setOnline(false);
            sDataManager.sendUser(mConnectedUser);
            mConnectedUser = null;
        }
        System.out.println("[SESSION-FX] Déconnexion");

        Platform.runLater(() -> showLoginScene());
    }
}
