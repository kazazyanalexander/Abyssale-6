package pkg_command;

import pkg_core.GameEngine;
import pkg_gameplay.Room;
import pkg_characters.Character;
import pkg_characters.Player;
import pkg_ui_components.NPCChatDialog;
import pkg_utility.Lang;

/**
 * TalkCommand - Commande pour parler aux personnages via dialogue IA.
 *
 * @author Alexander KAZAZYAN
 * @version 05/2026
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

        String secondWord = getSecondWord();

        if (secondWord == null) {
            // Aucun nom spécifié -> afficher la popup de sélection des personnages
            if (pGameEngine.getGui() == null) {
                // Mode headless : afficher la liste dans la console
                pGameEngine.log(Lang.localizableString("talk_to_whom") + ":");
                for (Character c : vCurrentRoom.getCharacters()) {
                    pGameEngine.log("  - " + Lang.localizableString(c.getName()));
                }
                return false;
            }
            pGameEngine.showTalkPopup(); // Ouvre CharacterInteractionPopup en mode TALK
            return false;
        }

        // Un nom a été fourni -> ouvrir directement le chat avec ce personnage
        Character vCharacter = vCurrentRoom.getCharacter(secondWord);
        if (vCharacter == null) {
            pGameEngine.log(String.format(Lang.localizableString("character_not_found"), secondWord));
            return false;
        }

        openChatDialog(pGameEngine, vCharacter);
        return false;
    }

    /**
     * Ouvre la boîte de dialogue de chat pour le personnage spécifié.
     * 
     * @param pGameEngine Le moteur de jeu
     * @param pCharacter  Le personnage avec lequel parler
     * 
     */
    private void openChatDialog(GameEngine pGameEngine, Character pCharacter) {
        if (pGameEngine.getGui() == null) {
            pGameEngine.log(Lang.localizableString("chat_only_graphical"));
            return;
        }
        new NPCChatDialog(pGameEngine.getGui(), pCharacter);
    }
}