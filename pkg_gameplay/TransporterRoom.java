package pkg_gameplay;

import java.util.List;
import java.util.Random;
import pkg_utility.Lang;

/**
 * TransporterRoom - Une pièce spéciale qui téléporte le joueur aléatoirement
 * dans une autre pièce chaque fois qu'il tente de la quitter.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class TransporterRoom extends Room {
    /** Générateur de nombres aléatoires */
    private Random aRandom;
    /** Liste de toutes les destinations possibles */
    private List<Room> aDestinationRooms;
    /** Clé de la pièce forcée (null si aléatoire réel) */
    private String aForcedDestinationKey;
    /** Indique si on est en mode test */
    private boolean aTestMode;

    /**
     * Constructeur de la TransporterRoom.
     * 
     * @param pRoomKey Clé d'identification de la pièce
     * @param pImage   Nom du fichier image
     */
    public TransporterRoom(final String pRoomKey, final String pImage) {
        super(pRoomKey, pImage);
        this.aRandom = new Random();
        this.aTestMode = false;
        this.aForcedDestinationKey = null;
    }

    /**
     * Initialise la liste des destinations possibles.
     * Cette méthode doit être appelée après la création de toutes les pièces.
     * 
     * @param pRooms Liste de toutes les pièces du jeu
     */
    public void initializeDestinations(final List<Room> pRooms) {
        this.aDestinationRooms = pRooms;
    }

    /**
     * Active le mode test et force la prochaine destination.
     * 
     * @param pRoomKey La clé de la pièce vers laquelle se téléporter
     */
    public void setForcedDestination(final String pRoomKey) {
        this.aForcedDestinationKey = pRoomKey;
        this.aTestMode = true;
    }

    /**
     * Désactive le mode test et revient à l'aléatoire réel.
     */
    public void clearForcedDestination() {
        this.aForcedDestinationKey = null;
        this.aTestMode = false;
    }

    /**
     * Vérifie si on est en mode test.
     * 
     * @return true si le mode test est actif
     */
    public boolean isTestMode() {
        return this.aTestMode;
    }

    /**
     * Retourne une destination aléatoire ou forcée selon le mode.
     * 
     * @return Une pièce (aléatoire ou forcée)
     */
    private Room getRandomDestination() {
        // Mode test avec destination forcée
        if (this.aTestMode && this.aForcedDestinationKey != null) {
            for (Room vRoom : this.aDestinationRooms) {
                if (vRoom.getRoomKey().equals(this.aForcedDestinationKey)) {
                    return vRoom;
                }
            }
        }

        // Mode normal ou test sans destination forcée
        if (this.aDestinationRooms == null || this.aDestinationRooms.isEmpty()) {
            return this; // Si pas de destinations, reste sur place
        }

        int vIndex = this.aRandom.nextInt(this.aDestinationRooms.size());
        return this.aDestinationRooms.get(vIndex);
    }

    /**
     * Surcharge de getExit() pour la TransporterRoom.
     * Au lieu de retourner la pièce dans la direction demandée,
     * retourne une pièce aléatoire ou forcée.
     * 
     * @param pDirection La direction (ignorée)
     * @return Une pièce (aléatoire ou forcée)
     */
    @Override
    public Room getExit(final String pDirection) {
        return getRandomDestination();
    }

    /**
     * @return Une description spéciale pour la TransporterRoom
     */
    @Override
    public String getLongDescription() {
        String vDescription = super.getLongDescription() + "\n" +
                Lang.localizableString("transporter_room_description");

        // Indication du mode test dans la description
        if (this.aTestMode) {
            vDescription += "\n" + String.format(
                    Lang.localizableString("transporter_test_mode"),
                    this.aForcedDestinationKey != null ? Lang.localizableString("short_" + this.aForcedDestinationKey)
                            : Lang.localizableString("random"));
        }

        return vDescription;
    }
}