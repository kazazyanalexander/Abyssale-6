package pkg_command;

import pkg_core.GameEngine;
import pkg_gameplay.Room;
import pkg_characters.Player;
import pkg_utility.Lang;

/**
 * BackCommand - Commande pour revenir à la pièce précédente.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class BackCommand extends Command {
    /**
     * Constructeur par défaut de la commande BackCommand.
     * Initialise la commande sans paramètre supplémentaire.
     */
    public BackCommand() {
        // Constructeur par défaut
    }

    /**
     * Exécute la commande pour revenir à la pièce précédente.
     * 
     * @param pPlayer     Le joueur exécutant la commande
     * @param pGameEngine Le moteur de jeu
     * @return true si la commande a été exécutée avec succès, false sinon
     */
    @Override
    public boolean execute(Player pPlayer, GameEngine pGameEngine) {
        Room vPreviousRoom = pPlayer.getPreviousRoom();

        // Vérifier si la pièce précédente existe et est accessible
        if (vPreviousRoom != null && pPlayer.getCurrentRoom().isExit(vPreviousRoom)) {
            if (pPlayer.goBack()) {
                Room vCurrentRoom = pPlayer.getCurrentRoom();
                pGameEngine.setPlayerRoom(vCurrentRoom);
                pGameEngine.playSound("door.wav");
            }
        } else {
            if (vPreviousRoom == null) {
                pGameEngine.log(Lang.localizableString("back_start"));
            } else {
                pGameEngine.log(Lang.localizableString("back_trapdoor_blocked"));
            }
        }
        return false;
    }
}
