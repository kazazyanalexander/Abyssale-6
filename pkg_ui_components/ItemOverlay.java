package pkg_ui_components;

import javax.swing.ImageIcon;

/**
 * ItemOverlay - Overlay représentant un item.
 * 
 * Cette classe gère l'affichage des items sur le panneau de transition.
 * Les items peuvent être soit des items de la pièce (affichés en haut
 * au centre), soit des items de l'inventaire (affichés en bas à gauche).
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class ItemOverlay extends BaseOverlay {

    /** Type d'item overlay (ROOM_ITEM ou INVENTORY). */
    private final ItemOverlayType aType;

    /**
     * Constructeur d'un overlay d'item.
     * 
     * @param pIcon    L'icône de l'item
     * @param pItemKey La clé d'identification de l'item
     * @param pType    Le type d'item overlay
     * @param pSize    La taille de l'overlay en pixels (carré)
     */
    public ItemOverlay(final ImageIcon pIcon, final String pItemKey,
            final ItemOverlayType pType, final int pSize) {
        super(pIcon, pItemKey, pSize, pSize);
        this.aType = pType;
    }

    /**
     * Retourne le type d'item overlay.
     * 
     * @return Le type (ROOM_ITEM ou INVENTORY)
     */
    public ItemOverlayType getType() {
        return this.aType;
    }

    /**
     * Retourne une description textuelle de l'overlay.
     * 
     * @return La description de l'item
     */
    @Override
    public String toString() {
        return "ItemOverlay{name=" + this.aNameKey + ", type=" + this.aType +
                ", pos=(" + this.aX + "," + this.aY + ")}";
    }

    /**
     * Énumération des types d'item overlay.
     */
    public enum ItemOverlayType {
        /** Item présent dans la pièce (affiché en haut au centre). */
        ROOM_ITEM,
        /** Item présent dans l'inventaire (affiché en bas à gauche). */
        INVENTORY
    }
}