package pkg_utility;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Lang gère l'internationalisation (i18n) pour le jeu.
 * Il charge et gère les ressources textuelles localisées à partir de fichiers
 * de propriétés,
 * permettant un changement de langue facile et la génération de chaînes
 * formatées.
 * Utilisation :
 * - Créer avec les codes langue/pays : new Lang("fr", "FR")
 * - Obtenir des chaînes simples : getString("key")
 * - Obtenir des chaînes formatées : getFormattedString("key", param1,
 * param2...)
 * Les fichiers de propriétés doivent être nommés :
 * messages_{langue}_{pays}.properties
 * Exemple : messages_fr_FR.properties pour le français (France)
 * messages_en_US.properties pour l'anglais (USA)
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class Lang {
    /** Instance unique du singleton */
    private static Lang instance;
    /** Bundle de ressources localisées */
    private ResourceBundle aBundle;

    /**
     * Construit un Lang pour la langue et le pays spécifiés.
     */
    private Lang() {
        // Définit la langue dans votre Lang
        setLanguage("FR", "fr");
    }

    /**
     * Construit un Lang pour la langue et le pays spécifiés.
     * 
     * @return L'instance unique de Lang
     */
    public static Lang getInstance() {
        if (instance == null) {
            instance = new Lang();
        }
        return instance;
    }

    /**
     * Modifie les paramètres actuels de langue et de pays.
     * Recharge le bundle de ressources approprié pour la nouvelle locale.
     * 
     * @param pLanguageCode Code langue ISO 639 (peut être "fr", "en", "de", "zh")
     * @param pCountryCode  Code pays ISO 3166
     * @return true si la langue a été changée avec succès, false sinon (par exemple
     *         si les codes sont invalides ou si le bundle de ressources n'a pas pu
     *         être chargé)
     */
    public boolean setLanguage(final String pLanguageCode, final String pCountryCode) {
        if (pLanguageCode == null || pCountryCode == null) {
            System.err.println("❌ Langue ou pays null");
            return false;
        }

        // Convertir en minuscules pour la comparaison
        String vLang = pLanguageCode.toLowerCase();

        // Liste des langues supportées
        if (!vLang.equals("en") && !vLang.equals("fr") && !vLang.equals("de") && !vLang.equals("zh")) {
            System.err.println("❌ Langue non supportée: " + pLanguageCode);
            return false;
        }

        try {
            final Locale vLocale = Locale.of(vLang, pCountryCode);
            // Charge les propriétés depuis "messages_{langue}_{pays}.properties"
            this.aBundle = ResourceBundle.getBundle("messages.messages", vLocale);
            // System.out.println("✅ Langue changée: " + vLang + "_" + pCountryCode);
            return true;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement de la langue: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retourne le code de langue actuel (ex: "fr", "en", "de", "zh").
     * 
     * @return Le code de langue actuel
     */
    public String getLanguage() {
        return this.aBundle.getLocale().getLanguage();
    }

    /**
     * Récupère une chaîne localisée pour la clé donnée.
     * 
     * @param pKey La clé de propriété à rechercher
     * @return La valeur de la chaîne localisée
     * @throws java.util.MissingResourceException si la clé n'est pas trouvée
     */
    private String getString(final String pKey) {
        return this.aBundle.getString(pKey);
    }

    /**
     * Récupère une chaîne localisée pour la clé donnée.
     * 
     * @param key La clé de propriété à rechercher
     * @return La valeur de la chaîne localisée
     */
    public static String localizableString(final String key) {
        return Lang.getInstance().getString(key);
    }

}
