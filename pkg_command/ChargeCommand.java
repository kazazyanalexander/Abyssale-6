package pkg_command;

import pkg_core.GameEngine;
import pkg_characters.Player;
import pkg_items.Beamer;
import pkg_utility.Lang;

/**
 * ChargeCommand - Commande pour charger le beamer.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class ChargeCommand extends Command {
    /**
     * Constructeur par défaut de la commande ChargeCommand.
     * Initialise la commande sans paramètre supplémentaire.
     */
    public ChargeCommand() {
        // Constructeur par défaut
    }

    /**
     * Constructeur de la commande ChargeCommand.
     * 
     * @param pPlayer     Le joueur exécutant la commande
     * @param pGameEngine Le moteur de jeu
     * @return true si la commande a été exécutée avec succès, false sinon
     */
    @Override
    public boolean execute(Player pPlayer, GameEngine pGameEngine) {
        Beamer vBeamer = pPlayer.findBeamerInInventory();

        if (vBeamer == null) {
            pGameEngine.log(Lang.localizableString("no_beamer"));
            return false;
        }

        if (vBeamer.isCharged()) {
            pGameEngine.log(Lang.localizableString("beamer_already_charged"));
            return false;
        }

        vBeamer.charge(pPlayer.getCurrentRoom());
        pGameEngine.log(Lang.localizableString("beamer_charged") + " " +
                pPlayer.getCurrentRoom().getShortDescription());

        pGameEngine.playSound("charge.wav");
        return false;
    }

}
