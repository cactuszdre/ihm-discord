package main.java.com.ubo.tp.message.ihm.javafx;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.animation.RotateTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.common.EmojiList;
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
    private ScrollPane mScrollPane;

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

        mScrollPane = new ScrollPane(mMessagesBox);
        mScrollPane.setFitToWidth(true);
        mScrollPane.setStyle("-fx-background: #36393f; -fx-background-color: #36393f;");
        VBox.setVgrow(mScrollPane, Priority.ALWAYS);

        getChildren().addAll(mChannelNameLabel, mSearchField, mScrollPane);
    }

    // ========== IMessageView ==========

    @Override
    public void setMessages(List<Message> messages) {
        Platform.runLater(() -> {
            mMessagesBox.getChildren().clear();
            for (Message msg : messages) {
                VBox msgBox = new VBox(2);
                msgBox.setPadding(new Insets(4, 10, 4, 10));

                HBox row = new HBox(8);
                row.setAlignment(Pos.CENTER_LEFT);

                VBox bubble = new VBox(2);
                HBox.setHgrow(bubble, Priority.ALWAYS);

                Label author = new Label(msg.getSender().getName());
                author.setStyle("-fx-text-fill: #ffffff; -fx-font-weight: bold; -fx-font-size: 13px;");

                String displayText = EmojiList.replaceEmojis(msg.getText());
                Label text = new Label(displayText);
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

                // Reactions bar
                HBox reactionsBar = createReactionsBar(msg);

                msgBox.getChildren().addAll(row, reactionsBar);
                mMessagesBox.getChildren().add(msgBox);
            }
        });
    }

    /**
     * Crée la barre de réactions pour un message.
     */
    private HBox createReactionsBar(final Message msg) {
        HBox bar = new HBox(4);
        bar.setPadding(new Insets(2, 0, 2, 8));
        bar.setAlignment(Pos.CENTER_LEFT);

        Map<String, Set<UUID>> reactions = msg.getReactions();
        if (reactions != null) {
            for (Map.Entry<String, Set<UUID>> entry : reactions.entrySet()) {
                String emoji = entry.getKey();
                int count = entry.getValue().size();
                boolean userReacted = mCurrentUser != null && entry.getValue().contains(mCurrentUser.getUuid());

                Label reactionLabel = new Label(emoji + " " + count);
                reactionLabel.setStyle(
                        "-fx-font-size: 12px; -fx-padding: 2 6; -fx-cursor: hand; "
                                + "-fx-border-color: " + (userReacted ? "#5865f2" : "#40444b") + "; "
                                + "-fx-border-radius: 4; -fx-background-radius: 4; "
                                + "-fx-background-color: "
                                + (userReacted ? "rgba(88,101,242,0.15)" : "#2f3136") + "; "
                                + "-fx-text-fill: " + (userReacted ? "#5865f2" : "#b9bbbe") + ";");
                reactionLabel.setOnMouseClicked(e -> {
                    if (mActionListener != null) {
                        mActionListener.onAddReaction(msg, emoji);
                    }
                });
                bar.getChildren().add(reactionLabel);
            }
        }

        // Bouton "+" pour ajouter
        Label addBtn = new Label("☺+");
        addBtn.setStyle("-fx-font-size: 12px; -fx-padding: 2 6; -fx-cursor: hand; "
                + "-fx-border-color: #40444b; -fx-border-radius: 4; -fx-background-radius: 4; "
                + "-fx-background-color: #2f3136; -fx-text-fill: #72767d;");
        addBtn.setOnMouseClicked(e -> {
            ContextMenu popup = new ContextMenu();
            for (String emoji : EmojiList.REACTION_EMOJIS) {
                MenuItem item = new MenuItem(emoji);
                item.setStyle("-fx-font-size: 16px;");
                item.setOnAction(ev -> {
                    if (mActionListener != null) {
                        mActionListener.onAddReaction(msg, emoji);
                    }
                });
                popup.getItems().add(item);
            }
            popup.show(addBtn, javafx.geometry.Side.BOTTOM, 0, 0);
        });
        bar.getChildren().add(addBtn);

        return bar;
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

    @Override
    public void triggerEasterEgg(String command) {
        Platform.runLater(() -> {
            if ("/party".equals(command)) {
                triggerConfetti();
            } else if ("/flip".equals(command)) {
                triggerFlip();
            } else if ("/earthquake".equals(command)) {
                triggerEarthquake();
            }
        });
    }

    // ========== Easter Eggs ==========

    /**
     * /party — Confettis animés.
     */
    private void triggerConfetti() {
        Stage stage = (Stage) getScene().getWindow();
        if (stage == null) return;

        StackPane root = findRootStack();
        if (root == null) return;

        Canvas canvas = new Canvas(stage.getWidth(), stage.getHeight());
        canvas.setMouseTransparent(true);
        root.getChildren().add(canvas);

        final Random random = new Random();
        final int count = 120;
        final double[][] particles = new double[count][5]; // x, y, size, speed, colorIdx
        final Color[] colors = {
                Color.RED, Color.YELLOW, Color.LIME, Color.CYAN,
                Color.MAGENTA, Color.ORANGE, Color.PINK, Color.web("#5865f2")
        };

        for (int i = 0; i < count; i++) {
            particles[i][0] = random.nextDouble() * canvas.getWidth();
            particles[i][1] = -random.nextDouble() * canvas.getHeight() / 2;
            particles[i][2] = 4 + random.nextInt(8);
            particles[i][3] = 2 + random.nextDouble() * 4;
            particles[i][4] = random.nextInt(colors.length);
        }

        final long[] startTime = { System.nanoTime() };
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double elapsed = (now - startTime[0]) / 1_000_000_000.0;
                if (elapsed > 3.0) {
                    stop();
                    root.getChildren().remove(canvas);
                    return;
                }

                GraphicsContext gc = canvas.getGraphicsContext2D();
                gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

                for (double[] p : particles) {
                    p[1] += p[3];
                    p[0] += (random.nextDouble() - 0.5) * 2;
                    gc.setFill(colors[(int) p[4]]);
                    gc.fillRect(p[0], p[1], p[2], p[2]);
                }
            }
        };
        timer.start();
    }

    /**
     * /flip — Rotation 180° puis retour.
     */
    private void triggerFlip() {
        Stage stage = (Stage) getScene().getWindow();
        if (stage == null) return;

        RotateTransition rotate = new RotateTransition(Duration.millis(600), stage.getScene().getRoot());
        rotate.setFromAngle(0);
        rotate.setToAngle(180);
        rotate.setAxis(Rotate.X_AXIS);
        rotate.setOnFinished(e -> {
            PauseTransition pause = new PauseTransition(Duration.millis(800));
            pause.setOnFinished(e2 -> {
                RotateTransition back = new RotateTransition(Duration.millis(600), stage.getScene().getRoot());
                back.setFromAngle(180);
                back.setToAngle(360);
                back.setAxis(Rotate.X_AXIS);
                back.setOnFinished(e3 -> stage.getScene().getRoot().setRotate(0));
                back.play();
            });
            pause.play();
        });
        rotate.play();
    }

    /**
     * /earthquake — Fenêtre qui tremble 2 secondes.
     */
    private void triggerEarthquake() {
        Stage stage = (Stage) getScene().getWindow();
        if (stage == null) return;

        double origX = stage.getX();
        double origY = stage.getY();
        Random random = new Random();

        final long[] startTime = { System.nanoTime() };
        AnimationTimer shakeTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double elapsed = (now - startTime[0]) / 1_000_000_000.0;
                if (elapsed > 2.0) {
                    stop();
                    stage.setX(origX);
                    stage.setY(origY);
                    return;
                }
                int dx = random.nextInt(16) - 8;
                int dy = random.nextInt(16) - 8;
                stage.setX(origX + dx);
                stage.setY(origY + dy);
            }
        };
        shakeTimer.start();
    }

    /**
     * Cherche le StackPane racine pour l'overlay confetti.
     */
    private StackPane findRootStack() {
        javafx.scene.Parent root = getScene().getRoot();
        if (root instanceof StackPane) {
            return (StackPane) root;
        }
        // Wrap in StackPane if needed
        return null;
    }
}
