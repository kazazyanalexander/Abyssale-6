package pkg_gameplay;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import pkg_characters.Character;
import pkg_characters.Entity;
import pkg_characters.MovingCharacter;
import pkg_characters.Player;
import pkg_items.Item;
import pkg_items.ItemList;
import pkg_items.ItemType;
import pkg_utility.Direction;
import pkg_utility.Lang;

/**
 * Classe Room - une pièce dans un jeu d'aventure.
 * Cette classe fait partie de l'application "Station Abyssale-6".
 * Une "Room" représente un emplacement dans le décor du jeu.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class Room implements Serializable {
    /** Version de serialisation automatique */
    private static final long serialVersionUID = 1L;

    /** Clé d'identification unique de la pièce (utilisée pour la localisation). */
    private final String aRoomKey;

    /**
     * Collection des sorties de la pièce, associant une direction à une pièce
     * voisine.
     */
    private final HashMap<String, Room> aExits;

    /** Nom du fichier image associé à cette pièce. */
    private final String aImageName;

    /** Collection des objets présents dans la pièce. */
    private ItemList aItems;

    /** Ensemble des directions qui sont des trappes (sorties à sens unique). */
    private final HashSet<String> aTrapDoors;

    /** Collection des portes verrouillées présentes dans la pièce. */
    private final HashMap<String, Door> aDoors;

    /**
     * Liste unifiée des entités (joueur et personnages) présentes dans la pièce.
     */
    private final List<Entity> aEntities;

    /**
     * Crée une pièce avec une clé d'identification et une image.
     * 
     * @param pRoomKey Clé d'identification unique de la pièce
     * @param pImage   Nom du fichier image associé
     */
    public Room(final String pRoomKey, final String pImage) {
        this.aRoomKey = pRoomKey;
        this.aExits = new HashMap<>();
        this.aItems = new ItemList();
        this.aImageName = pImage;
        this.aTrapDoors = new HashSet<>();
        this.aDoors = new HashMap<>();
        this.aEntities = new ArrayList<>();
    }

    // ===== GESTION DES SORTIES =====

    /**
     * Définit une sortie de cette pièce vers une autre pièce.
     * 
     * @param pDirection La direction de la sortie (enum Direction)
     * @param pNeighbor  La pièce voisine accessible
     */
    public void setExit(final Direction pDirection, final Room pNeighbor) {
        this.aExits.put(pDirection.toString(), pNeighbor);
    }

    /**
     * Définit une sortie de cette pièce vers une autre pièce.
     * 
     * @param pDirection La direction de la sortie (chaîne de caractères)
     * @param pNeighbor  La pièce voisine accessible
     */
    public void setExit(final String pDirection, final Room pNeighbor) {
        this.aExits.put(pDirection, pNeighbor);
    }

    /**
     * Retourne la pièce atteinte en allant dans la direction spécifiée.
     * 
     * @param pDirection La direction à vérifier
     * @return La pièce voisine, ou null si aucune sortie dans cette direction
     */
    public Room getExit(final String pDirection) {
        return this.aExits.get(pDirection);
    }

    /**
     * Vérifie si une pièce donnée est accessible directement depuis cette pièce.
     * 
     * @param pRoom La pièce à vérifier
     * @return true si la pièce est une sortie directe, false sinon
     */
    public boolean isExit(final Room pRoom) {
        return this.aExits.containsValue(pRoom);
    }

    // ===== GESTION DES PORTES =====

    /**
     * Définit une porte verrouillée dans une direction donnée (avec enum
     * Direction).
     * 
     * @param pDirection       La direction de la porte
     * @param pDoorId          Identifiant unique de la porte
     * @param pRequiredKey     Type de clé requis pour ouvrir/fermer
     * @param pInitiallyLocked true si la porte est initialement verrouillée
     * @param pAutoLock        true si la porte se verrouille automatiquement après
     *                         passage
     */
    public void setLockedDoor(final Direction pDirection, final String pDoorId,
            final ItemType pRequiredKey, final boolean pInitiallyLocked,
            final boolean pAutoLock) {
        String vDir = pDirection.toString();
        if (this.aExits.containsKey(vDir)) {
            this.aDoors.put(vDir, new Door(pDoorId, pRequiredKey, pInitiallyLocked, pAutoLock));
        }
    }

    /**
     * Définit une porte verrouillée dans une direction donnée (avec chaîne de
     * caractères).
     * 
     * @param pDirection       La direction de la porte
     * @param pDoorId          Identifiant unique de la porte
     * @param pRequiredKey     Type de clé requis pour ouvrir/fermer
     * @param pInitiallyLocked true si la porte est initialement verrouillée
     * @param pAutoLock        true si la porte se verrouille automatiquement après
     *                         passage
     */
    public void setLockedDoor(final String pDirection, final String pDoorId,
            final ItemType pRequiredKey, final boolean pInitiallyLocked,
            final boolean pAutoLock) {
        if (this.aExits.containsKey(pDirection)) {
            this.aDoors.put(pDirection, new Door(pDoorId, pRequiredKey, pInitiallyLocked, pAutoLock));
        }
    }

    /**
     * Vérifie si une porte est présente dans la direction donnée.
     * 
     * @param pDirection La direction à vérifier
     * @return true si une porte existe dans cette direction
     */
    public boolean hasDoor(final String pDirection) {
        return this.aDoors.containsKey(pDirection);
    }

    /**
     * Retourne la porte située dans la direction donnée.
     * 
     * @param pDirection La direction
     * @return La porte, ou null si aucune porte dans cette direction
     */
    public Door getDoor(final String pDirection) {
        return this.aDoors.get(pDirection);
    }

    /**
     * Vérifie si la porte dans la direction donnée est verrouillée.
     * 
     * @param pDirection La direction
     * @return true si la porte existe et est verrouillée
     */
    public boolean isDoorLocked(final String pDirection) {
        Door vDoor = this.aDoors.get(pDirection);
        return vDoor != null && vDoor.isLocked();
    }

    /**
     * Tente de déverrouiller la porte dans la direction donnée avec une clé.
     * 
     * @param pDirection La direction de la porte
     * @param pKey       La clé utilisée
     * @return true si la porte a été déverrouillée avec succès
     */
    public boolean unlockDoor(final String pDirection, final Item pKey) {
        Door vDoor = this.aDoors.get(pDirection);
        return vDoor != null && vDoor.unlock(pKey);
    }

    /**
     * Tente de verrouiller la porte dans la direction donnée avec une clé.
     * 
     * @param pDirection La direction de la porte
     * @param pKey       La clé utilisée
     * @return true si la porte a été verrouillée avec succès
     */
    public boolean lockDoor(final String pDirection, final Item pKey) {
        Door vDoor = this.aDoors.get(pDirection);
        return vDoor != null && vDoor.lock(pKey);
    }

    /**
     * Notifie le passage par une porte (pour les portes à verrouillage
     * automatique).
     * 
     * @param pDirection La direction de la porte franchie
     */
    public void doorPassed(final String pDirection) {
        Door vDoor = this.aDoors.get(pDirection);
        if (vDoor != null) {
            vDoor.afterPassage();
        }
    }

    // ===== GESTION DES TRAPPES =====

    /**
     * Définit une sortie comme étant une trappe (franchissable dans un seul sens).
     * 
     * @param pDirection La direction de la sortie à transformer en trappe
     */
    public void setTrapDoor(final Direction pDirection) {
        String vDir = pDirection.toString();
        if (this.aExits.containsKey(vDir)) {
            this.aTrapDoors.add(vDir);
        }
    }

    /**
     * Définit une sortie comme étant une trappe (franchissable dans un seul sens).
     * 
     * @param pDirection La direction de la sortie à transformer en trappe
     */
    public void setTrapDoor(final String pDirection) {
        if (this.aExits.containsKey(pDirection)) {
            this.aTrapDoors.add(pDirection);
        }
    }

    /**
     * Vérifie si la sortie dans la direction donnée est une trappe.
     * 
     * @param pDirection La direction à vérifier
     * @return true si c'est une trappe, false sinon
     */
    public boolean isTrapDoor(final String pDirection) {
        return this.aTrapDoors.contains(pDirection);
    }

    // ===== GESTION UNIFIÉE DES ENTITÉS =====

    /**
     * Ajoute une entité (joueur ou personnage) à la pièce.
     * 
     * @param pEntity L'entité à ajouter
     */
    public void addEntity(final Entity pEntity) {
        if (pEntity != null && !this.aEntities.contains(pEntity)) {
            this.aEntities.add(pEntity);
        }
    }

    /**
     * Retire une entité de la pièce.
     * 
     * @param pEntity L'entité à retirer
     * @return true si retirée avec succès, false sinon
     */
    public boolean removeEntity(final Entity pEntity) {
        return this.aEntities.remove(pEntity);
    }

    /**
     * Retourne la liste de toutes les entités présentes dans la pièce.
     * 
     * @return Une copie de la liste des entités
     */
    public List<Entity> getEntities() {
        return new ArrayList<>(this.aEntities);
    }

    /**
     * Vérifie si la pièce contient au moins une entité.
     * 
     * @return true si la pièce contient des entités
     */
    public boolean hasEntities() {
        return !this.aEntities.isEmpty();
    }

    /**
     * Retourne la liste des personnages (non-joueurs) présents dans la pièce.
     * 
     * @return La liste des personnages
     */
    public List<Character> getCharacters() {
        List<Character> vChars = new ArrayList<>();
        for (Entity vEntity : this.aEntities) {
            if (vEntity instanceof Character && !(vEntity instanceof Player)) {
                vChars.add((Character) vEntity);
            }
        }
        return vChars;
    }

    /**
     * Vérifie si la pièce contient au moins un personnage.
     * 
     * @return true si la pièce contient des personnages
     */
    public boolean hasCharacters() {
        for (Entity vEntity : this.aEntities) {
            if (vEntity instanceof Character && !(vEntity instanceof Player)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Recherche un personnage par son nom dans la pièce.
     * 
     * @param pName Le nom du personnage recherché
     * @return Le personnage trouvé, ou null si aucun personnage ne correspond
     */
    public Character getCharacter(final String pName) {
        for (Entity vEntity : this.aEntities) {
            if (vEntity instanceof Character && !(vEntity instanceof Player)
                    && vEntity.getName().equalsIgnoreCase(pName)) {
                return (Character) vEntity;
            }
        }
        return null;
    }

    // ===== GESTION DES OBJETS =====

    /**
     * Ajoute un objet dans la pièce.
     * 
     * @param pItem L'objet à ajouter
     */
    public void addItem(final Item pItem) {
        this.aItems.addItem(pItem);
    }

    /**
     * Retire un objet de la pièce en utilisant son nom.
     * 
     * @param pItemName Le nom de l'objet à retirer
     * @return L'objet retiré, ou null si non trouvé
     */
    public Item removeItem(final String pItemName) {
        return this.aItems.removeItem(pItemName);
    }

    /**
     * Retire un objet spécifique de la pièce.
     * 
     * @param pItem L'objet à retirer
     * @return true si l'objet a été retiré avec succès
     */
    public boolean removeItem(final Item pItem) {
        return this.aItems.removeItem(pItem);
    }

    /**
     * Vérifie si la pièce contient un objet avec le nom donné.
     * 
     * @param pItemName Le nom de l'objet à rechercher
     * @return true si l'objet est présent
     */
    public boolean containsItem(final String pItemName) {
        return this.aItems.containsItem(pItemName);
    }

    /**
     * Récupère un objet par son nom sans le retirer de la pièce.
     * 
     * @param pItemName Le nom de l'objet
     * @return L'objet trouvé, ou null s'il n'existe pas
     */
    public Item getItem(final String pItemName) {
        return this.aItems.getItem(pItemName);
    }

    /**
     * Retourne la liste des objets présents dans la pièce.
     * 
     * @return La liste des objets
     */
    public ItemList getItems() {
        return this.aItems;
    }

    // ===== DESCRIPTIONS =====

    /**
     * Retourne la clé d'identification de la pièce.
     * 
     * @return La clé de la pièce
     */
    public String getRoomKey() {
        return this.aRoomKey;
    }

    /**
     * Retourne la description courte de la pièce.
     * 
     * @return La description courte localisée
     */
    public String getShortDescription() {
        return Lang.localizableString("short_" + this.aRoomKey);
    }

    /**
     * Retourne une description longue de la pièce, incluant les sorties,
     * les objets et les entités présentes.
     * 
     * @return La description longue de la pièce
     */
    public String getLongDescription() {
        String vDescription = Lang.localizableString(this.aRoomKey) + "\n" +
                this.getExitString() + "\n" +
                this.getItemString() +
                this.getEntityString();

        if (!this.aTrapDoors.isEmpty()) {
            vDescription += "\n" + Lang.localizableString("trapdoor_warning");
        }

        return vDescription;
    }

    /**
     * Retourne une chaîne décrivant les sorties de la pièce.
     * 
     * @return La description des sorties
     */
    private String getExitString() {
        String vReturnString = Lang.localizableString("exits");
        for (String vDirection : this.aExits.keySet()) {
            vReturnString += " " + Lang.localizableString(vDirection);

            if (this.aTrapDoors.contains(vDirection)) {
                vReturnString += "⚠️";
            }
            if (this.isDoorLocked(vDirection)) {
                vReturnString += " 🔒";
            }
        }
        return vReturnString;
    }

    /**
     * Retourne une chaîne décrivant les objets présents dans la pièce.
     * 
     * @return La description des objets
     */
    private String getItemString() {
        if (this.aItems.isEmpty()) {
            return "";
        }
        StringBuilder vResult = new StringBuilder(Lang.localizableString("found_item"));
        for (Item vItem : this.aItems.getItems()) {
            vResult.append(" ").append(vItem.getInformation());
        }
        return vResult + ".\n";
    }

    /**
     * Retourne une description textuelle des entités présentes dans la pièce.
     * 
     * @return La description des entités (personnages, etc.)
     */
    private String getEntityString() {
        List<String> vDescriptions = new ArrayList<>();

        for (Entity vEntity : this.aEntities) {
            if (!(vEntity instanceof Player)) {
                vDescriptions.add(vEntity.getFullDescription());
            }
        }

        if (vDescriptions.isEmpty()) {
            return "";
        }

        StringBuilder vResult = new StringBuilder("\n" + Lang.localizableString("presences"));
        for (String vDesc : vDescriptions) {
            vResult.append("\n  • ").append(vDesc);
        }
        return vResult.toString();
    }

    // ===== MÉTHODES UTILITAIRES =====

    /**
     * Retourne le nom du fichier image associé à la pièce.
     * 
     * @return Le nom de l'image
     */
    public String getImageName() {
        return this.aImageName;
    }

    /**
     * Vérifie si cette pièce est le réacteur (salle de victoire).
     * 
     * @return true si c'est la salle du réacteur
     */
    public boolean isReactor() {
        return "room_reacteur".equals(this.aRoomKey);
    }

    // ===== MÉTHODES STATIQUES =====

    /**
     * Vérifie si une chaîne correspond à une clé de pièce valide.
     * 
     * @param pRoomKey La clé à vérifier
     * @return true si la clé est valide
     */
    public static boolean isValidRoomKey(final String pRoomKey) {
        String[] vValidKeys = {
                "room_sas", "room_posteGarde", "room_serre", "room_labo",
                "room_dortoir", "room_infirmerie", "room_machines", "room_reacteur",
                "room_obs", "room_hydro", "room_eng", "room_air", "room_med",
                "room_transporter"
        };
        for (String vKey : vValidKeys) {
            if (vKey.equals(pRoomKey)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Méthode statique pour initialiser toute la carte du jeu.
     * Retourne un tableau contenant deux pièces : la pièce de départ (Sas) et les
     * dortoirs.
     * 
     * @return Un tableau de Room contenant [vSas, vDortoir]
     */
    public static Room[] createRooms() {
        // 1. Création des pièces
        final Room vSas = new Room("room_sas", "sas.gif");
        final Room vPosteGarde = new Room("room_posteGarde", "poste.gif");
        final Room vSerre = new Room("room_serre", "serre.gif");
        final Room vLabo = new Room("room_labo", "labo.gif");
        final Room vDortoir = new Room("room_dortoir", "dortoir.gif");
        final Room vInfirmerie = new Room("room_infirmerie", "infirmerie.gif");
        final Room vMachines = new Room("room_machines", "machines.gif");
        final Room vReacteur = new Room("room_reacteur", "win.gif");
        final Room vObservation = new Room("room_obs", "obs.gif");
        final Room vHydroponics = new Room("room_hydro", "hydro.gif");
        final Room vEngine = new Room("room_eng", "engine.gif");
        final Room vAirlock = new Room("room_air", "airlock.gif");
        final Room vMedbay = new Room("room_med", "medbay.gif");
        final TransporterRoom vTransporter = new TransporterRoom("room_transporter", "transporter.gif");

        // 2. Configuration des connexions avec l'enum Direction
        vSas.setExit(Direction.NORTH, vAirlock);
        vSas.setExit(Direction.UP, vTransporter);

        vTransporter.setExit(Direction.DOWN, vSas);

        vAirlock.setExit(Direction.SOUTH, vSas);
        vAirlock.setExit(Direction.NORTH, vPosteGarde);
        vAirlock.setExit(Direction.UP, vDortoir);
        vAirlock.addItem(ItemType.BEAMER.createItem());

        vPosteGarde.setExit(Direction.SOUTH, vAirlock);
        vPosteGarde.setExit(Direction.EAST, vLabo);
        vPosteGarde.setExit(Direction.NORTH, vMedbay);

        vMedbay.setExit(Direction.SOUTH, vPosteGarde);
        vMedbay.setExit(Direction.WEST, vSerre);

        vSerre.setExit(Direction.EAST, vMedbay);
        vSerre.setLockedDoor(Direction.EAST, "door_serre", ItemType.BLUE_CARD, true, false);

        vLabo.setExit(Direction.WEST, vPosteGarde);
        vLabo.setExit(Direction.DOWN, vEngine);
        vLabo.setLockedDoor(Direction.DOWN, "door_labo_down", ItemType.DIVING_SUIT, true, true);

        vEngine.setExit(Direction.UP, vLabo);
        vEngine.setExit(Direction.EAST, vMachines);
        vEngine.setLockedDoor(Direction.EAST, "door_engine_machines", ItemType.RED_CARD, true, false);
        vEngine.addItem(ItemType.WRENCH.createItem());

        vDortoir.setExit(Direction.EAST, vInfirmerie);
        vDortoir.setTrapDoor(Direction.EAST);
        vDortoir.setExit(Direction.DOWN, vAirlock);
        vDortoir.setExit(Direction.NORTH, vObservation);

        vInfirmerie.setExit(Direction.NORTH, vHydroponics);

        vObservation.setExit(Direction.SOUTH, vDortoir);
        vObservation.setExit(Direction.EAST, vHydroponics);
        vObservation.addItem(ItemType.TORCH.createItem());

        vHydroponics.setExit(Direction.WEST, vObservation);
        vHydroponics.addItem(ItemType.GENEETIC.createItem());

        vMachines.setExit(Direction.NORTH, vReacteur);
        vMachines.setExit(Direction.WEST, vEngine);
        vMachines.setLockedDoor(Direction.NORTH, "door_machines", ItemType.WRENCH, true, false);

        // 3. Initialisation des destinations pour la TransporterRoom sauf reactor
        List<Room> vAllRooms = new ArrayList<>();
        vAllRooms.add(vSas);
        vAllRooms.add(vPosteGarde);
        vAllRooms.add(vSerre);
        vAllRooms.add(vLabo);
        vAllRooms.add(vDortoir);
        vAllRooms.add(vInfirmerie);
        vAllRooms.add(vMachines);
        vAllRooms.add(vObservation);
        vAllRooms.add(vHydroponics);
        vAllRooms.add(vEngine);
        vAllRooms.add(vAirlock);
        vAllRooms.add(vMedbay);
        vAllRooms.add(vTransporter);
        vTransporter.initializeDestinations(vAllRooms);

        // 4. Création des personnages (ils s'ajoutent automatiquement via le
        // constructeur)

        // Exemple : Le scientifique échange teleport contre un oxygène
        new Character("character_scientist", "character_scientist_desc", vLabo)
                .setGreeting("character_scientist_greeting")
                .addExchange(ItemType.BEAMER,
                        ItemType.OXYGEN.createItem(),
                        "character_scientist_exchange");

        new Character("character_doctor", "character_doctor_desc", vMedbay)
                .setGreeting("character_doctor_greeting")
                .addExchange(ItemType.OXYGEN,
                        ItemType.MAGIC_COOKIE.createItem(),
                        "character_doctor_exchange");

        new Character("character_guard", "character_guard_desc", vPosteGarde)
                .setGreeting("character_guard_greeting")
                .addExchange(ItemType.TORCH,
                        ItemType.BLUE_CARD.createItem(),
                        "character_guard_exchange");

        new Character("character_engineer", "character_engineer_desc", vSerre)
                .setGreeting("character_engineer_greeting")
                .addExchange(ItemType.GENEETIC,
                        ItemType.DIVING_SUIT.createItem(),
                        "character_engineer_exchange");

        new Character("character_nurse", "character_nurse_desc", vInfirmerie)
                .setGreeting("character_nurse_greeting")
                .addExchange(ItemType.BLUE_CARD,
                        ItemType.FIRSTAID.createItem(),
                        "character_nurse_exchange");

        new Character("character_geneticist", "character_geneticist_desc", vHydroponics)
                .setGreeting("character_geneticist_greeting")
                .addExchange(ItemType.FIRSTAID,
                        ItemType.RED_CARD.createItem(),
                        "character_geneticist_exchange");

        // 5. Création des personnages mobiles
        new MovingCharacter("character_wandering_tech", "character_wandering_tech_desc", vHydroponics,
                MovingCharacter.MovementStrategy.RANDOM)
                .setGreeting("character_wandering_tech_greeting")
                .addItemResponse(ItemType.WRENCH.createItem(), "character_wandering_tech_wrench");

        List<Room> vPath = new ArrayList<>();
        vPath.add(vSas);
        vPath.add(vAirlock);
        vPath.add(vDortoir);
        vPath.add(vInfirmerie);
        vPath.add(vHydroponics);
        vPath.add(vObservation);

        new MovingCharacter("character_researcher", "character_researcher_desc", vSas,
                MovingCharacter.MovementStrategy.FOLLOW_PATH)
                .setPath(vPath)
                .setGreeting("character_researcher_greeting")
                .addItemResponse(ItemType.WRENCH.createItem(), "character_wandering_tech_wrench");

        // 6. Retourner un tableau contenant les deux pièces demandées
        return new Room[] { vSas, vSerre };
    }

    /**
     * Retourne une représentation textuelle détaillée de la pièce pour la
     * sauvegarde.
     */
    @Override
    public String toString() {
        StringBuilder vResult = new StringBuilder();
        vResult.append("Room{")
                .append("key=").append(this.aRoomKey)
                .append(", exits={");

        // Exporter les sorties
        boolean vFirst = true;
        for (Map.Entry<String, Room> vEntry : this.aExits.entrySet()) {
            if (!vFirst) {
                vResult.append(",");
            }
            vResult.append(vEntry.getKey()).append(":").append(vEntry.getValue().getRoomKey());
            vFirst = false;
        }
        vResult.append("}");

        // Exporter les trappes
        vResult.append(", trapDoors=").append(this.aTrapDoors.toString());

        // Exporter les portes
        vResult.append(", doors={");
        vFirst = true;
        for (Map.Entry<String, Door> vEntry : this.aDoors.entrySet()) {
            if (!vFirst) {
                vResult.append(",");
            }
            vResult.append(vEntry.getKey()).append("=").append(vEntry.getValue().toString());
            vFirst = false;
        }
        vResult.append("}");

        // Exporter les items
        vResult.append(", items=").append(this.aItems.toString());

        // Exporter les entités (personnages, joueur)
        vResult.append(", entities=[");
        vFirst = true;
        for (Entity vEntity : this.aEntities) {
            if (!vFirst) {
                vResult.append(",");
            }
            vResult.append(vEntity.toString());
            vFirst = false;
        }
        vResult.append("]");

        vResult.append("}");
        return vResult.toString();
    }

}