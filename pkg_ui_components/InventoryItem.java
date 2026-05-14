package pkg_ui_components;

import pkg_items.Item;

/**
 * Wrapper pour afficher un Item dans la liste avec formatage
 * [x] pour les objets possédés, [ ] pour les objets dans la pièce
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
class InventoryItem {
    /** L'item associé au wrapper */
    private final Item aItem;
    /** Indique si l'item est dans l'inventaire ou dans la pièce */
    private final boolean aOwned;

    /**
     * Constructeur du wrapper.
     * 
     * @param pItem  L'item à afficher
     * @param pOwned true si l'item est dans l'inventaire, false s'il est dans la
     *               pièce
     */
    public InventoryItem(Item pItem, boolean pOwned) {
        this.aItem = pItem;
        this.aOwned = pOwned;
    }

    /**
     * Retourne l'item associé au wrapper
     * 
     * @return L'item associé au wrapper
     */
    public Item getItem() {
        return this.aItem;
    }

    /**
     * Indique si l'item est dans l'inventaire ou dans la pièce
     * 
     * @return true si l'item est dans l'inventaire, false s'il est dans la pièce
     */
    public boolean isOwned() {
        return this.aOwned;
    }

    /**
     * Retourne une représentation textuelle du wrapper
     * 
     * @return Une chaîne de caractères formatée
     */
    @Override
    public String toString() {
        String vCheckbox = this.aOwned ? "[x]" : "[ ]";
        String vName = this.aItem.getInformation();
        String vWeight = String.format("%.1f kg", this.aItem.getWeight() / 1000.0);

        return String.format("%s %-25s %s", vCheckbox, vName, vWeight);
    }
}
