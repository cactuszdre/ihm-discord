package main.java.com.ubo.tp.message.ihm.javafx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.io.File;
import java.util.UUID;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.channel.ChannelController;
import main.java.com.ubo.tp.message.ihm.message.MessageController;
import main.java.com.ubo.tp.message.ihm.notification.NotificationManager;
import main.java.com.ubo.tp.message.ihm.user.UserController;

/**
 * Application JavaFX — Shell mince qui réutilise les Controllers Swing
 * existants.
 *
 * Architecture :
 * - Login/Inscription : logique locale (AccountController couplé à Swing)
 * - Canaux : ChannelController + ChannelListViewFX (IChannelView)
 * - Messages: MessageController + MessageListViewFX (IMessageView) +
 * MessageInputViewFX
 * - Users : UserController + UserListViewFX (IUserView)
 */
public class MessageAppFX extends Application implements ISessionObserver {

    /** DataManager partagé (initialisé avant launch()). */
    private static DataManager sDataManager;

    private Stage mPrimaryStage;
    private Session mSession;
    private User mConnectedUser;
    private User mLastConnectedUser;

    // Controllers (ceux du Swing, réutilisés)
    private ChannelController mChannelController;
    private MessageController mMessageController;
    private UserController mUserController;
    private NotificationManager mNotificationManager;

    // Vues FX
    private ChannelListViewFX mChannelListView;
    private MessageListViewFX mMessageListView;
    private MessageInputViewFX mMessageInputView;
    private UserListViewFX mUserListView;

    // Toast container
    private StackPane mToastContainer;

    /** Appelé par MessageAppLauncher avant launch(). */
    public static void setDataManager(DataManager dm) {
        sDataManager = dm;
    }

    @Override
    public void start(Stage primaryStage) {
        mPrimaryStage = primaryStage;
        mPrimaryStage.setTitle("MessageApp — JavaFX");

        mSession = new Session();
        mSession.addObserver(this);

        initDirectory();
        showLoginScene();
        mPrimaryStage.show();

        // Shutdown hook
        mPrimaryStage.setOnCloseRequest(e -> {
            if (mLastConnectedUser != null) {
                mLastConnectedUser.setOnline(false);
                sDataManager.sendUser(mLastConnectedUser);
            }
            Platform.exit();
        });
    }

    private void initDirectory() {
        File defaultDir = new File("bdd");
        if (defaultDir.exists() && defaultDir.isDirectory()
                && defaultDir.canRead() && defaultDir.canWrite()) {
            sDataManager.setExchangeDirectory(defaultDir.getAbsolutePath());
        }
    }

    // ========== LOGIN ==========

    private void showLoginScene() {
        VBox root = new VBox(12);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #36393f;");

        Label title = FXHelper.styledLabel("MessageApp", 28, true, "#ffffff");
        Label subtitle = FXHelper.styledLabel("Heureux de vous revoir !", 14, false, "#b9bbbe");

        Label tagLabel = FXHelper.styledLabel("TAG UTILISATEUR (@)", 11, false, "#b9bbbe");
        TextField tagField = FXHelper.styledTextField("Entrez votre tag...");

        Label passwordLabel = FXHelper.styledLabel("MOT DE PASSE", 11, false, "#b9bbbe");
        PasswordField passwordField = FXHelper.styledPasswordField("Mot de passe...");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ed4245; -fx-font-size: 12px;");

        Button loginBtn = FXHelper.styledButton("Se connecter", "#5865f2");
        loginBtn.setMaxWidth(300);
        loginBtn.setOnAction(e -> {
            String tag = tagField.getText().trim();
            String password = passwordField.getText();
            if (tag.isEmpty()) {
                errorLabel.setText("Veuillez saisir votre tag.");
                return;
            }
            if (password.isEmpty()) {
                errorLabel.setText("Veuillez saisir votre mot de passe.");
                return;
            }
            User user = findUserByTag(tag);
            if (user == null) {
                errorLabel.setText("Aucun utilisateur trouvé.");
                return;
            }
            if (!user.getUserPassword().equals(password)) {
                errorLabel.setText("Mot de passe incorrect.");
                return;
            }
            mSession.connect(user);
        });

        Hyperlink registerLink = new Hyperlink("Besoin d'un compte ? S'inscrire");
        registerLink.setStyle("-fx-text-fill: #00aff4; -fx-font-size: 12px;");
        registerLink.setOnAction(e -> showRegistrationScene());

        root.getChildren().addAll(title, subtitle, tagLabel, tagField,
                passwordLabel, passwordField, errorLabel, loginBtn, registerLink);
        mPrimaryStage.setScene(new Scene(root, 450, 520));
    }

