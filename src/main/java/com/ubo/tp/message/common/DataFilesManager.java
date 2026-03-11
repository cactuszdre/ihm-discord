package main.java.com.ubo.tp.message.common;

import java.io.File;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import main.java.com.ubo.tp.message.datamodel.Channel;
import main.java.com.ubo.tp.message.datamodel.Message;
import main.java.com.ubo.tp.message.datamodel.User;

/**
 * Classe de gestion des conversion des objets entre le datamodel et les
 * fichiers de propriété.
 *
 * @author S.Lucas
 */
public class DataFilesManager {

	/**
	 * Clé du fichier de propriété pour l'attribut uuid
	 */
	protected static final String PROPERTY_KEY_UUID = "UUID";

	/**
	 * Clé du fichier de propriété pour l'attribut tag
	 */
	protected static final String PROPERTY_KEY_USER_TAG = "Tag";

	/**
	 * Clé du fichier de propriété pour l'attribut password
	 */
	protected static final String PROPERTY_KEY_USER_PASSWORD = "This_is_not_the_password";

	/**
	 * Clé du fichier de propriété pour l'attribut name
	 */
	protected static final String PROPERTY_KEY_NAME = "Name";

	/**
	 * Clé du fichier de propriété pour l'attribut online
	 */
	protected static final String PROPERTY_KEY_USER_ONLINE = "Online";

	/**
	 * Clé du fichier de propriété pour l'attribut Sender
	 */
	protected static final String PROPERTY_KEY_MESSAGE_SENDER = "Sender";

	/**
	 * Clé du fichier de propriété pour l'attribut Recipient
	 */
	protected static final String PROPERTY_KEY_MESSAGE_RECIPIENT = "Recipient";

	/**
	 * Clé du fichier de propriété pour l'attribut Date
	 */
	protected static final String PROPERTY_KEY_MESSAGE_DATE = "Date";

	/**
	 * Clé du fichier de propriété pour l'attribut Text
	 */
	protected static final String PROPERTY_KEY_MESSAGE_TEXT = "Text";

	/**
	 * Clé du fichier de propriété pour l'attribut Reactions
	 */
	protected static final String PROPERTY_KEY_MESSAGE_REACTIONS = "Reactions";

	/**
	 * Clé du fichier de propriété pour l'attribut Creator
	 */
	protected static final String PROPERTY_KEY_CHANNEL_CREATOR = "Creator";

	/**
	 * Clé du fichier de propriété pour l'attribut Users
	 */
	protected static final String PROPERTY_KEY_CHANNEL_USERS = "Users";

	/**
	 * Séparateur pour les utilisateurs.
	 */
	protected static final String USER_SEPARATOR = ";";

	/**
	 * Chemin d'accès au répertoire d'échange.
	 */
	protected String mDirectoryPath;

	/**
	 * Lecture du fichier de propriété pour un {@link User}
	 *
	 * @param userFileName
	 */
	public User readUser(File userFile) {
		User user = null;

		if (userFile != null && userFile.getName().endsWith(Constants.USER_FILE_EXTENSION) && userFile.exists()) {
			Properties properties = PropertiesManager.loadProperties(userFile.getAbsolutePath());

			String uuid = properties.getProperty(PROPERTY_KEY_UUID, UUID.randomUUID().toString());
			String tag = properties.getProperty(PROPERTY_KEY_USER_TAG, "NoTag");
			String password = decrypt(properties.getProperty(PROPERTY_KEY_USER_PASSWORD, "NoPassword"));
			String name = properties.getProperty(PROPERTY_KEY_NAME, "NoName");
			String online = properties.getProperty(PROPERTY_KEY_USER_ONLINE, "false");

			user = new User(UUID.fromString(uuid), tag, password, name);
			user.setOnline(Boolean.parseBoolean(online));
		}

		return user;
	}

	/**
	 * Génération d'un fichier pour un utilisateur ({@link User}).
	 *
	 * @param user Utilisateur à générer.
	 */
	public void writeUserFile(User user) {
		Properties properties = new Properties();

		// Récupération du chemin pour le fichier à générer
		String destFileName = this.getFileName(user.getUuid(), Constants.USER_FILE_EXTENSION);

		properties.setProperty(PROPERTY_KEY_UUID, user.getUuid().toString());
		properties.setProperty(PROPERTY_KEY_USER_TAG, user.getUserTag());
		properties.setProperty(PROPERTY_KEY_USER_PASSWORD, encrypt(user.getUserPassword()));
		properties.setProperty(PROPERTY_KEY_NAME, user.getName());
		properties.setProperty(PROPERTY_KEY_USER_ONLINE, String.valueOf(user.isOnline()));

		PropertiesManager.writeProperties(properties, destFileName);
	}

