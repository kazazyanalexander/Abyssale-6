package pkg_command;

import pkg_core.GameEngine;
import pkg_characters.Player;
import pkg_utility.Lang;

/**
 * UnknownCommand - Commande pour les commandes inconnues.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class UnknownCommand extends Command {
    /**
     * Constructeur par défaut de la commande UnknownCommand.
     * Initialise la commande sans paramètre supplémentaire.
     */
    public UnknownCommand() {
        // Constructeur par défaut
    }

    /**
     * Exécute la commande pour les commandes inconnues.
     * 
     * @param pPlayer     Le joueur exécutant la commande
     * @param pGameEngine Le moteur de jeu
     * @return true si la commande a été exécutée avec succès, false sinon
     */
    @Override
    public boolean execute(Player pPlayer, GameEngine pGameEngine) {
        pGameEngine.log(Lang.localizableString("wrong_command"));
        return false;
    }
}