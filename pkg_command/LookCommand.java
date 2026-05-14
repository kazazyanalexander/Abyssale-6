package pkg_command;

import pkg_core.GameEngine;
import pkg_characters.Player;

/**
 * LookCommand - Commande pour observer la pièce actuelle.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class LookCommand extends Command {
    /**
     * Constructeur par défaut de la commande LookCommand.
     * Initialise la commande sans paramètre supplémentaire.
     */
    public LookCommand() {
        // Constructeur par défaut
    }

    /**
     * Exécute la commande pour observer la pièce actuelle.
     * 
     * @param pPlayer     Le joueur exécutant la commande
     * @param pGameEngine Le moteur de jeu
     * @return true si la commande a été exécutée avec succès, false sinon
     */
    @Override
    public boolean execute(Player pPlayer, GameEngine pGameEngine) {
        pGameEngine.log(pPlayer.getCurrentRoom().getLongDescription());
        return false;
    }
}
