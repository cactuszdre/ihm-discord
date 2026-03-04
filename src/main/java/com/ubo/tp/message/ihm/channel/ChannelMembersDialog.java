package main.java.com.ubo.tp.message.ihm.channel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;

import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.common.DiscordButton;
import main.java.com.ubo.tp.message.ihm.common.DiscordTheme;

/**
 * Dialogue modal de gestion des membres d'un canal privé.
 * Affiche la liste des membres actuels (avec bouton Retirer)
 * et la liste des utilisateurs disponibles (avec bouton Ajouter).
 */
public class ChannelMembersDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    /**
     * Canal à gérer.
     */
    private Channel mChannel;

    /**
     * Listener d'actions (Controller).
     */
    private IChannelActionListener mActionListener;

    /**
     * Liste des membres actuels.
     */
    private List<User> mCurrentMembers;

    /**
     * Liste des utilisateurs disponibles (non membres).
     */
    private List<User> mAvailableUsers;

    /**
     * Panel de la liste des membres.
     */
    private JPanel mMembersPanel;

    /**
     * Panel de la liste des utilisateurs disponibles.
     */
    private JPanel mAvailablePanel;

    /**
     * Constructeur.
     *
     * @param parent         fenêtre parente
     * @param channel        canal à gérer
     * @param currentMembers membres actuels du canal
     * @param availableUsers utilisateurs disponibles (non membres)
     * @param actionListener listener d'actions
     */
    public ChannelMembersDialog(Component parent, Channel channel,
            List<User> currentMembers, List<User> availableUsers,
            IChannelActionListener actionListener) {
        super((Frame) SwingUtilities.getWindowAncestor(parent),
                "Gérer les membres — #" + channel.getName(), true);
        this.mChannel = channel;
        this.mCurrentMembers = currentMembers;
        this.mAvailableUsers = availableUsers;
        this.mActionListener = actionListener;

        initDialog();
    }

    /**
     * Initialisation du dialogue.
     */
    private void initDialog() {
        this.setSize(500, 450);
        this.setLocationRelativeTo(getParent());
        this.getContentPane().setBackground(DiscordTheme.BACKGROUND_SECONDARY);
        this.setLayout(new BorderLayout(0, 8));

        // ---- Titre ----
        JLabel titleLabel = new JLabel("Membres du canal #" + mChannel.getName());
        titleLabel.setFont(DiscordTheme.FONT_SUBHEADER);
        titleLabel.setForeground(DiscordTheme.TEXT_HEADER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(16, 16, 8, 16));
        this.add(titleLabel, BorderLayout.NORTH);

        // ---- Contenu principal (2 colonnes) ----
        JPanel contentPanel = new JPanel(new BorderLayout(8, 0));
        contentPanel.setBackground(DiscordTheme.BACKGROUND_SECONDARY);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 16, 16));

        // Colonne gauche : membres actuels
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(DiscordTheme.BACKGROUND_TERTIARY);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel membersLabel = new JLabel("MEMBRES ACTUELS");
        membersLabel.setFont(DiscordTheme.FONT_SMALL);
        membersLabel.setForeground(DiscordTheme.TEXT_MUTED);
        membersLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        leftPanel.add(membersLabel, BorderLayout.NORTH);

        mMembersPanel = new JPanel();
        mMembersPanel.setLayout(new BoxLayout(mMembersPanel, BoxLayout.Y_AXIS));
        mMembersPanel.setBackground(DiscordTheme.BACKGROUND_TERTIARY);

        JScrollPane membersScroll = new JScrollPane(mMembersPanel);
        membersScroll.setBorder(BorderFactory.createEmptyBorder());
        membersScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        membersScroll.getViewport().setBackground(DiscordTheme.BACKGROUND_TERTIARY);
        membersScroll.getVerticalScrollBar().setUnitIncrement(16);
        leftPanel.add(membersScroll, BorderLayout.CENTER);

        // Colonne droite : utilisateurs disponibles
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(DiscordTheme.BACKGROUND_TERTIARY);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));

        JLabel availableLabel = new JLabel("UTILISATEURS DISPONIBLES");
        availableLabel.setFont(DiscordTheme.FONT_SMALL);
        availableLabel.setForeground(DiscordTheme.TEXT_MUTED);
        availableLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        rightPanel.add(availableLabel, BorderLayout.NORTH);

        mAvailablePanel = new JPanel();
        mAvailablePanel.setLayout(new BoxLayout(mAvailablePanel, BoxLayout.Y_AXIS));
        mAvailablePanel.setBackground(DiscordTheme.BACKGROUND_TERTIARY);

        JScrollPane availableScroll = new JScrollPane(mAvailablePanel);
        availableScroll.setBorder(BorderFactory.createEmptyBorder());
        availableScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        availableScroll.getViewport().setBackground(DiscordTheme.BACKGROUND_TERTIARY);
        availableScroll.getVerticalScrollBar().setUnitIncrement(16);
        rightPanel.add(availableScroll, BorderLayout.CENTER);

        // Assemblage en SplitPane-like (deux colonnes)
        contentPanel.add(leftPanel, BorderLayout.WEST);
        contentPanel.add(rightPanel, BorderLayout.CENTER);

        leftPanel.setPreferredSize(new Dimension(220, 0));

        this.add(contentPanel, BorderLayout.CENTER);

        // ---- Bouton Fermer ----
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(DiscordTheme.BACKGROUND_SECONDARY);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 16, 12, 16));

        DiscordButton closeButton = new DiscordButton("Fermer");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        bottomPanel.add(closeButton);
        this.add(bottomPanel, BorderLayout.SOUTH);

        // Remplir les listes
        refreshLists();
    }

    /**
     * Rafraîchit les deux listes.
     */
    private void refreshLists() {
        // ---- Membres actuels ----
        mMembersPanel.removeAll();
        for (final User member : mCurrentMembers) {
            JPanel row = createUserRow(member, false);
            mMembersPanel.add(row);
        }
        mMembersPanel.add(Box.createVerticalGlue());
        mMembersPanel.revalidate();
        mMembersPanel.repaint();

        // ---- Utilisateurs disponibles ----
        mAvailablePanel.removeAll();
        for (final User user : mAvailableUsers) {
            JPanel row = createUserRow(user, true);
            mAvailablePanel.add(row);
        }
        mAvailablePanel.add(Box.createVerticalGlue());
        mAvailablePanel.revalidate();
        mAvailablePanel.repaint();
    }

    /**
     * Crée une ligne pour un utilisateur avec un bouton Ajouter ou Retirer.
     *
     * @param user      utilisateur à afficher
     * @param isAddable true pour Ajouter, false pour Retirer
     */
    private JPanel createUserRow(final User user, final boolean isAddable) {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(DiscordTheme.BACKGROUND_TERTIARY);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        row.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));

        // Avatar (petit cercle avec initiale)
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(DiscordTheme.BLURPLE);
                g2.fillOval(2, 2, 24, 24);

                String initial = user.getName().substring(0, 1).toUpperCase();
                g2.setColor(Color.WHITE);
                g2.setFont(DiscordTheme.FONT_SMALL);
                int tw = g2.getFontMetrics().stringWidth(initial);
                int th = g2.getFontMetrics().getAscent();
                g2.drawString(initial, 2 + (24 - tw) / 2, 2 + (24 + th) / 2 - 3);
                g2.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(28, 28));
        avatarPanel.setBackground(DiscordTheme.BACKGROUND_TERTIARY);
        row.add(avatarPanel, BorderLayout.WEST);

        // Nom + Tag
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBackground(DiscordTheme.BACKGROUND_TERTIARY);

        JLabel nameLabel = new JLabel(user.getName());
        nameLabel.setFont(DiscordTheme.FONT_NORMAL);
        nameLabel.setForeground(DiscordTheme.TEXT_NORMAL);
        infoPanel.add(nameLabel);

        JLabel tagLabel = new JLabel("@" + user.getUserTag());
        tagLabel.setFont(DiscordTheme.FONT_SMALL);
        tagLabel.setForeground(DiscordTheme.TEXT_MUTED);
        infoPanel.add(tagLabel);

        row.add(infoPanel, BorderLayout.CENTER);

        // Bouton action
        boolean isCreator = user.getUuid().equals(mChannel.getCreator().getUuid());

        if (isAddable) {
            DiscordButton addBtn = new DiscordButton("Ajouter");
            addBtn.setPreferredSize(new Dimension(80, 26));
            addBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (mActionListener != null) {
                        mActionListener.onAddUserToChannel(mChannel, user);
                        // Mettre à jour les listes localement
                        mCurrentMembers.add(user);
                        mAvailableUsers.remove(user);
                        refreshLists();
                    }
                }
            });
            row.add(addBtn, BorderLayout.EAST);
        } else if (!isCreator) {
            DiscordButton removeBtn = new DiscordButton("Retirer");
            removeBtn.setPreferredSize(new Dimension(80, 26));
            removeBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (mActionListener != null) {
                        mActionListener.onRemoveUserFromChannel(mChannel, user);
                        mCurrentMembers.remove(user);
                        mAvailableUsers.add(user);
                        refreshLists();
                    }
                }
            });
            row.add(removeBtn, BorderLayout.EAST);
        } else {
            JLabel creatorLabel = new JLabel("Créateur");
            creatorLabel.setFont(DiscordTheme.FONT_SMALL);
            creatorLabel.setForeground(DiscordTheme.GREEN);
            row.add(creatorLabel, BorderLayout.EAST);
        }

        return row;
    }
}
