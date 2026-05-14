package pkg_command;

import pkg_core.GameEngine;
import pkg_characters.Player;

/**
 * Command - Classe abstraite représentant une commande dans le jeu.
 * Toutes les commandes concrètes doivent hériter de cette classe.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public abstract class Command {
    /** Second mot de la commande */
    private String aSecondWord;

    /**
     * Constructeur par défaut.
     */
    public Command() {
        aSecondWord = null;
    }

    /**
     * Retourne le second mot de la commande.
     * 
     * @return Le second mot de la commande, ou null si aucun second mot n'existe
     */
    public String getSecondWord() {
        return aSecondWord;
    }

    /**
     * Définit le second mot de la commande.
     * 
     * @param pSecondWord Le second mot
     */
    public void setSecondWord(final String pSecondWord) {
        aSecondWord = pSecondWord;
    }

    /**
     * Vérifie si la commande a un second mot.
     * 
     * @return true si un second mot existe, false sinon
     */
    public boolean hasSecondWord() {
        return aSecondWord != null;
    }

    /**
     * Exécute la commande.
     * 
     * @param pPlayer     Le joueur
     * @param pGameEngine Le moteur de jeu (pour les actions complexes)
     * @return true si la commande termine le jeu, false sinon
     */
    public abstract boolean execute(Player pPlayer, GameEngine pGameEngine);
}
