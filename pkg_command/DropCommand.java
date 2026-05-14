package pkg_command;

import pkg_core.GameEngine;
import pkg_characters.Player;
import pkg_items.Item;
import pkg_utility.Lang;

/**
 * DropCommand - Commande pour déposer un objet.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class DropCommand extends Command {
    /**
     * Constructeur par défaut de la commande DropCommand.
     * Initialise la commande sans paramètre supplémentaire.
     */
    public DropCommand() {
        // Constructeur par défaut
    }

    /**
     * Exécute la commande pour déposer un objet.
     * 
     * @param pPlayer     Le joueur exécutant la commande
     * @param pGameEngine Le moteur de jeu
     * @return true si la commande a été exécutée avec succès, false sinon
     */
    @Override
    public boolean execute(Player pPlayer, GameEngine pGameEngine) {
        if (!hasSecondWord()) {
            pGameEngine.log(Lang.localizableString("drop_what") + "\n");
            return false;
        }

        String vItemName = getSecondWord();
        Item vItem = pPlayer.dropItem(vItemName);

        pGameEngine.refreshInventory();

        if (vItem != null) {
            pGameEngine.log(Lang.localizableString("item_dropped") + " " + vItem.getInformation());
            pGameEngine.log(pPlayer.getTotalWeighString() + "\n");
        } else {
            pGameEngine.log(Lang.localizableString("cannot_drop") + "\n");
        }
        return false;
    }
}
