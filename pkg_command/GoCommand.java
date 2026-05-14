package pkg_command;

import pkg_core.GameEngine;
import pkg_gameplay.Door;
import pkg_gameplay.Room;
import pkg_gameplay.TransporterRoom;
import pkg_ui_components.ReactorPuzzleDialog;
import javax.swing.SwingUtilities;
import pkg_characters.Player;
import pkg_utility.Lang;

/**
 * GoCommand - Commande pour se déplacer dans une direction.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class GoCommand extends Command {
    /**
     * Constructeur par défaut de la commande GoCommand.
     * Initialise la commande sans paramètre supplémentaire.
     */
    public GoCommand() {
        // Constructeur par défaut
    }

    /**
     * Exécute la commande pour se déplacer dans une direction.
     * 
     * @param pPlayer     Le joueur exécutant la commande
     * @param pGameEngine Le moteur de jeu
     * @return true si la commande a été exécutée avec succès, false sinon
     */
    @Override
    public boolean execute(Player pPlayer, GameEngine pGameEngine) {
        if (!hasSecondWord()) {
            pGameEngine.log(Lang.localizableString("where_to_go"));
            return false;
        }

        final String vDirection = getSecondWord();
        final Room vCurrentRoom = pPlayer.getCurrentRoom();

        // Vérifier si la porte est verrouillée
        if (vCurrentRoom.isDoorLocked(vDirection)) {
            Door vDoor = vCurrentRoom.getDoor(vDirection);
            pGameEngine.log(Lang.localizableString("door_locked_message"));
            pGameEngine.log(vDoor.getStatusDescription());
            pGameEngine.playSound("access.wav");
            return false;
        }

        final Room vNextRoom = vCurrentRoom.getExit(vDirection);

        if (vNextRoom == null) {
            pGameEngine.log(Lang.localizableString("no_door"));
            return false;
        }

        // Vérifier si c'est une TransporterRoom
        boolean vIsTransporter = vCurrentRoom instanceof TransporterRoom;

        if (vIsTransporter) {
            pGameEngine.log(Lang.localizableString("transporter_activated"));
        }

        // Notifier le passage par une porte
        if (vCurrentRoom.hasDoor(vDirection)) {
            vCurrentRoom.doorPassed(vDirection);
        }

        pPlayer.pushHistory();

        // Déplacer le joueur
        pGameEngine.setPlayerRoom(vNextRoom);

        if (vIsTransporter) {
            pGameEngine.log(Lang.localizableString("transporter_arrival"));
        }

        pGameEngine.playSound("door.wav");

        // Après chaque commande, faire bouger les personnages mobiles
        pGameEngine.moveAllCharacters();

        // ===== Vérifier si la pièce est le réacteur =====
        if (vNextRoom.isReactor()) {
            // Afficher le puzzle avant de déclarer la victoire
            this.showReactorPuzzle(pGameEngine);
            // Retourner false car le jeu ne se termine pas immédiatement
            // (le puzzle décidera via handleVictory ou handleGameOver)
            return false;
        }

        return false;
    }

    /**
     * Affiche la boîte de dialogue du puzzle du réacteur.
     * 
     * @param pGameEngine Le moteur de jeu
     */
    private void showReactorPuzzle(final GameEngine pGameEngine) {
        // Mettre en pause le timer du jeu pendant le puzzle
        pGameEngine.pauseTimer();

        SwingUtilities.invokeLater(() -> {
            ReactorPuzzleDialog vDialog = new ReactorPuzzleDialog(pGameEngine.getGui(), pGameEngine);
            vDialog.setVisible(true);

            // Note: Le résultat est déjà géré par le listener dans ReactorPuzzleDialog
            // Si le puzzle est résolu avec succès, handleVictory aura été appelé et le jeu
            // se terminera
            // Si le puzzle échoue, handleGameOver aura été appelé directement
        });
    }
}