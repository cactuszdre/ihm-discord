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
import main.java.com.ubo.tp.message.datamodel.User;
import main.java.com.ubo.tp.message.ihm.login.AccountController;

/**
 * Classe principale l'application.
 *
 * @author S.Lucas
 */
public class MessageApp implements ISessionObserver {
	/**
	 * Gestionnaire de données.
	 */
	protected DataManager mDataManager;

	/**
	 * Session de l'application.
	 */
	protected Session mSession;

	/**
	 * Contrôleur des comptes.
	 */
	protected AccountController mAccountController;

	/**
	 * Vue principale de l'application.
	 */
	protected MessageAppMainView mMainView;

	/**
	 * Constructeur.
	 *
	 * @param dataManager gestionnaire de données
	 */
	public MessageApp(DataManager dataManager) {
		this.mDataManager = dataManager;
		this.mSession = new Session();
		this.mSession.addObserver(this);
		this.mAccountController = new AccountController(dataManager, mSession);
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
	}

	/**
	 * Initialisation du look and feel de l'application.
	 */
	protected void initLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// En cas d'erreur, on garde le Look&Feel par défaut
			System.err.println("Impossible de charger le Look&Feel système : " + e.getMessage());
		}
	}

	/**
	 * Initialisation de l'interface graphique.
	 */
	protected void initGui() {
		// Création de la vue principale avec le contrôleur de comptes
		this.mMainView = new MessageAppMainView(mAccountController);

		// Listener pour réagir à la connexion réussie
		this.mMainView.setLoginSuccessListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// La session notifiera via notifyLogin()
			}
		});

		// Enregistrement de l'observateur pour log en console
		MessageAppDatabaseObserver observer = new MessageAppDatabaseObserver();
		this.mDataManager.addObserver(observer);
	}

	/**
	 * Initialisation du répertoire d'échange (depuis la conf ou depuis un file
	 * chooser). <br/>
	 * <b>Le chemin doit obligatoirement avoir été saisi et être valide avant de
	 * pouvoir utiliser l'application</b>
	 */
	protected void initDirectory() {
		// Boucle tant qu'un répertoire valide n'a pas été sélectionné
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
				// L'utilisateur a annulé : on quitte l'application
				System.exit(0);
			}
		}
	}

	/**
	 * Indique si le fichier donné est valide pour servir de répertoire d'échange
	 *
	 * @param directory , Répertoire à tester.
	 */
	protected boolean isValidExchangeDirectory(File directory) {
		// Valide si répertoire disponible en lecture et écriture
		return directory != null && directory.exists() && directory.isDirectory() && directory.canRead()
				&& directory.canWrite();
	}

	/**
	 * Initialisation du répertoire d'échange.
	 *
	 * @param directoryPath
	 */
	protected void initDirectory(String directoryPath) {
		mDataManager.setExchangeDirectory(directoryPath);
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
		System.out.println("[SESSION] Utilisateur connecté : " + connectedUser.getName() + " (@"
				+ connectedUser.getUserTag() + ")");
		mMainView.showMainPanel();
	}

	@Override
	public void notifyLogout() {
		System.out.println("[SESSION] Déconnexion");
		mMainView.showLoginPanel();
	}
}
