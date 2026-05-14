package pkg_command;

import pkg_core.GameEngine;
import pkg_characters.Player;
import pkg_utility.Lang;

/**
 * TestCommand - Commande pour exécuter un fichier de test.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class TestCommand extends Command {
    /**
     * Constructeur par défaut de la commande TestCommand.
     * Initialise la commande sans paramètre supplémentaire.
     */
    public TestCommand() {
        // Constructeur par défaut
    }

    /**
     * Exécute la commande pour exécuter un fichier de test.
     * 
     * @param pPlayer     Le joueur exécutant la commande
     * @param pGameEngine Le moteur de jeu
     * @return true si la commande a été exécutée avec succès, false sinon
     */
    @Override
    public boolean execute(Player pPlayer, GameEngine pGameEngine) {
        if (!hasSecondWord()) {
            pGameEngine.log(Lang.localizableString("test_error_no_file"));
            return false;
        }

        String vFileName = "tests/" + getSecondWord() + ".txt";
        pGameEngine.runTestFile(vFileName);
        return false;
    }
}
