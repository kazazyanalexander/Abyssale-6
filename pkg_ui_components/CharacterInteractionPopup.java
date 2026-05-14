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
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import pkg_characters.Character;
import pkg_characters.Player;
import pkg_items.Item;
import pkg_utility.Lang;
import java.awt.FlowLayout;
import java.awt.Component;

/**
 * CharacterInteractionPopup - Popup pour interagir avec les personnages
 * (parler, donner).
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class CharacterInteractionPopup extends JDialog {
    /** Moteur de jeu principal */
    private final GameEngine aEngine;
    /** Joueur principal */
    private final Player aPlayer;
    /** Pièce actuelle */
    private final Room aCurrentRoom;

    // Composants de l'interface
    /** Liste des personnages dans la pièce */
    private JList<CharacterItem> aCharacterList;
    /** Modèle de la liste des personnages */
    private DefaultListModel<CharacterItem> aCharacterListModel;
    /** Label de la pièce */
    private JLabel aRoomLabel;
    /** Panneau d'interaction */
    private JPanel aInteractionPanel;

    /** Mode de l'interaction (talk ou give) */
    private final InteractionMode aMode;

    /**
     * Modes d'interaction possibles.
     */
    public enum InteractionMode {
        /** Mode conversation - parle au personnage. */
        TALK,
        /** Mode don - donne un objet au personnage. */
        GIVE
    }

    // ── Constantes de style pour l'interface graphique ─────────────────────────

    /** Couleur de fond de la popup avec transparence (noir semi-transparent). */
    private static final Color POPUP_BG = new Color(30, 30, 40, 240);

    /** Couleur de fond des panneaux internes (gris foncé). */
    private static final Color PANEL_BG = new Color(40, 40, 50);

    /** Couleur du texte principal (blanc bleuté). */
    private static final Color TEXT_COLOR = new Color(220, 240, 255);

    /**
     * Couleur d'accentuation pour les bordures et éléments interactifs (bleu
     * clair).
     */
    private static final Color ACCENT_COLOR = new Color(100, 200, 255);

    /** Couleur du bouton de fermeture (rouge). */
    private static final Color CLOSE_BUTTON_COLOR = new Color(200, 80, 80);

    /**
     * Constructeur pour la popup d'interaction avec les personnages.
     * 
     * @param pOwner  La fenêtre parente
     * @param pEngine Le moteur de jeu
     * @param pPlayer Le joueur
     * @param pMode   Le mode d'interaction (TALK ou GIVE)
     */
    public CharacterInteractionPopup(final Frame pOwner, final GameEngine pEngine,
            final Player pPlayer, final InteractionMode pMode) {
        super(pOwner, getTitleForMode(pMode), true);
        this.aEngine = pEngine;
        this.aPlayer = pPlayer;
        this.aCurrentRoom = pPlayer.getCurrentRoom();
        this.aMode = pMode;

        this.initializeUI();
        this.loadCharacterData();
        pack();

        // Positionnement centré sur le parent
        if (pOwner != null) {
            setLocationRelativeTo(pOwner);
        }
    }

    /**
     * Retourne le titre de la popup selon le mode d'interaction.
     * 
     * @param pMode Le mode d'interaction
     * @return Le titre de la popup selon le mode
     */
    private static String getTitleForMode(final InteractionMode pMode) {
        switch (pMode) {
            case TALK:
                return Lang.localizableString("talk_title");
            case GIVE:
                return Lang.localizableString("give_title");
            default:
                return "";
        }
    }

    /**
     * Initialise l'interface utilisateur.
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(POPUP_BG);
        setUndecorated(true);

        JPanel vMainPanel = new JPanel(new BorderLayout(15, 15));
        vMainPanel.setBackground(PANEL_BG);
        vMainPanel.setBorder(createPopupBorder());

        vMainPanel.add(createTitleBarPanel(), BorderLayout.NORTH);
        vMainPanel.add(createCharacterListPanel(), BorderLayout.CENTER);
        vMainPanel.add(createInteractionPanel(), BorderLayout.SOUTH);

        add(vMainPanel, BorderLayout.CENTER);

        // Fermeture avec Echap
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /**
     * Crée la barre de titre.
     * 
     * @return Le panneau de la barre de titre
     */
    private JPanel createTitleBarPanel() {
        JPanel vPanel = new JPanel(new BorderLayout());
        vPanel.setBackground(PANEL_BG);
        vPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));

        // Titre
        JLabel vTitleLabel = new JLabel(getTitleForMode(aMode).toUpperCase());
        vTitleLabel.setFont(new Font("Monospaced", Font.BOLD, 20));
        vTitleLabel.setForeground(ACCENT_COLOR);
        vPanel.add(vTitleLabel, BorderLayout.WEST);

        // Bouton de fermeture
        vPanel.add(createCloseButton(), BorderLayout.EAST);

        // Informations sur la pièce
        this.aRoomLabel = new JLabel(aCurrentRoom.getShortDescription());
        this.aRoomLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        this.aRoomLabel.setForeground(TEXT_COLOR);
        this.aRoomLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JPanel vMainPanel = new JPanel(new BorderLayout());
        vMainPanel.setBackground(PANEL_BG);
        vMainPanel.add(vPanel, BorderLayout.NORTH);
        vMainPanel.add(this.aRoomLabel, BorderLayout.CENTER);

        return vMainPanel;
    }

    /**
     * Crée le panneau avec la liste des personnages.
     * 
     * @return Le panneau de la liste des personnages
     */
    private JPanel createCharacterListPanel() {
        JPanel vPanel = new JPanel(new BorderLayout());
        vPanel.setBackground(PANEL_BG);
        vPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        this.aCharacterListModel = new DefaultListModel<>();
        this.aCharacterList = new JList<>(this.aCharacterListModel);
        this.aCharacterList.setBackground(PANEL_BG);
        this.aCharacterList.setForeground(TEXT_COLOR);
        this.aCharacterList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        this.aCharacterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.aCharacterList.setCellRenderer(new CharacterItemRenderer());

        // Double-clic pour interagir
        this.aCharacterList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    CharacterItem vSelected = aCharacterList.getSelectedValue();
                    if (vSelected != null) {
                        performInteraction(vSelected);
                    }
                }
            }
        });

        JScrollPane vScrollPane = new JScrollPane(this.aCharacterList);
        vScrollPane.setBackground(PANEL_BG);
        vScrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR));
        vScrollPane.getViewport().setBackground(PANEL_BG);

        vPanel.add(vScrollPane, BorderLayout.CENTER);

        return vPanel;
    }

    /**
     * Crée le panneau d'interaction avec les boutons.
     * 
     * @return Le panneau d'interaction avec les boutons
     */
    private JPanel createInteractionPanel() {
        this.aInteractionPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        this.aInteractionPanel.setBackground(PANEL_BG);
        this.aInteractionPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));

        this.aInteractionPanel.add(this.createInteractButton());
        this.aInteractionPanel.add(this.createCancelButton());

        return this.aInteractionPanel;
    }

    /**
     * Crée le bouton d'interaction principal.
     * 
     * @return Le bouton d'interaction principal (parler ou donner selon le mode)
     */
    private AppButton createInteractButton() {
        String vButtonText = aMode == InteractionMode.TALK ? Lang.localizableString("talk_button")
                : Lang.localizableString("give_button");

        AppButton vButton = new AppButton(
                vButtonText,
                new Color(60, 60, 80), ACCENT_COLOR, // normal bg, hover bg
                TEXT_COLOR, Color.BLACK, // normal fg, hover fg
                new MatteBorder(2, 2, 2, 2, ACCENT_COLOR));

        vButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        vButton.setFocusPainted(false);
        vButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));

        vButton.addActionListener(e -> {
            CharacterItem vSelected = aCharacterList.getSelectedValue();
            if (vSelected != null) {
                performInteraction(vSelected);
            } else {
                JOptionPane.showMessageDialog(this,
                        Lang.localizableString("no_character_selected"),
                        Lang.localizableString("warning"),
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        return vButton;
    }

    /**
     * Crée le bouton d'annulation.
     * 
     * @return Le bouton d'annulation
     */
    private AppButton createCancelButton() {

        AppButton vButton = new AppButton(
                Lang.localizableString("cancel"),
                new Color(60, 60, 80), ACCENT_COLOR, // normal bg, hover bg
                TEXT_COLOR, Color.BLACK, // normal fg, hover fg
                new MatteBorder(2, 2, 2, 2, ACCENT_COLOR));

        vButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        vButton.setFocusPainted(false);
        vButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLOSE_BUTTON_COLOR),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));

        vButton.addActionListener(e -> dispose());

        return vButton;
    }

    /**
     * Crée le bouton de fermeture.
     * 
     * @return Le bouton de fermeture
     */
    private AppButton createCloseButton() {
        AppButton vButton = new AppButton(
                "✕",
                CLOSE_BUTTON_COLOR, ACCENT_COLOR, // normal bg, hover bg
                TEXT_COLOR, Color.BLACK, // normal fg, hover fg
                new MatteBorder(2, 2, 2, 2, ACCENT_COLOR));

        vButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        vButton.setFocusPainted(false);
        vButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE, 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));

        vButton.addActionListener(e -> dispose());
        vButton.setToolTipText(Lang.localizableString("close_tooltip"));

        return vButton;
    }

    /**
     * Crée la bordure de la popup.
     * 
     * @return La bordure de la popup
     */
    private Border createPopupBorder() {
        return BorderFactory.createCompoundBorder(
                new LineBorder(ACCENT_COLOR, 2, true),
                BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    /**
     * Charge la liste des personnages présents dans la pièce.
     */
    private void loadCharacterData() {
        this.aCharacterListModel.clear();

        List<Character> vCharacters = this.aCurrentRoom.getCharacters();

        if (vCharacters.isEmpty()) {
            // Aucun personnage dans la pièce
            JOptionPane.showMessageDialog(this,
                    Lang.localizableString("no_characters_here"),
                    Lang.localizableString("info"),
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
            return;
        }

        for (Character vChar : vCharacters) {
            this.aCharacterListModel.addElement(new CharacterItem(vChar));
        }
    }

    /**
     * Effectue l'interaction avec le personnage sélectionné.
     * 
     * @param pCharacterItem L'item de personnage sélectionné
     */
    private void performInteraction(final CharacterItem pCharacterItem) {
        Character vCharacter = pCharacterItem.getCharacter();

        switch (aMode) {
            case TALK:
                // Exécuter la commande talk
                this.aEngine.interpretCommand("talk " + vCharacter.getName());
                break;

            case GIVE:
                // Pour give, il faut sélectionner un objet
                this.showItemSelectionDialog(vCharacter);
                break;
        }

        this.dispose();
    }

    /**
     * Affiche une boîte de dialogue pour sélectionner l'objet à donner.
     * Utilise un wrapper pour afficher une description plus riche tout en
     * conservant le nom de l'objet pour le moteur de jeu.
     * 
     * @param pCharacter Le personnage à qui donner l'objet
     */

    private void showItemSelectionDialog(final Character pCharacter) {
        List<Item> vInventory = this.aPlayer.getInventory().getItems();
        if (vInventory.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    Lang.localizableString("inventory_empty"),
                    Lang.localizableString("warning"),
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Créer les wrappers
        ItemDisplayWrapper[] vWrappers = new ItemDisplayWrapper[vInventory.size()];
        for (int i = 0; i < vInventory.size(); i++) {
            vWrappers[i] = new ItemDisplayWrapper(vInventory.get(i));
        }

        // Création du dialogue stylisé
        JDialog vDialog = new JDialog(this, Lang.localizableString("give_title"), true);
        vDialog.setLayout(new BorderLayout(10, 10));
        vDialog.getContentPane().setBackground(POPUP_BG);
        vDialog.setUndecorated(true);

        JPanel vMainPanel = new JPanel(new BorderLayout(15, 15));
        vMainPanel.setBackground(PANEL_BG);
        vMainPanel.setBorder(createSelectionBorder());

        // Titre
        JLabel vTitleLabel = new JLabel(Lang.localizableString("give_title").toUpperCase());
        vTitleLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        vTitleLabel.setForeground(ACCENT_COLOR);
        vTitleLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        vMainPanel.add(vTitleLabel, BorderLayout.NORTH);

        // Liste des objets
        DefaultListModel<ItemDisplayWrapper> vListModel = new DefaultListModel<>();
        for (ItemDisplayWrapper vWrapper : vWrappers) {
            vListModel.addElement(vWrapper);
        }
        JList<ItemDisplayWrapper> vItemList = new JList<>(vListModel);
        vItemList.setBackground(PANEL_BG);
        vItemList.setForeground(TEXT_COLOR);
        vItemList.setFont(new Font("Monospaced", Font.PLAIN, 14));
        vItemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        vItemList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,
                    int index, boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof ItemDisplayWrapper) {
                    setText(value.toString());
                    setForeground(TEXT_COLOR);
                    if (isSelected) {
                        setBackground(new Color(100, 150, 255, 100));
                    } else {
                        setBackground(PANEL_BG);
                    }
                }
                return c;
            }
        });

        JScrollPane vScrollPane = new JScrollPane(vItemList);
        vScrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR));
        vScrollPane.getViewport().setBackground(PANEL_BG);
        vMainPanel.add(vScrollPane, BorderLayout.CENTER);

        // Panneau des boutons
        JPanel vButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        vButtonPanel.setBackground(PANEL_BG);

        AppButton vGiveButton = new AppButton(Lang.localizableString("give_button"), new Color(100, 200, 100));
        vGiveButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        AppButton vCancelButton = new AppButton(Lang.localizableString("cancel"), CLOSE_BUTTON_COLOR);
        vCancelButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        vButtonPanel.add(vGiveButton);
        vButtonPanel.add(vCancelButton);
        vMainPanel.add(vButtonPanel, BorderLayout.SOUTH);

        vDialog.add(vMainPanel);
        vDialog.pack();
        vDialog.setLocationRelativeTo(this);

        // Double‑clic pour confirmer
        vItemList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    ItemDisplayWrapper vSelected = vItemList.getSelectedValue();
                    if (vSelected != null) {
                        vDialog.dispose();
                        aEngine.interpretCommand("give " + vSelected.getItem().getName());
                    }
                }
            }
        });

        // Bouton Donner
        vGiveButton.addActionListener(e -> {
            ItemDisplayWrapper vSelected = vItemList.getSelectedValue();
            if (vSelected != null) {
                vDialog.dispose();
                aEngine.interpretCommand("give " + vSelected.getItem().getName());
            } else {
                JOptionPane.showMessageDialog(vDialog,
                        Lang.localizableString("no_item_selected"),
                        Lang.localizableString("warning"),
                        JOptionPane.WARNING_MESSAGE);
            }
        });

        // Bouton Annuler
        vCancelButton.addActionListener(e -> vDialog.dispose());

        // Fermeture avec Échap
        vDialog.getRootPane().registerKeyboardAction(
                e -> vDialog.dispose(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW);

        vDialog.setVisible(true);
    }

    /**
     * Crée une bordure pour le dialogue de sélection d'objet.
     * 
     * @return La bordure pour le dialogue de sélection d'objet
     */
    private Border createSelectionBorder() {
        return BorderFactory.createCompoundBorder(
                new LineBorder(ACCENT_COLOR, 2, true),
                BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }
}