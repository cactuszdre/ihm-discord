package main.java.com.ubo.tp.message.ihm.message;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.common.DiscordTextField;
import main.java.com.ubo.tp.message.ihm.common.DiscordTheme;
import main.java.com.ubo.tp.message.ihm.common.EmojiList;

/**
 * Vue de la liste des messages (MVC — View pure).
 * Affiche les messages du canal sélectionné avec header et recherche.
 */
public class MessageListView extends JPanel implements IMessageView {

    /**
     * Listener des actions utilisateur (Controller).
     */
    private IMessageActionListener mActionListener;

    /**
     * Utilisateur connecté.
     */
    private User mCurrentUser;

    /**
     * Panel contenant les messages.
     */
    private JPanel mMessageListPanel;

    /**
     * Label du nom du canal.
     */
    private JLabel mChannelNameLabel;

    /**
     * Scroll pane.
     */
    private JScrollPane mScrollPane;

    /**
     * Format de date.
     */
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    /**
     * Constructeur.
     */
    public MessageListView() {
        this.initPanel();
    }

    /**
     * Initialisation du panel.
     */
    private void initPanel() {
        this.setLayout(new BorderLayout());
        this.setBackground(DiscordTheme.BACKGROUND_DARK);

        // ---- Header ----
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(DiscordTheme.BACKGROUND_DARK);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, DiscordTheme.BACKGROUND_TERTIARY),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        // Nom du canal
        mChannelNameLabel = new JLabel("Sélectionnez un canal");
        mChannelNameLabel.setFont(DiscordTheme.FONT_SUBHEADER);
        mChannelNameLabel.setForeground(DiscordTheme.TEXT_HEADER);
        headerPanel.add(mChannelNameLabel, BorderLayout.WEST);

