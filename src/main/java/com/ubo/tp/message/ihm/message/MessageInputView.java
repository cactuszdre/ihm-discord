package main.java.com.ubo.tp.message.ihm.message;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.common.DiscordButton;
import main.java.com.ubo.tp.message.ihm.common.DiscordTextField;
import main.java.com.ubo.tp.message.ihm.common.DiscordTheme;
import main.java.com.ubo.tp.message.ihm.common.EmojiList;

/**
 * Vue de saisie de message (MVC — View pure).
 * Barre de saisie avec champ texte, bouton envoi, compteur de caractères,
 * menu d'autocomplétion des @mentions et des :emojis.
 */
public class MessageInputView extends JPanel implements IMessageInputView {

    /**
     * Taille maximale d'un message (SRS-MAP-MSG-008).
     */
    private static final int MAX_MESSAGE_LENGTH = 200;

    /**
     * Champ de saisie.
     */
    private DiscordTextField mTextField;

    /**
     * Compteur de caractères.
     */
    private JLabel mCharCounter;

    /**
     * Listener des actions (Controller).
     */
    private IMessageActionListener mActionListener;

    /**
     * Popup de mentions.
     */
    private JPopupMenu mMentionPopup;

    /**
     * Popup d'emojis.
     */
    private JPopupMenu mEmojiPopup;

    /**
     * Liste des utilisateurs disponibles pour @mention.
     */
    private List<User> mAvailableUsers;

    /**
     * Constructeur.
     */
    public MessageInputView() {
        this.initPanel();
    }

    /**
     * Définit le listener des actions (Controller).
     */
    public void setActionListener(IMessageActionListener listener) {
        this.mActionListener = listener;
    }

    /**
     * Met à jour la liste des utilisateurs disponibles pour @mention.
     */
    public void setAvailableUsers(List<User> users) {
        this.mAvailableUsers = users;
    }

