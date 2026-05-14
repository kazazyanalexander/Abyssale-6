package pkg_command;

import pkg_core.GameEngine;
import pkg_gameplay.Room;
import pkg_characters.Player;
import pkg_items.Beamer;
import pkg_utility.Lang;

/**
 * FireCommand - Commande pour déclencher le beamer.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class FireCommand extends Command {
    /**
     * Constructeur par défaut de la commande FireCommand.
     * Initialise la commande sans paramètre supplémentaire.
     */
    public FireCommand() {
        // Constructeur par défaut
    }

    /**
     * Constructeur de la commande FireCommand.
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

        if (!vBeamer.isCharged()) {
            pGameEngine.log(Lang.localizableString("beamer_not_charged"));
            return false;
        }

        Room vTargetRoom = vBeamer.fire();

        if (vTargetRoom == null) {
            pGameEngine.log(Lang.localizableString("beamer_error"));
            return false;
        }

        pPlayer.pushHistory();
        pGameEngine.setPlayerRoom(vTargetRoom);

        pGameEngine.log(Lang.localizableString("beamer_fired"));
        pGameEngine.log(vTargetRoom.getLongDescription());
        pGameEngine.showImage(vTargetRoom.getImageName());

        pGameEngine.playSound("teleport.wav");
        return false;
    }

}
