package pkg_items;

import pkg_gameplay.Room;
import pkg_utility.Lang;

/**
 * Beamer - Dispositif de téléportation qui peut mémoriser une pièce et y
 * téléporter le joueur.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class Beamer extends GenericItem {
    /** Version de serialisation automatique */
    private static final long serialVersionUID = 1L;
    /** Pièce mémorisée (null si non chargé) */
    private Room aMemorizedRoom;
    /** État de charge */
    private boolean aIsCharged;

    /**
     * Constructeur du beamer.
     */
    public Beamer() {
        super(ItemType.BEAMER);
        this.aMemorizedRoom = null;
        this.aIsCharged = false;
    }

    /**
     * Charge le beamer avec la pièce actuelle.
     * 
     * @param pCurrentRoom La pièce où se trouve le joueur
     * @return true si le chargement a réussi
     */
    public boolean charge(Room pCurrentRoom) {
        this.aMemorizedRoom = pCurrentRoom;
        this.aIsCharged = true;
        return true;
    }

    /**
     * Déclenche le beamer pour retourner à la pièce mémorisée.
     * 
     * @return La pièce mémorisée, ou null si non chargé
     */
    public Room fire() {
        if (!this.aIsCharged) {
            return null;
        }

        Room vTargetRoom = this.aMemorizedRoom;

        // Réinitialiser après utilisation (optionnel - si réutilisable)
        this.aMemorizedRoom = null;
        this.aIsCharged = false;

        return vTargetRoom;
    }

    /**
     * Vérifie si le beamer est chargé.
     * 
     * @return true si chargé, false sinon
     */
    public boolean isCharged() {
        return this.aIsCharged;
    }

    /**
     * Récupère la pièce mémorisée.
     * 
     * @return La pièce mémorisée, ou null si non chargé
     */
    public Room getMemorizedRoom() {
        return this.aMemorizedRoom;
    }

    /**
     * Retourne une description complète du beamer, incluant son état de charge et
     * la pièce mémorisée.
     * 
     * @return Description complète avec état de charge localisé
     */
    @Override
    public String getInformation() {
        String vBase = Lang.localizableString(super.getName());
        if (this.aIsCharged) {
            // Format: "Téléporteur [⚡ Chargé - Sas d'entrée]"
            return String.format(Lang.localizableString("beamer_charged_format"),
                    vBase,
                    this.aMemorizedRoom.getShortDescription());
        } else {
            // Format: "Téléporteur [❌ Déchargé]"
            return String.format(Lang.localizableString("beamer_discharged_format"), vBase);
        }
    }

    /**
     * Format: Beamer{type=BEAMER,charged=true,room=room_sas}
     * ou: Beamer{type=BEAMER,charged=false}
     */
    @Override
    public String toString() {
        if (this.aIsCharged && this.aMemorizedRoom != null) {
            return String.format("Beamer{type=%s,charged=true,room=%s}",
                    getType().name(), this.aMemorizedRoom.getRoomKey());
        } else {
            return String.format("Beamer{type=%s,charged=false}", getType().name());
        }
    }

    /**
     * Force le chargement du beamer (pour la restauration).
     * 
     * @param pRoom La pièce mémorisée
     */
    public void forceCharge(final Room pRoom) {
        this.aMemorizedRoom = pRoom;
        this.aIsCharged = true;
    }
}
