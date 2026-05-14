package pkg_command;

import pkg_core.GameEngine;
import pkg_exceptions.GameException;
import pkg_characters.Player;
import pkg_items.Item;
import pkg_utility.Lang;

//.;
/**
 * TakeCommand - Commande pour prendre un objet.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class TakeCommand extends Command {
    /**
     * Constructeur par défaut de la commande TakeCommand.
     * Initialise la commande sans paramètre supplémentaire.
     */
    public TakeCommand() {
        // Constructeur par défaut
    }

    /**
     * Exécute la commande pour prendre un objet.
     * 
     * @param pPlayer     Le joueur exécutant la commande
     * @param pGameEngine Le moteur de jeu
     * @return true si la commande a été exécutée avec succès, false sinon
     */
    @Override
    public boolean execute(Player pPlayer, GameEngine pGameEngine) {
        if (!hasSecondWord()) {
            pGameEngine.log(Lang.localizableString("take_what") + "\n");
            return false;
        }

        String vItemName = getSecondWord();
        try {
            Item vItem = pPlayer.takeItem(vItemName);

            pGameEngine.refreshInventory();

            pGameEngine.log(Lang.localizableString("item_taken") + " " + vItem.getInformation());
            pGameEngine.log(pPlayer.getTotalWeighString() + "\n");

        } catch (GameException e) {
            pGameEngine.log(Lang.localizableString("cannot_take") + " "
                    + Lang.localizableString(e.getResourceKey()) + "\n");
        }
        return false;
    }
}