	/**
	 * Génération d'un fichier pour un utilisateur ({@link User}).
	 *
	 * @param user Utilisateur à générer.
	 */
	public void writeChannelFile(Channel channel) {
		Properties properties = new Properties();

		// Récupération du chemin pour le fichier à générer
		String destFileName = this.getFileName(channel.getUuid(), Constants.CHANNEL_FILE_EXTENSION);

		properties.setProperty(PROPERTY_KEY_UUID, channel.getUuid().toString());
		properties.setProperty(PROPERTY_KEY_NAME, channel.getName());
		properties.setProperty(PROPERTY_KEY_CHANNEL_CREATOR, channel.getCreator().getUuid().toString());
		properties.setProperty(PROPERTY_KEY_CHANNEL_USERS, this.getUsersAsString(channel.getUsers()));

		PropertiesManager.writeProperties(properties, destFileName);
	}

	/**
	 * Lecture du fichier de propriété pour un {@link Channel}
	 *
	 * @param channelFile
	 * @param userMap
	 */
	public Channel readChannel(File channelFile, Map<UUID, User> userMap) {
		Channel channel = null;

		if (channelFile != null && channelFile.getName().endsWith(Constants.CHANNEL_FILE_EXTENSION)
				&& channelFile.exists()) {
			Properties properties = PropertiesManager.loadProperties(channelFile.getAbsolutePath());

			String uuid = properties.getProperty(PROPERTY_KEY_UUID, UUID.randomUUID().toString());
			String channelName = properties.getProperty(PROPERTY_KEY_NAME, "NoName");
			String channelCreator = properties.getProperty(PROPERTY_KEY_CHANNEL_CREATOR,
					Constants.UNKNONWN_USER_UUID.toString());
			String channelUsers = properties.getProperty(PROPERTY_KEY_CHANNEL_USERS, "");

			User creator = getUserFromUuid(channelCreator, userMap);
			List<User> allUsers = this.getUsersFromString(channelUsers, userMap);

			channel = new Channel(UUID.fromString(uuid), creator, channelName, allUsers);
		}

		return channel;
	}

	/**
	 * Lecture du fichier de propriété pour un {@link Message}
	 *
	 * @param messageFile
	 * @param userMap
	 */
	public Message readMessage(File messageFile, Map<UUID, User> userMap) {
		Message message = null;

		if (messageFile != null && messageFile.getName().endsWith(Constants.MESSAGE_FILE_EXTENSION)
				&& messageFile.exists()) {
			Properties properties = PropertiesManager.loadProperties(messageFile.getAbsolutePath());

			String uuid = properties.getProperty(PROPERTY_KEY_UUID, UUID.randomUUID().toString());
			String senderUuid = properties.getProperty(PROPERTY_KEY_MESSAGE_SENDER,
					Constants.UNKNONWN_USER_UUID.toString());
			String recipientUuid = properties.getProperty(PROPERTY_KEY_MESSAGE_RECIPIENT,
					Constants.UNKNONWN_USER_UUID.toString());
			String emissionDateStr = properties.getProperty(PROPERTY_KEY_MESSAGE_DATE, "0");
			String text = properties.getProperty(PROPERTY_KEY_MESSAGE_TEXT, "NoText");

			User sender = getUserFromUuid(senderUuid, userMap);
			long emissionDate = Long.valueOf(emissionDateStr);

			message = new Message(UUID.fromString(uuid), sender, UUID.fromString(recipientUuid), emissionDate, text,
					parseReactions(properties.getProperty(PROPERTY_KEY_MESSAGE_REACTIONS, "")));
		}

		return message;
	}

	/**
	 * Génération d'un fichier pour un Message ({@link Message}).
	 *
	 * @param message Message à générer.
	 */
	public void writeMessageFile(Message message) {
		Properties properties = new Properties();

		// Récupération du chemin pour le fichier à générer
		String destFileName = this.getFileName(message.getUuid(), Constants.MESSAGE_FILE_EXTENSION);

		properties.setProperty(PROPERTY_KEY_UUID, message.getUuid().toString());
		properties.setProperty(PROPERTY_KEY_MESSAGE_SENDER, message.getSender().getUuid().toString());
		properties.setProperty(PROPERTY_KEY_MESSAGE_RECIPIENT, message.getRecipient().toString());
		properties.setProperty(PROPERTY_KEY_MESSAGE_DATE, String.valueOf(message.getEmissionDate()));
		properties.setProperty(PROPERTY_KEY_MESSAGE_TEXT, message.getText());
		properties.setProperty(PROPERTY_KEY_MESSAGE_REACTIONS, serializeReactions(message.getReactions()));

		PropertiesManager.writeProperties(properties, destFileName);
	}

