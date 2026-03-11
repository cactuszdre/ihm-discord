package main.java.com.ubo.tp.message.ihm.common;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Liste prédéfinie des emojis avec leur code texte et leur caractère Unicode.
 * Utilisée pour l'autocomplétion dans le champ de saisie et pour les réactions.
 */
public class EmojiList {

    /**
     * Constructeur privé (classe utilitaire).
     */
    private EmojiList() {
    }

    /**
     * Map ordonnée : code (ex: ":smile:") → caractère Unicode (ex: "😊").
     */
    public static final Map<String, String> EMOJIS = new LinkedHashMap<>();

    /**
     * Emojis de réaction disponibles (sous-ensemble).
     */
    public static final String[] REACTION_EMOJIS = { "👍", "❤️", "😂", "😮", "😢", "😠" };

    static {
        EMOJIS.put(":smile:", "😊");
        EMOJIS.put(":smirk:", "😏");
        EMOJIS.put(":sad:", "😢");
        EMOJIS.put(":fire:", "🔥");
        EMOJIS.put(":heart:", "❤️");
        EMOJIS.put(":thumbsup:", "👍");
        EMOJIS.put(":joy:", "😂");
        EMOJIS.put(":surprised:", "😮");
        EMOJIS.put(":angry:", "😠");
        EMOJIS.put(":cool:", "😎");
        EMOJIS.put(":wave:", "👋");
        EMOJIS.put(":clap:", "👏");
        EMOJIS.put(":rocket:", "🚀");
        EMOJIS.put(":star:", "⭐");
        EMOJIS.put(":check:", "✅");
        EMOJIS.put(":x:", "❌");
        EMOJIS.put(":party:", "🎉");
        EMOJIS.put(":think:", "🤔");
        EMOJIS.put(":eyes:", "👀");
        EMOJIS.put(":100:", "💯");
    }

    /**
     * Recherche les emojis dont le code commence par le préfixe donné.
     *
     * @param prefix le début du code emoji (ex: ":sm")
     * @return liste des entrées correspondantes (code → unicode)
     */
    public static List<Map.Entry<String, String>> search(String prefix) {
        List<Map.Entry<String, String>> results = new ArrayList<>();
        String lower = prefix.toLowerCase();
        for (Map.Entry<String, String> entry : EMOJIS.entrySet()) {
            if (entry.getKey().startsWith(lower)) {
                results.add(entry);
            }
        }
        return results;
    }

    /**
     * Remplace tous les codes emoji dans le texte par leurs caractères Unicode.
     *
     * @param text le texte contenant des codes emoji
     * @return le texte avec les codes remplacés
     */
    public static String replaceEmojis(String text) {
        String result = text;
        for (Map.Entry<String, String> entry : EMOJIS.entrySet()) {
            result = result.replace(entry.getKey(), entry.getValue());
        }
        return result;
    }
}
