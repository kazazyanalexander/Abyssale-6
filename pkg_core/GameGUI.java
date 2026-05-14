package pkg_core;

import pkg_gameplay.Room;

import pkg_ui_components.AppButton;
import pkg_ui_components.InventoryPopup;
import pkg_ui_components.TransitionPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import pkg_characters.Player;
import pkg_utility.Lang;

/**
 * Interface graphique principale du jeu "Station Abyssale-6".
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */

public class GameGUI extends JFrame {

    /** Moteur de jeu principal */
    private GameEngine aEngine;
    /** Joueur actuel */
    private Player aPlayer;

    /**
     * Label affichant le timer de jeu.
     */
    private JLabel aTimerLabel;

    /**
     * Label affichant le nom de la pièce actuelle.
     */
    private JLabel aRoomLabel;

    /**
     * Zone de texte affichant les descriptions et les messages du jeu.
     */
    private JTextArea aTerminalArea;

    /**
     * Panneau personnalisé pour afficher les images de fond avec des transitions de
     * fondu fluides.
     */
    private TransitionPanel aImagePanel;

    /**
     * Popup d'inventaire qui affiche les objets du joueur et de la pièce,
     * avec des options contextuelles pour interagir avec les items.
     */
    private InventoryPopup aInventoryPopup;

    /**
     * Champ de texte pour la saisie des commandes par le joueur.
     */
    private JTextField aCommandField;

    // Constantes pour les commandes
    /** Commande pour regarder autour de soi. */
    private static final String CMD_LOOK = "LOOK";
    /** Commande pour ouvrir l'inventaire. */
    private static final String CMD_INV = "INV";
    /** Commande pour revenir à la pièce précédente. */
    private static final String CMD_BACK = "BACK";
    /** Commande pour afficher l'aide. */
    private static final String CMD_HELP = "HELP";
    /** Commande pour quitter le jeu. */
    private static final String CMD_QUIT = "QUIT";
    /** Commande pour parler avec un personnage. */
    private static final String CMD_TALK = "TALK";
    /** Commande pour donner un objet à un personnage. */
    private static final String CMD_GIVE = "GIVE";
    /** Commande pour sauvegarder la partie. */
    private static final String CMD_SAVE = "SAVE";
    /** Commande pour charger une partie. */
    private static final String CMD_LOAD = "LOAD";
    /** URL de base pour les images de fond. */
    private static final String REMOTE_IMAGE_BASE_URL = "https://perso.esiee.fr/~kazazyaa/images/";

    /**
     * Map associant les commandes des boutons à leurs actions correspondantes,
     * permettant une gestion centralisée des interactions des boutons.
     */
    private Map<String, Runnable> aButtonActions;

    // Couleurs de l'interface
    /** Couleur de fond sombre pour l'ensemble de l'interface. */
    private static final Color DARK_BG = new Color(12, 14, 20);

    /** Couleur de fond pour les cadres et les panneaux principaux. */
    private static final Color FRAME_BG = new Color(36, 40, 52);

    /**
     * Couleur de fond pour les panneaux individuels, créant une hiérarchie visuelle
     * avec le cadre.
     */
    private static final Color PANEL_BG = new Color(26, 30, 40);

    /**
     * Couleur du texte pour une bonne lisibilité sur les fonds sombres,
     * avec un léger ton bleu pour l'ambiance.
     */
    private static final Color TEXT_COLOR = new Color(200, 210, 230);

    /**
     * Vert lumineux pour la zone de terminal, rappelant les écrans rétro
     * et renforçant l'immersion dans un environnement de station spatiale.
     */
    private static final Color TERMINAL_GREEN = new Color(120, 255, 140);

    /**
     * Couleur d'accentuation pour les bordures et les éléments interactifs,
     * ajoutant une touche de couleur sans être trop vive.
     */
    private static final Color ACCENT = new Color(110, 150, 200);

