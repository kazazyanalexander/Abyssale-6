package pkg_command;

import pkg_core.GameEngine;
import pkg_gameplay.Room;
import pkg_characters.Character;
import pkg_characters.Player;
import pkg_utility.Lang;

/**
 * TalkCommand - Commande pour parler aux personnages.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class TalkCommand extends Command {
    /**
     * Constructeur par défaut de la commande TalkCommand.
     * Initialise la commande sans paramètre supplémentaire.
     */
    public TalkCommand() {
        // Constructeur par défaut
    }

    /**
     * Exécute la commande pour parler aux personnages.
     * 
     * @param pPlayer     Le joueur exécutant la commande
     * @param pGameEngine Le moteur de jeu
     * @return true si la commande a été exécutée avec succès, false sinon
     */
    @Override
    public boolean execute(Player pPlayer, GameEngine pGameEngine) {
        Room vCurrentRoom = pPlayer.getCurrentRoom();

        if (!vCurrentRoom.hasCharacters()) {
            pGameEngine.log(Lang.localizableString("no_characters_here"));
            return false;
        }

        if (!hasSecondWord()) {
            // Parler à tous → utiliser la popup
            pGameEngine.showTalkPopup();
            return false;
        }

        // Parler à un personnage spécifique
        String vCharName = getSecondWord();
        Character vChar = vCurrentRoom.getCharacter(vCharName);

        if (vChar == null) {
            pGameEngine.log(String.format(
                    Lang.localizableString("character_not_found"),
                    vCharName));
            return false;
        }

        pGameEngine.log(vChar.speak());
        return false;
    }
}