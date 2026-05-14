package pkg_gameplay;

import java.io.Serializable;
import pkg_items.Item;
import pkg_items.ItemType;
import pkg_utility.Lang;

/**
 * Door - Représente une porte qui peut être verrouillée ou déverrouillée.
 * Une porte peut être franchie seulement si elle est déverrouillée.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class Door implements Serializable {
    /** Version de serialisation automatique */
    private static final long serialVersionUID = 1L;
    /** Identifiant unique de la porte */
    private final String aDoorId;
    /** Type de clé requis pour ouvrir/fermer la porte */
    private final ItemType aRequiredKey;
    /** État de verrouillage */
    private boolean aIsLocked;
    /** Si true, se verrouille automatiquement après passage */
    private boolean aAutoLock;

    /**
     * Constructeur d'une porte.
     * 
     * @param pDoorId          Identifiant unique de la porte
     * @param pRequiredKey     Type de clé requis
     * @param pInitiallyLocked true si la porte est initialement verrouillée
     * @param pAutoLock        true si la porte se verrouille automatiquement
     */
    public Door(final String pDoorId, final ItemType pRequiredKey,
            final boolean pInitiallyLocked, final boolean pAutoLock) {
        this.aDoorId = pDoorId;
        this.aRequiredKey = pRequiredKey;
        this.aIsLocked = pInitiallyLocked;
        this.aAutoLock = pAutoLock;
    }

    /**
     * Tente d'ouvrir/déverrouiller la porte avec une clé.
     * 
     * @param pKey La clé utilisée
     * @return true si la porte a été déverrouillée, false sinon
     */
    public boolean unlock(final Item pKey) {
        if (pKey != null && pKey.getType() == this.aRequiredKey) {
            this.aIsLocked = false;
            return true;
        }
        return false;
    }

    /**
     * Verrouille la porte (si elle a une clé appropriée).
     * 
     * @param pKey La clé utilisée
     * @return true si la porte a été verrouillée, false sinon
     */
    public boolean lock(final Item pKey) {
        if (pKey != null && pKey.getType() == this.aRequiredKey) {
            this.aIsLocked = true;
            return true;
        }
        return false;
    }

    /**
     * Vérifie si la porte est verrouillée.
     * 
     * @return true si verrouillée, false sinon
     */
    public boolean isLocked() {
        return this.aIsLocked;
    }

    /**
     * Définit l'état de verrouillage de la porte.
     * 
     * @param pLocked true pour verrouiller, false pour déverrouiller
     */
    public void setLocked(boolean pLocked) {
        this.aIsLocked = pLocked;
    }

    /**
     * Retourne l'identifiant de la porte.
     * 
     * @return L'identifiant de la porte
     */
    public String getDoorId() {
        return this.aDoorId;
    }

    /**
     * Retourne le type de clé requis pour cette porte.
     * 
     * @return Le type de clé requis
     */
    public ItemType getRequiredKey() {
        return this.aRequiredKey;
    }

    /**
     * Déclenche le verrouillage automatique si configuré.
     * À appeler après avoir franchi la porte.
     */
    public void afterPassage() {
        if (this.aAutoLock) {
            this.aIsLocked = true;
        }
    }

    /**
     * Retourne une description de l'état de la porte (verrouillée/déverrouillée).
     * 
     * @return Description de l'état de la porte
     */
    public String getStatusDescription() {
        if (this.aIsLocked) {
            return String.format(Lang.localizableString("door_locked"),
                    Lang.localizableString(this.aRequiredKey.getNameKey()));
        } else {
            return Lang.localizableString("door_unlocked");
        }
    }

    /**
     * Format: Door{id=door_serre,key=BLUE_CARD,locked=true,autolock=false}
     * 
     * @return Représentation textuelle de la porte pour le débogage
     */
    @Override
    public String toString() {
        return String.format("Door{id=%s,key=%s,locked=%b,autolock=%b}",
                this.aDoorId, this.aRequiredKey.name(), this.aIsLocked, this.aAutoLock);
    }

    /**
     * Indique si la porte est à verrouillage automatique.
     * 
     * @return true si la porte est à verrouillage automatique
     */
    public boolean isAutoLock() {
        return this.aAutoLock;
    }
}