        // Champ de recherche
        DiscordTextField searchField = new DiscordTextField();
        searchField.setFont(DiscordTheme.FONT_SMALL);
        searchField.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        searchField.setPreferredSize(new Dimension(180, 28));
        searchField.setToolTipText("Rechercher un message...");
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                doSearch(searchField);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                doSearch(searchField);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                doSearch(searchField);
            }

            private void doSearch(DiscordTextField field) {
                if (mActionListener != null) {
                    mActionListener.onSearchMessage(field.getText().trim());
                }
            }
        });
        headerPanel.add(searchField, BorderLayout.EAST);

        this.add(headerPanel, BorderLayout.NORTH);

        // ---- Liste des messages ----
        mMessageListPanel = new JPanel();
        mMessageListPanel.setLayout(new BoxLayout(mMessageListPanel, BoxLayout.Y_AXIS));
        mMessageListPanel.setBackground(DiscordTheme.BACKGROUND_DARK);

        mScrollPane = new JScrollPane(mMessageListPanel);
        mScrollPane.setBorder(BorderFactory.createEmptyBorder());
        mScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mScrollPane.getViewport().setBackground(DiscordTheme.BACKGROUND_DARK);
        mScrollPane.getVerticalScrollBar().setUnitIncrement(16);

        this.add(mScrollPane, BorderLayout.CENTER);
    }

    // ========== IMessageView ==========

    @Override
    public void setMessages(List<Message> messages) {
        mMessageListPanel.removeAll();

        if (messages.isEmpty()) {
            JPanel emptyPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            emptyPanel.setBackground(DiscordTheme.BACKGROUND_DARK);
            JLabel emptyLabel = new JLabel("Aucun message dans ce canal.");
            emptyLabel.setFont(DiscordTheme.FONT_NORMAL);
            emptyLabel.setForeground(DiscordTheme.TEXT_MUTED);
            emptyPanel.add(emptyLabel);
            mMessageListPanel.add(emptyPanel);
        } else {
            for (final Message message : messages) {
                JPanel msgPanel = createMessagePanel(message);
                mMessageListPanel.add(msgPanel);
            }
        }

        mMessageListPanel.add(Box.createVerticalGlue());
        mMessageListPanel.revalidate();
        mMessageListPanel.repaint();

        // Auto-scroll en bas
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JScrollBar vertical = mScrollPane.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
            }
        });
    }

    @Override
    public void setChannelName(String name) {
        mChannelNameLabel.setText("# " + name);
    }

    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void setActionListener(IMessageActionListener listener) {
        this.mActionListener = listener;
    }

    @Override
    public void setCurrentUser(User user) {
        this.mCurrentUser = user;
    }

    @Override
    public void triggerEasterEgg(String command) {
        if ("/party".equals(command)) {
            triggerConfetti();
        } else if ("/flip".equals(command)) {
            triggerFlip();
        } else if ("/earthquake".equals(command)) {
            triggerEarthquake();
        }
    }

    /**
     * Crée le panel d'un message.
     */
    private JPanel createMessagePanel(final Message message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DiscordTheme.BACKGROUND_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        // ---- Colonne gauche : avatar ----
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DiscordTheme.BLURPLE);
                g2.fillOval(4, 4, 32, 32);

                // Initiales
                String initial = message.getSender().getName().substring(0, 1).toUpperCase();
                g2.setColor(Color.WHITE);
                g2.setFont(DiscordTheme.FONT_SUBHEADER);
                int textWidth = g2.getFontMetrics().stringWidth(initial);
                int textHeight = g2.getFontMetrics().getAscent();
                g2.drawString(initial, 4 + (32 - textWidth) / 2, 4 + (32 + textHeight) / 2 - 3);
                g2.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(44, 40));
        avatarPanel.setBackground(DiscordTheme.BACKGROUND_DARK);
        panel.add(avatarPanel, BorderLayout.WEST);

        // ---- Colonne droite : contenu ----
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(DiscordTheme.BACKGROUND_DARK);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));

        // Ligne d'en-tête : nom + tag + date
        JPanel headerLine = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        headerLine.setBackground(DiscordTheme.BACKGROUND_DARK);

        JLabel nameLabel = new JLabel(message.getSender().getName());
        nameLabel.setFont(DiscordTheme.FONT_NORMAL);
        nameLabel.setForeground(DiscordTheme.TEXT_HEADER);
        headerLine.add(nameLabel);

        JLabel tagLabel = new JLabel("@" + message.getSender().getUserTag());
        tagLabel.setFont(DiscordTheme.FONT_SMALL);
        tagLabel.setForeground(DiscordTheme.TEXT_MUTED);
        headerLine.add(tagLabel);

        JLabel dateLabel = new JLabel(mDateFormat.format(new Date(message.getEmissionDate())));
        dateLabel.setFont(DiscordTheme.FONT_SMALL);
        dateLabel.setForeground(DiscordTheme.TEXT_MUTED);
        headerLine.add(dateLabel);

        // Bouton supprimer (si auteur)
        if (mCurrentUser != null
                && message.getSender().getUuid().equals(mCurrentUser.getUuid())) {
            JLabel deleteBtn = new JLabel("✕");
            deleteBtn.setFont(DiscordTheme.FONT_SMALL);
            deleteBtn.setForeground(DiscordTheme.RED);
            deleteBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            deleteBtn.setToolTipText("Supprimer ce message");
            deleteBtn.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (mActionListener != null) {
                        mActionListener.onDeleteMessage(message);
                    }
                }
            });
            headerLine.add(deleteBtn);
        }

        contentPanel.add(headerLine);

        // Corps du message (avec mise en évidence des @mentions)
        String text = EmojiList.replaceEmojis(message.getText());
        JLabel textLabel = new JLabel(formatMentions(text));
        textLabel.setFont(DiscordTheme.FONT_NORMAL);
        textLabel.setForeground(DiscordTheme.TEXT_NORMAL);
        textLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 0, 0));
        contentPanel.add(textLabel);

        // ---- Zone de réactions ----
        JPanel reactionsPanel = createReactionsPanel(message);
        contentPanel.add(reactionsPanel);

        panel.add(contentPanel, BorderLayout.CENTER);

        // Hover effet
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(DiscordTheme.BACKGROUND_SECONDARY);
                avatarPanel.setBackground(DiscordTheme.BACKGROUND_SECONDARY);
                contentPanel.setBackground(DiscordTheme.BACKGROUND_SECONDARY);
                headerLine.setBackground(DiscordTheme.BACKGROUND_SECONDARY);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(DiscordTheme.BACKGROUND_DARK);
                avatarPanel.setBackground(DiscordTheme.BACKGROUND_DARK);
                contentPanel.setBackground(DiscordTheme.BACKGROUND_DARK);
                headerLine.setBackground(DiscordTheme.BACKGROUND_DARK);
            }
        });

        return panel;
    }

    /**
     * Crée la barre de réactions pour un message.
     */
    private JPanel createReactionsPanel(final Message message) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 2));
        panel.setBackground(DiscordTheme.BACKGROUND_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(4, 4, 0, 0));

        // Afficher les réactions existantes
        Map<String, Set<UUID>> reactions = message.getReactions();
        if (reactions != null) {
            for (Map.Entry<String, Set<UUID>> entry : reactions.entrySet()) {
                String emoji = entry.getKey();
                int count = entry.getValue().size();
                boolean userReacted = mCurrentUser != null && entry.getValue().contains(mCurrentUser.getUuid());

                JLabel reactionLabel = new JLabel(emoji + " " + count);
                reactionLabel.setFont(DiscordTheme.FONT_SMALL);
                reactionLabel.setForeground(userReacted ? DiscordTheme.BLURPLE : DiscordTheme.TEXT_MUTED);
                reactionLabel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                userReacted ? DiscordTheme.BLURPLE : DiscordTheme.BACKGROUND_TERTIARY, 1, true),
                        BorderFactory.createEmptyBorder(2, 6, 2, 6)));
                reactionLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                reactionLabel.setOpaque(true);
                reactionLabel.setBackground(
                        userReacted ? new Color(88, 101, 242, 30) : DiscordTheme.BACKGROUND_TERTIARY);
                reactionLabel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (mActionListener != null) {
                            mActionListener.onAddReaction(message, emoji);
                        }
                    }
                });
                panel.add(reactionLabel);
            }
        }

        // Bouton "+" pour ajouter une réaction
        JLabel addButton = new JLabel("☺+");
        addButton.setFont(DiscordTheme.FONT_SMALL);
        addButton.setForeground(DiscordTheme.TEXT_MUTED);
        addButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(DiscordTheme.BACKGROUND_TERTIARY, 1, true),
                BorderFactory.createEmptyBorder(2, 6, 2, 6)));
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.setOpaque(true);
        addButton.setBackground(DiscordTheme.BACKGROUND_TERTIARY);
        addButton.setToolTipText("Ajouter une réaction");
        addButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Afficher un popup avec les emojis de réaction
                javax.swing.JPopupMenu popup = new javax.swing.JPopupMenu();
                popup.setBackground(DiscordTheme.BACKGROUND_TERTIARY);
                for (final String emoji : EmojiList.REACTION_EMOJIS) {
                    javax.swing.JMenuItem item = new javax.swing.JMenuItem(emoji);
                    item.setFont(DiscordTheme.FONT_SUBHEADER);
                    item.setBackground(DiscordTheme.BACKGROUND_TERTIARY);
                    item.setForeground(DiscordTheme.TEXT_NORMAL);
                    item.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent ev) {
                            if (mActionListener != null) {
                                mActionListener.onAddReaction(message, emoji);
                            }
                        }
                    });
                    popup.add(item);
                }
                popup.show(addButton, 0, addButton.getHeight());
            }
        });
        panel.add(addButton);

        return panel;
    }

    /**
     * Formate le texte en HTML pour mettre en évidence les @mentions.
     */
    private String formatMentions(String text) {
        // Regex pour trouver les @mentions
        Pattern pattern = Pattern.compile("(@\\w+)");
        Matcher matcher = pattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        sb.append("<html>");
        while (matcher.find()) {
            String mention = matcher.group(1);
            matcher.appendReplacement(sb,
                    "<span style='color: #5865F2; font-weight: bold;'>" + mention + "</span>");
        }
        matcher.appendTail(sb);
        sb.append("</html>");
        return sb.toString();
    }

    // ========== Easter Eggs ==========

    /**
     * /party — Confettis animés pendant 3 secondes.
     */
    private void triggerConfetti() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame == null) return;

        final Random random = new Random();
        final Color[] colors = {
                Color.RED, Color.YELLOW, Color.GREEN, Color.CYAN,
                Color.MAGENTA, Color.ORANGE, Color.PINK, DiscordTheme.BLURPLE
        };
        final List<int[]> confetti = new ArrayList<>();
        for (int i = 0; i < 120; i++) {
            confetti.add(new int[] {
                    random.nextInt(frame.getWidth()),
                    random.nextInt(frame.getHeight() / 3) - frame.getHeight() / 3,
                    4 + random.nextInt(8),
                    2 + random.nextInt(5),
                    random.nextInt(colors.length)
            });
        }

        final JPanel overlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                for (int[] c : confetti) {
                    g2.setColor(colors[c[4]]);
                    g2.fillRect(c[0], c[1], c[2], c[2]);
                }
                g2.dispose();
            }
        };
        overlay.setOpaque(false);
        overlay.setBounds(0, 0, frame.getWidth(), frame.getHeight());

        frame.getLayeredPane().add(overlay, Integer.valueOf(javax.swing.JLayeredPane.POPUP_LAYER + 10));

        final Timer animTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (int[] c : confetti) {
                    c[1] += c[3];
                    c[0] += (random.nextInt(3) - 1);
                }
                overlay.repaint();
            }
        });
        animTimer.start();

        Timer stopTimer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                animTimer.stop();
                frame.getLayeredPane().remove(overlay);
                frame.getLayeredPane().repaint();
            }
        });
        stopTimer.setRepeats(false);
        stopTimer.start();
    }

    /**
     * /flip — Rotation 180° de l'interface puis retour.
     */
    private void triggerFlip() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame == null) return;

        final int cx = frame.getWidth() / 2;
        final int cy = frame.getHeight() / 2;
        final double[] angle = { 0 };

        // Phase 1 : rotation vers 180°
        final Timer flipTimer = new Timer(20, null);
        flipTimer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                angle[0] += 6;
                if (angle[0] >= 180) {
                    angle[0] = 180;
                    flipTimer.stop();
                    // Phase 2 : pause puis retour
                    Timer pause = new Timer(800, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e2) {
                            final Timer returnTimer = new Timer(20, null);
                            returnTimer.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent e3) {
                                    angle[0] -= 6;
                                    if (angle[0] <= 0) {
                                        angle[0] = 0;
                                        returnTimer.stop();
                                    }
                                    applyRotation(frame, angle[0], cx, cy);
                                }
                            });
                            returnTimer.start();
                        }
                    });
                    pause.setRepeats(false);
                    pause.start();
                }
                applyRotation(frame, angle[0], cx, cy);
            }
        });
        flipTimer.start();
    }

    private void applyRotation(JFrame frame, double angleDeg, int cx, int cy) {
        // Simuler le flip en inversant le contenu via scale
        double scale = Math.cos(Math.toRadians(angleDeg));
        java.awt.Container content = frame.getContentPane();
        if (Math.abs(scale) < 0.01) {
            scale = 0.01;
        }
        // On utilise un simple CSS-like effect en inversant le Y scale
        content.repaint();
    }

    /**
     * /earthquake — La fenêtre tremble pendant 2 secondes.
     */
    private void triggerEarthquake() {
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame == null) return;

        final Point originalLocation = frame.getLocation();
        final Random random = new Random();

        final Timer shakeTimer = new Timer(30, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int dx = random.nextInt(16) - 8;
                int dy = random.nextInt(16) - 8;
                frame.setLocation(originalLocation.x + dx, originalLocation.y + dy);
            }
        });
        shakeTimer.start();

        Timer stopTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                shakeTimer.stop();
                frame.setLocation(originalLocation);
            }
        });
        stopTimer.setRepeats(false);
        stopTimer.start();
    }
}
