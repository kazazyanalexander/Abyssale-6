package pkg_characters;

import pkg_gameplay.Room;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import pkg_items.Item;
import pkg_utility.Direction;
import pkg_utility.Lang;

/**
 * MovingCharacter - Personnage non-joueur capable de se déplacer entre les
 * pièces.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class MovingCharacter extends Character {
    /** Version de serialisation automatique */
    private static final long serialVersionUID = 1L;
    /** Générateur de nombres aléatoires */
    private static final Random RANDOM = new Random();

    /** Chemin prédéfini (optionnel) */
    private List<Room> aPath;
    /** Index actuel dans le chemin */
    private int aCurrentPathIndex;
    /** Stratégie de déplacement */
    private MovementStrategy aStrategy;

    // ===== Attributs pour FOLLOW_PLAYER =====
    /** Joueur à suivre */
    private Player aTargetPlayer;

    /**
     * Stratégies de déplacement possibles.
     */
    public enum MovementStrategy {
        /** Déplacement aléatoire vers une sortie */
        RANDOM,
        /** Suit un chemin prédéfini */
        FOLLOW_PATH,
        /** Suit le joueur */
        FOLLOW_PLAYER
    }

    /**
     * Constructeur d'un personnage mobile avec stratégie par défaut (RANDOM).
     * 
     * @param pNameKey        Clé pour le nom localisé
     * @param pDescriptionKey Clé pour la description
     * @param pCurrentRoom    Pièce de départ
     */
    public MovingCharacter(final String pNameKey, final String pDescriptionKey, final Room pCurrentRoom) {
        super(pNameKey, pDescriptionKey, pCurrentRoom);
        this.aStrategy = MovementStrategy.RANDOM;
        this.aPath = new ArrayList<>();
        this.aCurrentPathIndex = 0;
    }

    /**
     * Constructeur avec stratégie spécifique.
     * 
     * @param pNameKey        Clé pour le nom localisé
     * @param pDescriptionKey Clé pour la description
     * @param pCurrentRoom    Pièce de départ
     * @param pStrategy       Stratégie de déplacement
     */
    public MovingCharacter(final String pNameKey, final String pDescriptionKey,
            final Room pCurrentRoom, final MovementStrategy pStrategy) {
        super(pNameKey, pDescriptionKey, pCurrentRoom);
        this.aStrategy = pStrategy;
        this.aPath = new ArrayList<>();
        this.aCurrentPathIndex = 0;
    }

    /**
     * Définit le message de bienvenue (surcharge pour retourner MovingCharacter).
     */
    @Override
    public MovingCharacter setGreeting(final String pGreetingKey) {
        super.setGreeting(pGreetingKey);
        return this;
    }

    /**
     * Ajoute une réponse spécifique (surcharge pour retourner MovingCharacter).
     */
    @Override
    public MovingCharacter addItemResponse(final Item pItem, final String pResponseKey) {
        super.addItemResponse(pItem, pResponseKey);
        return this;
    }

    /**
     * Définit l'objet requis pour l'aide (surcharge pour retourner
     * MovingCharacter).
     */
    @Override
    public MovingCharacter setHelpItem(final Item pItem, final String pHelpMessageKey) {
        super.setHelpItem(pItem, pHelpMessageKey);
        return this;
    }

    /**
     * Définit un chemin prédéfini pour le personnage.
     * 
     * @param pPath Liste des pièces à suivre dans l'ordre
     * @return this (pour chaînage)
     */
    public MovingCharacter setPath(final List<Room> pPath) {
        this.aPath = new ArrayList<>(pPath);
        this.aStrategy = MovementStrategy.FOLLOW_PATH;
        return this;
    }

    /**
     * Définit la stratégie de déplacement.
     * 
     * @param pStrategy Nouvelle stratégie
     * @return this (pour chaînage)
     */
    public MovingCharacter setStrategy(final MovementStrategy pStrategy) {
        this.aStrategy = pStrategy;
        return this;
    }

    /**
     * Définit le joueur à suivre.
     * 
     * @param pPlayer Le joueur à suivre
     * @return this (pour chaînage)
     */
    public MovingCharacter setTargetPlayer(final Player pPlayer) {
        this.aTargetPlayer = pPlayer;
        return this;
    }

    /**
     * Fait se déplacer le personnage selon sa stratégie.
     */
    public void move() {
        Room vCurrentRoom = getCurrentRoom();
        if (vCurrentRoom == null)
            return;

        Room vNextRoom = null;

        switch (this.aStrategy) {
            case RANDOM:
                vNextRoom = getRandomAdjacentRoom(vCurrentRoom);
                break;

            case FOLLOW_PATH:
                vNextRoom = getNextPathRoom();
                break;

            case FOLLOW_PLAYER:
                vNextRoom = getNextRoomTowardsPlayer();
                break;
        }

        if (vNextRoom != null && vNextRoom != vCurrentRoom) {
            setCurrentRoom(vNextRoom);
        }
    }

    /**
     * Calcule la prochaine pièce pour se rapprocher du joueur.
     * 
     * @return La pièce vers laquelle se déplacer, ou null si pas de déplacement
     */
    private Room getNextRoomTowardsPlayer() {
        if (this.aTargetPlayer == null)
            return null;

        Room vPlayerRoom = this.aTargetPlayer.getCurrentRoom();
        Room vCurrentRoom = getCurrentRoom();

        // Si le joueur est dans la même pièce, ne pas bouger
        if (vPlayerRoom == vCurrentRoom)
            return null;

        // Essayer de trouver un chemin vers le joueur
        return findPathTowards(vPlayerRoom);
    }

    /**
     * Trouve une pièce adjacente qui se rapproche du joueur.
     * Utilise une recherche simple en largeur (BFS) pour trouver un chemin.
     * 
     * @param pTargetRoom La pièce cible (celle du joueur)
     * @return La prochaine pièce vers laquelle se déplacer
     */
    private Room findPathTowards(final Room pTargetRoom) {
        Room vCurrentRoom = getCurrentRoom();

        // BFS pour trouver un chemin vers le joueur
        Queue<Room> vQueue = new LinkedList<>();
        Map<Room, Room> vPrevious = new HashMap<>();
        Set<Room> vVisited = new HashSet<>();

        vQueue.add(vCurrentRoom);
        vVisited.add(vCurrentRoom);
        vPrevious.put(vCurrentRoom, null);

        while (!vQueue.isEmpty()) {
            Room vRoom = vQueue.poll();

            // Si on a trouvé la pièce du joueur
            if (vRoom == pTargetRoom) {
                // Reconstruire le chemin et retourner la première étape
                return reconstructFirstStep(vPrevious, vCurrentRoom, pTargetRoom);
            }

            // Explorer les voisins
            for (Direction vDir : Direction.getAll()) {
                Room vNeighbor = vRoom.getExit(vDir.toString());
                if (vNeighbor != null && !vVisited.contains(vNeighbor)) {
                    // Ne pas traverser les portes verrouillées
                    if (vRoom.isDoorLocked(vDir.toString())) {
                        continue;
                    }
                    vQueue.add(vNeighbor);
                    vVisited.add(vNeighbor);
                    vPrevious.put(vNeighbor, vRoom);
                }
            }
        }

        // Si aucun chemin trouvé, essayer de se déplacer aléatoirement
        return getRandomAdjacentRoom(vCurrentRoom);
    }

    /**
     * Reconstruit le premier pas du chemin vers la cible.
     * 
     * @param pPrevious Map des pièces visitées et leur pièce précédente
     * @param pStart    La pièce de départ (actuelle)
     * @param pTarget   La pièce cible (celle du joueur)
     * @return La première pièce à suivre pour se rapprocher du joueur
     */
    private Room reconstructFirstStep(Map<Room, Room> pPrevious,
            Room pStart, Room pTarget) {
        Room vCurrent = pTarget;
        Room vPrevious = pPrevious.get(vCurrent);

        // Remonter jusqu'à trouver le premier pas depuis le départ
        while (vPrevious != null && vPrevious != pStart) {
            vCurrent = vPrevious;
            vPrevious = pPrevious.get(vCurrent);
        }

        return vCurrent;
    }

    /**
     * Retourne une pièce adjacente aléatoire (non verrouillée).
     * 
     * @param pRoom La pièce actuelle
     * @return Une pièce adjacente aléatoire
     */
    private Room getRandomAdjacentRoom(final Room pRoom) {
        List<Room> vExits = new ArrayList<>();
        for (Direction vDir : Direction.getAll()) {
            Room vExit = pRoom.getExit(vDir.toString());
            if (vExit != null) {
                // Ne pas traverser les portes verrouillées
                if (!pRoom.isDoorLocked(vDir.toString())) {
                    vExits.add(vExit);
                }
            }
        }

        if (vExits.isEmpty())
            return null;
        return vExits.get(RANDOM.nextInt(vExits.size()));
    }

    /**
     * Retourne la prochaine pièce du chemin prédéfini.
     * Si le chemin est vide, retourne null. Sinon, retourne la pièce à l'index
     * actuel et incrémente l'index (en boucle).
     * 
     * @return La prochaine pièce sur le chemin
     */
    private Room getNextPathRoom() {
        if (this.aPath.isEmpty())
            return null;
        Room vNext = this.aPath.get(this.aCurrentPathIndex);
        this.aCurrentPathIndex = (this.aCurrentPathIndex + 1) % this.aPath.size();
        return vNext;
    }

    /**
     * Retourne la description complète du personnage, incluant sa stratégie de
     * déplacement.
     * 
     * @return La description complète avec indication du comportement
     */
    @Override
    public String getFullDescription() {
        String vDesc = super.getFullDescription();
        String vStrategyDesc = getStrategyDescription();

        if (!vStrategyDesc.isEmpty()) {
            vDesc += "\n  " + vStrategyDesc;
        }

        return vDesc;
    }

    /**
     * Retourne la description localisée de la stratégie.
     * 
     * @return La description localisée de la stratégie
     */
    private String getStrategyDescription() {
        switch (this.aStrategy) {
            case RANDOM:
                return Lang.localizableString("moving_strategy_random");
            case FOLLOW_PATH:
                return Lang.localizableString("moving_strategy_path");
            case FOLLOW_PLAYER:
                return Lang.localizableString("moving_strategy_player");
            default:
                return "";
        }
    }

    /**
     * Retourne la stratégie de déplacement.
     * 
     * @return La stratégie de déplacement
     */
    public MovementStrategy getStrategy() {
        return this.aStrategy;
    }

    /**
     * Retourne le chemin prédéfini.
     * 
     * @return Le chemin (copie)
     */
    public List<Room> getPath() {
        return this.aPath != null ? new ArrayList<>(this.aPath) : null;
    }

    /**
     * Retourne l'index actuel dans le chemin.
     * 
     * @return L'index actuel dans le chemin
     */
    public int getPathIndex() {
        return this.aCurrentPathIndex;
    }

    /**
     * Définit l'index actuel dans le chemin (pour les personnages avec stratégie
     * FOLLOW_PATH).
     * Si l'index dépasse la taille du chemin, il boucle au début.
     * 
     * @param pIndex Le nouvel index dans le chemin
     */
    public void setPathIndex(int pIndex) {
        if (this.aPath != null && !this.aPath.isEmpty()) {
            this.aCurrentPathIndex = pIndex % this.aPath.size();
        }
    }

    /**
     * Format étendu pour inclure les attributs de suivi
     * 
     * @return La représentation textuelle du personnage mobile avec détails de la
     *         stratégie de déplacement
     */
    @Override
    public String toString() {
        StringBuilder vResult = new StringBuilder(super.toString().replace("Character", "MovingCharacter"));

        int vInsertPos = vResult.lastIndexOf("}");
        vResult.deleteCharAt(vInsertPos);

        vResult.append(",strategy=").append(this.aStrategy);

        if (this.aStrategy == MovementStrategy.FOLLOW_PATH && !this.aPath.isEmpty()) {
            vResult.append(",path=[");
            boolean vFirst = true;
            for (Room vRoom : this.aPath) {
                if (!vFirst)
                    vResult.append(",");
                vResult.append(vRoom.getRoomKey());
                vFirst = false;
            }
            vResult.append("],index=").append(this.aCurrentPathIndex);
        }

        vResult.append("}");
        return vResult.toString();
    }
}