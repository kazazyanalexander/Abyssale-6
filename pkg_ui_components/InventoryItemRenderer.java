package pkg_ui_components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import pkg_utility.ImageUtils;

/**
 * Renderer personnalisé pour colorer les items selon leur statut
 * Affiche les items possédés en vert et les items dans la pièce en bleu clair.
 * La sélection est indiquée par une couleur de fond personnalisée.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
class InventoryItemRenderer extends DefaultListCellRenderer {

    /** Couleur de fond pour les items possédés par le joueur (vert clair). */
    private static final Color OWNED_COLOR = new Color(150, 255, 150);

    /** Couleur de fond pour les items présents dans la pièce (bleu clair). */
    private static final Color NOT_OWNED_COLOR = new Color(220, 240, 255);

    /** Couleur de fond pour l'élément sélectionné (bleu semi-transparent). */
    private static final Color SELECTION_BG = new Color(100, 150, 255, 100);

    /**
     * Constructeur par défaut du renderer.
     */
    public InventoryItemRenderer() {
        // Constructeur par défaut
    }

    /**
     * Retourne le composant de rendu pour un item de la liste
     * 
     * @param list         La liste dans laquelle l'item est affiché
     * @param value        L'objet à afficher (doit être une instance de
     *                     InventoryItem)
     * @param index        L'index de l'item dans la liste
     * @param isSelected   Indique si l'item est sélectionné
     * @param cellHasFocus Indique si l'item a le focus
     * @return Le composant de rendu pour l'item
     */
    @Override
    public Component getListCellRendererComponent(JList<?> list, Object value,
            int index, boolean isSelected,
            boolean cellHasFocus) {

        Component c = super.getListCellRendererComponent(list, value, index,
                isSelected, cellHasFocus);

        if (value instanceof InventoryItem) {
            InventoryItem vItem = (InventoryItem) value;

            // Couleur selon que l'objet est possédé ou non
            if (vItem.isOwned()) {
                setForeground(OWNED_COLOR);
                setFont(getFont().deriveFont(Font.BOLD));
            } else {
                setForeground(NOT_OWNED_COLOR);
            }

            // Couleur de sélection personnalisée
            if (isSelected) {
                setBackground(SELECTION_BG);
            } else {
                setBackground(list.getBackground());
            }

            // Ajouter une icône selon le type (optionnel)
            setIcon(ImageUtils.loadItemIcon(vItem.getItem(), 24));
        }

        return c;
    }

}
