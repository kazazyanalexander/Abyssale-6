package pkg_utility;

import pkg_core.GameEngine;
import pkg_gameplay.Door;
import pkg_gameplay.Room;
import pkg_gameplay.TransporterRoom;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import pkg_characters.MovingCharacter;
import pkg_characters.MovingCharacter.MovementStrategy;
import pkg_characters.Character;

import pkg_characters.Player;
import pkg_items.Beamer;
import pkg_items.DivingSuit;
import pkg_items.Item;
import pkg_items.ItemType;
import pkg_items.MagicCookie;
import pkg_items.Torch;

/**
 * GameLoaderXML - Charge une partie sauvegardée au format XML.
 * Version utilisant l'attribut image du fichier XML.
 * 
 * @author Alexander KAZAZYAN
 * @version 05/2026
 */
public class GameLoaderXML {

    /** Répertoire des sauvegardes */
    private static final String SAVE_DIR = "saves/";

    /** Map pour retrouver les pièces par leur clé */
    private static Map<String, Room> aRoomMap;

    /**
     * Constructeur par défaut de la classe GameLoaderXML.
     */
    public GameLoaderXML() {
        // Constructeur par défaut
    }

    /**
     * Charge une partie sauvegardée en XML.
     * 
     * @param pFileName Le nom du fichier (sans extension)
     * @return Le moteur de jeu restauré, ou null en cas d'erreur
     */
    public static GameEngine loadGameFromXML(final String pFileName) {
        String vFilePath = SAVE_DIR + pFileName + ".xml";

        try {
            // 1. Analyser le XML
            DocumentBuilderFactory vFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder vBuilder = vFactory.newDocumentBuilder();
            Document vDoc = vBuilder.parse(new File(vFilePath));
            vDoc.getDocumentElement().normalize();

            System.out.println("🔍 Chargement XML: " + vFilePath);

            // 2. D'ABORD: Créer des pièces fraîches (avec TOUS les personnages statiques)
            Room[] vRooms = Room.createRooms();
            Room vStartingRoom = vRooms[0]; // vSas

            // 3. Recréer la map de toutes les pièces à partir du monde frais
            aRoomMap = new HashMap<>();
            List<Room> vAllRooms = collectAllRoomsFresh(vStartingRoom);
            for (Room vRoom : vAllRooms) {
                aRoomMap.put(vRoom.getRoomKey(), vRoom);
            }

            // 4. Initialiser les TransporterRooms
            for (Room vRoom : vAllRooms) {
                if (vRoom instanceof TransporterRoom) {
                    ((TransporterRoom) vRoom).initializeDestinations(vAllRooms);
                }
            }

            System.out.println("   ✅ " + aRoomMap.size() + " pièces recréées avec personnages");

            // 5. METTRE À JOUR les états des pièces depuis le XML (portes, trappes, objets)
            updateRoomsFromXML(vDoc);

            // 6. Récupérer l'élément joueur et créer le joueur depuis le XML
            NodeList vPlayerNodes = vDoc.getElementsByTagName("player");
            if (vPlayerNodes.getLength() == 0) {
                System.err.println("❌ Aucun joueur trouvé dans le fichier");
                return null;
            }
            Element vPlayerElem = (Element) vPlayerNodes.item(0);
            Player vPlayer = createPlayerFromXML(vPlayerElem);
            if (vPlayer == null) {
                System.err.println("❌ Échec de création du joueur");
                return null;
            }

            // 7. Créer un nouveau moteur de jeu
            GameEngine vEngine = new GameEngine(true);

            // 8. Extraire le temps depuis le XML
            Element vGameElem = (Element) vDoc.getDocumentElement();
            int vTimeLeft = 600;
            if (vGameElem.hasAttribute("timeLeft")) {
                try {
                    vTimeLeft = Integer.parseInt(vGameElem.getAttribute("timeLeft"));
                } catch (NumberFormatException e) {
                    // Conserver la valeur par défaut
                }
            }
            vEngine.setTimeLeft(vTimeLeft);

            // 9. Définir le joueur
            vEngine.setPlayer(vPlayer);
            vPlayer.setGameEngine(vEngine);

            // 10. Récupérer l'élément des personnages mobiles et recréer les personnages
            // mobiles
            NodeList vMovingCharNodes = vDoc.getElementsByTagName("movingCharacters");
            if (vMovingCharNodes.getLength() > 0) {
                Element vMovingCharsElem = (Element) vMovingCharNodes.item(0);
                List<MovingCharacter> vMovingChars = createMovingCharactersFromXML(vMovingCharsElem);
                for (MovingCharacter vChar : vMovingChars) {
                    vEngine.addMovingCharacter(vChar);
                }
            }

            // 11. Mettre à jour les états des personnages statiques depuis le XML
            updateStaticCharacterStates(vDoc);

            System.out.println("🎉 Chargement XML réussi!");
            return vEngine;

        } catch (FileNotFoundException e) {
            System.err.println("Fichier non trouvé: " + SAVE_DIR + pFileName + ".xml");
            return null;
        } catch (Exception e) {
            System.err.println("❌ Erreur de chargement: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Collecte toutes les pièces à partir d'un monde frais.
     * Parcourt récursivement toutes les pièces accessibles depuis la pièce de
     * départ.
     * 
     * @param pStartRoom La pièce de départ pour la collecte
     * @return La liste de toutes les pièces du monde
     */
    private static List<Room> collectAllRoomsFresh(Room pStartRoom) {
        List<Room> vAllRooms = new ArrayList<>();
        Set<Room> vVisited = new HashSet<>();
        collectRoomsRecursive(pStartRoom, vVisited, vAllRooms);

        System.out.println("   🏠 Pièces dans le monde frais: " + vAllRooms.size());
        for (Room vRoom : vAllRooms) {
            System.out.println("      - " + vRoom.getRoomKey());
        }

        return vAllRooms;
    }

    /**
     * Parcourt récursivement toutes les pièces à partir d'une pièce donnée.
     * Évite les cycles en utilisant un ensemble des pièces déjà visitées.
     * 
     * @param pRoom     La pièce actuelle à examiner
     * @param pVisited  Ensemble des pièces déjà visitées (pour éviter les cycles)
     * @param pAllRooms Liste pour stocker toutes les pièces trouvées
     */
    private static void collectRoomsRecursive(Room pRoom, Set<Room> pVisited, List<Room> pAllRooms) {
        if (pRoom == null || pVisited.contains(pRoom))
            return;
        pVisited.add(pRoom);
        pAllRooms.add(pRoom);
        for (Direction vDir : Direction.getAll()) {
            Room vExit = pRoom.getExit(vDir.toString());
            if (vExit != null) {
                collectRoomsRecursive(vExit, pVisited, pAllRooms);
            }
        }
    }

    /**
     * Met à jour les états des pièces à partir du XML.
     * Met à jour les portes verrouillées, les trappes et les objets de chaque
     * pièce.
     * 
     * @param pDoc Le document XML contenant les informations des pièces
     */
    private static void updateRoomsFromXML(final Document pDoc) {
        NodeList vRoomNodes = pDoc.getElementsByTagName("room");

        for (int i = 0; i < vRoomNodes.getLength(); i++) {
            Node vNode = vRoomNodes.item(i);

            // Ignorer les nœuds non-éléments (comme les nœuds texte, commentaires, etc.)
            if (vNode.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            Element vRoomElem = (Element) vNode;
            String vKey = vRoomElem.getAttribute("key");

            // Ignorer si la clé est vide ou null
            if (vKey == null || vKey.isEmpty()) {
                continue; // Ignorer silencieusement - ce sont probablement des nœuds d'espacement
            }

            Room vRoom = aRoomMap.get(vKey);
            if (vRoom == null) {
                System.err.println("   ⚠ Pièce non trouvée: " + vKey);
                continue;
            }

            System.out.println("   🔧 Mise à jour de: " + vKey);

            // Mettre à jour les portes verrouillées
            updateDoorsFromXML(vRoomElem, vRoom);

            // Mettre à jour les trappes
            updateTrapDoorsFromXML(vRoomElem, vRoom);

            // Mettre à jour les objets (vider et ré-ajouter)
            updateItemsFromXML(vRoomElem, vRoom);
        }
    }

    /**
     * Met à jour l'état des portes à partir du XML.
     * Restaure l'état verrouillé/déverrouillé de chaque porte.
     * 
     * @param pRoomElem L'élément XML de la pièce
     * @param pRoom     La pièce à mettre à jour
     */
    private static void updateDoorsFromXML(final Element pRoomElem, final Room pRoom) {
        NodeList vDoorNodes = pRoomElem.getElementsByTagName("door");

        for (int j = 0; j < vDoorNodes.getLength(); j++) {
            Element vDoorElem = (Element) vDoorNodes.item(j);
            String vDir = vDoorElem.getAttribute("direction");
            boolean vLocked = Boolean.parseBoolean(vDoorElem.getAttribute("locked"));

            if (pRoom.hasDoor(vDir)) {
                Door vDoor = pRoom.getDoor(vDir);
                vDoor.setLocked(vLocked);
                System.out.println("     🔒 Porte " + vDir + " verrouillée=" + vLocked);
            }
        }
    }

    /**
     * Met à jour les trappes à partir du XML.
     * Les trappes sont des sorties à sens unique.
     * 
     * @param pRoomElem L'élément XML de la pièce
     * @param pRoom     La pièce à mettre à jour
     */
    private static void updateTrapDoorsFromXML(final Element pRoomElem, final Room pRoom) {
        NodeList vTrapNodes = pRoomElem.getElementsByTagName("trapdoor");

        for (int j = 0; j < vTrapNodes.getLength(); j++) {
            Element vTrapElem = (Element) vTrapNodes.item(j);
            String vDir = vTrapElem.getAttribute("direction");
            pRoom.setTrapDoor(vDir);
        }
    }

    /**
     * Met à jour les objets de la pièce (remplace l'existant).
     * Vide d'abord tous les objets présents, puis ajoute ceux du XML.
     * 
     * @param pRoomElem L'élément XML de la pièce
     * @param pRoom     La pièce à mettre à jour
     */
    private static void updateItemsFromXML(final Element pRoomElem, final Room pRoom) {
        // Vider les objets existants
        for (Item vItem : pRoom.getItems().getItems()) {
            pRoom.removeItem(vItem);
        }

        NodeList vItemNodes = pRoomElem.getElementsByTagName("item");

        for (int j = 0; j < vItemNodes.getLength(); j++) {
            Element vItemElem = (Element) vItemNodes.item(j);
            Item vItem = createItemFromXML(vItemElem);

            if (vItem != null) {
                pRoom.addItem(vItem);
                System.out.println("     📦 Item mis à jour: " + vItem.getType().name());
            }
        }
    }

    /**
     * Met à jour l'état des personnages statiques (helpGiven, etc.).
     * Restaure l'état des personnages qui ne se déplacent pas.
     * 
     * @param pDoc Le document XML contenant les informations des personnages
     */
    private static void updateStaticCharacterStates(final Document pDoc) {
        NodeList vStaticCharNodes = pDoc.getElementsByTagName("staticCharacters");
        if (vStaticCharNodes.getLength() == 0)
            return;

        Element vStaticCharsElem = (Element) vStaticCharNodes.item(0);
        NodeList vCharNodes = vStaticCharsElem.getElementsByTagName("character");

        System.out.println("   👥 Mise à jour de " + vCharNodes.getLength() + " personnages statiques...");

        for (int i = 0; i < vCharNodes.getLength(); i++) {
            Element vCharElem = (Element) vCharNodes.item(i);

            String vNameKey = getTagValue("nameKey", vCharElem);
            String vRoomKey = getTagValue("currentRoom", vCharElem);
            String vHelpGivenStr = getTagValue("helpGiven", vCharElem);

            Room vRoom = aRoomMap.get(vRoomKey);
            if (vRoom != null) {
                Character vChar = vRoom.getCharacter(vNameKey);
                if (vChar != null && vHelpGivenStr != null) {
                    vChar.setHelpGiven(Boolean.parseBoolean(vHelpGivenStr));
                    System.out.println("     ✅ État restauré pour: " + vNameKey);
                }
            }
        }
    }

    /**
     * Crée le joueur à partir du XML (modifié pour utiliser les pièces existantes).
     * Restaure la pièce actuelle, le poids maximum, l'historique et l'inventaire.
     * 
     * @param pPlayerElem L'élément XML du joueur
     * @return Le joueur créé avec son état restauré
     */
    private static Player createPlayerFromXML(final Element pPlayerElem) {
        System.out.println("   👤 Création du joueur...");

        // Récupérer la pièce actuelle depuis le XML
        String vCurrentRoomKey = getTagValue("currentRoom", pPlayerElem);
        Room vCurrentRoom = aRoomMap.get(vCurrentRoomKey);

        if (vCurrentRoom == null && !aRoomMap.isEmpty()) {
            vCurrentRoom = aRoomMap.values().iterator().next();
            System.err.println("   ⚠ Pièce actuelle non trouvée, utilisation de: " + vCurrentRoom.getRoomKey());
        }

        Player vPlayer = new Player(vCurrentRoom);

        // Restaurer le poids maximum (AVANT l'inventaire)
        String vMaxWeightStr = getTagValue("maxWeight", pPlayerElem);
        if (vMaxWeightStr != null) {
            try {
                vPlayer.setMaxWeight(Integer.parseInt(vMaxWeightStr));
            } catch (NumberFormatException e) {
                // Conserver la valeur par défaut
            }
        }

        // Restaurer l'historique
        restorePlayerHistory(pPlayerElem, vPlayer);

        // Restaurer l'inventaire (APRÈS le poids maximum)
        restorePlayerInventory(pPlayerElem, vPlayer);

        return vPlayer;
    }

    // ==================== CRÉATION D'OBJETS ====================

    /**
     * Crée un objet à partir d'un élément XML.
     * Gère les différents types d'objets (Beamer, DivingSuit, Torch, etc.).
     * 
     * @param pItemElem L'élément XML de l'objet
     * @return L'objet créé ou null en cas d'erreur
     */
    private static Item createItemFromXML(final Element pItemElem) {
        String vType = pItemElem.getAttribute("type");
        if (vType == null || vType.isEmpty()) {
            System.err.println("       ⚠ Item sans type, ignoré");
            return null;
        }

        try {
            ItemType vItemType = ItemType.valueOf(vType);
            Item vItem = vItemType.createItem();

            System.out.println("       🔧 Configuration item: " + vType);

            // Restaurer l'état spécifique selon le type d'objet
            if (vItem instanceof Beamer) {
                return configureBeamerFromXML((Beamer) vItem, pItemElem);
            } else if (vItem instanceof MagicCookie) {
                return vItem;
            } else if (vItem instanceof DivingSuit) {
                return configureDivingSuitFromXML((DivingSuit) vItem, pItemElem);
            } else if (vItem instanceof Torch) {
                return configureTorchFromXML((Torch) vItem, pItemElem);
            } else {
                return vItem;
            }

        } catch (IllegalArgumentException e) {
            System.err.println("       ⚠ Type d'item inconnu: " + vType);
            return null;
        }
    }

    /**
     * Configure un Beamer à partir du XML.
     * Restaure l'état de charge et la pièce mémorisée.
     * 
     * @param pBeamer Le beamer à configurer
     * @param pElem   L'élément XML du beamer
     * @return Le beamer configuré
     */
    private static Beamer configureBeamerFromXML(final Beamer pBeamer, final Element pElem) {
        if (pElem.hasAttribute("charged")) {
            boolean vCharged = Boolean.parseBoolean(pElem.getAttribute("charged"));

            if (vCharged && pElem.hasAttribute("room")) {
                String vRoomKey = pElem.getAttribute("room");
                Room vRoom = aRoomMap.get(vRoomKey);
                if (vRoom != null) {
                    // Méthode spéciale pour forcer l'état sans passer par le jeu
                    pBeamer.forceCharge(vRoom);
                    System.out.println("       ⚡ Beamer chargé vers: " + vRoomKey);
                }
            }
        }
        return pBeamer;
    }

    /**
     * Configure un DivingSuit (combinaison de plongée) à partir du XML.
     * Restaure le niveau d'oxygène.
     * 
     * @param pSuit La combinaison à configurer
     * @param pElem L'élément XML de la combinaison
     * @return La combinaison configurée
     */
    private static DivingSuit configureDivingSuitFromXML(final DivingSuit pSuit, final Element pElem) {
        if (pElem.hasAttribute("oxygen")) {
            int vOxygen = Integer.parseInt(pElem.getAttribute("oxygen"));
            pSuit.setOxygenLevel(vOxygen);
        }
        return pSuit;
    }

    /**
     * Configure une Torche à partir du XML.
     * Restaure le niveau de batterie.
     * 
     * @param pTorch La torche à configurer
     * @param pElem  L'élément XML de la torche
     * @return La torche configurée
     */
    private static Torch configureTorchFromXML(final Torch pTorch, final Element pElem) {
        if (pElem.hasAttribute("battery")) {
            int vBattery = Integer.parseInt(pElem.getAttribute("battery"));
            pTorch.setBatteryLife(vBattery);
        }
        return pTorch;
    }

    // ==================== RESTAURATION DU JOUEUR ====================

    /**
     * Restaure l'historique du joueur.
     * Reconstruit la pile des pièces précédemment visitées.
     * 
     * @param pPlayerElem L'élément XML du joueur
     * @param vPlayer     Le joueur à configurer
     * @return true si au moins une entrée a été restaurée
     */
    private static boolean restorePlayerHistory(final Element pPlayerElem, final Player vPlayer) {
        NodeList vHistoryNodes = pPlayerElem.getElementsByTagName("history");
        if (vHistoryNodes.getLength() == 0) {
            System.out.println("     📜 Pas de balise history trouvée");
            return false;
        }

        Element vHistoryElem = (Element) vHistoryNodes.item(0);
        NodeList vRoomNodes = vHistoryElem.getElementsByTagName("room");

        System.out.println("     📜 " + vRoomNodes.getLength() + " entrées d'historique trouvées");

        // L'historique par défaut contient déjà la pièce actuelle
        // On vide d'abord l'historique (sauf la pièce actuelle)
        while (vPlayer.getHistorySize() > 1) {
            vPlayer.goBack();
        }

        if (vRoomNodes.getLength() == 0) {
            return true; // Historique vide mais OK
        }

        // Reconstruire l'historique
        List<Room> vHistoryRooms = new ArrayList<>();
        for (int i = 0; i < vRoomNodes.getLength(); i++) {
            Node vRoomNode = vRoomNodes.item(i);
            if (vRoomNode == null)
                continue;

            String vRoomKey = vRoomNode.getTextContent();
            if (vRoomKey == null || vRoomKey.trim().isEmpty())
                continue;

            vRoomKey = vRoomKey.trim();
            Room vRoom = aRoomMap.get(vRoomKey);
            if (vRoom != null) {
                vHistoryRooms.add(vRoom);
                System.out.println("       📍 Pièce d'historique: " + vRoomKey);
            } else {
                System.err.println("     ⚠ Pièce d'historique non trouvée: " + vRoomKey);
            }
        }

        // Ajouter dans l'ordre inverse (car pushHistory ajoute au sommet)
        for (int i = vHistoryRooms.size() - 1; i >= 0; i--) {
            Room vRoom = vHistoryRooms.get(i);
            if (!vRoom.equals(vPlayer.getCurrentRoom())) {
                vPlayer.pushHistory(vRoom);
                System.out.println("       ➕ Ajout à l'historique: " + vRoom.getRoomKey());
            }
        }

        return !vHistoryRooms.isEmpty();
    }

    /**
     * Restaure l'inventaire du joueur.
     * Ajoute tous les objets sauvegardés à l'inventaire du joueur et met à jour le
     * poids.
     * 
     * @param pPlayerElem L'élément XML du joueur
     * @param vPlayer     Le joueur à configurer
     * @return true si au moins un objet a été restauré
     */
    private static boolean restorePlayerInventory(final Element pPlayerElem, final Player vPlayer) {
        NodeList vInvNodes = pPlayerElem.getElementsByTagName("inventory");
        if (vInvNodes.getLength() == 0) {
            System.out.println("     🎒 Pas de balise inventory trouvée");
            return false;
        }

        Element vInvElem = (Element) vInvNodes.item(0);
        NodeList vItemNodes = vInvElem.getElementsByTagName("item");

        System.out.println("     🎒 " + vItemNodes.getLength() + " objets trouvés");

        // Réinitialiser le poids actuel du joueur
        vPlayer.getInventory().clear();

        // Réinitialiser le compteur de poids
        // Note: Il faut accéder directement au champ ou utiliser un setter
        // Solution temporaire: recalculer le poids après ajout

        int vTotalWeight = 0;
        int vCount = 0;

        for (int i = 0; i < vItemNodes.getLength(); i++) {
            Node vItemNode = vItemNodes.item(i);
            if (!(vItemNode instanceof Element))
                continue;

            Element vItemElem = (Element) vItemNode;
            Item vItem = createItemFromXML(vItemElem);

            if (vItem != null) {
                // Ajouter directement à l'inventaire
                vPlayer.getInventory().addItem(vItem);
                vTotalWeight += vItem.getWeight();
                vCount++;
                System.out
                        .println("       📦 Item ajouté: " + vItem.getType().name() + " (" + vItem.getWeight() + "g)");
            }
        }

        // Mettre à jour le poids actuel du joueur
        // Nécessite d'ajouter une méthode setCurrentWeight dans Player
        vPlayer.setCurrentWeight(vTotalWeight);

        System.out.println("     ⚖️ Poids total restauré: " + vTotalWeight + "g / " + vPlayer.getMaxWeight() + "g");

        return vCount > 0;
    }

    // ==================== PERSONNAGES MOBILES ====================

    /**
     * Crée les personnages mobiles à partir du XML.
     * Les personnages mobiles peuvent se déplacer selon différentes stratégies.
     * 
     * @param pCharsElem L'élément XML des personnages mobiles
     * @return La liste des personnages mobiles créés
     */
    private static List<MovingCharacter> createMovingCharactersFromXML(final Element pCharsElem) {
        List<MovingCharacter> vChars = new ArrayList<>();
        NodeList vCharNodes = pCharsElem.getElementsByTagName("character");

        System.out.println("     " + vCharNodes.getLength() + " personnages trouvés");

        for (int i = 0; i < vCharNodes.getLength(); i++) {
            Element vCharElem = (Element) vCharNodes.item(i);

            String vNameKey = getTagValue("nameKey", vCharElem);
            String vRoomKey = getTagValue("currentRoom", vCharElem);
            String vStrategyStr = getTagValue("strategy", vCharElem);

            if (vNameKey == null) {
                System.err.println("     ⚠ Personnage sans nameKey, ignoré");
                continue;
            }
            if (vRoomKey == null) {
                System.err.println("     ⚠ Personnage " + vNameKey + " sans currentRoom, ignoré");
                continue;
            }
            if (vStrategyStr == null) {
                System.err.println("     ⚠ Personnage " + vNameKey + " sans strategy, ignoré");
                continue;
            }

            Room vRoom = aRoomMap.get(vRoomKey);
            if (vRoom == null) {
                System.err.println("     ⚠ Pièce non trouvée pour personnage " + vNameKey + ": " + vRoomKey);
                continue;
            }

            try {
                MovementStrategy vStrategy = MovementStrategy.valueOf(vStrategyStr);

                MovingCharacter vChar = new MovingCharacter(vNameKey, vNameKey + "_desc", vRoom, vStrategy);

                // Restaurer le chemin si nécessaire
                restoreCharacterPath(vCharElem, vChar);

                vChars.add(vChar);
                System.out.println("     🚶 Personnage créé: " + vNameKey + " [" + vStrategy + "]");

            } catch (IllegalArgumentException e) {
                System.err.println("     ⚠ Stratégie invalide pour " + vNameKey + ": " + vStrategyStr);
            }
        }

        return vChars;
    }

    /**
     * Restaure le chemin d'un personnage mobile.
     * Pour les personnages suivant un chemin prédéfini (FOLLOW_PATH).
     * 
     * @param vCharElem L'élément XML du personnage
     * @param vChar     Le personnage à configurer
     */
    private static void restoreCharacterPath(final Element vCharElem, final MovingCharacter vChar) {
        NodeList vPathNodes = vCharElem.getElementsByTagName("path");
        if (vPathNodes.getLength() == 0)
            return;

        Element vPathElem = (Element) vPathNodes.item(0);
        NodeList vPathRoomNodes = vPathElem.getElementsByTagName("room");

        List<Room> vPath = new ArrayList<>();
        for (int j = 0; j < vPathRoomNodes.getLength(); j++) {
            String vPathRoomKey = vPathRoomNodes.item(j).getTextContent();
            Room vPathRoom = aRoomMap.get(vPathRoomKey);
            if (vPathRoom != null) {
                vPath.add(vPathRoom);
            }
        }

        if (!vPath.isEmpty()) {
            vChar.setPath(vPath);

            String vPathIndex = getTagValue("pathIndex", vCharElem);
            if (vPathIndex != null) {
                vChar.setPathIndex(Integer.parseInt(vPathIndex));
            }

            System.out.println("       🗺️ Chemin restauré: " + vPath.size() + " étapes");
        }
    }

    // ==================== MÉTHODES UTILITAIRES ====================

    /**
     * Récupère la valeur textuelle d'une balise XML.
     * Parcourt les nœuds enfants pour trouver le contenu texte.
     * 
     * @param pTag     Le nom de la balise à rechercher
     * @param pElement L'élément parent contenant la balise
     * @return La valeur textuelle de la balise, ou null si elle n'existe pas
     */
    private static String getTagValue(final String pTag, final Element pElement) {
        NodeList vNodes = pElement.getElementsByTagName(pTag);
        if (vNodes.getLength() == 0)
            return null;

        Node vNode = vNodes.item(0);
        if (vNode == null)
            return null;

        // Récupérer le premier enfant texte
        Node vChild = vNode.getFirstChild();
        if (vChild == null)
            return "";

        return vChild.getNodeValue();
    }
}