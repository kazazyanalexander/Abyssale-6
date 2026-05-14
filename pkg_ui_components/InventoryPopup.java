package pkg_ui_components;

import pkg_core.GameEngine;
import pkg_gameplay.Room;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import pkg_characters.Player;
import pkg_items.Beamer;
import pkg_items.Item;
import pkg_items.MagicCookie;
import pkg_utility.Lang;

/**
 * Panneau d'inventaire s'affichant comme une popup modale.
 * Design inspiré de l'image inventory.png.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public final class InventoryPopup extends JDialog {

    /** Référence au moteur de jeu pour exécuter les commandes */
    private final GameEngine aEngine;

    /** Référence au joueur pour accéder à l'inventaire et aux statistiques */
    private final Player aPlayer;

    // Composants de l'interface

    /** Liste affichant les items de l'inventaire et de la pièce */
    private JList<InventoryItem> aInventoryList;

    /** Modèle de données pour la liste d'inventaire */
    private DefaultListModel<InventoryItem> aListModel;

    /** Label affichant le résumé de l'inventaire (nombre d'items, poids total) */
    private JLabel aInventorySummaryLabel;

    /** Label affichant le poids actuel par rapport au poids maximum */
    private JLabel aWeightLabel;

    /**
     * Barre de progression indiquant le poids actuel par rapport au poids maximum
     */
    private JProgressBar aWeightBar;

    /**
     * Menu contextuel qui s'adapte dynamiquement selon l'état de l'item sélectionné
     */
    private AppPopupMenu aContextMenu;

    // ── Constantes de style pour l'interface graphique ─────────────────────────

    /** Couleur de fond de la popup avec transparence (noir semi-transparent). */
    private static final Color POPUP_BG = new Color(30, 30, 40, 240);

    /** Couleur de fond des panneaux internes (gris foncé). */
    private static final Color PANEL_BG = new Color(40, 40, 50);

    /** Couleur du texte principal (blanc bleuté). */
    private static final Color TEXT_COLOR = new Color(220, 240, 255);

    /**
     * Couleur d'accentuation pour les titres et les éléments sélectionnés (bleu
     * clair).
     */
    private static final Color ACCENT_COLOR = new Color(100, 200, 255);

    /** Couleur du bouton de fermeture (rouge). */
    private static final Color CLOSE_BUTTON_COLOR = new Color(200, 80, 80);

    /** Couleur de fond du menu contextuel (gris moyen). */
    private static final Color MENU_BG = new Color(50, 50, 65);

    /**
     * Constructeur de la popup d'inventaire.
     * 
     * @param pOwner  La fenêtre parente
     * @param pEngine Le moteur de jeu
     * @param pPlayer Le joueur
     */
    public InventoryPopup(final Frame pOwner, final GameEngine pEngine, final Player pPlayer) {
        super(pOwner, Lang.localizableString("inventory_title"), true);
        this.aEngine = pEngine;
        this.aPlayer = pPlayer;

        this.initializeUI();
        this.loadInventoryData();
        this.pack();

        // Positionnement personnalisé : Haut-Centre du propriétaire
        if (pOwner != null) {
            final int vX = pOwner.getX() + (pOwner.getWidth() - this.getWidth()) / 2;
            final int vY = pOwner.getY() + 100;
            this.setLocation(vX, vY);
        } else {
            this.setLocationRelativeTo(null);
        }
    }

    /**
     * Initialise l'interface utilisateur de la popup.
     */
    private void initializeUI() {
        this.setLayout(new BorderLayout(10, 10));
        this.getContentPane().setBackground(POPUP_BG);
        this.setUndecorated(true);

        // Panneau principal avec bordure arrondie
        final JPanel vMainPanel = new JPanel(new BorderLayout(15, 15));
        vMainPanel.setBackground(PANEL_BG);
        vMainPanel.setBorder(this.createPopupBorder());

        // Panneau d'en-tête avec titre et bouton de fermeture
        vMainPanel.add(this.createTitleBarPanel(), BorderLayout.NORTH);

        // Panneau central : Liste d'inventaire avec menu contextuel et clic simple
        vMainPanel.add(this.createInventoryListPanel(), BorderLayout.CENTER);

        // Panneau des actions
        vMainPanel.add(this.createActionPanel(), BorderLayout.SOUTH);

        this.add(vMainPanel, BorderLayout.CENTER);

        // Permettre la fermeture avec Echap
        this.getRootPane().registerKeyboardAction(
                pE -> this.dispose(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /**
     * Crée le panneau avec la liste des objets, son menu contextuel et la gestion
     * du clic.
     * 
     * @return Le panneau de liste
     */
    private JPanel createInventoryListPanel() {
        final JPanel vPanel = new JPanel(new BorderLayout());
        vPanel.setBackground(PANEL_BG);
        vPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Modèle de liste
        this.aListModel = new DefaultListModel<>();
        this.aInventoryList = new JList<>(this.aListModel);
        this.aInventoryList.setBackground(PANEL_BG);
        this.aInventoryList.setForeground(TEXT_COLOR);
        this.aInventoryList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        this.aInventoryList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.aInventoryList.setCellRenderer(new InventoryItemRenderer());

        // Créer le menu contextuel
        this.aContextMenu = new AppPopupMenu();

        // Gestionnaire de clic
        this.aInventoryList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent pEvent) {
                final int vIndex = aInventoryList.locationToIndex(pEvent.getPoint());
                if (vIndex < 0) {
                    return;
                }

                aInventoryList.setSelectedIndex(vIndex);

                if (pEvent.getButton() == MouseEvent.BUTTON1 && pEvent.getClickCount() == 2) {
                    final InventoryItem vSelected = aInventoryList.getSelectedValue();
                    if (vSelected != null) {
                        toggleTakeDrop(vSelected);
                    }
                }
            }

            @Override
            public void mousePressed(final MouseEvent pEvent) {
                if (pEvent.isPopupTrigger()) {
                    showDynamicContextMenu(pEvent);
                }
            }

            @Override
            public void mouseReleased(final MouseEvent pEvent) {
                if (pEvent.isPopupTrigger()) {
                    showDynamicContextMenu(pEvent);
                }
            }
        });

        final JScrollPane vScrollPane = new JScrollPane(this.aInventoryList);
        vScrollPane.setBackground(PANEL_BG);
        vScrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR));
        vScrollPane.getViewport().setBackground(PANEL_BG);

        vPanel.add(vScrollPane, BorderLayout.CENTER);

        return vPanel;
    }

    /**
     * Alterne entre prendre et déposer un objet selon son état.
     * 
     * @param pSelected L'item sélectionné
     */
    private void toggleTakeDrop(final InventoryItem pSelected) {
        final Item vItem = pSelected.getItem();
        final boolean vIsOwned = pSelected.isOwned();

        if (vIsOwned) {
            this.aEngine.interpretCommand("drop " + vItem.getName());
        } else {
            if (vItem.canBePickedUp()) {
                this.aEngine.interpretCommand("take " + vItem.getName());
            } else {
                JOptionPane.showMessageDialog(this,
                        Lang.localizableString("cannot_pickup"),
                        Lang.localizableString("warning"),
                        JOptionPane.WARNING_MESSAGE);
            }
        }

        this.loadInventoryData();
    }

    /**
     * Affiche un menu contextuel dynamique selon l'état de l'item sélectionné.
     * 
     * @param pEvent L'événement de souris
     */
    private void showDynamicContextMenu(final MouseEvent pEvent) {
        final int vIndex = this.aInventoryList.locationToIndex(pEvent.getPoint());
        if (vIndex < 0) {
            return;
        }

        this.aInventoryList.setSelectedIndex(vIndex);
        final InventoryItem vSelected = this.aInventoryList.getSelectedValue();

        if (vSelected == null) {
            return;
        }

        final Item vItem = vSelected.getItem();
        final boolean vIsOwned = vSelected.isOwned();

        this.aContextMenu.removeAll();

        // 1. INSPECTER - toujours disponible
        this.aContextMenu.addItem(Lang.localizableString("inspect"), () -> this.doAction("inspect"));
        this.aContextMenu.addSeparator();

        // 2. PRENDRE - disponible si l'item n'est pas possédé ET peut être pris
        if (!vIsOwned && vItem.canBePickedUp()) {
            this.aContextMenu.addItem(Lang.localizableString("take"), () -> this.doAction("take"));
        }

        // 3. JETER - disponible si l'item est possédé
        if (vIsOwned) {
            this.aContextMenu.addItem(Lang.localizableString("drop"), () -> this.doAction("drop"));
        }

        // 4. UTILISER - disponible si l'item est utilisable
        if (vItem.isUsable()) {
            if (this.aContextMenu.getComponentCount() > 0 &&
                    !(this.aContextMenu
                            .getComponent(this.aContextMenu.getComponentCount() - 1) instanceof JPopupMenu.Separator)) {
                this.aContextMenu.addSeparator();
            }
            this.aContextMenu.addItem(Lang.localizableString("use"), () -> this.doAction("use"));
        }

        // 5. MANGER - disponible si l'item est un cookie magique
        if (vItem instanceof MagicCookie) {
            if (this.aContextMenu.getComponentCount() > 0 &&
                    !(this.aContextMenu
                            .getComponent(this.aContextMenu.getComponentCount() - 1) instanceof JPopupMenu.Separator)) {
                this.aContextMenu.addSeparator();
            }
            this.aContextMenu.addItem(Lang.localizableString("eat"), () -> this.doAction("eat"));
        }

        // Actions spécifiques au Beamer
        if (vItem instanceof Beamer) {
            final Beamer vBeamer = (Beamer) vItem;

            if (this.aContextMenu.getComponentCount() > 0 &&
                    !(this.aContextMenu
                            .getComponent(this.aContextMenu.getComponentCount() - 1) instanceof JPopupMenu.Separator)) {
                this.aContextMenu.addSeparator();
            }

            if (vIsOwned && !vBeamer.isCharged()) {
                this.aContextMenu.addItem(Lang.localizableString("charge"), () -> this.doAction("charge"));
            }

            if (vIsOwned && vBeamer.isCharged()) {
                this.aContextMenu.addItem(Lang.localizableString("fire") + " → " +
                        vBeamer.getMemorizedRoom().getShortDescription(),
                        () -> this.doAction("fire"));
            }
        }

        if (this.aContextMenu.getComponentCount() == 0) {
            final JMenuItem vNoActionItem = new JMenuItem(Lang.localizableString("no_actions"));
            vNoActionItem.setBackground(MENU_BG);
            vNoActionItem.setForeground(Color.GRAY);
            vNoActionItem.setFont(new Font("SansSerif", Font.ITALIC, 12));
            vNoActionItem.setEnabled(false);
            vNoActionItem.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            this.aContextMenu.add(vNoActionItem);
        }

        this.aContextMenu.show(this.aInventoryList, pEvent.getX(), pEvent.getY());
    }

    /**
     * Exécute l'action sur l'item sélectionné.
     * 
     * @param pAction L'identifiant de l'action
     */
    private void doAction(final String pAction) {
        final InventoryItem vSelected = this.aInventoryList.getSelectedValue();
        if (vSelected != null) {
            this.executeAction(pAction, vSelected);
        }
    }

    /**
     * Crée la barre de titre avec bouton de fermeture.
     * 
     * @return Le panneau de la barre de titre
     */
    private JPanel createTitleBarPanel() {
        final JPanel vPanel = new JPanel(new BorderLayout());
        vPanel.setBackground(PANEL_BG);
        vPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        final JLabel vTitleLabel = new JLabel(Lang.localizableString("inventory_title").toUpperCase());
        vTitleLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
        vTitleLabel.setForeground(ACCENT_COLOR);
        vPanel.add(vTitleLabel, BorderLayout.WEST);

        vPanel.add(this.createCloseButton(), BorderLayout.EAST);

        final JPanel vStatsPanel = this.createStatsPanel();

        final JPanel vMainPanel = new JPanel(new BorderLayout());
        vMainPanel.setBackground(PANEL_BG);
        vMainPanel.add(vPanel, BorderLayout.NORTH);
        vMainPanel.add(vStatsPanel, BorderLayout.CENTER);

        return vMainPanel;
    }

    /**
     * Crée le panneau des statistiques.
     * 
     * @return Le panneau des statistiques
     */
    private JPanel createStatsPanel() {
        final JPanel vStatsPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        vStatsPanel.setBackground(PANEL_BG);
        vStatsPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        this.aInventorySummaryLabel = new JLabel("", SwingConstants.CENTER);
        this.aInventorySummaryLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        this.aInventorySummaryLabel.setForeground(TEXT_COLOR);
        vStatsPanel.add(this.aInventorySummaryLabel);

        final JPanel vWeightPanel = new JPanel(new BorderLayout(5, 0));
        vWeightPanel.setBackground(PANEL_BG);

        this.aWeightLabel = new JLabel();
        this.aWeightLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        this.aWeightLabel.setForeground(TEXT_COLOR);
        vWeightPanel.add(this.aWeightLabel, BorderLayout.WEST);

        this.aWeightBar = new JProgressBar(0, 100);
        this.aWeightBar.setStringPainted(true);
        this.aWeightBar.setForeground(ACCENT_COLOR);
        this.aWeightBar.setBackground(PANEL_BG);
        this.aWeightBar.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR));
        vWeightPanel.add(this.aWeightBar, BorderLayout.CENTER);

        vStatsPanel.add(vWeightPanel);

        return vStatsPanel;
    }

    /**
     * Crée le bouton de fermeture stylisé.
     * 
     * @return Le bouton de fermeture
     */
    private AppButton createCloseButton() {
        final AppButton vButton = new AppButton(
                "✕",
                CLOSE_BUTTON_COLOR, ACCENT_COLOR,
                TEXT_COLOR, Color.BLACK,
                new MatteBorder(2, 2, 2, 2, ACCENT_COLOR));

        vButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        vButton.setFocusPainted(false);
        vButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        vButton.addActionListener(pE -> this.dispose());
        vButton.setToolTipText(Lang.localizableString("close_tooltip"));

        return vButton;
    }

    /**
     * Crée une bordure arrondie pour la popup.
     * 
     * @return La bordure
     */
    private Border createPopupBorder() {
        return BorderFactory.createCompoundBorder(
                new LineBorder(ACCENT_COLOR, 2, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    /**
     * Crée le panneau des boutons d'action.
     * 
     * @return Le panneau des actions
     */
    private JPanel createActionPanel() {
        final JPanel vPanel = new JPanel(new GridLayout(1, 6, 10, 10));
        vPanel.setBackground(PANEL_BG);
        vPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        vPanel.add(this.createActionButton(Lang.localizableString("inspect"), "inspect"));
        vPanel.add(this.createActionButton(Lang.localizableString("take"), "take"));
        vPanel.add(this.createActionButton(Lang.localizableString("drop"), "drop"));
        vPanel.add(this.createActionButton(Lang.localizableString("use"), "use"));
        vPanel.add(this.createActionButton(Lang.localizableString("eat"), "eat"));
        vPanel.add(this.createActionButton(Lang.localizableString("charge"), "charge"));

        return vPanel;
    }

    /**
     * Crée un bouton d'action stylisé.
     * 
     * @param pText   Le texte du bouton
     * @param pAction L'identifiant de l'action
     * @return Le bouton configuré
     */
    private AppButton createActionButton(final String pText, final String pAction) {
        final AppButton vButton = new AppButton(
                pText,
                new Color(60, 60, 80), ACCENT_COLOR,
                TEXT_COLOR, Color.BLACK,
                new MatteBorder(2, 2, 2, 2, ACCENT_COLOR));

        vButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        vButton.setFocusPainted(false);
        vButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        vButton.addActionListener(pE -> {
            final InventoryItem vSelected = this.aInventoryList.getSelectedValue();
            if (vSelected != null) {
                this.executeAction(pAction, vSelected);
            } else {
                JOptionPane.showMessageDialog(this,
                        Lang.localizableString("no_item_selected"),
                        Lang.localizableString("warning"),
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        return vButton;
    }

    /**
     * Exécute une action sur un item sélectionné.
     * 
     * @param pAction   L'action à exécuter
     * @param pSelected L'item sélectionné
     */
    private void executeAction(final String pAction, final InventoryItem pSelected) {
        final Item vItem = pSelected.getItem();

        switch (pAction) {
            case "drop":
                if (pSelected.isOwned()) {
                    this.aEngine.interpretCommand("drop " + vItem.getName());
                } else {
                    this.showWarning(Lang.localizableString("cannot_drop"));
                }
                break;

            case "use":
                if (vItem.isUsable()) {
                    this.aEngine.interpretCommand("use " + vItem.getName());
                } else {
                    this.showWarning(Lang.localizableString("cannot_use"));
                }
                break;

            case "inspect":
                this.showItemDetails(vItem);
                break;

            case "take":
                if (!pSelected.isOwned() && vItem.canBePickedUp()) {
                    this.aEngine.interpretCommand("take " + vItem.getName());
                } else if (!vItem.canBePickedUp()) {
                    this.showWarning(Lang.localizableString("cannot_pickup"));
                } else {
                    this.showWarning(Lang.localizableString("already_owned"));
                }
                break;

            case "eat":
                if (vItem instanceof MagicCookie) {
                    this.aEngine.interpretCommand("eat " + vItem.getName());
                } else {
                    this.showWarning(Lang.localizableString("cannot_eat"));
                }
                break;

            case "charge":
                if (vItem instanceof Beamer && pSelected.isOwned()) {
                    final Beamer vBeamer = (Beamer) vItem;
                    if (!vBeamer.isCharged()) {
                        this.aEngine.interpretCommand("charge");
                    } else {
                        this.showWarning(Lang.localizableString("beamer_already_charged"));
                    }
                }
                break;

            case "fire":
                if (vItem instanceof Beamer && pSelected.isOwned()) {
                    final Beamer vBeamer = (Beamer) vItem;
                    if (vBeamer.isCharged()) {
                        this.aEngine.interpretCommand("fire");
                    } else {
                        this.showWarning(Lang.localizableString("beamer_not_charged"));
                    }
                }
                break;
        }

        this.loadInventoryData();
    }

    /**
     * Affiche un message d'avertissement.
     * 
     * @param pMessage Le message à afficher
     */
    private void showWarning(final String pMessage) {
        JOptionPane.showMessageDialog(this,
                pMessage,
                Lang.localizableString("warning"),
                JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Charge les données de l'inventaire.
     */
    public void loadInventoryData() {
        this.aListModel.clear();

        for (final Item vItem : this.aPlayer.getInventory().getItems()) {
            this.aListModel.addElement(new InventoryItem(vItem, true));
        }

        final Room vCurrentRoom = this.aPlayer.getCurrentRoom();
        for (final Item vItem : vCurrentRoom.getItems().getItems()) {
            this.aListModel.addElement(new InventoryItem(vItem, false));
        }

        this.updateStats();
    }

    /**
     * Met à jour les statistiques d'inventaire.
     */
    private void updateStats() {
        final int vItemCount = this.aPlayer.getInventory().size();
        final double vCurrentWeight = this.aPlayer.getCurrentWeight() / 1000.0;
        final double vMaxWeight = this.aPlayer.getMaxWeight() / 1000.0;
        final int vWeightPercent = (int) ((vCurrentWeight * 100) / vMaxWeight);

        this.aInventorySummaryLabel.setText(String.format(
                Lang.localizableString("inventory_summary_format"),
                vItemCount, vCurrentWeight, vMaxWeight));

        this.aWeightLabel.setText(String.format(
                Lang.localizableString("weight_format"),
                vCurrentWeight, vMaxWeight));
        this.aWeightBar.setValue(vWeightPercent);

        if (vWeightPercent > 90) {
            this.aWeightBar.setForeground(Color.RED);
        } else if (vWeightPercent > 70) {
            this.aWeightBar.setForeground(Color.ORANGE);
        } else {
            this.aWeightBar.setForeground(ACCENT_COLOR);
        }
    }

    /**
     * Affiche les détails d'un objet.
     * 
     * @param pItem L'objet à examiner
     */
    public void showItemDetails(final Item pItem) {
        this.aEngine.showItemDetails(pItem);
        this.loadInventoryData();
    }
}