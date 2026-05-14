package pkg_command;

import pkg_core.GameEngine;
import pkg_characters.Player;
import pkg_utility.Lang;

/**
 * SpecialCommand - Commande spéciale pour les actions sans classe dédiée (ex:
 * command).
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class LangCommand extends Command {
    /**
     * Constructeur par défaut de la commande LangCommand.
     * Initialise la commande sans paramètre supplémentaire.
     */
    public LangCommand() {
        // Constructeur par défaut
    }

    /**
     * Exécute la commande pour changer la langue.
     * 
     * @param pPlayer     Le joueur exécutant la commande
     * @param pGameEngine Le moteur de jeu
     * @return true si la commande a été exécutée avec succès, false sinon
     */
    @Override
    public boolean execute(Player pPlayer, GameEngine pGameEngine) {
        if (!hasSecondWord()) {
            pGameEngine.log(Lang.localizableString("lang_error_no_code") + "\n");
            return false;
        }

        String vLangCode = getSecondWord();
        if (Lang.getInstance().setLanguage(vLangCode.toUpperCase(), vLangCode)) {
            pGameEngine.log(Lang.localizableString("lang_changed") + " " + vLangCode + "\n");
            // Réafficher les informations de la pièce pour appliquer la nouvelle langue
            pGameEngine.printLocationInfo();
        } else {
            pGameEngine.log(Lang.localizableString("lang_error_invalid_code") + " : " + vLangCode + "\n");
        }
        return false;
    }
}
