package main.java.com.ubo.tp.message.ihm;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.session.ISessionObserver;
import main.java.com.ubo.tp.message.core.session.Session;
import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.channel.ChannelController;
import main.java.com.ubo.tp.message.ihm.login.AccountController;
import main.java.com.ubo.tp.message.ihm.message.MessageController;
import main.java.com.ubo.tp.message.ihm.notification.NotificationManager;
import main.java.com.ubo.tp.message.ihm.user.UserController;

/**
 * Classe principale de l'application (MVC — Coordinateur).
 * Instancie les Views (via MainView) et les Controllers,
 * et câble les composants MVC entre eux.
 */
public class MessageApp implements ISessionObserver {
	/**
	 * Gestionnaire de données (Model).
	 */
	protected DataManager mDataManager;

	/**
	 * Session de l'application (Model).
	 */
	protected Session mSession;

	/**
	 * Contrôleur des comptes.
	 */
	protected AccountController mAccountController;

	/**
	 * Contrôleur des canaux.
	 */
	protected ChannelController mChannelController;

	/**
	 * Contrôleur des messages.
	 */
	protected MessageController mMessageController;

	/**
	 * Contrôleur des utilisateurs.
	 */
	protected UserController mUserController;

	/**
	 * Gestionnaire de notifications (SRS-MAP-MSG-010).
	 */
	protected NotificationManager mNotificationManager;

	/**
	 * Vue principale de l'application.
	 */
	protected MessageAppMainView mMainView;

	/**
	 * Utilisateur précédemment connecté (sauvegardé pour le logout).
	 */
	private User mLastConnectedUser;

	/**
	 * Constructeur.
	 *
	 * @param dataManager gestionnaire de données
	 */
	public MessageApp(DataManager dataManager) {
		this.mDataManager = dataManager;
		this.mSession = new Session();
		this.mSession.addObserver(this);
	}

