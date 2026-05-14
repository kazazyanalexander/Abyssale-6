package pkg_items;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Classe ItemList - Gère une collection d'objets (Item) pour une entité du jeu.
 * Cette classe fait partie de l'application "Station Abyssale-6".
 * 
 * Elle permet de mutualiser la gestion des items entre les classes Room et
 * Player,
 * en offrant une interface uniforme pour ajouter, retirer et interroger des
 * items.
 * 
 * La collection interne est encapsulée et n'est pas accessible depuis
 * l'extérieur.
 * L'utilisation d'une Map (TreeMap) permet une recherche efficace des items par
 * leur nom.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class ItemList implements Serializable {
    /** Version de serialisation automatique */
    private static final long serialVersionUID = 1L;
    /** Collection associant un nom d'item à l'item correspondant */
    private Map<String, Item> aItems;

    /**
     * Constructeur par défaut.
     * Initialise une collection vide d'items.
     */
    public ItemList() {
        this.aItems = new TreeMap<>();
    }

    /**
     * Ajoute un item à la collection.
     * 
     * @param pItem L'item à ajouter
     */
    public void addItem(Item pItem) {
        if (pItem != null) {
            this.aItems.put(pItem.getName(), pItem);
        }
    }

    /**
     * Retire et retourne un item de la collection en utilisant son nom.
     * 
     * @param pItemName Le nom de l'item à retirer
     * @return L'item retiré, ou null si aucun item avec ce nom n'est trouvé
     */
    public Item removeItem(String pItemName) {
        return this.aItems.remove(pItemName);
    }

    /**
     * Retire un item spécifique de la collection.
     * 
     * @param pItem L'item à retirer
     * @return true si l'item a été retiré avec succès, false sinon
     */
    public boolean removeItem(Item pItem) {
        if (pItem != null && this.aItems.containsKey(pItem.getName())) {
            return this.aItems.remove(pItem.getName()) != null;
        }
        return false;
    }

    /**
     * Vérifie si la collection contient un item avec le nom donné.
     * 
     * @param pItemName Le nom de l'item à rechercher
     * @return true si un item avec ce nom est présent, false sinon
     */
    public boolean containsItem(String pItemName) {
        return this.aItems.containsKey(pItemName);
    }

    /**
     * Retourne un item sans le retirer de la collection.
     * 
     * @param pItemName Le nom de l'item à obtenir
     * @return L'item correspondant au nom, ou null s'il n'existe pas
     */
    public Item getItem(String pItemName) {
        return this.aItems.get(pItemName);
    }

    /**
     * Retourne une liste de tous les items présents dans la collection.
     * Cette méthode est utile pour itérer sur tous les items sans exposer
     * directement la structure interne de la collection.
     * 
     * @return Une nouvelle liste contenant tous les items de la collection
     */
    public List<Item> getItems() {
        return new ArrayList<>(this.aItems.values());
    }

    /**
     * Retourne le nombre d'items dans la collection.
     * 
     * @return La taille de la collection
     */
    public int size() {
        return this.aItems.size();
    }

    /**
     * Vérifie si la collection est vide.
     * 
     * @return true si la collection ne contient aucun item, false sinon
     */
    public boolean isEmpty() {
        return this.aItems.isEmpty();
    }

    /**
     * Retourne une description textuelle de tous les items de la collection.
     * 
     * @param pPrefix Le préfixe à ajouter avant la liste des items
     * @return Une chaîne formatée décrivant tous les items
     */
    public String getDescription(String pPrefix) {
        if (this.aItems.isEmpty()) {
            return "";
        }

        StringBuilder vResult = new StringBuilder(pPrefix);
        for (Item vItem : this.aItems.values()) {
            vResult.append(" ").append(vItem.getInformation());
        }
        return vResult.toString();
    }

    /**
     * Vide complètement la collection.
     */
    public void clear() {
        this.aItems.clear();
    }

    /**
     * Format: [Item{type=BEAMER,charged=false}, Item{type=WRENCH}]
     */
    @Override
    public String toString() {
        StringBuilder vResult = new StringBuilder("[");
        boolean vFirst = true;
        for (Item vItem : this.aItems.values()) {
            if (!vFirst) {
                vResult.append(", ");
            }
            vResult.append(vItem.toString());
            vFirst = false;
        }
        vResult.append("]");
        return vResult.toString();
    }

}
