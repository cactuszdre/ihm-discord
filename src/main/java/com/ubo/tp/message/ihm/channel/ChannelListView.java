package main.java.com.ubo.tp.message.ihm.channel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.ihm.common.DiscordButton;
import main.java.com.ubo.tp.message.ihm.common.DiscordTextField;
import main.java.com.ubo.tp.message.ihm.common.DiscordTheme;

/**
 * Vue de la liste des canaux (MVC — View pure).
 * Sidebar gauche affichant les canaux avec recherche et création.
 */
public class ChannelListView extends JPanel implements IChannelView {

    /**
     * Listener des actions utilisateur (Controller).
     */
    private IChannelActionListener mActionListener;

    /**
     * Panel contenant la liste des canaux.
     */
    private JPanel mChannelListPanel;

    /**
     * Champ de recherche.
     */
    private DiscordTextField mSearchField;

    /**
     * Canal actuellement sélectionné.
     */
    private Channel mSelectedChannel;

    /**
     * Constructeur.
     */
    public ChannelListView() {
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
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(DiscordTheme.BACKGROUND_SECONDARY);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));

        JLabel titleLabel = new JLabel("CANAUX");
        titleLabel.setFont(DiscordTheme.FONT_SMALL);
        titleLabel.setForeground(DiscordTheme.TEXT_MUTED);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        // Bouton "+" pour créer un canal
        JLabel addButton = new JLabel("+");
        addButton.setFont(DiscordTheme.FONT_HEADER);
        addButton.setForeground(DiscordTheme.TEXT_MUTED);
        addButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        addButton.setToolTipText("Créer un canal");
        addButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showCreateChannelMenu(addButton);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                addButton.setForeground(DiscordTheme.TEXT_HEADER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                addButton.setForeground(DiscordTheme.TEXT_MUTED);
            }
        });
        headerPanel.add(addButton, BorderLayout.EAST);

        this.add(headerPanel, BorderLayout.NORTH);

        // ---- Recherche ----
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(DiscordTheme.BACKGROUND_SECONDARY);
        searchPanel.setBorder(BorderFactory.createEmptyBorder(0, 8, 8, 8));

        mSearchField = new DiscordTextField();
        mSearchField.setFont(DiscordTheme.FONT_SMALL);
        mSearchField.setBorder(BorderFactory.createEmptyBorder(6, 8, 6, 8));
        // Placeholder effect handled via tooltip
        mSearchField.setToolTipText("Rechercher un canal...");
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
                    mActionListener.onSearchChannel(mSearchField.getText().trim());
                }
            }
        });
        searchPanel.add(mSearchField, BorderLayout.CENTER);

        // Panel combiné header + search
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.Y_AXIS));
        topPanel.setBackground(DiscordTheme.BACKGROUND_SECONDARY);
        topPanel.add(headerPanel);
        topPanel.add(searchPanel);

        this.add(topPanel, BorderLayout.NORTH);

        // ---- Liste des canaux ----
        mChannelListPanel = new JPanel();
        mChannelListPanel.setLayout(new BoxLayout(mChannelListPanel, BoxLayout.Y_AXIS));
        mChannelListPanel.setBackground(DiscordTheme.BACKGROUND_SECONDARY);

        JScrollPane scrollPane = new JScrollPane(mChannelListPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getViewport().setBackground(DiscordTheme.BACKGROUND_SECONDARY);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Affiche le menu de création de canal.
     */
    private void showCreateChannelMenu(Component anchor) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(DiscordTheme.BACKGROUND_TERTIARY);

        JMenuItem publicItem = new JMenuItem("Canal public");
        publicItem.setBackground(DiscordTheme.BACKGROUND_TERTIARY);
        publicItem.setForeground(DiscordTheme.TEXT_NORMAL);
        publicItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(
                        ChannelListView.this,
                        "Nom du canal public :",
                        "Créer un canal public",
                        JOptionPane.PLAIN_MESSAGE);
                if (name != null && !name.trim().isEmpty() && mActionListener != null) {
                    mActionListener.onCreatePublicChannel(name.trim());
                }
            }
        });
        menu.add(publicItem);

        JMenuItem privateItem = new JMenuItem("Canal privé");
        privateItem.setBackground(DiscordTheme.BACKGROUND_TERTIARY);
        privateItem.setForeground(DiscordTheme.TEXT_NORMAL);
        privateItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String name = JOptionPane.showInputDialog(
                        ChannelListView.this,
                        "Nom du canal privé :",
                        "Créer un canal privé",
                        JOptionPane.PLAIN_MESSAGE);
                if (name != null && !name.trim().isEmpty() && mActionListener != null) {
                    mActionListener.onCreatePrivateChannel(name.trim());
                }
            }
        });
        menu.add(privateItem);

        menu.show(anchor, 0, anchor.getHeight());
    }

    // ========== IChannelView ==========

    @Override
    public void setChannels(List<Channel> channels) {
        mChannelListPanel.removeAll();

        for (final Channel channel : channels) {
            JPanel channelRow = createChannelRow(channel);
            mChannelListPanel.add(channelRow);
        }

        // Espace de remplissage en bas
        mChannelListPanel.add(Box.createVerticalGlue());
        mChannelListPanel.revalidate();
        mChannelListPanel.repaint();
    }

    @Override
    public void setSelectedChannel(Channel channel) {
        this.mSelectedChannel = channel;
        // Refresh pour mettre à jour la surbrillance
        mChannelListPanel.repaint();
    }

    @Override
    public void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void setActionListener(IChannelActionListener listener) {
        this.mActionListener = listener;
    }

    /**
     * Crée un composant visuel pour un canal.
     */
    private JPanel createChannelRow(final Channel channel) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (mSelectedChannel != null && mSelectedChannel.getUuid().equals(channel.getUuid())) {
                    g2.setColor(DiscordTheme.BACKGROUND_DARK);
                } else {
                    g2.setColor(getBackground());
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        row.setOpaque(false);
        row.setBackground(DiscordTheme.BACKGROUND_SECONDARY);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

        // Icône : # pour public, 🔒 pour privé
        String prefix = channel.isPrivate() ? "\uD83D\uDD12" : "#";
        JLabel prefixLabel = new JLabel(prefix);
        prefixLabel.setFont(DiscordTheme.FONT_NORMAL);
        prefixLabel.setForeground(DiscordTheme.TEXT_MUTED);
        row.add(prefixLabel);

        // Nom du canal
        JLabel nameLabel = new JLabel(channel.getName());
        nameLabel.setFont(DiscordTheme.FONT_NORMAL);
        nameLabel.setForeground(DiscordTheme.TEXT_NORMAL);
        row.add(nameLabel);

        // Clic → sélection
        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1 && mActionListener != null) {
                    mActionListener.onChannelSelected(channel);
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (mSelectedChannel == null || !mSelectedChannel.getUuid().equals(channel.getUuid())) {
                    row.setBackground(new Color(60, 63, 69));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                row.setBackground(DiscordTheme.BACKGROUND_SECONDARY);
            }
        });

        // Clic droit → menu contextuel (supprimer / quitter)
        row.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.isPopupTrigger())
                    showContextMenu(e, channel);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger())
                    showContextMenu(e, channel);
            }
        });

        return row;
    }

    /**
     * Menu contextuel sur un canal (clic droit).
     */
    private void showContextMenu(MouseEvent e, Channel channel) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBackground(DiscordTheme.BACKGROUND_TERTIARY);

        if (channel.isPrivate()) {
            JMenuItem deleteItem = new JMenuItem("Supprimer le canal");
            deleteItem.setForeground(DiscordTheme.RED);
            deleteItem.setBackground(DiscordTheme.BACKGROUND_TERTIARY);
            deleteItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ev) {
                    if (mActionListener != null) {
                        mActionListener.onDeleteChannel(channel);
                    }
                }
            });
            menu.add(deleteItem);

            JMenuItem leaveItem = new JMenuItem("Quitter le canal");
            leaveItem.setForeground(DiscordTheme.TEXT_NORMAL);
            leaveItem.setBackground(DiscordTheme.BACKGROUND_TERTIARY);
            leaveItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent ev) {
                    if (mActionListener != null) {
                        mActionListener.onLeaveChannel(channel);
                    }
                }
            });
            menu.add(leaveItem);
        }

        menu.show(e.getComponent(), e.getX(), e.getY());
    }
}