	/**
	 * Récupération de l'utilisateur identifié.
	 * 
	 * @param uuid
	 * @param userMap
	 * @return
	 */
	protected User getUserFromUuid(String uuid, Map<UUID, User> userMap) {
		// Récupération de l'utilisateur en fonction de l'UUID
		User user = userMap.get(UUID.fromString(uuid));
		if (user == null) {
			user = userMap.get(Constants.UNKNONWN_USER_UUID);
		}

		return user;
	}

	/**
	 * Retourne un chemin d'accès au fichier pour l'uuid et l'extension donnés.
	 *
	 * @param objectUuid
	 * @param fileExtension
	 */
	protected String getFileName(UUID objectUuid, String fileExtension) {
		return mDirectoryPath + Constants.SYSTEM_FILE_SEPARATOR + objectUuid + "." + fileExtension;
	}

	/**
	 * Configure le chemin d'accès au répertoire d'échange.
	 *
	 * @param directoryPath
	 */
	public void setExchangeDirectory(String directoryPath) {
		this.mDirectoryPath = directoryPath;
	}

	/**
	 * Retourne la liste des identifiants des utilisateurs sour forme d'une chaine
	 * de caractère.
	 * 
	 * @param users
	 */
	protected String getUsersAsString(List<User> users) {
		String usersAsString = "";

		Iterator<User> iterator = users.iterator();
		while (iterator.hasNext()) {
			usersAsString += iterator.next().getUuid().toString();
			if (iterator.hasNext()) {
				usersAsString += USER_SEPARATOR;
			}
		}

		return usersAsString;
	}

	/**
	 * Retourne la liste des utilisateurs depuis une chaine de caractère.
	 * 
	 * @param users
	 * @param userMap
	 */
	protected List<User> getUsersFromString(String users, Map<UUID, User> userMap) {
		List<User> userList = new ArrayList<User>();

		String[] splittedUsers = users.split(USER_SEPARATOR);
		for (String userId : splittedUsers) {
			if (!userId.isEmpty()) {
				userList.add(getUserFromUuid(userId, userMap));
			}
		}

		return userList;
	}

	public static String encrypt(String data) {
		return Base64.getEncoder().encodeToString(data.getBytes());
	}

	public static String decrypt(String encryptedData) {
		byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
		return new String(decodedBytes);
	}

	/**
	 * Sérialise les réactions en chaîne.
	 * Format : "emoji1=uuid1,uuid2;emoji2=uuid3"
	 */
	protected String serializeReactions(Map<String, Set<UUID>> reactions) {
		if (reactions == null || reactions.isEmpty()) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, Set<UUID>> entry : reactions.entrySet()) {
			if (!first) {
				sb.append(";");
			}
			sb.append(entry.getKey()).append("=");
			boolean firstUuid = true;
			for (UUID uuid : entry.getValue()) {
				if (!firstUuid) {
					sb.append(",");
				}
				sb.append(uuid.toString());
				firstUuid = false;
			}
			first = false;
		}
		return sb.toString();
	}

	/**
	 * Parse les réactions depuis une chaîne.
	 */
	protected Map<String, Set<UUID>> parseReactions(String reactionsStr) {
		Map<String, Set<UUID>> reactions = new HashMap<>();
		if (reactionsStr == null || reactionsStr.isEmpty()) {
			return reactions;
		}
		String[] pairs = reactionsStr.split(";");
		for (String pair : pairs) {
			if (pair.isEmpty()) continue;
			String[] parts = pair.split("=", 2);
			if (parts.length == 2) {
				String emoji = parts[0];
				Set<UUID> uuids = new HashSet<>();
				String[] uuidStrs = parts[1].split(",");
				for (String uuidStr : uuidStrs) {
					if (!uuidStr.isEmpty()) {
						try {
							uuids.add(UUID.fromString(uuidStr));
						} catch (IllegalArgumentException e) {
							// UUID invalide, ignorer
						}
					}
				}
				if (!uuids.isEmpty()) {
					reactions.put(emoji, uuids);
				}
			}
		}
		return reactions;
	}
}
