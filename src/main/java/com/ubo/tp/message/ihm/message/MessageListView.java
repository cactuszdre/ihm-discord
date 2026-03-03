package main.java.com.ubo.tp.message.ihm.message;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.common.DiscordTextField;
import main.java.com.ubo.tp.message.ihm.common.DiscordTheme;

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

    /**
     * Crée le panel d'un message.
     */
    private JPanel createMessagePanel(final Message message) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DiscordTheme.BACKGROUND_DARK);
        panel.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

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
        String text = message.getText();
        JLabel textLabel = new JLabel(formatMentions(text));
        textLabel.setFont(DiscordTheme.FONT_NORMAL);
        textLabel.setForeground(DiscordTheme.TEXT_NORMAL);
        textLabel.setBorder(BorderFactory.createEmptyBorder(2, 6, 0, 0));
        contentPanel.add(textLabel);

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
}
