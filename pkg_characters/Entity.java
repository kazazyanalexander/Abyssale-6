package pkg_characters;

import pkg_gameplay.Room;

import java.io.Serializable;
import pkg_utility.Lang;

/**
 * Entity - Classe abstraite représentant une entité dans le jeu.
 * Peut être un joueur, un personnage non-joueur, ou un personnage mobile.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public abstract class Entity implements Serializable {
    /** Version de serialisation automatique */
    private static final long serialVersionUID = 1L;
    /** Clé pour le nom localisé */
    protected final String aNameKey;
    /** Clé pour la description */
    protected final String aDescriptionKey;
    /** Pièce où se trouve l'entité */
    protected Room aCurrentRoom;

    /**
     * Constructeur de base pour une entité.
     * 
     * @param pNameKey        Clé pour le nom localisé
     * @param pDescriptionKey Clé pour la description
     * @param pCurrentRoom    Pièce de départ
     */
    public Entity(final String pNameKey, final String pDescriptionKey, final Room pCurrentRoom) {
        this.aNameKey = pNameKey;
        this.aDescriptionKey = pDescriptionKey;
        this.aCurrentRoom = pCurrentRoom;
    }

    /**
     * Retourne le nom localisé de l'entité.
     * 
     * @return Le nom localisé de l'entité
     */
    public String getName() {
        return this.aNameKey;
    }

    /**
     * Retourne la description localisée de l'entité.
     * 
     * @return La description localisée de l'entité
     */
    public String getDescription() {
        return Lang.localizableString(this.aDescriptionKey);
    }

    /**
     * Retourne une description complète de l'entité, incluant son nom et sa
     * description.
     * 
     * @return La description complète de l'entité
     */
    public abstract String getFullDescription();

    /**
     * Retourne la pièce où se trouve actuellement l'entité.
     * 
     * @return La pièce où se trouve l'entité
     */
    public Room getCurrentRoom() {
        return this.aCurrentRoom;
    }

    /**
     * Déplace l'entité vers une autre pièce.
     * 
     * @param pRoom La nouvelle pièce
     */
    public void setCurrentRoom(final Room pRoom) {
        if (this.aCurrentRoom != null) {
            this.aCurrentRoom.removeEntity(this);
        }
        this.aCurrentRoom = pRoom;
        if (pRoom != null) {
            pRoom.addEntity(this);
        }
    }

    /**
     * Retourne une chaîne de caractères représentant l'entité.
     * 
     * @return Une chaîne de caractères représentant l'entité
     */
    @Override
    public String toString() {
        return String.format("Entity{nameKey=%s}", this.aNameKey);
    }
}
