package pkg_ui_components;

import java.util.ArrayList;
import java.util.List;

/**
 * OverlayManager - Gestionnaire des overlays pour le panneau de transition.
 * 
 * Cette classe centralise la gestion des trois types d'overlays :
 * <ul>
 * <li>Personnages (CharacterOverlay)</li>
 * <li>Items de la pièce (ItemOverlay de type ROOM_ITEM)</li>
 * <li>Items de l'inventaire (ItemOverlay de type INVENTORY)</li>
 * </ul>
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class OverlayManager {

    /** Liste des overlays de personnages actuellement affichés. */
    private List<CharacterOverlay> aCharacterOverlays;

    /** Liste des overlays de personnages en attente d'apparition. */
    private List<CharacterOverlay> aPendingCharacterOverlays;

    /** Liste des overlays d'items de la pièce actuellement affichés. */
    private List<ItemOverlay> aRoomItemOverlays;

    /** Liste des overlays d'items de la pièce en attente d'apparition. */
    private List<ItemOverlay> aPendingRoomItemOverlays;

    /** Liste des overlays d'items de l'inventaire actuellement affichés. */
    private List<ItemOverlay> aInventoryOverlays;

    /** Liste des overlays d'items de l'inventaire en attente d'apparition. */
    private List<ItemOverlay> aPendingInventoryOverlays;

    /**
     * Constructeur du gestionnaire d'overlays.
     */
    public OverlayManager() {
        this.aCharacterOverlays = new ArrayList<>();
        this.aRoomItemOverlays = new ArrayList<>();
        this.aInventoryOverlays = new ArrayList<>();
        this.aPendingCharacterOverlays = new ArrayList<>();
        this.aPendingRoomItemOverlays = new ArrayList<>();
        this.aPendingInventoryOverlays = new ArrayList<>();
    }

    // ===== GESTION DES PERSONNAGES =====

    /**
     * Ajoute un overlay de personnage.
     * 
     * @param pOverlay L'overlay à ajouter
     * @param pPending true pour ajouter à la file d'attente (après transition)
     */
    public void addCharacterOverlay(CharacterOverlay pOverlay, boolean pPending) {
        if (pPending) {
            this.aPendingCharacterOverlays.add(pOverlay);
        } else {
            this.aCharacterOverlays.add(pOverlay);
        }
    }

    /**
     * Supprime un overlay de personnage par sa clé.
     * 
     * @param pNameKey La clé du personnage
     */
    public void removeCharacterOverlay(String pNameKey) {
        this.aCharacterOverlays.removeIf(o -> o.getNameKey().equals(pNameKey));
        this.aPendingCharacterOverlays.removeIf(o -> o.getNameKey().equals(pNameKey));
    }

    /**
     * Supprime tous les overlays de personnages.
     */
    public void clearCharacterOverlays() {
        this.aCharacterOverlays.clear();
        this.aPendingCharacterOverlays.clear();
    }

    /**
     * Retourne la liste des overlays de personnages.
     * 
     * @return La liste des personnages
     */
    public List<CharacterOverlay> getCharacterOverlays() {
        return new ArrayList<>(this.aCharacterOverlays);
    }

    // ===== GESTION DES ITEMS DE LA PIÈCE =====

    /**
     * Ajoute un overlay d'item de la pièce.
     * 
     * @param pOverlay L'overlay à ajouter
     * @param pPending true pour ajouter à la file d'attente (après transition)
     */
    public void addRoomItemOverlay(ItemOverlay pOverlay, boolean pPending) {
        if (pPending) {
            this.aPendingRoomItemOverlays.add(pOverlay);
        } else {
            this.aRoomItemOverlays.add(pOverlay);
        }
    }

    /**
     * Supprime un overlay d'item de la pièce par sa clé.
     * 
     * @param pItemKey La clé de l'item
     */
    public void removeRoomItemOverlay(String pItemKey) {
        this.aRoomItemOverlays.removeIf(o -> o.getNameKey().equals(pItemKey));
        this.aPendingRoomItemOverlays.removeIf(o -> o.getNameKey().equals(pItemKey));
    }

    /**
     * Supprime tous les overlays d'items de la pièce.
     */
    public void clearRoomItemOverlays() {
        this.aRoomItemOverlays.clear();
        this.aPendingRoomItemOverlays.clear();
    }

    /**
     * Retourne la liste des overlays d'items de la pièce.
     * 
     * @return La liste des items de la pièce
     */
    public List<ItemOverlay> getRoomItemOverlays() {
        return new ArrayList<>(this.aRoomItemOverlays);
    }

    // ===== GESTION DE L'INVENTAIRE =====

    /**
     * Ajoute un overlay d'item de l'inventaire.
     * 
     * @param pOverlay L'overlay à ajouter
     * @param pPending true pour ajouter à la file d'attente (après transition)
     */
    public void addInventoryOverlay(ItemOverlay pOverlay, boolean pPending) {
        if (pPending) {
            this.aPendingInventoryOverlays.add(pOverlay);
        } else {
            this.aInventoryOverlays.add(pOverlay);
        }
    }

    /**
     * Supprime un overlay d'item de l'inventaire par sa clé.
     * 
     * @param pItemKey La clé de l'item
     */
    public void removeInventoryOverlay(String pItemKey) {
        this.aInventoryOverlays.removeIf(o -> o.getNameKey().equals(pItemKey));
        this.aPendingInventoryOverlays.removeIf(o -> o.getNameKey().equals(pItemKey));
    }

    /**
     * Supprime tous les overlays d'items de l'inventaire.
     */
    public void clearInventoryOverlays() {
        this.aInventoryOverlays.clear();
        this.aPendingInventoryOverlays.clear();
    }

    /**
     * Retourne la liste des overlays d'items de l'inventaire.
     * 
     * @return La liste des items de l'inventaire
     */
    public List<ItemOverlay> getInventoryOverlays() {
        return new ArrayList<>(this.aInventoryOverlays);
    }

    // ===== NETTOYAGE GÉNÉRAL =====

    /**
     * Supprime tous les overlays (personnages et items).
     */
    public void clearAll() {
        this.aCharacterOverlays.clear();
        this.aRoomItemOverlays.clear();
        this.aInventoryOverlays.clear();
        this.aPendingCharacterOverlays.clear();
        this.aPendingRoomItemOverlays.clear();
        this.aPendingInventoryOverlays.clear();
    }

    /**
     * Transfère les overlays en attente vers les listes actives.
     */
    public void commitPending() {
        this.aCharacterOverlays.addAll(this.aPendingCharacterOverlays);
        this.aPendingCharacterOverlays.clear();

        this.aRoomItemOverlays.addAll(this.aPendingRoomItemOverlays);
        this.aPendingRoomItemOverlays.clear();

        this.aInventoryOverlays.addAll(this.aPendingInventoryOverlays);
        this.aPendingInventoryOverlays.clear();
    }

}