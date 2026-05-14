package pkg_ui_components;

import pkg_items.Item;
import pkg_utility.Lang;

/**
 * Wrapper pour afficher un objet dans la liste.
 */
public class ItemDisplayWrapper {
    /** L'objet associé au wrapper */
    private final Item aItem;

    /**
     * Constructeur du wrapper.
     * 
     * @param pItem L'objet à afficher
     */
    public ItemDisplayWrapper(Item pItem) {
        this.aItem = pItem;
    }

    /**
     * Accesseur pour l'objet associé au wrapper.
     * 
     * @return L'objet associé au wrapper
     */
    public Item getItem() {
        return this.aItem;
    }

    /**
     * Retourne une représentation textuelle de l'objet pour l'affichage dans la
     * liste.
     * 
     * @return Une représentation textuelle de l'objet
     */
    @Override
    public String toString() {
        return Lang.localizableString(this.aItem.getName())
                + String.format(" (%.1f kg)", this.aItem.getWeight() / 1000.0);
    }
}