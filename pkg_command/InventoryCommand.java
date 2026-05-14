package pkg_command;

import pkg_core.GameEngine;
import pkg_characters.Player;

/**
 * InventoryCommand - Commande pour afficher l'inventaire.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class InventoryCommand extends Command {
    /**
     * Constructeur par défaut de la commande InventoryCommand.
     * Initialise la commande sans paramètre supplémentaire.
     */
    public InventoryCommand() {
        // Constructeur par défaut
    }

    /**
     * Exécute la commande pour afficher l'inventaire.
     * 
     * @param pPlayer     Le joueur exécutant la commande
     * @param pGameEngine Le moteur de jeu
     * @return true si la commande a été exécutée avec succès, false sinon
     */
    @Override
    public boolean execute(Player pPlayer, GameEngine pGameEngine) {
        pGameEngine.log(pPlayer.getInventoryString() + "\n");
        return false;
    }
}
