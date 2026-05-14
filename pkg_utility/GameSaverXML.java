package pkg_utility;

import pkg_core.GameEngine;
import pkg_gameplay.Door;
import pkg_gameplay.Room;
import pkg_gameplay.TransporterRoom;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import pkg_characters.MovingCharacter;
import pkg_characters.Player;
import pkg_items.Beamer;
import pkg_items.Item;

/**
 * GameSaverXML - Sauvegarde l'état du jeu au format XML avec les noms d'images.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class GameSaverXML {
    /** Répertoire des sauvegardes */
    private static final String SAVE_DIR = "saves/";
    /** Indentation */
    private static final String INDENT = "  ";

    /**
     * Constructeur par défaut de la classe GameSaverXML.
     */
    public GameSaverXML() {
        // Constructeur par défaut
    }

    /**
     * Sauvegarde l'état du jeu au format XML.
     * 
     * @param pEngine   Le moteur de jeu
     * @param pFileName Nom du fichier (sans extension)
     * @return true si la sauvegarde a réussi
     */
    public static boolean saveGameToXML(final GameEngine pEngine, final String pFileName) {
        // Créer le répertoire de sauvegarde
        File vSaveDir = new File(SAVE_DIR);
        if (!vSaveDir.exists()) {
            vSaveDir.mkdirs();
        }

        String vFilePath = SAVE_DIR + pFileName + ".xml";

        try (PrintWriter vOut = new PrintWriter(new FileWriter(vFilePath))) {

            vOut.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            vOut.println("<?xml-stylesheet type=\"text/xsl\" href=\"game-save.xsl\"?>");

            LocalDateTime vNow = LocalDateTime.now();
            vOut.printf("<game saveDate=\"%s\" timeLeft=\"%d\">%n",
                    vNow.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    pEngine.getTimeLeft());

            // Exporter les pièces avec leurs noms d'images
            exportRooms(pEngine, vOut, 1);

            // Exporter le joueur
            exportPlayer(pEngine.getPlayer(), vOut, 1);

            // Exporter les personnages mobiles
            exportMovingCharacters(pEngine.getMovingCharacters(), vOut, 1);

            vOut.println("</game>");

            System.out.println("✅ Jeu sauvegardé en XML: " + vFilePath);
            return true;

        } catch (IOException e) {
            System.err.println("❌ Erreur de sauvegarde XML: " + e.getMessage());
            return false;
        }
    }

    /**
     * Exporte toutes les pièces du jeu avec leurs noms d'images.
     * 
     * @param pEngine Le moteur de jeu
     * @param pOut    Le flux de sortie
     * @param pIndent L'indentation à utiliser
     */
    private static void exportRooms(final GameEngine pEngine, final PrintWriter pOut, int pIndent) {
        String vIndent = INDENT.repeat(pIndent);
        pOut.println(vIndent + "<rooms>");

        // Récupérer toutes les pièces
        List<Room> vAllRooms = getAllRooms(pEngine.getPlayer().getCurrentRoom());

        for (Room vRoom : vAllRooms) {
            // IMPORTANT: Sauvegarder le nom de l'image dans l'attribut image
            pOut.printf(vIndent + INDENT + "<room key=\"%s\" image=\"%s\">%n",
                    vRoom.getRoomKey(), vRoom.getImageName());

            // Type de pièce
            if (vRoom instanceof TransporterRoom) {
                pOut.println(vIndent + INDENT + INDENT + "<type>transporter</type>");
            } else {
                pOut.println(vIndent + INDENT + INDENT + "<type>normal</type>");
            }

            // Sorties
            pOut.println(vIndent + INDENT + INDENT + "<exits>");
            exportExits(vRoom, pOut, vIndent + INDENT + INDENT);
            pOut.println(vIndent + INDENT + INDENT + "</exits>");

            // Trappes
            exportTrapDoors(vRoom, pOut, vIndent + INDENT + INDENT);

            // Portes verrouillées
            exportDoors(vRoom, pOut, vIndent + INDENT + INDENT);

            // Items
            exportItems(vRoom, pOut, vIndent + INDENT + INDENT);

            pOut.println(vIndent + INDENT + "</room>");
        }

        pOut.println(vIndent + "</rooms>");
    }

    /**
     * Exporte les sorties d'une pièce.
     * 
     * @param pRoom   La pièce dont on exporte les sorties
     * @param pOut    Le flux de sortie
     * @param pIndent L'indentation à utiliser
     */
    private static void exportExits(final Room pRoom, final PrintWriter pOut, final String pIndent) {
        for (Direction vDir : Direction.getAll()) {
            Room vExit = pRoom.getExit(vDir.toString());
            if (vExit != null) {
                pOut.printf(pIndent + INDENT +
                        "<exit direction=\"%s\" target=\"%s\"/>%n",
                        vDir.toString(), vExit.getRoomKey());
            }
        }
    }

    /**
     * Exporte les trappes d'une pièce.
     * 
     * @param pRoom   La pièce dont on exporte les trappes
     * @param pOut    Le flux de sortie
     * @param pIndent L'indentation à utiliser
     */
    private static void exportTrapDoors(final Room pRoom, final PrintWriter pOut, final String pIndent) {
        for (Direction vDir : Direction.getAll()) {
            if (pRoom.isTrapDoor(vDir.toString())) {
                pOut.printf(pIndent + INDENT +
                        "<trapdoor direction=\"%s\"/>%n", vDir.toString());
            }
        }
    }

    /**
     * Exporte les portes verrouillées d'une pièce.
     * 
     * @param pRoom   La pièce dont on exporte les portes
     * @param pOut    Le flux de sortie
     * @param pIndent L'indentation à utiliser
     */
    private static void exportDoors(final Room pRoom, final PrintWriter pOut, final String pIndent) {
        for (Direction vDir : Direction.getAll()) {
            if (pRoom.hasDoor(vDir.toString())) {
                Door vDoor = pRoom.getDoor(vDir.toString());
                pOut.printf(pIndent + INDENT +
                        "<door direction=\"%s\" id=\"%s\" key=\"%s\" locked=\"%b\" autolock=\"%b\"/>%n",
                        vDir.toString(), vDoor.getDoorId(), vDoor.getRequiredKey().name(),
                        vDoor.isLocked(), vDoor.isAutoLock());
            }
        }
    }

    /**
     * Exporte les items d'une pièce.
     * 
     * @param pRoom   La pièce dont on exporte les items
     * @param pOut    Le flux de sortie
     * @param pIndent L'indentation à utiliser
     */
    private static void exportItems(final Room pRoom, final PrintWriter pOut, final String pIndent) {
        if (pRoom.getItems().isEmpty())
            return;

        pOut.println(pIndent + INDENT + "<items>");
        for (Item vItem : pRoom.getItems().getItems()) {
            exportItem(vItem, pOut, pIndent + INDENT + INDENT);
        }
        pOut.println(pIndent + INDENT + "</items>");
    }

    /**
     * Exporte un item avec ses attributs spécifiques.
     * 
     * @param pItem   L'item à exporter
     * @param pOut    Le flux de sortie
     * @param pIndent L'indentation à utiliser
     */
    private static void exportItem(final Item pItem, final PrintWriter pOut, final String pIndent) {
        if (pItem instanceof Beamer) {
            Beamer vBeamer = (Beamer) pItem;
            if (vBeamer.isCharged() && vBeamer.getMemorizedRoom() != null) {
                pOut.printf(pIndent +
                        "<item type=\"%s\" charged=\"true\" room=\"%s\"/>%n",
                        pItem.getType().name(),
                        vBeamer.getMemorizedRoom().getRoomKey());
            } else {
                pOut.printf(pIndent +
                        "<item type=\"%s\" charged=\"false\"/>%n",
                        pItem.getType().name());
            }
        } else {
            pOut.printf(pIndent +
                    "<item type=\"%s\"/>%n",
                    pItem.getType().name());
        }
    }

    /**
     * Exporte l'état du joueur.
     * 
     * @param pPlayer Le joueur à exporter
     * @param pOut    Le flux de sortie
     * @param pIndent L'indentation à utiliser
     */
    private static void exportPlayer(final Player pPlayer, final PrintWriter pOut, int pIndent) {
        String vIndent = INDENT.repeat(pIndent);
        pOut.println(vIndent + "<player>");

        pOut.printf(vIndent + INDENT +
                "<currentRoom>%s</currentRoom>%n",
                pPlayer.getCurrentRoom().getRoomKey());

        pOut.printf(vIndent + INDENT +
                "<maxWeight>%d</maxWeight>%n", pPlayer.getMaxWeight());

        pOut.printf(vIndent + INDENT +
                "<currentWeight>%d</currentWeight>%n", pPlayer.getCurrentWeight());

        // Historique
        pOut.println(vIndent + INDENT + "<history>");
        for (Room vRoom : pPlayer.getHistory()) {
            pOut.printf(vIndent + INDENT + INDENT +
                    "<room>%s</room>%n", vRoom.getRoomKey());
        }
        pOut.println(vIndent + INDENT + "</history>");

        // Inventaire
        if (!pPlayer.getInventory().isEmpty()) {
            pOut.println(vIndent + INDENT + "<inventory>");
            for (Item vItem : pPlayer.getInventory().getItems()) {
                exportItem(vItem, pOut, vIndent + INDENT + INDENT);
            }
            pOut.println(vIndent + INDENT + "</inventory>");
        }

        pOut.println(vIndent + "</player>");
    }

    /**
     * Exporte les personnages mobiles.
     * 
     * @param pChars  La liste des personnages à exporter
     * @param pOut    Le flux de sortie
     * @param pIndent L'indentation à utiliser
     */
    private static void exportMovingCharacters(final List<MovingCharacter> pChars,
            final PrintWriter pOut, int pIndent) {
        if (pChars.isEmpty())
            return;

        String vIndent = INDENT.repeat(pIndent);
        pOut.println(vIndent + "<movingCharacters>");

        for (MovingCharacter vChar : pChars) {
            pOut.println(vIndent + INDENT + "<character>");

            pOut.printf(vIndent + INDENT + INDENT +
                    "<nameKey>%s</nameKey>%n", vChar.getName());

            pOut.printf(vIndent + INDENT + INDENT +
                    "<currentRoom>%s</currentRoom>%n",
                    vChar.getCurrentRoom().getRoomKey());

            pOut.printf(vIndent + INDENT + INDENT +
                    "<strategy>%s</strategy>%n", vChar.getStrategy().name());

            if (vChar.getPath() != null && !vChar.getPath().isEmpty()) {
                pOut.println(vIndent + INDENT + INDENT + "<path>");
                for (Room vRoom : vChar.getPath()) {
                    pOut.printf(vIndent + INDENT + INDENT + INDENT +
                            "<room>%s</room>%n", vRoom.getRoomKey());
                }
                pOut.println(vIndent + INDENT + INDENT + "</path>");
                pOut.printf(vIndent + INDENT + INDENT +
                        "<pathIndex>%d</pathIndex>%n", vChar.getPathIndex());
            }

            pOut.println(vIndent + INDENT + "</character>");
        }

        pOut.println(vIndent + "</movingCharacters>");
    }

    /**
     * Récupère toutes les pièces du jeu.
     * 
     * @param pStartRoom La pièce de départ pour la collecte
     * @return Une liste de toutes les pièces du jeu
     */
    private static List<Room> getAllRooms(final Room pStartRoom) {
        List<Room> vAllRooms = new ArrayList<>();
        Set<Room> vVisited = new HashSet<>();
        collectRooms(pStartRoom, vVisited, vAllRooms);
        return vAllRooms;
    }

    /**
     * Collecte toutes les pièces du jeu à partir d'une pièce de départ.
     * 
     * @param pRoom     La pièce de départ pour la collecte
     * @param pVisited  L'ensemble des pièces déjà visitées
     * @param pAllRooms La liste de toutes les pièces du jeu
     */
    private static void collectRooms(final Room pRoom,
            Set<Room> pVisited,
            List<Room> pAllRooms) {
        if (pRoom == null || pVisited.contains(pRoom))
            return;

        pVisited.add(pRoom);
        pAllRooms.add(pRoom);

        for (Direction vDir : Direction.getAll()) {
            Room vExit = pRoom.getExit(vDir.toString());
            if (vExit != null) {
                collectRooms(vExit, pVisited, pAllRooms);
            }
        }
    }
}