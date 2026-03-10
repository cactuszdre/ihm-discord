package main.java.com.ubo.tp.message;

import main.java.com.ubo.tp.message.core.DataManager;
import main.java.com.ubo.tp.message.core.database.Database;
import main.java.com.ubo.tp.message.core.database.DbConnector;
import main.java.com.ubo.tp.message.core.database.EntityManager;
import main.java.com.ubo.tp.message.ihm.MessageApp;
import main.java.com.ubo.tp.message.ihm.ModeSelectionDialog;
import main.java.com.ubo.tp.message.ihm.javafx.MessageAppFX;

/**
 * Classe de lancement de l'application.
 *
 * @author S.Lucas
 */
public class MessageAppLauncher {

	/**
	 * Indique si le mode bouchoné est activé.
	 */
	protected static boolean IS_MOCK_ENABLED = false;

	/**
	 * Launcher.
	 *
	 * @param args
	 */
	public static void main(String[] args) {

		Database database = new Database();

		EntityManager entityManager = new EntityManager(database);

		DataManager dataManager = new DataManager(database, entityManager);

		DbConnector dbConnector = new DbConnector(database);

		if (IS_MOCK_ENABLED) {
			mock.MessageAppMock mock = new mock.MessageAppMock(dbConnector, dataManager);
			mock.showGUI();
		}

		// Affichage de la fenêtre de sélection de mode
		ModeSelectionDialog.show(new ModeSelectionDialog.ModeSelectionListener() {
			@Override
			public void onSwingSelected() {
				MessageApp messageApp = new MessageApp(dataManager);
				messageApp.init();
				messageApp.show();
			}

			@Override
			public void onJavaFXSelected() {
				MessageAppFX.setDataManager(dataManager);
				javafx.application.Application.launch(MessageAppFX.class, new String[] {});
			}
		});
	}
}