    /**
     * Couleur de fond des boutons, suffisamment contrastée pour être visible
     * mais cohérente avec la palette sombre de l'interface.
     */
    private static final Color BUTTON_BG = new Color(36, 40, 52);

    /**
     * Couleur de fond du champ de saisie, sombre pour s'intégrer à l'interface
     * tout en restant lisible.
     */
    private static final Color INPUT_BG = new Color(20, 22, 30);

    /** Largeur fixe pour les images de fond. */
    private static final int IMAGE_WIDTH = 900;

    /** Hauteur fixe pour les images de fond. */
    private static final int IMAGE_HEIGHT = 600;

    /**
     * Constructeur de l'interface graphique.
     * 
     * @param pEngine Le moteur de jeu
     */
    public GameGUI(final GameEngine pEngine) {
        this.aEngine = pEngine;
        this.initializeActions();
        this.initializeUI();
    }

    /**
     * Initialise les actions des boutons.
     */
    private void initializeActions() {
        this.aButtonActions = new HashMap<>();

        // Directions
        this.aButtonActions.put("N", () -> this.aEngine.interpretCommand("go north"));
        this.aButtonActions.put("S", () -> this.aEngine.interpretCommand("go south"));
        this.aButtonActions.put("E", () -> this.aEngine.interpretCommand("go east"));
        this.aButtonActions.put("W", () -> this.aEngine.interpretCommand("go west"));
        this.aButtonActions.put("U", () -> this.aEngine.interpretCommand("go up"));
        this.aButtonActions.put("D", () -> this.aEngine.interpretCommand("go down"));

        // Commandes spéciales
        this.aButtonActions.put(CMD_BACK, () -> this.aEngine.interpretCommand("back"));
        this.aButtonActions.put(CMD_TALK, () -> this.aEngine.interpretCommand("talk"));
        this.aButtonActions.put(CMD_GIVE, () -> this.aEngine.interpretCommand("give"));
        this.aButtonActions.put(CMD_HELP, () -> this.aEngine.interpretCommand("help"));
        this.aButtonActions.put(CMD_QUIT, () -> this.aEngine.interpretCommand("quit"));
        this.aButtonActions.put(CMD_LOOK, () -> this.aEngine.interpretCommand("look"));
        this.aButtonActions.put(CMD_SAVE, () -> this.aEngine.interpretCommand("save game"));
        this.aButtonActions.put(CMD_LOAD, () -> this.aEngine.interpretCommand("load game"));
        this.aButtonActions.put(CMD_INV, () -> {
            this.showInventoryPopup();
            this.aEngine.interpretCommand("inventory");
        });

    }

    /**
     * Met à jour le joueur (à appeler quand le joueur change).
     * 
     * @param pPlayer Le nouveau joueur
     */
    public void setPlayer(final Player pPlayer) {
        this.aPlayer = pPlayer;
    }

    /**
     * Initialise l'interface utilisateur.
     */
    private void initializeUI() {
        this.setTitle(Lang.localizableString("gui_title"));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout(12, 12));
        this.getContentPane().setBackground(DARK_BG);

        this.add(this.createTopBar(), BorderLayout.NORTH);

        // Panneau principal avec image à gauche et panneaux de contrôle à droite
        JPanel vMainContent = new JPanel(new BorderLayout(12, 0));
        vMainContent.setBackground(DARK_BG);
        vMainContent.add(createMainFrame(), BorderLayout.CENTER);
        vMainContent.add(createRightPanel(), BorderLayout.EAST);

        add(vMainContent, BorderLayout.CENTER);

        setSize(1200, 800);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // ───────────────────────────────────────── BARRE SUPÉRIEURE

