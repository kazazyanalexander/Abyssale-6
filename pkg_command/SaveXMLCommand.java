package pkg_command;

import pkg_core.GameEngine;
import javax.swing.JOptionPane;
import pkg_characters.Player;
import pkg_utility.GameSaverXML;
import pkg_utility.Lang;

/**
 * SaveXMLCommand - Commande pour sauvegarder l'état du jeu en XML.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class SaveXMLCommand extends Command {
    /**
     * Constructeur par défaut de la commande SaveXMLCommand.
     * Initialise la commande sans paramètre supplémentaire.
     */
    public SaveXMLCommand() {
        // Constructeur par défaut
    }

    /**
     * Exécute la commande de sauvegarde.
     * 
     * @param pPlayer     Le joueur exécutant la commande
     * @param pGameEngine Le moteur de jeu
     * @return true si la commande a été exécutée avec succès, false sinon
     */
    @Override
    public boolean execute(Player pPlayer, GameEngine pGameEngine) {

        String vFileName;

        if (hasSecondWord()) {
            vFileName = getSecondWord();
        } else {
            vFileName = JOptionPane.showInputDialog(
                    pGameEngine.getGui(),
                    Lang.localizableString("save_prompt"),
                    Lang.localizableString("save_title"),
                    JOptionPane.QUESTION_MESSAGE);

            if (vFileName == null || vFileName.trim().isEmpty()) {
                pGameEngine.log(Lang.localizableString("save_cancelled"));
                return false;
            }
        }

        // Nettoyer le nom
        vFileName = vFileName.trim().replaceAll("[^a-zA-Z0-9_-]", "_");

        boolean vSuccess = GameSaverXML.saveGameToXML(pGameEngine, vFileName);

        if (vSuccess) {
            pGameEngine.log(Lang.localizableString("save_success") + " " + vFileName + ".xml");
        } else {
            pGameEngine.log(Lang.localizableString("save_error"));
        }

        return false;
    }
}