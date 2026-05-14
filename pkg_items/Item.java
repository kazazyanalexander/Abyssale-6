package pkg_items;

import java.io.Serializable;

/**
 * Item - Base de tous les objets concrets.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public abstract class Item implements Serializable {
    /** Version de serialisation automatique */
    private static final long serialVersionUID = 1L;
    /** Type de l'objet */
    private final ItemType aType;

    /**
     * Crée un nouvel objet de type Item.
     * 
     * @param pType Le type de l'objet
     */
    protected Item(final ItemType pType) {
        this.aType = pType;
    }

    /**
     * Retourne le type de l'objet.
     * 
     * @return Le type de l'objet
     */
    public ItemType getType() {
        return this.aType;
    }

    /**
     * Retourne le nom de l'objet.
     * 
     * @return Le nom de l'objet
     */
    public String getName() {
        return this.aType.getNameKey();
    }

    /**
     * Retourne la description de l'objet.
     *
     * @return Le nom de l'image de l'objet
     */
    public String getImageName() {
        return this.aType.getImageName();
    }

    /**
     * Retourne le poids de l'objet en grammes.
     *
     * @return Le poids de l'objet
     */
    public int getWeight() {
        return this.aType.getWeight();
    }

    /**
     * Crée une copie manuelle de l'item.
     * À redéfinir dans les classes filles.
     * 
     * @return Une copie de l'item
     */
    public Item createCopy() {
        // Par défaut, utiliser la factory
        return this.aType.createItem();
    }

    /**
     * Retourne true si l'objet peut être transporté, false sinon.
     *
     * @return true si l'objet peut être transporté, false sinon
     */
    public boolean canBePickedUp() {
        return this.aType.canBePickedUp();
    }

    /**
     * Retourne true si l'objet est utilisable, false sinon.
     *
     * @return true si l'objet est utilisable, false sinon
     */
    public boolean isUsable() {
        return this.aType.isUsable();
    }

    /**
     * Retourne une description de l'objet.
     *
     * @return Une description de l'objet
     */
    public abstract String getInformation();

    /**
     * Format de base: Item{type=BEAMER}
     * Les sous-classes ajoutent leurs propres attributs
     * 
     * @return Une représentation textuelle de l'item
     */
    @Override
    public String toString() {
        return String.format("Item{type=%s}", aType.name());
    }
}
