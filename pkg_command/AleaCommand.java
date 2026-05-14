package pkg_command;

import pkg_core.GameEngine;
import pkg_gameplay.Room;
import pkg_characters.Player;
import pkg_utility.Lang;

/**
 * AleaCommand - Commande pour contrôler l'aléatoire de la TransporterRoom (mode
 * test).
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class AleaCommand extends Command {
    /**
     * Constructeur par défaut de la commande AleaCommand.
     * Initialise la commande sans paramètre supplémentaire.
     */
    public AleaCommand() {
        // Constructeur par défaut
    }

    /**
     * Exécute la commande pour contrôler l'aléatoire de la TransporterRoom.
     * 
     * @param pPlayer     Le joueur exécutant la commande
     * @param pGameEngine Le moteur du jeu
     * @return true si la commande a été exécutée avec succès, false sinon
     */
    @Override
    public boolean execute(Player pPlayer, GameEngine pGameEngine) {
        // Vérifier si on est en mode test
        if (!pGameEngine.isTestModeActive() && !pGameEngine.isDebugMode()) {
            pGameEngine.log(Lang.localizableString("alea_not_in_test"));
            return false;
        }

        if (!hasSecondWord()) {
            // Sans paramètre : désactive le mode forcé
            pGameEngine.clearTestModeForAllTransporters();
            pGameEngine.log(Lang.localizableString("alea_cleared"));
            return false;
        }

        // Avec paramètre : force la prochaine destination
        String vRoomKey = getSecondWord();

        // Vérifier que la clé de pièce existe
        if (Room.isValidRoomKey(vRoomKey)) {
            pGameEngine.setTestModeForAllTransporters(vRoomKey);
            pGameEngine.log(String.format(
                    Lang.localizableString("alea_set"),
                    Lang.localizableString("short_" + vRoomKey)));
        } else {
            pGameEngine.log(String.format(
                    Lang.localizableString("alea_invalid_room"),
                    vRoomKey));
        }

        return false;
    }
}