    /**
     * Crée la barre supérieure avec les informations de jeu.
     * 
     * @return Le panneau de la barre supérieure
     */
    private JPanel createTopBar() {
        JPanel vBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 18, 8));
        vBar.setBackground(FRAME_BG);
        vBar.setBorder(new MatteBorder(2, 2, 2, 2, ACCENT));

        Font vFont = new Font("Monospaced", Font.BOLD, 15);

        this.aTimerLabel = new JLabel(Lang.localizableString("gui_timer") + " 09:59");
        this.aTimerLabel.setFont(vFont);
        this.aTimerLabel.setForeground(TEXT_COLOR);

        this.aRoomLabel = new JLabel(Lang.localizableString("gui_room") + " ");
        this.aRoomLabel.setFont(vFont);
        this.aRoomLabel.setForeground(TEXT_COLOR);

        vBar.add(this.aTimerLabel);
        vBar.add(this.createSeparator());
        vBar.add(this.aRoomLabel);

        return vBar;
    }

    /**
     * Crée un séparateur visuel pour la barre supérieure.
     * 
     * @return Le label séparateur
     */
    private JLabel createSeparator() {
        JLabel vSeparator = new JLabel("|");
        vSeparator.setForeground(Color.GRAY);
        vSeparator.setFont(new Font("Monospaced", Font.BOLD, 15));
        return vSeparator;
    }

    // ───────────────────────────────────────── CADRE PRINCIPAL (IMAGE + TERMINAL)

    /**
     * Crée le cadre principal contenant l'image et le terminal.
     * 
     * @return Le panneau du cadre principal
     */
    private JPanel createMainFrame() {
        JPanel vFrame = new JPanel(new BorderLayout(8, 8));
        vFrame.setBackground(FRAME_BG);
        vFrame.setBorder(new MatteBorder(3, 3, 3, 3, ACCENT));
        vFrame.setPreferredSize(new Dimension(850, 800));

        vFrame.add(createImagePanel(), BorderLayout.CENTER);
        vFrame.add(createTerminalPanel(), BorderLayout.SOUTH);

        return vFrame;
    }

    /**
     * Crée le panneau d'affichage de l'image.
     * 
     * @return Le panneau d'image
     */
    private JPanel createImagePanel() {
        JPanel vPanel = new JPanel(new BorderLayout());
        vPanel.setBackground(PANEL_BG);
        vPanel.setBorder(new EmptyBorder(12, 12, 6, 12));

        this.aImagePanel = new TransitionPanel();

        this.aImagePanel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
        this.aImagePanel.setMinimumSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
        this.aImagePanel.setMaximumSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
        this.aImagePanel.setGameReferences(this.aEngine);
        vPanel.add(this.aImagePanel, BorderLayout.CENTER);
        return vPanel;
    }

    /**
     * Crée le panneau du terminal de texte.
     * 
     * @return Le panneau du terminal
     */
    private JPanel createTerminalPanel() {
        JPanel vPanel = new JPanel(new BorderLayout());
        vPanel.setBackground(PANEL_BG);
        vPanel.setBorder(new EmptyBorder(6, 12, 12, 12));

        this.aTerminalArea = new JTextArea();
        this.aTerminalArea.setBackground(PANEL_BG);
        this.aTerminalArea.setForeground(TERMINAL_GREEN);
        this.aTerminalArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        this.aTerminalArea.setEditable(false);
        this.aTerminalArea.setLineWrap(true);
        this.aTerminalArea.setWrapStyleWord(true);

        this.aTerminalArea.setRows(8);
        this.aTerminalArea.setColumns(50);
        this.aTerminalArea.setText("");

        JScrollPane vTerminalScrollPane = new JScrollPane(this.aTerminalArea);
        vTerminalScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        vTerminalScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        vTerminalScrollPane.setBorder(BorderFactory.createLineBorder(ACCENT));
        vTerminalScrollPane.setBackground(PANEL_BG);
        vTerminalScrollPane.setPreferredSize(new Dimension(700, 220));

        vPanel.add(vTerminalScrollPane, BorderLayout.CENTER);
        return vPanel;
    }

    // ───────────────────────────────────────── PANNEAU LATÉRAL DROIT

    /**
     * Crée le panneau latéral droit contenant les actions, la saisie et la
     * navigation.
     * Disposition : Actions (haut) → Input (milieu) → Navigation (bas)
     * 
     * @return Le panneau latéral droit
     */
    private JPanel createRightPanel() {
        JPanel vRightPanel = new JPanel(new BorderLayout(10, 10));
        vRightPanel.setBackground(DARK_BG);
        vRightPanel.setPreferredSize(new Dimension(400, 700));
        vRightPanel.setBorder(new EmptyBorder(0, 0, 0, 10));

        // Panneau des actions en haut
        vRightPanel.add(createActionsPanel(), BorderLayout.NORTH);

        // Panneau central : espace noir redimensionnable en CENTER, panneau de saisie
        // fixe en SOUTH
        JPanel vCenterPanel = new JPanel(new BorderLayout(0, 10));
        vCenterPanel.setBackground(DARK_BG);

        // Espace noir qui absorbe tout l'espace disponible au-dessus du panneau de
        // saisie
        JPanel vSpacer = new JPanel();
        vSpacer.setBackground(DARK_BG);

        vCenterPanel.add(vSpacer, BorderLayout.CENTER);
        vCenterPanel.add(createCommandInputPanel(), BorderLayout.SOUTH);

        vRightPanel.add(vCenterPanel, BorderLayout.CENTER);
        vRightPanel.add(createNavigationPanel(), BorderLayout.SOUTH);
        return vRightPanel;
    }

    /**
     * Crée le panneau d'actions (en haut à droite).
     * 
     * @return Le panneau d'actions
     */
    private JPanel createActionsPanel() {
        JPanel vPanel = createTitledPanel(Lang.localizableString("gui_actions"));

        // Disposition 3x3 pour les actions
        String[][] vLayout = {
                { CMD_BACK, CMD_HELP, CMD_SAVE },
                { CMD_INV, CMD_LOOK, CMD_LOAD },
                { CMD_TALK, CMD_GIVE, CMD_QUIT }
        };

        JPanel vButtonGrid = createButtonGrid(vLayout);
        vButtonGrid.setPreferredSize(new Dimension(350, 180));
        vPanel.add(vButtonGrid, BorderLayout.CENTER);

        return vPanel;
    }

    /**
     * Crée le panneau de saisie des commandes (au milieu à droite).
     * 
     * @return Le panneau de saisie
     */
    private JPanel createCommandInputPanel() {
        JPanel vPanel = createTitledPanel(Lang.localizableString("gui_input"));

        this.aCommandField = new JTextField();
        this.aCommandField.setBackground(INPUT_BG);
        this.aCommandField.setForeground(TERMINAL_GREEN);
        this.aCommandField.setFont(new Font("Monospaced", Font.PLAIN, 16));
        this.aCommandField.setCaretColor(TERMINAL_GREEN);
        this.aCommandField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        this.aCommandField.addActionListener(e -> sendCommand());

        JPanel vFieldPanel = new JPanel(new BorderLayout());
        vFieldPanel.setBackground(FRAME_BG);
        vFieldPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        vFieldPanel.add(this.aCommandField, BorderLayout.CENTER);

        vPanel.add(vFieldPanel, BorderLayout.CENTER);
        return vPanel;
    }

    /**
     * Crée le panneau de navigation (en bas à droite).
     * 
     * @return Le panneau de navigation
     */
    private JPanel createNavigationPanel() {
        JPanel vPanel = createTitledPanel(Lang.localizableString("gui_navigation"));

        // Disposition 4x3 pour la navigation
        String[][] vLayout = {
                { "U", "N", "D" },
                { "W", "S", "E" }
        };

        JPanel vButtonGrid = createButtonGrid(vLayout);
        vButtonGrid.setPreferredSize(new Dimension(350, 140));
        vPanel.add(vButtonGrid, BorderLayout.CENTER);

        return vPanel;
    }

    /**
     * Crée un panneau avec un titre.
     * 
     * @param pTitle Le titre du panneau
     * @return Le panneau titré
     */
    private JPanel createTitledPanel(final String pTitle) {
        JPanel vPanel = new JPanel(new BorderLayout());
        vPanel.setBackground(FRAME_BG);
        vPanel.setBorder(new TitledBorder(
                new MatteBorder(2, 2, 2, 2, ACCENT),
                pTitle,
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Monospaced", Font.BOLD, 16),
                TEXT_COLOR));
        return vPanel;
    }

    /**
     * Crée une grille de boutons à partir d'une disposition.
     * 
     * @param pLayout La disposition des boutons
     * @return Le panneau de la grille
     */
    private JPanel createButtonGrid(final String[][] pLayout) {
        int vRows = pLayout.length;
        int vCols = pLayout[0].length;

        JPanel vGrid = new JPanel(new GridLayout(vRows, vCols, 8, 8));
        vGrid.setBackground(FRAME_BG);
        vGrid.setBorder(new EmptyBorder(10, 10, 10, 10));

        for (String[] vRow : pLayout) {
            for (String vCmdId : vRow) {
                if (vCmdId.isEmpty()) {
                    // Bouton vide (invisible mais garde la grille)
                    JButton vEmptyButton = new JButton();
                    vEmptyButton.setEnabled(false);
                    vEmptyButton.setOpaque(false);
                    vEmptyButton.setContentAreaFilled(false);
                    vEmptyButton.setBorderPainted(false);
                    vGrid.add(vEmptyButton);
                } else {
                    String vDisplayText = getButtonLabel(vCmdId);
                    vGrid.add(createButton(vCmdId, vDisplayText));
                }
            }
        }
        return vGrid;
    }

    /**
     * Returns the localized label for a button command identifier.
     * 
     * @param cmdId The command identifier (e.g., "BACK", "N", "HELP")
     * @return Localized button text
     */
    private String getButtonLabel(String cmdId) {
        switch (cmdId) {
            // Directions
            case "N":
                return Lang.localizableString("north");
            case "S":
                return Lang.localizableString("south");
            case "E":
                return Lang.localizableString("east");
            case "W":
                return Lang.localizableString("west");
            case "U":
                return Lang.localizableString("up");
            case "D":
                return Lang.localizableString("down");
            // Action commands
            case CMD_BACK:
                return Lang.localizableString("gui_back");
            case CMD_HELP:
                return Lang.localizableString("gui_help");
            case CMD_SAVE:
                return Lang.localizableString("gui_save");
            case CMD_INV:
                return Lang.localizableString("gui_inv");
            case CMD_LOOK:
                return Lang.localizableString("gui_look");
            case CMD_LOAD:
                return Lang.localizableString("gui_load");
            case CMD_TALK:
                return Lang.localizableString("gui_talk");
            case CMD_GIVE:
                return Lang.localizableString("gui_give");
            case CMD_QUIT:
                return Lang.localizableString("gui_quit");
            default:
                return cmdId; // fallback (should not happen)
        }
    }

    /**
     * Crée un bouton stylisé.
     * 
     * @param pCommandId   Identifiant interne de la commande (ex: "BACK")
     * @param pDisplayText Texte localisé à afficher sur le bouton
     * @return Le bouton configuré
     */
    private AppButton createButton(final String pCommandId, final String pDisplayText) {
        AppButton vButton = new AppButton(
                pDisplayText,
                BUTTON_BG, ACCENT,
                Color.WHITE, Color.BLACK,
                new MatteBorder(2, 2, 2, 2, ACCENT));
        vButton.setFont(new Font("Monospaced", Font.BOLD, 14));
        vButton.addActionListener(e -> handleButtonAction(pCommandId));
        return vButton;
    }

    // ───────────────────────────────────────── GESTION DES ACTIONS

    /**
     * Gère les actions des boutons.
     * 
     * @param pCmd La commande associée au bouton
     */
    private void handleButtonAction(final String pCmd) {
        Runnable vAction = this.aButtonActions.get(pCmd);
        if (vAction != null) {
            vAction.run();
        }
    }

    /**
     * Envoie la commande saisie dans le champ de texte au moteur de jeu.
     */
    private void sendCommand() {
        String vCommand = this.aCommandField.getText().trim();
        if (!vCommand.isEmpty()) {
            this.println("> " + vCommand);
            this.aEngine.interpretCommand(vCommand);
            this.aCommandField.setText("");
        }
    }

    // ───────────────────────────────────────── API PUBLIQUE DE MISE À JOUR

    /**
     * Met à jour le timer affiché dans l'interface.
     * 
     * @param pTimeLeft Le temps restant en secondes
     */
    public final void updateTimer(final int pTimeLeft) {
        final int vMinutes = pTimeLeft / 60;
        final int vSeconds = pTimeLeft % 60;
        this.aTimerLabel.setText(Lang.localizableString("gui_timer") + " " +
                String.format("%02d:%02d", vMinutes, vSeconds));
    }

    /**
     * Met à jour le nom de la pièce affiché dans l'interface.
     * 
     * @param pName Le nom de la pièce
     */
    public void updateRoom(final String pName) {
        this.aRoomLabel.setText(Lang.localizableString("gui_room") + " " + pName);
    }

    /**
     * Ajoute du texte à la zone de terminal.
     * 
     * @param pText Le texte à ajouter
     */
    public void println(final String pText) {
        this.aTerminalArea.append(pText + "\n");
        this.aTerminalArea.setCaretPosition(this.aTerminalArea.getDocument().getLength());
    }

    /**
     * Affiche la popup d'inventaire.
     */
    private void showInventoryPopup() {
        if (this.aInventoryPopup == null || !this.aInventoryPopup.isVisible()) {
            this.aInventoryPopup = new InventoryPopup(this, this.aEngine, this.aPlayer);
            this.aInventoryPopup.setVisible(true);
        } else {
            aInventoryPopup.toFront();
        }
    }

    /**
     * Affiche un fichier image dans l'interface.
     * 
     * @param pImageName Le nom du fichier image à afficher
     */

    public void showImage(final String pImageName) {
        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                // Construire l'URL dynamiquement à partir du nom de l'image
                String urlString = REMOTE_IMAGE_BASE_URL + pImageName;
                URL imageUrl = URI.create(urlString).toURL();
                return new ImageIcon(imageUrl);
            }

            @Override
            protected void done() {
                try {
                    ImageIcon vIcon = get();
                    // Aucune vérification d'état – la transition se lance directement.
                    // L'image apparaîtra et s'animera une fois chargée.
                    aImagePanel.startTransition(vIcon);
                } catch (Exception e) {
                    System.err.println(Lang.localizableString("error_loading_icon") + " " + pImageName);
                    // Facultatif : charger une image de secours locale
                }
            }
        }.execute();
    }

    /**
     * Permet d'accéder au panneau d'image pour des opérations avancées (ex: effets
     * de
     * transition personnalisés).
     * 
     * @return Le panneau d'image
     */
    public TransitionPanel getImagePanel() {
        return this.aImagePanel;
    }

    /**
     * Force la mise à jour complète de l'interface.
     */
    public void forceRefresh() {
        if (this.aPlayer != null) {
            Room vRoom = this.aPlayer.getCurrentRoom();
            this.updateRoom(vRoom.getShortDescription());
            this.showImage(vRoom.getImageName());
            this.println(Lang.localizableString("load_refresh"));
        }
    }

    /**
     * Efface tout le contenu de la zone de terminal.
     */
    public void clearTerminal() {
        this.aTerminalArea.setText("");
    }

    /**
     * Rafraîchit la popup d'inventaire si elle est ouverte.
     */
    public void refreshInventoryPopup() {
        if (this.aInventoryPopup != null && this.aInventoryPopup.isVisible()) {
            this.aInventoryPopup.loadInventoryData();
            this.aInventoryPopup.repaint();
        }
    }
}