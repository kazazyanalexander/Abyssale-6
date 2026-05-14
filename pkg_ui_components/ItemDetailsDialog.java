package pkg_ui_components;

import pkg_core.GameEngine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.border.MatteBorder;
import pkg_items.Beamer;
import pkg_items.Item;
import pkg_items.MagicCookie;
import pkg_utility.Lang;
import pkg_utility.ImageUtils;

/**
 * ItemDetailsDialog - Dialogue modale affichant les détails d'un objet.
 * Peut être utilisé indépendamment de l'InventoryPopup.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public final class ItemDetailsDialog extends JDialog {
    /** Moteur de jeu principal */
    private final GameEngine aEngine;

    /** L'objet à examiner */
    private final Item aItem;
    /** Couleur du texte principal (blanc bleuté). */
    private static final Color TEXT_COLOR = new Color(220, 240, 255);

    /**
     * Couleur d'accentuation pour les titres et les éléments sélectionnés (bleu
     * clair).
     */
    private static final Color ACCENT_COLOR = new Color(100, 200, 255);

    /**
     * Constructeur du dialogue de détails d'objet.
     * 
     * @param pOwner  La fenêtre parente
     * @param pEngine Le moteur de jeu
     * @param pItem   L'objet à examiner
     */
    public ItemDetailsDialog(final Frame pOwner, final GameEngine pEngine, final Item pItem) {
        super(pOwner, Lang.localizableString("item_details_title"), true);
        this.aEngine = pEngine;
        this.aItem = pItem;

        initializeUI();
        pack();
        setLocationRelativeTo(pOwner);
    }

    /**
     * Initialise l'interface utilisateur.
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(40, 40, 50));
        setResizable(false);

        JPanel vMainPanel = new JPanel(new BorderLayout(15, 15));
        vMainPanel.setBackground(new Color(40, 40, 50));
        vMainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Panneau supérieur avec icône et titre
        JPanel vTopPanel = createTopPanel();
        vMainPanel.add(vTopPanel, BorderLayout.NORTH);

        // Panneau central avec les détails
        JPanel vDetailsPanel = createDetailsPanel();
        vMainPanel.add(vDetailsPanel, BorderLayout.CENTER);

        // Panneau inférieur avec les boutons d'action
        JPanel vButtonPanel = createButtonPanel();
        vMainPanel.add(vButtonPanel, BorderLayout.SOUTH);

        add(vMainPanel, BorderLayout.CENTER);

        // Fermeture avec Echap
        getRootPane().registerKeyboardAction(
                e -> dispose(),
                KeyStroke.getKeyStroke("ESCAPE"),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    /**
     * Crée le panneau supérieur avec l'icône et le titre.
     * 
     * @return Le panneau supérieur configuré
     */
    private JPanel createTopPanel() {
        JPanel vPanel = new JPanel(new BorderLayout(15, 0));
        vPanel.setBackground(new Color(40, 40, 50));

        // Icône de l'objet
        Icon vItemIcon = ImageUtils.loadItemIcon(aItem, 64);
        if (vItemIcon != null) {
            JLabel vIconLabel = new JLabel(vItemIcon);
            vPanel.add(vIconLabel, BorderLayout.WEST);
        }

        // Titre
        JLabel vTitleLabel = new JLabel(aItem.getInformation());
        vTitleLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        vTitleLabel.setForeground(new Color(100, 200, 255));
        vTitleLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        vPanel.add(vTitleLabel, BorderLayout.CENTER);

        return vPanel;
    }

    /**
     * Crée le panneau central avec les détails de l'objet.
     * 
     * @return Le panneau central configuré
     */
    private JPanel createDetailsPanel() {
        String vDetails = String.format(
                Lang.localizableString("item_details_format"),
                this.aItem.getInformation(),
                this.aItem.getWeight() / 1000.0,
                this.aItem.canBePickedUp() ? Lang.localizableString("yes") : Lang.localizableString("no"),
                this.aItem.getType().name());

        JTextArea vTextArea = new JTextArea(vDetails);
        vTextArea.setEditable(false);
        vTextArea.setBackground(new Color(30, 30, 40));
        vTextArea.setForeground(new Color(220, 240, 255));
        vTextArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        vTextArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(100, 200, 255)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        vTextArea.setLineWrap(true);
        vTextArea.setWrapStyleWord(true);

        JPanel vPanel = new JPanel(new BorderLayout());
        vPanel.setBackground(new Color(40, 40, 50));
        vPanel.add(vTextArea, BorderLayout.CENTER);

        return vPanel;
    }

    /**
     * Crée le panneau inférieur avec les boutons d'action.
     * 
     * @return Le panneau inférieur configuré
     */
    private JPanel createButtonPanel() {
        JPanel vPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        vPanel.setBackground(new Color(40, 40, 50));

        // Bouton Fermer
        AppButton vCloseButton = createActionButton(Lang.localizableString("close"));

        vCloseButton.addActionListener(e -> dispose());
        vPanel.add(vCloseButton);

        // Bouton Utiliser (si applicable)
        if (this.aItem.isUsable()) {
            AppButton vUseButton = createActionButton(Lang.localizableString("use"));

            vUseButton.addActionListener(e -> {
                this.aEngine.interpretCommand("use " + this.aItem.getName());
                dispose();
            });
            vPanel.add(vUseButton);
        }

        // Bouton Manger (si c'est un cookie magique)
        if (this.aItem instanceof MagicCookie) {
            AppButton vEatButton = createActionButton(Lang.localizableString("eat"));

            vEatButton.addActionListener(e -> {
                this.aEngine.interpretCommand("eat " + this.aItem.getName());
                dispose();
            });
            vPanel.add(vEatButton);
        }

        // Bouton Charger/Déclencher (si beamer)
        if (this.aItem instanceof Beamer) {
            Beamer vBeamer = (Beamer) this.aItem;
            if (!vBeamer.isCharged()) {
                AppButton vChargeButton = createActionButton(Lang.localizableString("charge"));

                vChargeButton.addActionListener(e -> {
                    this.aEngine.interpretCommand("charge");
                    dispose();
                });
                vPanel.add(vChargeButton);
            } else {
                AppButton vFireButton = createActionButton(Lang.localizableString("fire"));

                vFireButton.addActionListener(e -> {
                    this.aEngine.interpretCommand("fire");
                    dispose();
                });
                vPanel.add(vFireButton);
            }
        }

        return vPanel;
    }

    /**
     * Crée un bouton d'action stylisé.
     * 
     * @param pText Le texte du bouton
     * @return Le bouton configuré
     */
    private AppButton createActionButton(final String pText) {
        AppButton vButton = new AppButton(
                pText,
                new Color(60, 60, 80), ACCENT_COLOR, // normal bg, hover bg
                TEXT_COLOR, Color.BLACK, // normal fg, hover fg
                new MatteBorder(2, 2, 2, 2, ACCENT_COLOR));

        vButton.setFont(new Font("SansSerif", Font.BOLD, 12));
        vButton.setFocusPainted(false);
        vButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT_COLOR),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));

        return vButton;
    }

}