package pkg_core;

import pkg_command.Command;

import pkg_gameplay.Room;
import pkg_gameplay.TransporterRoom;
import pkg_ui_components.AppButton;
import pkg_ui_components.CharacterCallout;
import pkg_ui_components.CharacterInteractionPopup;
import pkg_ui_components.ItemDetailsDialog;
import pkg_ui_components.TransitionPanel;
import pkg_characters.Character;
import pkg_characters.MovingCharacter;
import pkg_characters.Player;
import pkg_items.Item;
import pkg_utility.Direction;
import pkg_utility.Lang;
import pkg_utility.MusicPlayer;
import pkg_utility.SapiTTS;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FlowLayout;
import java.awt.BorderLayout;

/**
 * Cette classe fait partie de l'application "Station Abyssale-6".
 * "Station Abyssale-6" est un jeu d'aventure très simple, basé sur du texte.
 * Cette classe crée toutes les pièces, crée l'analyseur syntaxique et démarre
 * le jeu. Elle évalue et exécute également les commandes que
 * l'analyseur syntaxique retourne.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class GameEngine {
    /** Analyseur syntaxique */
    private Parser aParser;
    /** Musique de fond */
    private final MusicPlayer aMusicPlayer;
    /** Joueur */
    private Player aPlayer;
    /** Interface graphique */
    private final GameGUI aGui;
    /** Timer */
    private Timer aGameTimer;
    /** Temps restant */
    private int aTimeLeft = 600;
    /** Liste de tous les personnages mobiles */
    private List<MovingCharacter> aMovingCharacters;
    /** Liste des TransporterRooms dans le jeu */
    private List<TransporterRoom> aTransporterRooms;
    /** Mode sans affichage graphique (pour les tests) */
    private boolean aHeadlessMode;

    /**
     * Constructeur pour les objets de la classe GameEngine (mode graphique normal)
     */
    public GameEngine() {
        this.aHeadlessMode = false;
        this.aParser = new Parser();
        this.aGui = new GameGUI(this);
        this.aMusicPlayer = new MusicPlayer();
        this.aMusicPlayer.playBackgroundMusic("theme.wav");
        this.aMovingCharacters = new ArrayList<>();
        this.aTransporterRooms = new ArrayList<>();
        this.createRooms();
        this.printWelcome();
        // Configuration du minuteur principal (déclenchement toutes les 1000ms)
        this.aGameTimer = new Timer(1000, e -> this.updateTimer());
        this.aGameTimer.start();
    }

    /**
     * Constructeur spécial pour le chargement ou le mode headless (sans interface
     * graphique).
     * 
     * @param pHeadlessMode true pour un moteur sans GUI (tests), false pour un
     *                      chargement normal
     */
    public GameEngine(boolean pHeadlessMode) {
        this.aHeadlessMode = pHeadlessMode;
        this.aParser = new Parser();
        this.aGui = null; // Pas d'interface graphique en mode headless
        this.aMusicPlayer = new MusicPlayer();
        this.aMovingCharacters = new ArrayList<>();
        this.aTransporterRooms = new ArrayList<>();
        this.aGameTimer = null; // Sera recréé si nécessaire
        initGameWorld(); // Initialiser le monde du jeu sans GUI
    }

    /**
     * Initialise le monde du jeu (pièces, joueur, personnages) sans dépendances
     * graphiques.
     * Utilisé aussi bien par le mode headless que par le chargement de partie.
     */
    public void initGameWorld() {
        // Créer les pièces (la méthode static Room.createRooms() est indépendante de la
        // GUI)
        Room[] vRooms = Room.createRooms();
        Room vStartingRoom = vRooms[0]; // vSas
        Room vSerre = vRooms[1]; // vSerre

        // Collecter toutes les pièces pour initialiser les TransporterRooms
        List<Room> vAllRooms = collectAllRooms(vStartingRoom);
        initializeAllTransporterRooms(vAllRooms);

        // Créer le joueur
        this.aPlayer = new Player(vStartingRoom);
        this.aPlayer.setGameEngine(this);

        // Créer le personnage "stalker" qui suit le joueur (indépendant de la GUI)
        new MovingCharacter("character_stalker", "character_stalker_desc", vSerre,
                MovingCharacter.MovementStrategy.FOLLOW_PLAYER)
                .setGreeting("character_stalker_greeting")
                .setTargetPlayer(this.aPlayer);

        // Récupérer tous les personnages mobiles
        collectMovingCharacters(vStartingRoom);

        // Placer le joueur dans sa pièce de départ sans utiliser la GUI
        this.aPlayer.setCurrentRoom(vStartingRoom);
        if (!this.aHeadlessMode) {
            // En mode graphique normal, on peut afficher la description
            this.log(vStartingRoom.getLongDescription());
        }
    }

    /**
     * Définit le temps restant (pour la restauration).
     * 
     * @param pTimeLeft Le temps restant à définir
     */
    public void setTimeLeft(final int pTimeLeft) {
        this.aTimeLeft = pTimeLeft;
    }

    /**
     * Définit le joueur (pour la restauration).
     * 
     * @param pPlayer Le joueur à définir
     */
    public void setPlayer(final Player pPlayer) {
        this.aPlayer = pPlayer;
    }

    /**
     * Retourne l'interface graphique du jeu.
     * 
     * @return L'interface graphique du jeu
     */
    public GameGUI getGui() {
        return this.aGui;
    }

    /**
     * Affiche le message d'accueil pour le joueur.
     */
    private void printWelcome() {
        this.log(Lang.localizableString("welcome"));
        this.printLocationInfo();
        this.showImage(this.aPlayer.getCurrentRoom().getImageName());

          // Utiliser un Timer Swing pour retarder la parole
        Timer timer = new Timer(100, e -> {
            // Exécuter la parole dans un thread séparé pour ne pas bloquer l'interface
            new Thread(() -> {
                SapiTTS.speak(Lang.localizableString("welcome"), true);
            }).start();
        });
        timer.setRepeats(false); // S'assurer qu'il ne s'exécute qu'une seule fois
        timer.start();
    }

    /**
     * Crée toutes les pièces et relie leurs sorties entre elles (mode graphique
     * normal).
     */
    private void createRooms() {
        // Réutiliser l'initialisation du monde
        initGameWorld();

        // Mettre à jour l'interface graphique avec les éléments créés
        if (!this.aHeadlessMode && this.aGui != null) {
            this.aGui.setPlayer(this.aPlayer);
            this.aGui.updateRoom(this.aPlayer.getCurrentRoom().getShortDescription());
            this.aGui.updateTimer(this.aTimeLeft);
            this.setPlayerRoom(this.aPlayer.getCurrentRoom()); // déclenche aussi l'affichage
        }
    }

    /**
     * Collecte toutes les pièces du jeu de manière récursive.
     * 
     * @param pStartRoom La pièce de départ
     * @return Liste de toutes les pièces du jeu
     */
    private List<Room> collectAllRooms(Room pStartRoom) {
        Set<Room> vVisited = new HashSet<>();
        List<Room> vAllRooms = new ArrayList<>();
        collectAllRoomsRecursive(pStartRoom, vVisited, vAllRooms);
        return vAllRooms;
    }

    /**
     * Méthode récursive pour collecter toutes les pièces.
     * 
     * @param pRoom     La pièce actuelle
     * @param pVisited  Ensemble des pièces visitées
     * @param pAllRooms Liste pour stocker toutes les pièces
     */
    private void collectAllRoomsRecursive(Room pRoom, Set<Room> pVisited, List<Room> pAllRooms) {
        if (pRoom == null || pVisited.contains(pRoom))
            return;

        pVisited.add(pRoom);
        pAllRooms.add(pRoom);

        // Parcourir récursivement les sorties
        for (Direction vDir : Direction.getAll()) {
            Room vExit = pRoom.getExit(vDir.toString());
            if (vExit != null) {
                collectAllRoomsRecursive(vExit, pVisited, pAllRooms);
            }
        }
    }

    /**
     * Initialise toutes les TransporterRooms avec la liste des destinations.
     * 
     * @param pAllRooms Liste de toutes les pièces du jeu
     */
    private void initializeAllTransporterRooms(List<Room> pAllRooms) {
        for (Room vRoom : pAllRooms) {
            if (vRoom instanceof TransporterRoom) {
                TransporterRoom vTransporterRoom = (TransporterRoom) vRoom;
                vTransporterRoom.initializeDestinations(pAllRooms);
                this.aTransporterRooms.add(vTransporterRoom);
            }
        }
    }

    /**
     * Active le mode test pour toutes les TransporterRooms.
     * 
     * @param pRoomKey La clé de la pièce de destination forcée
     */
    public void setTestModeForAllTransporters(String pRoomKey) {
        for (TransporterRoom vRoom : this.aTransporterRooms) {
            vRoom.setForcedDestination(pRoomKey);
        }
    }

    /**
     * Désactive le mode test pour toutes les TransporterRooms.
     */
    public void clearTestModeForAllTransporters() {
        for (TransporterRoom vRoom : this.aTransporterRooms) {
            vRoom.clearForcedDestination();
        }
    }

    /**
     * Vérifie si au moins une TransporterRoom est en mode test.
     * 
     * @return true si une TransporterRoom est en mode test
     */
    public boolean isTestModeActive() {
        for (TransporterRoom vRoom : this.aTransporterRooms) {
            if (vRoom.isTestMode()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Collecte tous les personnages mobiles du jeu.
     * 
     * @param pRoom La pièce actuelle
     */
    private void collectMovingCharacters(Room pRoom) {
        Set<Room> vVisited = new HashSet<>();
        Set<String> vAddedNames = new HashSet<>(); // Pour éviter les doublons
        this.aMovingCharacters.clear(); // Vider la liste avant de la remplir
        this.collectMovingCharactersRecursive(pRoom, vVisited, vAddedNames);
    }

    /**
     * Méthode récursive pour collecter les personnages mobiles.
     * 
     * @param pRoom       La pièce actuelle
     * @param pVisited    Pièces déjà visitées
     * @param pAddedNames Noms déjà ajoutés (pour éviter les doublons)
     */
    private void collectMovingCharactersRecursive(Room pRoom, Set<Room> pVisited, Set<String> pAddedNames) {
        if (pRoom == null || pVisited.contains(pRoom))
            return;

        pVisited.add(pRoom);

        // Chercher les personnages mobiles dans cette pièce
        for (Character vChar : pRoom.getCharacters()) {
            if (vChar instanceof MovingCharacter) {
                // Éviter les doublons par nom
                if (!pAddedNames.contains(vChar.getName())) {
                    this.aMovingCharacters.add((MovingCharacter) vChar);
                    pAddedNames.add(vChar.getName());
                }
            }
        }

        // Parcourir récursivement les sorties
        for (Direction vDir : Direction.getAll()) {
            Room vExit = pRoom.getExit(vDir.toString());
            if (vExit != null) {
                collectMovingCharactersRecursive(vExit, pVisited, pAddedNames);
            }
        }
    }

    /**
     * Traite une ligne de commande.
     * 
     * @param pCommandLine La ligne de commande
     */
    public void interpretCommand(final String pCommandLine) {
        this.log("> " + pCommandLine);

        Command vCommand = aParser.getCommand(pCommandLine);
        // En mode headless, on passe aGui = null. Les commandes doivent savoir gérer ce
        // cas.
        boolean vWantToQuit = vCommand.execute(this.aPlayer, this);

        if (vWantToQuit) {
            this.endGame();
        }
    }

    /**
     * Fait bouger tous les personnages mobiles.
     */
    public void moveAllCharacters() {
        for (MovingCharacter vChar : this.aMovingCharacters) {
            Room vOldRoom = vChar.getCurrentRoom();
            vChar.move();
            Room vNewRoom = vChar.getCurrentRoom();

            if (vOldRoom != vNewRoom) {
                Room vPlayerRoom = this.aPlayer.getCurrentRoom();

                if (vNewRoom == vPlayerRoom) {
                    this.loadAndAddCharacterOverlay(vChar);
                    this.log(String.format(Lang.localizableString("character_enters"),
                            Lang.localizableString(vChar.getName())));
                }

                if (vOldRoom == vPlayerRoom) {
                    if (!aHeadlessMode && aGui != null && aGui.getImagePanel() != null) {
                        aGui.getImagePanel().removeCharacterOverlay(vChar.getName());
                    }
                    this.log(String.format(Lang.localizableString("character_leaves"),
                            Lang.localizableString(vChar.getName())));
                }
            }
        }
    }

    /**
     * Ajoute un personnage mobile à la liste de suivi.
     * 
     * @param pChar Le personnage à ajouter
     */
    public void addMovingCharacter(final MovingCharacter pChar) {
        if (pChar != null && !this.aMovingCharacters.contains(pChar)) {
            this.aMovingCharacters.add(pChar);
        }
    }

    /**
     * Joue un son.
     * 
     * @param pSoundFile Le fichier de son à jouer
     */
    public void playSound(final String pSoundFile) {
        this.aMusicPlayer.playSFX(pSoundFile);
    }

    /**
     * Affiche des informations sur la pièce actuelle.
     */
    public void printLocationInfo() {
        log(this.aPlayer.getCurrentRoom().getLongDescription());
    }

    /**
     * Termine le jeu en désactivant l'interface utilisateur.
     */
    private void endGame() {
        this.log(Lang.localizableString("end_game"));
    }

    /**
     * Lit et exécute les commandes à partir d'un fichier texte.
     * Les lignes commençant par '#' sont ignorées (commentaires).
     * 
     * @param pFileName Le nom du fichier de test
     * @return true si toutes les vérifications ont réussi, false sinon
     */
    public boolean runTestFile(final String pFileName) {

        boolean vTestPassed = true;
        System.setProperty("debug", "true");
        if (this.aGui != null) {
            this.aGui.clearTerminal();
        }
        try (InputStream vStream = getClass().getClassLoader().getResourceAsStream(pFileName)) {
            if (vStream == null) {
                this.log(Lang.localizableString("test_error_file_not_found") + " : " + pFileName);
                return false;
            }
            try (Scanner vScanner = new Scanner(vStream, "UTF-8")) {
                int vLineNumber = 0;
                int vExecutedCommands = 0;

                while (vScanner.hasNextLine()) {
                    String vLine = vScanner.nextLine();
                    vLineNumber++;

                    if (vLine.trim().isEmpty()) {
                        continue;
                    }

                    if (vLine.trim().startsWith("#")) {
                        this.log(Lang.localizableString("test_line_comment") + " " + vLineNumber + " " + vLine);
                        continue;
                    }

                    if (vLine.trim().startsWith("roomis ")) {
                        String vExpectedRoom = vLine.substring(7).trim();
                        if (!this.verifyCurrentRoom(vExpectedRoom)) {
                            this.log("---  ERREUR LIGNE " + vLineNumber + " : Devrait être dans '" + vExpectedRoom
                                    + "'");
                            vTestPassed = false;
                        }
                        continue;
                    } else if (vLine.trim().startsWith("roomhas ")) {
                        String vExpectedItem = vLine.substring(8).trim();
                        if (!this.verifyRoomHasItem(vExpectedItem)) {
                            this.log("---  ERREUR LIGNE " + vLineNumber + " : La pièce devrait contenir '"
                                    + vExpectedItem
                                    + "'");
                            vTestPassed = false;
                        }
                        continue;
                    } else if (vLine.trim().startsWith("roomhasnot ")) {
                        String vUnexpectedItem = vLine.substring(11).trim();
                        if (!this.verifyRoomHasNotItem(vUnexpectedItem)) {
                            this.log("---  ERREUR LIGNE " + vLineNumber + " : La pièce ne devrait PAS contenir '"
                                    + vUnexpectedItem + "'");
                            vTestPassed = false;
                        }
                        continue;
                    } else if (vLine.trim().startsWith("playerhas ")) {
                        String vExpectedItem = vLine.substring(10).trim();
                        if (!this.verifyPlayerHasItem(vExpectedItem)) {
                            this.log(Lang.localizableString("test_error_player_has") + " " + vLineNumber + " : "
                                    + vExpectedItem);
                            vTestPassed = false;
                        }
                        continue;
                    } else if (vLine.trim().startsWith("playerhasnot ")) {
                        String vUnexpectedItem = vLine.substring(13).trim();
                        if (!this.verifyPlayerHasNotItem(vUnexpectedItem)) {
                            this.log(Lang.localizableString("test_error_player_hasnot") + " " + vLineNumber + " : "
                                    + vUnexpectedItem);
                            vTestPassed = false;
                        }
                        continue;
                    } else if (vLine.trim().startsWith("settestmode ")) {
                        String vDestinationRoom = vLine.substring(12).trim();
                        this.setTestModeForAllTransporters(vDestinationRoom);
                        this.log("Mode test activé avec destination: " + vDestinationRoom);
                        continue;
                    } else if (vLine.trim().startsWith("cleartestmode")) {
                        this.clearTestModeForAllTransporters();
                        this.log("Mode test désactivé");
                        continue;
                    }

                    this.log(Lang.localizableString("test_command") + " " + vLine);
                    this.interpretCommand(vLine);
                    vExecutedCommands++;
                }

                this.log(Lang.localizableString("test_summary_header"));
                this.log(Lang.localizableString("test_file") + " " + pFileName);
                this.log(Lang.localizableString("test_lines_total") + " " + vLineNumber);
                this.log(Lang.localizableString("test_commands") + " " + vExecutedCommands);
                this.log(vTestPassed ? "--- TEST RÉUSSI ---" : "--- TEST ÉCHOUÉ ---");
                this.log(Lang.localizableString("test_summary_footer"));
            }
        } catch (IOException e) {
            this.log("Erreur d’entrée/sortie lors du test : " + e.getMessage());
            vTestPassed = false;
        } finally {
            // Nettoyage garanti dans tous les cas
            System.setProperty("debug", "false");
            this.clearTestModeForAllTransporters();
        }

        return vTestPassed;
    }

    /**
     * Vérifie si la pièce actuelle est la pièce attendue.
     * 
     * @param pExpectedRoomName Nom de la pièce attendue
     * @return true si la pièce actuelle est la pièce attendue
     */
    private boolean verifyCurrentRoom(String pExpectedRoomName) {
        return this.aPlayer.getCurrentRoom().getRoomKey().equals(pExpectedRoomName);
    }

    /**
     * Vérifie si la pièce actuelle contient l'objet attendu.
     * 
     * @param pItemName Nom de l'objet attendu
     * @return true si la pièce contient l'objet
     */
    private boolean verifyRoomHasItem(String pItemName) {
        return this.aPlayer.getCurrentRoom().containsItem(pItemName);
    }

    /**
     * Vérifie si la pièce actuelle ne contient pas l'objet attendu.
     *
     * @param pItemName Nom de l'objet attendu
     * @return true si la pièce ne contient pas l'objet
     */
    private boolean verifyRoomHasNotItem(String pItemName) {
        return !this.aPlayer.getCurrentRoom().containsItem(pItemName);
    }

    /**
     * Vérifie si le joueur a l'objet attendu.
     *
     * @param pItemName Nom de l'objet attendu
     * @return true si le joueur a l'objet
     */
    private boolean verifyPlayerHasItem(String pItemName) {
        return aPlayer.hasItem(pItemName);
    }

    /**
     * Vérifie si le joueur n'a pas l'objet attendu.
     * 
     * @param pItemName Nom de l'objet attendu
     * @return true si le joueur n'a pas l'objet
     */
    private boolean verifyPlayerHasNotItem(String pItemName) {
        return !aPlayer.hasItem(pItemName);
    }

    /**
     * Met à jour le temps restant et déclenche les alertes ou la fin de partie.
     */
    private void updateTimer() {
        this.aTimeLeft--;
        if (!this.aHeadlessMode && this.aGui != null) {
            this.aGui.updateTimer(this.aTimeLeft);
        }

        if (this.aTimeLeft <= 0) {
            this.handleGameOver();
        } else if (this.aTimeLeft == 10) {
            this.handleTenSecondLeft();
        }
    }

    /**
     * Émet une alerte sonore pour signaler l'urgence des 10 dernières secondes.
     */
    private void handleTenSecondLeft() {
        this.aMusicPlayer.playSFX("countdown.wav");
    }

    /**
     * Affiche la liste des commandes disponibles.
     * 
     * @return La liste des commandes
     */
    public String getCommandWords() {
        return this.aParser.showCommands();
    }

    /**
     * Indique si le jeu est en mode debug (pour les tests).
     * 
     * @return true si on est en mode debug
     */
    public boolean isDebugMode() {
        return Boolean.parseBoolean(System.getProperty("debug", "false"));
    }

    /**
     * Affiche la popup de dialogue avec les personnages (uniquement en mode
     * graphique).
     */
    public void showTalkPopup() {
        if (this.aHeadlessMode) {
            this.log("Mode headless : popup ignorée");
            return;
        }
        Room vCurrentRoom = this.aPlayer.getCurrentRoom();
        if (!vCurrentRoom.hasCharacters()) {
            this.log(Lang.localizableString("no_characters_here"));
            return;
        }

        SwingUtilities.invokeLater(() -> {
            CharacterInteractionPopup vPopup = new CharacterInteractionPopup(
                    this.aGui, this, this.aPlayer, CharacterInteractionPopup.InteractionMode.TALK);
            vPopup.setVisible(true);
        });
    }

    /**
     * Affiche la popup pour donner un objet (uniquement en mode graphique).
     */
    public void showGivePopup() {
        if (this.aHeadlessMode) {
            this.log("Mode headless : popup ignorée");
            return;
        }
        Room vCurrentRoom = this.aPlayer.getCurrentRoom();
        if (!vCurrentRoom.hasCharacters()) {
            this.log(Lang.localizableString("no_characters_here"));
            return;
        }

        if (this.aPlayer.getInventory().isEmpty()) {
            this.log(Lang.localizableString("inventory_empty"));
            return;
        }

        SwingUtilities.invokeLater(() -> {
            CharacterInteractionPopup vPopup = new CharacterInteractionPopup(
                    this.aGui, this, this.aPlayer, CharacterInteractionPopup.InteractionMode.GIVE);
            vPopup.setVisible(true);
        });
    }

    /**
     * Met à jour la pièce du joueur et affiche la description.
     * 
     * @param pNewRoom La nouvelle pièce
     */
    public void setPlayerRoom(final Room pNewRoom) {
        if (pNewRoom == null)
            return;

        Room vOldRoom = this.aPlayer.getCurrentRoom();
        this.aPlayer.setCurrentRoom(pNewRoom);

        this.log(pNewRoom.getLongDescription());

        if (!this.aHeadlessMode) {
            SwingUtilities.invokeLater(() -> {
                this.aGui.updateRoom(pNewRoom.getShortDescription());
                this.showImage(pNewRoom.getImageName());
            });
            this.updateRoomVisuals(true);
            if (vOldRoom != pNewRoom) {
                this.checkForCharactersInRoom(pNewRoom);
            }
        }
    }

    /**
     * Met à jour l'affichage des personnages, des items de la pièce et de
     * l'inventaire.
     * 
     * @param pUpdateCharacters Indique si les personnages doivent être mis à jour
     */
    private void updateRoomVisuals(boolean pUpdateCharacters) {
        if (this.aHeadlessMode || this.aGui == null)
            return;
        Room vCurrentRoom = this.aPlayer.getCurrentRoom();
        TransitionPanel vPanel = this.aGui.getImagePanel();
        if (vPanel == null)
            return;

        if (pUpdateCharacters) {
            vPanel.clearAllOverlays();
            for (Character vChar : vCurrentRoom.getCharacters()) {
                this.loadAndAddCharacterOverlay(vChar);
            }
        } else {
            vPanel.clearRoomItemOverlays();
            vPanel.clearInventoryOverlays();
        }

        for (Item vItem : vCurrentRoom.getItems().getItems()) {
            this.loadAndAddRoomItemOverlay(vItem);
        }

        for (Item vItem : this.aPlayer.getInventory().getItems()) {
            this.loadAndAddInventoryOverlay(vItem);
        }
    }

    /**
     * Charge et ajoute l'overlay d'un item de l'inventaire.
     * 
     * @param pItem L'item à ajouter à l'inventaire
     */
    public void loadAndAddInventoryOverlay(final Item pItem) {
        if (this.aHeadlessMode || this.aGui == null || this.aGui.getImagePanel() == null)
            return;

        String vImageName = "items/" + pItem.getImageName();
        URL vImageURL = getClass().getClassLoader().getResource(vImageName);
        if (vImageURL != null) {
            ImageIcon vIcon = new ImageIcon(vImageURL);
            this.aGui.getImagePanel().addInventoryOverlay(vIcon, pItem.getName());
        }
    }

    /**
     * Charge et ajoute l'overlay d'un item de la pièce.
     * 
     * @param pItem L'item à ajouter à la pièce
     */
    private void loadAndAddRoomItemOverlay(final Item pItem) {
        if (this.aHeadlessMode || this.aGui == null || this.aGui.getImagePanel() == null)
            return;

        String vImageName = "items/" + pItem.getImageName();
        URL vImageURL = getClass().getClassLoader().getResource(vImageName);
        if (vImageURL != null) {
            ImageIcon vIcon = new ImageIcon(vImageURL);
            this.aGui.getImagePanel().addRoomItemOverlay(vIcon, pItem.getName());
        }
    }

    /**
     * Vérifie si la pièce contient des personnages et affiche un callout (mode
     * graphique).
     * 
     * @param pRoom La pièce à vérifier
     */
    private void checkForCharactersInRoom(final Room pRoom) {
        if (this.aHeadlessMode)
            return;
        List<Character> vCharacters = pRoom.getCharacters();
        if (vCharacters.isEmpty())
            return;

        for (int i = 0; i < vCharacters.size(); i++) {
            final Character vChar = vCharacters.get(i);
            if (vChar instanceof MovingCharacter)
                continue;

            final int vDelay = i * 2000;
            SwingUtilities.invokeLater(() -> {
                Timer vDelayTimer = new Timer(vDelay, e -> {
                    this.showCharacterCallout(vChar);
                });
                vDelayTimer.setRepeats(false);
                vDelayTimer.start();
            });
        }
    }

    /**
     * Affiche un callout pour un personnage (mode graphique).
     * 
     * @param pCharacter Le personnage
     */
    private void showCharacterCallout(final Character pCharacter) {
        if (this.aHeadlessMode || this.aGui == null)
            return;
        TransitionPanel vImagePanel = this.aGui.getImagePanel();
        if (vImagePanel != null) {
            new CharacterCallout(this.aGui, vImagePanel, pCharacter);
        }
    }

    /**
     * Arrête proprement le moteur de jeu avant la fermeture.
     */
    public void shutdown() {
        if (this.aGameTimer != null) {
            this.aGameTimer.stop();
        }
        if (this.aMusicPlayer != null) {
            this.aMusicPlayer.stopMusic();
        }
        if (this.aGui != null) {
            this.aGui.dispose();
        }
        this.aMovingCharacters.clear();
        this.aTransporterRooms.clear();
    }

    /**
     * Permet de relancer le jeu après une partie.
     */
    public void restartGame() {
        // Arrêter le minuteur existant
        if (this.aGameTimer != null && this.aGameTimer.isRunning()) {
            this.aGameTimer.stop();
        }

        // Réinitialiser le temps
        this.aTimeLeft = 600;

        // Vider les collections dynamiques (elles seront repeuplées par initGameWorld)
        this.aMovingCharacters.clear();
        this.aTransporterRooms.clear();

        // Reconstruire l'intégralité du monde du jeu (nouveau joueur, pièces,
        // personnages)
        this.initGameWorld();

        // Créer et démarrer un nouveau minuteur
        this.aGameTimer = new Timer(1000, e -> this.updateTimer());
        this.aGameTimer.start();

        // Mettre à jour l'interface graphique (si ce n'est pas en mode headless)
        if (!this.aHeadlessMode && this.aGui != null) {
            this.aGui.setPlayer(this.aPlayer); // attacher le nouveau joueur
            this.aGui.updateRoom(this.aPlayer.getCurrentRoom().getShortDescription());
            this.aGui.updateTimer(this.aTimeLeft);
            this.showImage(this.aPlayer.getCurrentRoom().getImageName()); // afficher l'image de la pièce de départ

            // Effacer tous les overlays et les reconstruire à partir du nouvel état
            if (this.aGui.getImagePanel() != null) {
                this.aGui.getImagePanel().clearAllOverlays();
            }
            this.updateRoomVisuals(true); // redessiner les personnages et objets
            this.aGui.refreshInventoryPopup(); // rafraîchir le panneau d'inventaire

            // Afficher la description de la pièce, mais PAS le message d'accueil
            this.log(this.aPlayer.getCurrentRoom().getLongDescription());
        } else if (this.aHeadlessMode) {
            this.log(this.aPlayer.getCurrentRoom().getLongDescription());
        }

        // Redémarrer la musique de fond (conserve le même thème)
        this.aMusicPlayer.playBackgroundMusic("theme.wav");
    }

    /**
     * Retourne le temps restant.
     * 
     * @return Le temps restant
     */
    public int getTimeLeft() {
        return this.aTimeLeft;
    }

    /**
     * Retourne la liste des personnages mobiles.
     * 
     * @return La liste des personnages mobiles
     */
    public List<MovingCharacter> getMovingCharacters() {
        return this.aMovingCharacters;
    }

    /**
     * Retourne le joueur.
     * 
     * @return Le joueur
     */
    public Player getPlayer() {
        return this.aPlayer;
    }

    /**
     * Remplace le moteur de jeu actuel par un nouveau (après chargement).
     * 
     * @param pNewEngine Le nouveau moteur de jeu
     */
    public void replaceWith(final GameEngine pNewEngine) {
        if (this.aGameTimer != null) {
            this.aGameTimer.stop();
        }
        if (this.aMusicPlayer != null) {
            this.aMusicPlayer.stopMusic();
        }

        this.aPlayer = pNewEngine.aPlayer;
        this.aMovingCharacters = pNewEngine.aMovingCharacters;
        this.aTransporterRooms = pNewEngine.aTransporterRooms;
        this.aTimeLeft = pNewEngine.aTimeLeft;

        this.aParser = new Parser();

        this.aGameTimer = new Timer(1000, e -> this.updateTimer());
        this.aGameTimer.start();

        SwingUtilities.invokeLater(() -> {
            if (this.aGui != null) {
                this.aGui.setPlayer(this.aPlayer);
            }
            String vRoomName = this.aPlayer.getCurrentRoom().getShortDescription();
            if (this.aGui != null) {
                this.aGui.updateRoom(vRoomName);
            }
            this.log(Lang.localizableString("load_success") + " " + vRoomName);
            this.log(this.aPlayer.getCurrentRoom().getLongDescription());
            this.showImage(this.aPlayer.getCurrentRoom().getImageName());
            if (this.aGui != null) {
                this.aGui.updateTimer(this.aTimeLeft);
            }
        });

        if (this.aMusicPlayer != null) {
            this.aMusicPlayer.playBackgroundMusic("theme.wav");
        }
        if (this.aGui != null) {
            this.aGui.getImagePanel().clearAllOverlays();
        }
    }

    /**
     * Met en pause le timer du jeu (utilisé pendant le puzzle).
     */
    public void pauseTimer() {
        if (this.aGameTimer != null && this.aGameTimer.isRunning()) {
            this.aGameTimer.stop();
        }
    }

    /**
     * Déclenche la séquence de victoire.
     */
    public void handleVictory() {
        if (this.aGameTimer != null) {
            this.aGameTimer.stop();
        }
        this.log(Lang.localizableString("win"));

        if (this.aHeadlessMode) {
            // En mode headless, on termine juste le programme avec succès
            System.exit(0);
        }

        // Version graphique
        final Timer vDelayTimer = new Timer(1000, e -> {
            if (this.aMusicPlayer != null) {
                this.aMusicPlayer.playSFX("congratulations.wav");
            }
        });
        vDelayTimer.setRepeats(false);
        vDelayTimer.start();

        JDialog vDialog = new JDialog(this.aGui, Lang.localizableString("victory_title"), true);
        vDialog.setLayout(new BorderLayout(10, 10));
        vDialog.getContentPane().setBackground(new Color(40, 40, 50));

        JPanel vPanel = new JPanel(new BorderLayout(15, 15));
        vPanel.setBackground(new Color(40, 40, 50));
        vPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel vMessageLabel = new JLabel(Lang.localizableString("victory_message"));
        vMessageLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        vMessageLabel.setForeground(new Color(220, 240, 255));
        vMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        vPanel.add(vMessageLabel, BorderLayout.CENTER);

        JPanel vButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        vButtonPanel.setBackground(new Color(40, 40, 50));

        AppButton vNewGameButton = new AppButton(Lang.localizableString("new_game"), new Color(100, 200, 100));
        AppButton vQuitButton = new AppButton(Lang.localizableString("quit_game"), new Color(200, 80, 80));

        vNewGameButton.addActionListener(e -> {
            vDialog.dispose();
            this.restartGame();
        });
        vQuitButton.addActionListener(e -> {
            vDialog.dispose();
            System.exit(0);
        });

        vButtonPanel.add(vNewGameButton);
        vButtonPanel.add(vQuitButton);
        vPanel.add(vButtonPanel, BorderLayout.SOUTH);

        vDialog.add(vPanel);
        vDialog.pack();
        vDialog.setLocationRelativeTo(this.aGui);
        vDialog.setVisible(true);
    }

    /**
     * Gère la défaite du joueur suite à l'expiration du délai ou échec du puzzle.
     */
    public void handleGameOver() {
        if (this.aGameTimer != null && this.aGameTimer.isRunning()) {
            this.aGameTimer.stop();
        }
        this.log(Lang.localizableString("game_over_message"));

        if (this.aHeadlessMode) {
            // En mode headless, on termine avec un code d'erreur
            System.exit(1);
        }

        this.showImage("lost.gif");

        final Timer vDelayTimer = new Timer(1000, e -> {
            if (this.aMusicPlayer != null) {
                this.aMusicPlayer.playSFX("explosion.wav");
            }
        });
        vDelayTimer.setRepeats(false);
        vDelayTimer.start();

        JDialog vDialog = new JDialog(this.aGui, Lang.localizableString("game_over_title"), true);
        vDialog.setLayout(new BorderLayout(10, 10));
        vDialog.getContentPane().setBackground(new Color(40, 40, 50));

        JPanel vPanel = new JPanel(new BorderLayout(15, 15));
        vPanel.setBackground(new Color(40, 40, 50));
        vPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel vMessageLabel = new JLabel(Lang.localizableString("game_over_confirm"));
        vMessageLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        vMessageLabel.setForeground(new Color(220, 240, 255));
        vMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        vPanel.add(vMessageLabel, BorderLayout.CENTER);

        JPanel vButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        vButtonPanel.setBackground(new Color(40, 40, 50));

        AppButton vNewGameButton = new AppButton(Lang.localizableString("new_game"), new Color(100, 200, 100));
        AppButton vQuitButton = new AppButton(Lang.localizableString("quit_game"), new Color(200, 80, 80));

        vNewGameButton.addActionListener(e -> {
            vDialog.dispose();
            this.restartGame();
        });
        vQuitButton.addActionListener(e -> {
            vDialog.dispose();
            System.exit(0);
        });

        vButtonPanel.add(vNewGameButton);
        vButtonPanel.add(vQuitButton);
        vPanel.add(vButtonPanel, BorderLayout.SOUTH);

        vDialog.add(vPanel);
        vDialog.pack();
        vDialog.setLocationRelativeTo(this.aGui);
        vDialog.setVisible(true);
    }

    /**
     * Charge et ajoute l'overlay d'un personnage (mode graphique).
     * 
     * @param pCharacter Le personnage à ajouter
     */
    public void loadAndAddCharacterOverlay(final Character pCharacter) {
        if (this.aHeadlessMode || this.aGui == null || this.aGui.getImagePanel() == null)
            return;

        String vImageName = "characters/" + pCharacter.getName() + ".png";
        URL vImageURL = getClass().getClassLoader().getResource(vImageName);
        if (vImageURL != null) {
            ImageIcon vIcon = new ImageIcon(vImageURL);
            this.aGui.getImagePanel().addCharacterOverlay(vIcon, pCharacter.getName());
        } else {
            System.out.println(Lang.localizableString("error_image_not_found") + " " + pCharacter.getName());
        }
    }

    /**
     * Charge et ajoute l'overlay d'un item (dans la pièce).
     * 
     * @param pItem L'item à ajouter
     */
    public void loadAndAddItemOverlay(final Item pItem) {
        if (this.aHeadlessMode || this.aGui == null || this.aGui.getImagePanel() == null)
            return;

        String vImageName = "items/" + pItem.getImageName();
        URL vImageURL = getClass().getClassLoader().getResource(vImageName);
        if (vImageURL != null) {
            ImageIcon vIcon = new ImageIcon(vImageURL);
            this.aGui.getImagePanel().addRoomItemOverlay(vIcon, pItem.getName());
        }
    }

    /**
     * Force le rafraîchissement de l'affichage de l'inventaire.
     */
    public void refreshInventory() {
        if (this.aHeadlessMode)
            return;
        if (this.aGui != null) {
            this.updateRoomVisuals(false);
            this.aGui.refreshInventoryPopup();
        }
    }

    /**
     * Affiche les détails d'un objet.
     * 
     * @param pItem L'objet à examiner
     */
    public void showItemDetails(final Item pItem) {
        if (this.aHeadlessMode) {
            this.log("Détails de l'objet : " + pItem.getInformation());
            return;
        }
        ItemDetailsDialog vDialog = new ItemDetailsDialog(this.aGui, this, pItem);
        vDialog.setVisible(true);
    }

    /**
     * Helper pour afficher les messages de log dans la console ou la GUI selon le
     * mode.
     * 
     * @param message Le message à afficher
     */
    public void log(String message) {
        if (this.aHeadlessMode) {
            System.out.println(message);
        } else if (this.aGui != null) {
            this.aGui.println(message);
        }
    }

    /**
     * Affiche un fichier image dans l'interface.
     * 
     * @param pImageName Le nom du fichier image à afficher
     */
    public void showImage(final String pImageName) {
        if (this.aHeadlessMode || this.aGui == null)
            return;

        this.aGui.showImage(pImageName);
    }
}