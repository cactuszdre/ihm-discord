package main.java.com.ubo.tp.message.datamodel;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Classe du modèle représentant un message.
 *
 * @author S.Lucas
 */
public class Message extends AbstractMessageAppObject {

	/**
	 * Utilisateur source du message.
	 */
	protected final User mSender;

	/**
	 * Destinataire du message.
	 */
	protected final UUID mRecipient;

	/**
	 * Date d'émission du message.
	 */
	protected final long mEmissionDate;

	/**
	 * Corps du message.
	 */
	protected final String mText;

	/**
	 * Réactions sur ce message : emoji → set d'UUIDs des utilisateurs ayant réagi.
	 */
	protected Map<String, Set<UUID>> mReactions;

	/**
	 * Constructeur.
	 *
	 * @param sender    utilisateur à l'origine du message.
	 * @param recipient destinataire du message.
	 * @param text      corps du message.
	 */
	public Message(User sender, UUID recipient, String text) {
		this(UUID.randomUUID(), sender, recipient, System.currentTimeMillis(), text,
				new HashMap<String, Set<UUID>>());
	}

	/**
	 * Constructeur.
	 *
	 * @param messageUuid  identifiant du message.
	 * @param sender       utilisateur à l'origine du message.
	 * @param recipient    destinataire du message.
	 * @param emissionDate date d'émission du message.
	 * @param text         corps du message.
	 */
	public Message(UUID messageUuid, User sender, UUID recipient, long emissionDate, String text) {
		this(messageUuid, sender, recipient, emissionDate, text, new HashMap<String, Set<UUID>>());
	}

	/**
	 * Constructeur complet avec réactions.
	 */
	public Message(UUID messageUuid, User sender, UUID recipient, long emissionDate, String text,
			Map<String, Set<UUID>> reactions) {
		super(messageUuid);
		mSender = sender;
		mRecipient = recipient;
		mEmissionDate = emissionDate;
		mText = text;
		mReactions = reactions != null ? reactions : new HashMap<String, Set<UUID>>();
	}

	/**
	 * @return l'utilisateur source du message.
	 */
	public User getSender() {
		return mSender;
	}

	/**
	 * @return le destinataire du message.
	 */
	public UUID getRecipient() {
		return mRecipient;
	}

	/**
	 * @return le corps du message.
	 */
	public String getText() {
		return mText;
	}

	/**
	 * Retourne la date d'émission.
	 */
	public long getEmissionDate() {
		return this.mEmissionDate;
	}

	/**
	 * @return les réactions sur ce message.
	 */
	public Map<String, Set<UUID>> getReactions() {
		return mReactions;
	}

	/**
	 * Définit les réactions.
	 */
	public void setReactions(Map<String, Set<UUID>> reactions) {
		this.mReactions = reactions;
	}

	/**
	 * Ajoute ou retire la réaction d'un utilisateur (toggle).
	 *
	 * @param emoji  l'emoji de la réaction
	 * @param userId l'UUID de l'utilisateur
	 */
	public void toggleReaction(String emoji, UUID userId) {
		Set<UUID> users = mReactions.get(emoji);
		if (users == null) {
			users = new HashSet<UUID>();
			mReactions.put(emoji, users);
		}
		if (users.contains(userId)) {
			users.remove(userId);
			if (users.isEmpty()) {
				mReactions.remove(emoji);
			}
		} else {
			users.add(userId);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();

		sb.append("[");
		sb.append(this.getClass().getName());
		sb.append("] : ");
		sb.append(this.getUuid());
		sb.append(" {");
		sb.append(this.getText());
		sb.append("}");

		return sb.toString();
	}
}