	/**
	 * Initialisation de l'application.
	 */
	public void init() {
		// Init du look and feel de l'application
		this.initLookAndFeel();

		// Initialisation de l'IHM
		this.initGui();

		// Initialisation du répertoire d'échange
		this.initDirectory();

		// Ajout d'un shutdown hook pour déconnecter proprement à la fermeture
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				if (mLastConnectedUser != null) {
					mLastConnectedUser.setOnline(false);
					mDataManager.sendUser(mLastConnectedUser);
				}
			}
		}));
	}

	/**
	 * Initialisation du look and feel de l'application.
	 */
	protected void initLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Impossible de charger le Look&Feel système : " + e.getMessage());
		}
	}

	/**
	 * Initialisation de l'interface graphique et câblage MVC.
	 */
	protected void initGui() {
		// Création de la vue principale (toutes les sous-vues sont créées dedans)
		this.mMainView = new MessageAppMainView();

		// ---- Câblage MVC : Login ----
		this.mAccountController = new AccountController(mDataManager, mSession);
		this.mAccountController.setLoginView(mMainView.getLoginPanel());
		this.mAccountController.setRegistrationView(mMainView.getRegistrationPanel());

		// Listener de déconnexion
		this.mMainView.setLogoutListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mSession.disconnect();
			}
		});

		// Enregistrement de l'observateur pour log en console
		MessageAppDatabaseObserver observer = new MessageAppDatabaseObserver();
		this.mDataManager.addObserver(observer);
	}

	/**
	 * Câble les contrôleurs du panel principal (canaux, messages, utilisateurs).
	 * Appelé après la connexion de l'utilisateur.
	 */
	private void initMainControllers() {
		// ---- Câblage MVC : Canaux ----
		mChannelController = new ChannelController(
				mDataManager, mSession, mMainView.getChannelListView());

		// ---- Câblage MVC : Messages ----
		mMessageController = new MessageController(
				mDataManager, mSession,
				mMainView.getMessageListView(),
				mMainView.getMessageInputView());

		// ---- Câblage MVC : Utilisateurs ----
		mUserController = new UserController(
				mDataManager, mSession, mMainView.getUserListView());

		// Définir l'utilisateur connecté dans la vue utilisateurs
		mMainView.getUserListView().setCurrentUser(mSession.getConnectedUser());

		// ---- Liaison canal → messages ----
		mChannelController.setChannelSelectionListener(
				new ChannelController.IChannelSelectionListener() {
					@Override
					public void onChannelSelected(Channel channel) {
						mMessageController.setCurrentChannel(channel);
					}
				});

		// ---- Liaison utilisateur → DM (canal privé) ----
		mUserController.setDirectMessageListener(
				new UserController.IDirectMessageListener() {
					@Override
					public void onDirectMessageChannelReady(Channel dmChannel) {
						// Sélectionner le canal DM dans la vue des canaux
						mChannelController.onChannelSelected(dmChannel);
					}
				});

		// ---- Liaison suppression de compte → déconnexion (SRS-MAP-USR-010) ----
		mUserController.setAccountDeletionListener(
				new UserController.IAccountDeletionListener() {
					@Override
					public void onAccountDeleted() {
						// Empêcher la sauvegarde du statut online lors du logout
						mLastConnectedUser = null;
						mSession.disconnect();
					}
				});

		// ---- Notifications DM / @mention (SRS-MAP-MSG-010) ----
		mNotificationManager = new NotificationManager(mDataManager, mSession);
	}

	/**
	 * Initialisation du répertoire d'échange.
	 */
	protected void initDirectory() {
		// Tentative d'utilisation automatique du dossier "bdd" du projet
		File defaultDir = new File("bdd");
		if (isValidExchangeDirectory(defaultDir)) {
			this.initDirectory(defaultDir.getAbsolutePath());
			return;
		}

		// Fallback : sélection manuelle
		boolean validDirectory = false;

		while (!validDirectory) {
			JFileChooser fileChooser = new JFileChooser();
			fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			fileChooser.setDialogTitle("Choisir le répertoire d'échange");
			fileChooser.setAcceptAllFileFilterUsed(false);

			int result = fileChooser.showOpenDialog(this.mMainView);

			if (result == JFileChooser.APPROVE_OPTION) {
				File selectedDir = fileChooser.getSelectedFile();
				if (this.isValidExchangeDirectory(selectedDir)) {
					this.initDirectory(selectedDir.getAbsolutePath());
					validDirectory = true;
				} else {
					JOptionPane.showMessageDialog(
							this.mMainView,
							"Le répertoire sélectionné n'est pas valide.\nVeuillez choisir un autre répertoire.",
							"Répertoire invalide",
							JOptionPane.WARNING_MESSAGE);
				}
			} else {
				System.exit(0);
			}
		}
	}

	/**
	 * Indique si le fichier donné est valide pour servir de répertoire d'échange.
	 */
	protected boolean isValidExchangeDirectory(File directory) {
		return directory != null && directory.exists() && directory.isDirectory()
				&& directory.canRead() && directory.canWrite();
	}

	/**
	 * Initialisation du répertoire d'échange.
	 */
	protected void initDirectory(String directoryPath) {
		mDataManager.setExchangeDirectory(directoryPath);
	}

	/**
	 * Réinitialise tous les utilisateurs à offline.
	 * Appelé au démarrage pour nettoyer les statuts obsolètes
	 * (ex: fermeture brutale sans logout).
	 */
	private void resetAllUsersOffline() {
		for (User user : mDataManager.getUsers()) {
			if (user.isOnline()) {
				user.setOnline(false);
				mDataManager.sendUser(user);
			}
		}
	}

	public void show() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mMainView.pack();
				mMainView.setLocationRelativeTo(null);
				mMainView.setVisible(true);
			}
		});
	}

	// ========== ISessionObserver ==========

	@Override
	public void notifyLogin(User connectedUser) {
		System.out.println("[SESSION] Utilisateur connecté : " + connectedUser.getName()
				+ " (@" + connectedUser.getUserTag() + ")");

		// Sauvegarder la référence pour le logout et marquer comme en ligne
		mLastConnectedUser = connectedUser;
		connectedUser.setOnline(true);
		mDataManager.sendUser(connectedUser);

		// Câblage des contrôleurs du panel principal
		initMainControllers();

		// Basculer vers le panel principal
		mMainView.showMainPanel();
	}

	@Override
	public void notifyLogout() {
		System.out.println("[SESSION] Déconnexion");

		// Marquer l'utilisateur comme hors ligne
		// Note: mSession.getConnectedUser() est déjà null à ce stade,
		// on utilise la référence sauvegardée lors du login.
		if (mLastConnectedUser != null) {
			mLastConnectedUser.setOnline(false);
			mDataManager.sendUser(mLastConnectedUser);
			mLastConnectedUser = null;
		}

		// Désenregistrement des observers (SKILL.md §4.6)
		if (mChannelController != null) {
			mChannelController.dispose();
		}
		if (mMessageController != null) {
			mMessageController.dispose();
		}
		if (mUserController != null) {
			mUserController.dispose();
		}
		if (mNotificationManager != null) {
			mNotificationManager.dispose();
		}

		// Nettoyage des contrôleurs
		mChannelController = null;
		mMessageController = null;
		mUserController = null;
		mNotificationManager = null;

		// Retour au login
		mMainView.showLoginPanel();
	}
}