    // ========== INSCRIPTION ==========

    private void showRegistrationScene() {
        VBox root = new VBox(12);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        root.setStyle("-fx-background-color: #36393f;");

        Label title = FXHelper.styledLabel("Créer un compte", 28, true, "#ffffff");
        Label subtitle = FXHelper.styledLabel("Rejoignez MessageApp", 14, false, "#b9bbbe");

        Label nameLabel = FXHelper.styledLabel("NOM D'UTILISATEUR", 11, false, "#b9bbbe");
        TextField nameField = FXHelper.styledTextField("Votre nom...");

        Label tagLabel = FXHelper.styledLabel("TAG UTILISATEUR (@)", 11, false, "#b9bbbe");
        TextField tagField = FXHelper.styledTextField("Votre tag unique...");

        Label passLabel = FXHelper.styledLabel("MOT DE PASSE", 11, false, "#b9bbbe");
        PasswordField passField = FXHelper.styledPasswordField("Choisissez un mot de passe...");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: #ed4245; -fx-font-size: 12px;");

        Button registerBtn = FXHelper.styledButton("S'inscrire", "#57f287");
        registerBtn.setMaxWidth(300);
        registerBtn.setStyle(registerBtn.getStyle() + "-fx-text-fill: #000000;");
        registerBtn.setOnAction(e -> {
            String name = nameField.getText().trim();
            String tag = tagField.getText().trim();
            String pwd = passField.getText();
            if (name.isEmpty()) {
                errorLabel.setText("Le nom est obligatoire.");
                return;
            }
            if (tag.isEmpty()) {
                errorLabel.setText("Le tag est obligatoire.");
                return;
            }
            if (findUserByTag(tag) != null) {
                errorLabel.setText("Ce tag est déjà utilisé.");
                return;
            }
            sDataManager.sendUser(new User(UUID.randomUUID(), tag, pwd, name));
            showLoginScene();
        });

        Hyperlink loginLink = new Hyperlink("Déjà un compte ? Se connecter");
        loginLink.setStyle("-fx-text-fill: #00aff4; -fx-font-size: 12px;");
        loginLink.setOnAction(e -> showLoginScene());

        root.getChildren().addAll(title, subtitle, nameLabel, nameField,
                tagLabel, tagField, passLabel, passField,
                errorLabel, registerBtn, loginLink);
        mPrimaryStage.setScene(new Scene(root, 450, 580));
    }

    // ========== MAIN SCENE ==========

    private void showMainScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #36393f;");

        // Toast
        mToastContainer = new StackPane();
        mToastContainer.setPickOnBounds(false);
        mToastContainer.setAlignment(Pos.BOTTOM_RIGHT);
        mToastContainer.setPadding(new Insets(0, 20, 20, 0));

