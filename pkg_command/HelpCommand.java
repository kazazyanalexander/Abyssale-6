package pkg_command;

import pkg_core.GameEngine;
import pkg_characters.Player;
import pkg_utility.Lang;

/**
 * HelpCommand - Commande pour afficher l'aide.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class HelpCommand extends Command {
    /**
     * Constructeur par défaut de la commande HelpCommand.
     * Initialise la commande sans paramètre supplémentaire.
     */
    public HelpCommand() {
        // Constructeur par défaut
    }

    /**
     * Exécute la commande pour afficher l'aide.
     * 
     * @param pPlayer     Le joueur exécutant la commande
     * @param pGameEngine Le moteur de jeu
     * @return true si la commande a été exécutée avec succès, false sinon
     */
    @Override
    public boolean execute(Player pPlayer, GameEngine pGameEngine) {
        pGameEngine.log(Lang.localizableString("help"));
        pGameEngine.log(pGameEngine.getCommandWords() + "\n");

        // Ajouter l'aide spécifique pour le beamer
        pGameEngine.log(Lang.localizableString("help_beamer"));
        pGameEngine.log(Lang.localizableString("help_charge"));
        pGameEngine.log(Lang.localizableString("help_fire"));

        // ===== Aide pour les personnages =====
        pGameEngine.log(Lang.localizableString("help_characters"));
        pGameEngine.log(Lang.localizableString("help_talk"));
        pGameEngine.log(Lang.localizableString("help_give"));
        return false;
    }
}