    /**
     * Initialisation du panel.
     */
    private void initPanel() {
        this.setLayout(new BorderLayout(8, 0));
        this.setBackground(DiscordTheme.BACKGROUND_DARK);
        this.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, DiscordTheme.BACKGROUND_TERTIARY),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)));

        // Champ de saisie
        mTextField = new DiscordTextField();
        mTextField.setFont(DiscordTheme.FONT_NORMAL);
        mTextField.setToolTipText("Envoyer un message...");
        mTextField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    doSend();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                updateCounter();
                String text = mTextField.getText();
                // Détecter @ pour l'autocomplétion de mentions
                if (e.getKeyChar() == '@') {
                    showMentionPopup();
                }
                // Détecter : pour l'autocomplétion d'emojis
                else if (text.length() > 0) {
                    int lastColon = text.lastIndexOf(':');
                    if (lastColon >= 0) {
                        String partial = text.substring(lastColon);
                        // Ne montrer que si on est en train de taper un code emoji (pas déjà fermé)
                        if (partial.length() > 1 && countChar(partial, ':') == 1) {
                            showEmojiPopup(partial);
                        }
                    }
                }
            }
        });
        this.add(mTextField, BorderLayout.CENTER);

        // Panel droit : compteur + bouton
        JPanel rightPanel = new JPanel(new BorderLayout(6, 0));
        rightPanel.setBackground(DiscordTheme.BACKGROUND_DARK);

        mCharCounter = new JLabel("0/" + MAX_MESSAGE_LENGTH);
        mCharCounter.setFont(DiscordTheme.FONT_SMALL);
        mCharCounter.setForeground(DiscordTheme.TEXT_MUTED);
        rightPanel.add(mCharCounter, BorderLayout.WEST);

        DiscordButton sendButton = new DiscordButton("Envoyer");
        sendButton.setPreferredSize(new Dimension(90, 32));
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                doSend();
            }
        });
        rightPanel.add(sendButton, BorderLayout.EAST);

        this.add(rightPanel, BorderLayout.EAST);
    }

    /**
     * Envoie le message via le listener.
     */
    private void doSend() {
        String text = mTextField.getText().trim();
        if (text.isEmpty())
            return;

        // Remplacer les codes emoji par les caractères Unicode avant envoi
        text = EmojiList.replaceEmojis(text);

        if (text.length() > MAX_MESSAGE_LENGTH) {
            // La View signale visuellement, mais ne valide pas la logique
            mCharCounter.setForeground(DiscordTheme.RED);
            return;
        }

        if (mActionListener != null) {
            mActionListener.onSendMessage(text);
        }
        mTextField.setText("");
        updateCounter();
    }

    /**
     * Met à jour le compteur de caractères.
     */
    private void updateCounter() {
        int length = mTextField.getText().length();
        mCharCounter.setText(length + "/" + MAX_MESSAGE_LENGTH);

        if (length > MAX_MESSAGE_LENGTH) {
            mCharCounter.setForeground(DiscordTheme.RED);
        } else if (length > MAX_MESSAGE_LENGTH * 0.9) {
            mCharCounter.setForeground(DiscordTheme.WARNING);
        } else {
            mCharCounter.setForeground(DiscordTheme.TEXT_MUTED);
        }
    }

    /**
     * Affiche le popup d'autocomplétion des @mentions.
     */
    private void showMentionPopup() {
        if (mAvailableUsers == null || mAvailableUsers.isEmpty())
            return;

        if (mMentionPopup != null) {
            mMentionPopup.setVisible(false);
        }

        mMentionPopup = new JPopupMenu();
        mMentionPopup.setBackground(DiscordTheme.BACKGROUND_TERTIARY);

        for (final User user : mAvailableUsers) {
            JMenuItem item = new JMenuItem(user.getName() + " (@" + user.getUserTag() + ")");
            item.setBackground(DiscordTheme.BACKGROUND_TERTIARY);
            item.setForeground(DiscordTheme.TEXT_NORMAL);
            item.setFont(DiscordTheme.FONT_NORMAL);
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Insérer la mention dans le texte
                    String current = mTextField.getText();
                    // Remplacer le dernier @ par @tag
                    int lastAt = current.lastIndexOf('@');
                    if (lastAt >= 0) {
                        mTextField.setText(current.substring(0, lastAt) + "@" + user.getUserTag() + " ");
                    }
                    mTextField.requestFocusInWindow();
                    updateCounter();
                }
            });
            mMentionPopup.add(item);
        }

        mMentionPopup.show(mTextField, 0, -mMentionPopup.getPreferredSize().height);
    }

    /**
     * Affiche le popup d'autocomplétion des emojis.
     *
     * @param partial le début du code emoji (ex: ":sm")
     */
    private void showEmojiPopup(String partial) {
        if (mEmojiPopup != null) {
            mEmojiPopup.setVisible(false);
        }

        List<Map.Entry<String, String>> results = EmojiList.search(partial);
        if (results.isEmpty()) {
            return;
        }

        mEmojiPopup = new JPopupMenu();
        mEmojiPopup.setBackground(DiscordTheme.BACKGROUND_TERTIARY);

        for (final Map.Entry<String, String> entry : results) {
            JMenuItem item = new JMenuItem(entry.getValue() + "  " + entry.getKey());
            item.setBackground(DiscordTheme.BACKGROUND_TERTIARY);
            item.setForeground(DiscordTheme.TEXT_NORMAL);
            item.setFont(DiscordTheme.FONT_NORMAL);
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // Remplacer le :code par l'emoji unicode
                    String current = mTextField.getText();
                    int lastColon = current.lastIndexOf(':');
                    if (lastColon >= 0) {
                        mTextField.setText(current.substring(0, lastColon) + entry.getValue());
                    }
                    mTextField.requestFocusInWindow();
                    updateCounter();
                }
            });
            mEmojiPopup.add(item);
        }

        mEmojiPopup.show(mTextField, 0, -mEmojiPopup.getPreferredSize().height);
    }

    /**
     * Compte le nombre d'occurrences d'un caractère dans une chaîne.
     */
    private int countChar(String s, char c) {
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == c) {
                count++;
            }
        }
        return count;
    }
}
