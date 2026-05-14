package pkg_command;

import pkg_core.GameEngine;
import pkg_gameplay.Room;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import pkg_characters.Player;
import pkg_utility.GameLoaderXML;
import pkg_utility.Lang;

/**
 * LoadXMLCommand - Commande pour charger une partie sauvegardée en XML.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class LoadXMLCommand extends Command {
    /**
     * Constructeur par défaut de la commande LoadXMLCommand.
     * Initialise la commande sans paramètre supplémentaire.
     */
    public LoadXMLCommand() {
        // Constructeur par défaut
    }

    /**
     * Exécute la commande de chargement.
     * 
     * @param pPlayer     Le joueur exécutant la commande
     * @param pGameEngine Le moteur de jeu
     * @return true si la commande a été exécutée avec succès, false sinon
     */
    @Override
    public boolean execute(Player pPlayer, GameEngine pGameEngine) {

        String vFileName = getSecondWord();
        if (vFileName == null || vFileName.isEmpty()) {
            pGameEngine.log(Lang.localizableString("load_usage"));
            return false;
        }

        pGameEngine.log(Lang.localizableString("load_loading") + " " + vFileName + "...");

        // Charger la partie
        GameEngine vNewEngine = null;
        try {
            vNewEngine = GameLoaderXML.loadGameFromXML(vFileName);
        } catch (Exception e) {
            // Any error (file not found, parsing error) -> show user-friendly message
            pGameEngine.log(Lang.localizableString("load_file_not_found") + " " + vFileName);
            return false;
        }

        if (vNewEngine != null) {
            pGameEngine.log(Lang.localizableString("load_success"));

            // Sauvegarder la pièce actuelle pour vérification
            Room vNewRoom = vNewEngine.getPlayer().getCurrentRoom();
            System.out.println("🔍 Pièce après chargement: " + vNewRoom.getRoomKey());

            // Remplacer le moteur
            pGameEngine.replaceWith(vNewEngine);

            // Force refresh des overlays après un court délai
            Timer vRefreshTimer = new Timer(500, e -> {
                SwingUtilities.invokeLater(() -> {
                    // Rafraîchir complètement l'interface
                    pGameEngine.getGui().forceRefresh();

                    // Mettre à jour les overlays des items dans la pièce
                    Room vCurrentRoom = pGameEngine.getPlayer().getCurrentRoom();
                    if (vCurrentRoom != null) {
                        // Recharger tous les items de la pièce
                        for (pkg_items.Item vItem : vCurrentRoom.getItems().getItems()) {
                            pGameEngine.loadAndAddItemOverlay(vItem);
                        }

                        // Recharger les items de l'inventaire
                        for (pkg_items.Item vItem : pGameEngine.getPlayer().getInventory().getItems()) {
                            pGameEngine.loadAndAddInventoryOverlay(vItem);
                        }

                        // Recharger les personnages
                        for (pkg_characters.Character vChar : vCurrentRoom.getCharacters()) {
                            pGameEngine.loadAndAddCharacterOverlay(vChar);
                        }
                    }

                    pGameEngine.log(Lang.localizableString("load_ready"));
                });
            });
            vRefreshTimer.setRepeats(false);
            vRefreshTimer.start();

            return false;
        } else {
            pGameEngine.log(Lang.localizableString("load_error_xml") + " " + vFileName);
            return false;
        }
    }
}