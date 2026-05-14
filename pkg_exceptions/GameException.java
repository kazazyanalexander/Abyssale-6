package pkg_exceptions;

/**
 * GameException - Classe de base pour les exceptions spécifiques au jeu.
 * Hérite de la classe Exception.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class GameException extends Exception {
    /** Clé de ressource pour l'internationalisation des messages d'erreur */
    private String aResourceKey;

    /**
     * Constructeur de GameException.
     * 
     * @param pResourceKey La clé de ressource permettant d'obtenir le message
     *                     d'erreur internationalisé
     */
    public GameException(String pResourceKey) {
        this.aResourceKey = pResourceKey;
    }

    /**
     * Retourne la clé de ressource associée à cette exception.
     * 
     * @return La clé de ressource pour l'internationalisation
     */
    public String getResourceKey() {
        return this.aResourceKey;
    }
}