        // Menu bar
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: #202225;");
        Menu fichier = new Menu("Fichier");
        MenuItem logout = new MenuItem("Se déconnecter");
        logout.setOnAction(e -> mSession.disconnect());
        fichier.getItems().add(logout);
        Menu help = new Menu("?");
        MenuItem about = new MenuItem("À propos");
        about.setOnAction(e -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle("À propos");
            a.setHeaderText("MessageApp — JavaFX");
            a.setContentText("UBO M1 IHM — Simulation de messagerie.\nVersion JavaFX");
            a.showAndWait();
        });
        help.getItems().add(about);
        menuBar.getMenus().addAll(fichier, help);
        root.setTop(menuBar);

        // === Vues FX ===
        mChannelListView = new ChannelListViewFX();
        mChannelListView.setConnectedUser(mConnectedUser);

        mMessageListView = new MessageListViewFX();
        mMessageInputView = new MessageInputViewFX();

        mUserListView = new UserListViewFX();

        // Centre = messages + input
        VBox center = new VBox();
        center.setStyle("-fx-background-color: #36393f;");
        javafx.scene.layout.VBox.setVgrow(mMessageListView, javafx.scene.layout.Priority.ALWAYS);
        center.getChildren().addAll(mMessageListView, mMessageInputView);

        // Info utilisateur en bas du sidebar
        Label userLabel = FXHelper.styledLabel("● " + mConnectedUser.getName(), 12, false, "#57f287");
        Button logoutBtn = FXHelper.styledButton("Déconnexion", "#ed4245");
        logoutBtn.setMaxWidth(Double.MAX_VALUE);
        logoutBtn.setStyle(logoutBtn.getStyle() + "-fx-font-size: 11px;");
        logoutBtn.setOnAction(e -> mSession.disconnect());
        mChannelListView.getChildren().addAll(userLabel, logoutBtn);

        root.setLeft(mChannelListView);
        root.setCenter(center);
        root.setRight(mUserListView);

        StackPane mainStack = new StackPane(root, mToastContainer);
        mPrimaryStage.setScene(new Scene(mainStack, 1000, 650));

        // === Câblage MVC : réutilisation des controllers existants ===
        initMainControllers();
    }

    /**
     * Câble les controllers existants avec les nouvelles vues JavaFX.
     * Même logique que MessageApp.initMainControllers() du Swing.
     */
    private void initMainControllers() {
        // Canaux
        mChannelController = new ChannelController(sDataManager, mSession, mChannelListView);

        // Messages
        mMessageController = new MessageController(
                sDataManager, mSession, mMessageListView, mMessageInputView);

        // Utilisateurs
        mUserController = new UserController(sDataManager, mSession, mUserListView);
        mUserListView.setCurrentUser(mConnectedUser);

        // Canal sélectionné → afficher messages
        mChannelController.setChannelSelectionListener(channel -> mMessageController.setCurrentChannel(channel));

        // DM → sélectionner canal
        mUserController.setDirectMessageListener(dmChannel -> mChannelController.onChannelSelected(dmChannel));

        // Suppression compte → déconnexion
        mUserController.setAccountDeletionListener(() -> {
            mLastConnectedUser = null;
            mSession.disconnect();
        });

        // Notifications
        mNotificationManager = new NotificationManager(sDataManager, mSession);
    }

    // ========== Toast ==========

    private void showToast(String message) {
        if (mToastContainer == null)
            return;
        Platform.runLater(() -> {
            Label toast = new Label(message);
            toast.setStyle("-fx-background-color: #202225; -fx-text-fill: #ffffff; "
                    + "-fx-padding: 12 20; -fx-background-radius: 8; -fx-font-size: 13px;");
            toast.setMaxWidth(350);
            toast.setWrapText(true);
            mToastContainer.getChildren().add(toast);

            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), toast);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();

            PauseTransition pause = new PauseTransition(Duration.seconds(3));
            pause.setOnFinished(e -> {
                FadeTransition fadeOut = new FadeTransition(Duration.millis(300), toast);
                fadeOut.setFromValue(1);
                fadeOut.setToValue(0);
                fadeOut.setOnFinished(ev -> mToastContainer.getChildren().remove(toast));
                fadeOut.play();
            });
            pause.play();
        });
    }

    // ========== Utilitaires ==========

    private User findUserByTag(String tag) {
        for (User u : sDataManager.getUsers()) {
            if (u.getUserTag().equalsIgnoreCase(tag))
                return u;
        }
        return null;
    }

    // ========== ISessionObserver ==========

    @Override
    public void notifyLogin(User connectedUser) {
        mConnectedUser = connectedUser;
        mLastConnectedUser = connectedUser;
        connectedUser.setOnline(true);
        sDataManager.sendUser(connectedUser);
        System.out.println("[SESSION-FX] Connecté : " + connectedUser.getName());
        Platform.runLater(() -> showMainScene());
    }

    @Override
    public void notifyLogout() {
        System.out.println("[SESSION-FX] Déconnexion");

        if (mLastConnectedUser != null) {
            mLastConnectedUser.setOnline(false);
            sDataManager.sendUser(mLastConnectedUser);
            mLastConnectedUser = null;
        }

        // Dispose controllers
        if (mChannelController != null)
            mChannelController.dispose();
        if (mMessageController != null)
            mMessageController.dispose();
        if (mUserController != null)
            mUserController.dispose();
        if (mNotificationManager != null)
            mNotificationManager.dispose();

        mChannelController = null;
        mMessageController = null;
        mUserController = null;
        mNotificationManager = null;
        mConnectedUser = null;

        Platform.runLater(() -> showLoginScene());
    }
}
