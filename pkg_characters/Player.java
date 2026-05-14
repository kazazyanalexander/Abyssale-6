package pkg_characters;

import pkg_core.GameEngine;
import pkg_gameplay.Room;
import pkg_exceptions.GameException;
import pkg_exceptions.ItemNotFoundException;
import pkg_exceptions.ItemNotPickableException;
import pkg_exceptions.ItemTooHeavyException;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import pkg_items.Beamer;
import pkg_items.Item;
import pkg_items.ItemList;
import pkg_utility.Lang;

/**
 * Classe représentant le joueur dans le jeu "Station Abyssale-6".
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class Player extends Entity {
    /** Version de serialisation automatique */
    private static final long serialVersionUID = 1L;
    /** Inventaire du joueur */
    private ItemList aInventory;
    /** Poids maximal du joueur */
    private int aMaxWeight;
    /** Poids actuel du joueur */
    private int aCurrentWeight;
    /** Historique des pièces visitées */
    private Stack<Room> aRoomHistory;
    /** Référence au moteur de jeu */
    private GameEngine aGameEngine;

    /**
     * Constructeur de la classe Player.
     * 
     * @param pStartingRoom La pièce où le joueur commence
     */
    public Player(final Room pStartingRoom) {
        super("player_name", "player_description", pStartingRoom);
        this.aInventory = new ItemList();
        this.aMaxWeight = 13000;
        this.aCurrentWeight = 0;
        this.aRoomHistory = new Stack<>();
        this.aRoomHistory.push(pStartingRoom);

        if (pStartingRoom != null) {
            pStartingRoom.addEntity(this);
        }
    }

    /**
     * Définit la référence au moteur de jeu.
     * 
     * @param pEngine Le moteur de jeu
     */
    public void setGameEngine(final GameEngine pEngine) {
        this.aGameEngine = pEngine;
    }

    /**
     * Retourne une copie de l'historique des pièces visitées par le joueur.
     * 
     * @return L'historique des pièces (copie)
     */
    public List<Room> getHistory() {
        return new ArrayList<>(this.aRoomHistory);
    }

    /**
     * Retourne la description complète du joueur, incluant son nom, sa description
     * et
     * son poids actuel et maximal.
     * 
     * @return La description complète du joueur
     */
    @Override
    public String getFullDescription() {
        return Lang.localizableString(getName()) + " - " + getDescription() +
                "\n" + getTotalWeighString();
    }

    /**
     * Retourne une chaîne de caractères indiquant le poids actuel et maximal du
     * joueur.
     * Format : "Poids : X kg / Y kg"
     * 
     * @return La chaîne de caractères représentant le poids actuel et maximal du
     *         joueur.
     */
    public String getTotalWeighString() {
        return String.format(Lang.localizableString("total_weight"),
                this.aCurrentWeight / 1000.0, this.aMaxWeight / 1000.0);
    }

    /**
     * Ajoute la pièce actuelle à l'historique.
     */
    public void pushHistory() {
        this.aRoomHistory.push(this.aCurrentRoom);
    }

    /**
     * Retourne à la pièce précédente.
     * 
     * @return true si le retour a réussi, false sinon (par exemple si l'historique
     *         est vide ou si la pièce précédente n'est pas accessible depuis la
     *         pièce actuelle)
     */
    public boolean goBack() {
        if (this.aRoomHistory.isEmpty())
            return false;

        Room vPreviousRoom = this.aRoomHistory.peek();
        if (!this.aCurrentRoom.isExit(vPreviousRoom)) {
            return false;
        }

        this.aCurrentRoom = this.aRoomHistory.pop();
        return true;
    }

    /**
     * Retourne la pièce précédente.
     * 
     * @return La pièce précédente
     */
    public Room getPreviousRoom() {
        if (this.aRoomHistory.isEmpty())
            return null;
        return this.aRoomHistory.peek();
    }

    /**
     * Tente de ramasser un objet dans la pièce actuelle.
     * 
     * @param pItemName Le nom de l'objet à ramasser
     * @return L'objet ramassé
     * @throws GameException si l'objet ne peut pas être pris
     */
    public Item takeItem(String pItemName) throws GameException {
        Item vItem = this.aCurrentRoom.removeItem(pItemName);

        if (vItem == null) {
            throw new ItemNotFoundException();
        }

        if (!vItem.canBePickedUp()) {
            this.aCurrentRoom.addItem(vItem);
            throw new ItemNotPickableException();
        }

        if (this.aCurrentWeight + vItem.getWeight() > this.aMaxWeight) {
            this.aCurrentRoom.addItem(vItem);
            throw new ItemTooHeavyException();
        }

        this.aInventory.addItem(vItem);
        this.aCurrentWeight += vItem.getWeight();

        return vItem;
    }

    /**
     * Tente de déposer un objet de l'inventaire dans la pièce actuelle.
     * 
     * @param pItemName Le nom de l'objet à déposer
     * @return L'objet déposé, ou null si l'opération a échoué
     */
    public Item dropItem(String pItemName) {
        Item vItem = this.aInventory.removeItem(pItemName);

        if (vItem == null) {
            return null; // Objet non trouvé dans l'inventaire
        }

        this.aCurrentWeight -= vItem.getWeight();
        this.aCurrentRoom.addItem(vItem);

        return vItem;
    }

    /**
     * Tente de manger un objet de l'inventaire (le retirer sans le déposer dans la
     * pièce).
     * 
     * @param pItemName Le nom de l'objet à manger
     * @return L'objet mangé, ou null si l'opération a échoué
     */
    public Item eatItem(String pItemName) {
        Item vItem = this.aInventory.removeItem(pItemName);

        if (vItem == null) {
            return null; // Objet non trouvé dans l'inventaire
        }

        this.aCurrentWeight -= vItem.getWeight();

        return vItem;
    }

    /**
     * Retourne la description de l'inventaire.
     * 
     * @return La description de l'inventaire
     */
    public String getInventoryString() {
        if (this.aInventory.isEmpty()) {
            return Lang.localizableString("inventory_empty");
        }

        String vResult = this.aInventory.getDescription(Lang.localizableString("inventory"));
        vResult += "\n" + getTotalWeighString();
        return vResult;
    }

    // Getters et setters
    /**
     * Retourne le poids actuel du joueur.
     * 
     * @return Le poids actuel du joueur
     */
    public int getCurrentWeight() {
        return this.aCurrentWeight;
    }

    /**
     * Définit le poids actuel du joueur (pour la restauration).
     * 
     * @param pCurrentWeight Le nouveau poids actuel en grammes
     */
    public void setCurrentWeight(int pCurrentWeight) {
        this.aCurrentWeight = pCurrentWeight;
    }

    /**
     * Retourne le poids maximal que le joueur peut porter.
     * 
     * @return Le poids maximal du joueur
     */
    public int getMaxWeight() {
        return this.aMaxWeight;
    }

    /**
     * Définit le poids maximal que le joueur peut porter.
     * 
     * @param pMaxWeight Le poids maximal du joueur
     */
    public void setMaxWeight(final int pMaxWeight) {
        this.aMaxWeight = pMaxWeight;
    }

    /**
     * Retourne l'inventaire du joueur.
     * 
     * @return L'inventaire du joueur
     */
    public ItemList getInventory() {
        return this.aInventory;
    }

    /**
     * Format: Player{currentRoom=room_sas,inventory=[...],maxWeight=21000,
     * currentWeight=1500,history=[room_sas,room_airlock]}
     * 
     * @return La chaîne de caractères représentant le joueur
     */
    @Override
    public String toString() {
        StringBuilder vResult = new StringBuilder();
        vResult.append("Player{")
                .append("currentRoom=").append(this.aCurrentRoom != null ? this.aCurrentRoom.getRoomKey() : "null")
                .append(",inventory=").append(this.aInventory.toString())
                .append(",maxWeight=").append(this.aMaxWeight)
                .append(",currentWeight=").append(this.aCurrentWeight)
                .append(",history=[");

        // Exporter l'historique (sans modifier la pile)
        Stack<Room> vTemp = new Stack<>();
        vTemp.addAll(this.aRoomHistory);

        boolean vFirst = true;
        while (!vTemp.isEmpty()) {
            if (!vFirst)
                vResult.append(",");
            vResult.append(vTemp.remove(0).getRoomKey());
            vFirst = false;
        }

        vResult.append("]}");
        return vResult.toString();
    }

    /**
     * Ajoute une pièce à l'historique sans vérification (pour la restauration).
     * 
     * @param pRoom La pièce à ajouter à l'historique
     */
    public void pushHistory(final Room pRoom) {
        this.aRoomHistory.push(pRoom);
    }

    /**
     * Retourne la taille de l'historique.
     * 
     * @return La taille de l'historique
     */
    public int getHistorySize() {
        return this.aRoomHistory.size();
    }

    /**
     * Vérifie si un objet est présent dans l'inventaire du joueur.
     *
     * @param pItemName Nom de l'objet à rechercher
     * @return true si l'objet est présent dans l'inventaire, false sinon
     */
    public boolean hasItem(final String pItemName) {
        return this.aInventory.containsItem(pItemName);
    }

    /**
     * Effectue un échange d'objets avec un personnage.
     * Retire l'objet donné de l'inventaire et ajoute l'objet reçu.
     * Met à jour le poids correctement.
     * Lance une exception si l'objet reçu est trop lourd.
     * 
     * @param pGivenItem    L'objet donné
     * @param pReceivedItem L'objet reçu
     * @return true si l'échange a réussi
     * @throws ItemTooHeavyException si l'objet reçu est trop lourd
     */
    public boolean exchangeItems(final Item pGivenItem, final Item pReceivedItem) throws ItemTooHeavyException {
        if (pGivenItem == null || pReceivedItem == null) {
            return false;
        }

        // Vérifier que l'objet donné est bien dans l'inventaire
        Item vItemInInventory = this.aInventory.getItem(pGivenItem.getName());
        if (vItemInInventory == null) {
            return false;
        }

        // Vérifier que l'objet reçu n'est pas trop lourd
        int vNewWeight = this.aCurrentWeight - pGivenItem.getWeight() + pReceivedItem.getWeight();
        if (vNewWeight > this.aMaxWeight) {
            throw new ItemTooHeavyException(); // Trop lourd après échange
        }

        // Retirer l'objet donné
        this.aInventory.removeItem(pGivenItem.getName());
        this.aCurrentWeight -= pGivenItem.getWeight();

        // Ajouter l'objet reçu
        this.aInventory.addItem(pReceivedItem);
        this.aCurrentWeight += pReceivedItem.getWeight();

        // Notifier les changements
        if (this.aGameEngine != null) {
            this.aGameEngine.refreshInventory();
        }

        return true;
    }

    /**
     * Cherche un beamer dans l'inventaire du joueur.
     * 
     * @return Le beamer trouvé, ou null s'il n'y en a pas
     */
    public Beamer findBeamerInInventory() {
        for (Item vItem : this.getInventory().getItems()) {
            if (vItem instanceof Beamer) {
                return (Beamer) vItem;
            }
        }
        return null;
    }
}