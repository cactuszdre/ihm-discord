package main.java.com.ubo.tp.message.ihm.user;

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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.common.DiscordTextField;
import main.java.com.ubo.tp.message.ihm.common.DiscordTheme;

/**
 * Vue de la liste des utilisateurs (MVC — View pure).
 * Sidebar droite affichant les utilisateurs avec statut en ligne et recherche.
 */
public class UserListView extends JPanel implements IUserView {

    /**
     * Listener des actions utilisateur (Controller).
     */
    private IUserActionListener mActionListener;

    /**
     * Panel contenant la liste des utilisateurs.
     */
    private JPanel mUserListPanel;

    /**
     * Champ de recherche.
     */
    private DiscordTextField mSearchField;

    /**
     * Constructeur.
     */
    public UserListView() {
        this.initPanel();
    }

    /**
     * Initialisation du panel.
     */
    private void initPanel() {
        this.setLayout(new BorderLayout());
        this.setBackground(DiscordTheme.BACKGROUND_SECONDARY);
        this.setPreferredSize(new Dimension(220, 0));

        // ---- Header ----
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(DiscordTheme.BACKGROUND_SECONDARY);

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(DiscordTheme.BACKGROUND_SECONDARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel titleLabel = new JLabel("UTILISATEURS");
        titleLabel.setFont(DiscordTheme.FONT_SMALL);
        titleLabel.setForeground(DiscordTheme.TEXT_MUTED);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        topPanel.add(headerPanel);

        // ---- Recherche ----
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(DiscordTheme.BACKGROUND_SECONDARY);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));

        mSearchField = new DiscordTextField();
        mSearchField.setFont(DiscordTheme.FONT_SMALL);
        mSearchField.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        mSearchField.setToolTipText("Rechercher un utilisateur...");
        mSearchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                doSearch();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                doSearch();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                doSearch();
            }

            private void doSearch() {
                if (mActionListener != null) {
                    mActionListener.onSearchUser(mSearchField.getText().trim());
                }
            }
        });
        searchPanel.add(mSearchField, BorderLayout.CENTER);
        topPanel.add(searchPanel);

        this.add(topPanel, BorderLayout.NORTH);

        // ---- Liste des utilisateurs ----
        mUserListPanel = new JPanel();
        mUserListPanel.setLayout(new BoxLayout(mUserListPanel, BoxLayout.Y_AXIS));
        mUserListPanel.setBackground(DiscordTheme.BACKGROUND_SECONDARY);

        JScrollPane scrollPane = new JScrollPane(mUserListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(DiscordTheme.BACKGROUND_SECONDARY);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        this.add(scrollPane, BorderLayout.CENTER);
    }

    // ========== IUserView ==========

    @Override
    public void setUsers(List<User> users) {
        mUserListPanel.removeAll();

        for (final User user : users) {
            JPanel userRow = createUserRow(user);
            mUserListPanel.add(userRow);
        }

        mUserListPanel.add(Box.createVerticalGlue());
        mUserListPanel.revalidate();
        mUserListPanel.repaint();
    }

    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void setActionListener(IUserActionListener listener) {
        this.mActionListener = listener;
    }

    /**
     * Crée un composant visuel pour un utilisateur.
     */
    private JPanel createUserRow(final User user) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        row.setOpaque(true);
        row.setBackground(DiscordTheme.BACKGROUND_SECONDARY);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        // Avatar (cercle coloré avec initiale)
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DiscordTheme.BLURPLE);
                g2.fillOval(2, 2, 28, 28);

                // Initiale
                String initial = user.getName().substring(0, 1).toUpperCase();
                g2.setColor(Color.WHITE);
                g2.setFont(DiscordTheme.FONT_NORMAL);
                int textWidth = g2.getFontMetrics().stringWidth(initial);
                int textHeight = g2.getFontMetrics().getAscent();
                g2.drawString(initial, 2 + (28 - textWidth) / 2, 2 + (28 + textHeight) / 2 - 3);

                // Indicateur en ligne (SRS-MAP-CHN-010)
                if (user.isOnline()) {
                    g2.setColor(DiscordTheme.GREEN);
                } else {
                    g2.setColor(DiscordTheme.TEXT_MUTED);
                }
                g2.fillOval(22, 22, 10, 10);
                // Bordure
                g2.setColor(DiscordTheme.BACKGROUND_SECONDARY);
                g2.drawOval(22, 22, 10, 10);

                g2.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(34, 34));
        avatarPanel.setBackground(DiscordTheme.BACKGROUND_SECONDARY);
        row.add(avatarPanel);

        // Informations
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(DiscordTheme.BACKGROUND_SECONDARY);

        JLabel nameLabel = new JLabel(user.getName());
        nameLabel.setFont(DiscordTheme.FONT_NORMAL);
        nameLabel.setForeground(user.isOnline() ? DiscordTheme.TEXT_HEADER : DiscordTheme.TEXT_MUTED);
        infoPanel.add(nameLabel);

        JLabel tagLabel = new JLabel("@" + user.getUserTag());
        tagLabel.setFont(DiscordTheme.FONT_SMALL);
        tagLabel.setForeground(DiscordTheme.TEXT_MUTED);
        infoPanel.add(tagLabel);

        row.add(infoPanel);

        // Hover effet
        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                row.setBackground(DiscordTheme.BACKGROUND_HOVER);
                avatarPanel.setBackground(DiscordTheme.BACKGROUND_HOVER);
                infoPanel.setBackground(DiscordTheme.BACKGROUND_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                row.setBackground(DiscordTheme.BACKGROUND_SECONDARY);
                avatarPanel.setBackground(DiscordTheme.BACKGROUND_SECONDARY);
                infoPanel.setBackground(DiscordTheme.BACKGROUND_SECONDARY);
            }
        });

        // Clic droit → message direct
        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger())
                    showContextMenu(e, user);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger())
                    showContextMenu(e, user);
            }
        });

        return row;
    }

    /**
     * Menu contextuel sur un utilisateur.
     */
    private void showContextMenu(MouseEvent e, final User user) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(DiscordTheme.BACKGROUND_TERTIARY);

        JMenuItem dmItem = new JMenuItem("Envoyer un message privé");
        dmItem.setBackground(DiscordTheme.BACKGROUND_TERTIARY);
        dmItem.setForeground(DiscordTheme.TEXT_NORMAL);
        dmItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ev) {
                if (mActionListener != null) {
                    mActionListener.onSendDirectMessage(user);
                }
            }
        });
        menu.add(dmItem);

        menu.show(e.getComponent(), e.getX(), e.getY());
    }
}
