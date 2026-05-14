package pkg_ui_components;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import pkg_characters.Character;
import pkg_characters.MovingCharacter;

/**
 * Renderer personnalisé pour la liste des personnages.
 */
public class CharacterItemRenderer extends DefaultListCellRenderer {
    /** Couleurs pour les personnages */
    private static final Color NPC_COLOR = new Color(150, 255, 150);
    /** Couleur pour les personnages en mouvement */
    private static final Color MOVING_COLOR = new Color(255, 200, 100);

    /**
     * Constructeur par défaut du renderer.
     */
    public CharacterItemRenderer() {
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
    public Component getListCellRendererComponent(
            JList<?> list, Object value, int index,
            boolean isSelected, boolean cellHasFocus) {

        Component vComponent = super.getListCellRendererComponent(
                list, value, index, isSelected, cellHasFocus);

        if (value instanceof CharacterItem) {
            CharacterItem vItem = (CharacterItem) value;
            Character vChar = vItem.getCharacter();

            // Couleur selon le type
            if (vChar instanceof MovingCharacter) {
                setForeground(MOVING_COLOR);
            } else {
                setForeground(NPC_COLOR);
            }

            if (isSelected) {
                setBackground(new Color(100, 150, 255, 100));
            }

        }

        return vComponent;
    }

}