package pkg_characters;

import pkg_gameplay.Room;
import pkg_items.Item;
import pkg_items.ItemType;
import pkg_utility.Lang;

import java.util.HashMap;
import java.util.Map;

/**
 * Character - Représente un personnage non-joueur (PNJ) dans le jeu.
 * Les personnages peuvent parler, interagir avec le joueur et réagir à certains
 * objets. Ils peuvent également échanger des objets avec le joueur.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class Character extends Entity {
    /** Version de serialisation automatique */
    private static final long serialVersionUID = 1L;
    /** Message de bienvenue (clé de localisation) */
    private String aGreetingKey;
    /** Map objet -> réponse (clés de localisation) */
    private Map<String, String> aItemResponses;
    /** Pour les messages uniques */
    private boolean aHasSpoken;
    /** Objet requis pour obtenir de l'aide */
    private Item aRequiredItem;
    /** Message d'aide (clé de localisation) */
    private String aHelpMessageKey;
    /** Si l'aide a déjà été donnée */
    private boolean aHelpGiven;

    // ===== Gestion des échanges d'objets =====
    /** Map objet donné -> objet reçu en échange */
    private Map<ItemType, Item> aExchangeItems;
    /** Message lors de l'échange (clé de localisation) */
    private String aExchangeMessageKey;

    /**
     * Constructeur de base d'un personnage.
     * 
     * @param pNameKey        Clé pour le nom localisé
     * @param pDescriptionKey Clé pour la description
     * @param pCurrentRoom    Pièce où se trouve le personnage
     */
    public Character(final String pNameKey, final String pDescriptionKey, final Room pCurrentRoom) {
        super(pNameKey, pDescriptionKey, pCurrentRoom);
        this.aItemResponses = new HashMap<>();
        this.aExchangeItems = new HashMap<>();
        this.aHasSpoken = false;
        this.aHelpGiven = false;

        if (pCurrentRoom != null) {
            pCurrentRoom.addEntity(this);
        }
    }

    /**
     * Définit le message de bienvenue du personnage.
     * 
     * @param pGreetingKey Clé du message de bienvenue
     * @return this (pour chaînage)
     */
    public Character setGreeting(final String pGreetingKey) {
        this.aGreetingKey = pGreetingKey;
        return this;
    }

    /**
     * Retourne la clé du message de bienvenue du personnage.
     * 
     * @return La clé du message de bienvenue
     */
    public String getGreetingKey() {
        return this.aGreetingKey;
    }

    /**
     * Ajoute une réponse spécifique pour un objet donné.
     * 
     * @param pItem        L'objet auquel réagir
     * @param pResponseKey Clé de la réponse
     * @return this (pour chaînage)
     */
    public Character addItemResponse(final Item pItem, final String pResponseKey) {
        this.aItemResponses.put(pItem.getName(), pResponseKey);
        return this;
    }

    /**
     * Définit l'objet requis pour obtenir de l'aide.
     * 
     * @param pItem           L'objet requis
     * @param pHelpMessageKey Clé du message d'aide
     * @return this (pour chaînage)
     */
    public Character setHelpItem(final Item pItem, final String pHelpMessageKey) {
        this.aRequiredItem = pItem;
        this.aHelpMessageKey = pHelpMessageKey;
        return this;
    }

    /**
     * Retourne le nom localisé du personnage.
     * 
     * @return Le nom localisé du personnage
     */
    private String getLocalisedName() {
        return Lang.localizableString(this.aNameKey);
    }

    /**
     * Retourne la description localisée du personnage.
     * 
     * @return La description localisée du personnage
     */
    @Override
    public String getDescription() {
        return Lang.localizableString(this.aDescriptionKey);
    }

    /**
     * Override de getFullDescription pour inclure le message de bienvenue (une
     * seule fois) et les autres éléments de description du personnage.
     * Format du message de description :
     * [Description de base du personnage]
     * [Message de bienvenue, si défini et pas encore affiché]
     * 
     * @return La description du personnage
     */
    @Override
    public String getFullDescription() {
        String vDesc = String.format(Lang.localizableString("character_description_format"),
                getLocalisedName(),
                getDescription());

        if (this.aGreetingKey != null && !this.aHasSpoken) {
            vDesc += String.format(Lang.localizableString("character_greeting_format"),
                    Lang.localizableString(this.aGreetingKey));
            this.aHasSpoken = true;
        }
        return vDesc;
    }

    /**
     * Fait parler le personnage.
     * Si le personnage a un échange possible, le message d'échange est inclus.
     * 
     * @return Le message du personnage
     */
    public String speak() {
        StringBuilder vMessage = new StringBuilder();

        // Message de base (salutation)
        if (this.aGreetingKey != null) {
            vMessage.append(String.format(Lang.localizableString("character_speak_format"),
                    getLocalisedName(),
                    Lang.localizableString(this.aGreetingKey)));
        } else {
            vMessage.append(String.format(Lang.localizableString("character_speak_no_greeting_format"),
                    getLocalisedName()));
        }

        // Ajouter le message d'échange si disponible
        if (this.aExchangeMessageKey != null && !this.aExchangeItems.isEmpty()) {
            vMessage.append("\n").append(String.format(Lang.localizableString("character_exchange_offer_format"),
                    getLocalisedName(),
                    Lang.localizableString(this.aExchangeMessageKey)));
        }

        return vMessage.toString();
    }

    // ===== MÉTHODES POUR LES ÉCHANGES =====

    /**
     * Ajoute un échange d'objet : le personnage donne un objet en échange d'un
     * autre.
     * 
     * @param pGivenItemType      Le type d'objet que le joueur doit donner
     * @param pReceivedItem       L'objet que le personnage donne en échange
     * @param pExchangeMessageKey Clé du message d'échange
     * @return this (pour chaînage)
     */
    public Character addExchange(final ItemType pGivenItemType, final Item pReceivedItem,
            final String pExchangeMessageKey) {
        this.aExchangeItems.put(pGivenItemType, pReceivedItem);
        this.aExchangeMessageKey = pExchangeMessageKey;
        return this;
    }

    /**
     * Vérifie si le personnage accepte un échange pour l'objet donné.
     * 
     * @param pGivenItem L'objet que le joueur veut donner
     * @return true si le personnage accepte l'échange
     */
    public boolean acceptsExchange(final Item pGivenItem) {
        return pGivenItem != null && this.aExchangeItems.containsKey(pGivenItem.getType());
    }

    /**
     * Effectue un échange : le joueur donne un objet, le personnage en donne un
     * autre.
     * 
     * @param pGivenItem L'objet donné par le joueur
     * @return L'objet reçu en échange, ou null si l'échange n'est pas possible
     */
    public Item performExchange(final Item pGivenItem) {
        if (pGivenItem == null)
            return null;

        Item vReceivedItem = this.aExchangeItems.get(pGivenItem.getType());
        if (vReceivedItem != null) {
            // Créer une copie de l'objet à donner
            return vReceivedItem.createCopy();
        }
        return null;
    }

    /**
     * Réagit à un objet donné par le joueur.
     * 
     * @param pItem L'objet donné
     * @return La réponse du personnage, ou null si pas de réaction
     */
    public String reactToItem(final Item pItem) {
        if (pItem == null)
            return null;

        // Vérifier d'abord les échanges
        if (acceptsExchange(pItem)) {
            return String.format(Lang.localizableString("character_exchange_offer_format"),
                    getLocalisedName(),
                    Lang.localizableString(this.aExchangeMessageKey));
        }

        // Vérifier les réponses spécifiques
        String vResponseKey = this.aItemResponses.get(pItem.getName());
        if (vResponseKey != null) {
            return String.format(Lang.localizableString("character_response_format"),
                    getLocalisedName(),
                    Lang.localizableString(vResponseKey));
        }

        // Vérifier si c'est l'objet requis pour l'aide
        if (!this.aHelpGiven && this.aRequiredItem != null &&
                this.aRequiredItem.getType() == pItem.getType()) {
            this.aHelpGiven = true;
            return String.format(Lang.localizableString("character_help_format"),
                    getLocalisedName(),
                    Lang.localizableString(this.aHelpMessageKey));
        }

        return null;
    }

    /**
     * Retourne l'objet requis pour obtenir de l'aide.
     * 
     * @return L'objet requis pour l'aide
     */
    public Item getRequiredItem() {
        return this.aRequiredItem;
    }

    /**
     * Définit si l'aide a déjà été donnée (pour la restauration).
     * 
     * @param pHelpGiven Si l'aide a déjà été donnée
     */
    public void setHelpGiven(boolean pHelpGiven) {
        this.aHelpGiven = pHelpGiven;
    }

    /**
     * Retourne la map des réponses spécifiques aux objets.
     * 
     * @return La map des objets d'échange
     */
    public Map<ItemType, Item> getExchangeItems() {
        return new HashMap<>(this.aExchangeItems);
    }

    /**
     * Format: Character{nameKey=character_scientist,currentRoom=room_labo,
     * greeting=character_scientist_greeting,hasSpoken=false,
     * requiredItem=BEAMER,helpGiven=false, exchanges={BLUE_CARD:MAGIC_COOKIE}}
     * 
     * @return Une représentation textuelle du personnage
     */
    @Override
    public String toString() {
        StringBuilder vResult = new StringBuilder();
        vResult.append("Character{")
                .append("nameKey=").append(this.aNameKey)
                .append(",currentRoom=").append(this.aCurrentRoom != null ? this.aCurrentRoom.getRoomKey() : "null")
                .append(",greeting=").append(this.aGreetingKey)
                .append(",hasSpoken=").append(this.aHasSpoken);

        if (this.aRequiredItem != null) {
            vResult.append(",requiredItem=").append(this.aRequiredItem.getType().name());
        }
        vResult.append(",helpGiven=").append(this.aHelpGiven);

        // Réponses
        if (!this.aItemResponses.isEmpty()) {
            vResult.append(",responses={");
            boolean vFirst = true;
            for (Map.Entry<String, String> vEntry : this.aItemResponses.entrySet()) {
                if (!vFirst)
                    vResult.append(",");
                vResult.append(vEntry.getKey()).append(":").append(vEntry.getValue());
                vFirst = false;
            }
            vResult.append("}");
        }

        // Échanges
        if (!this.aExchangeItems.isEmpty()) {
            vResult.append(",exchanges={");
            boolean vFirst = true;
            for (Map.Entry<ItemType, Item> vEntry : this.aExchangeItems.entrySet()) {
                if (!vFirst)
                    vResult.append(",");
                vResult.append(vEntry.getKey().name()).append(":").append(vEntry.getValue().getType().name());
                vFirst = false;
            }
            vResult.append("}");
        }

        vResult.append("}");
        return vResult.toString();
    }
}