package pkg_ui_components;

import javax.swing.ImageIcon;

/**
 * Overlay - Interface représentant un élément graphique superposé
 * sur le panneau de transition.
 * 
 * Un overlay peut être un personnage, un item de la pièce ou un item
 * de l'inventaire. Chaque overlay possède une icône, une clé d'identification,
 * une position et un niveau de transparence.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public interface Overlay {

    /**
     * Retourne l'icône de l'overlay.
     * 
     * @return L'icône à afficher
     */
    ImageIcon getIcon();

    /**
     * Retourne la clé d'identification de l'overlay.
     * 
     * @return La clé (nom du personnage ou de l'item)
     */
    String getNameKey();

    /**
     * Retourne la coordonnée X de l'overlay.
     * 
     * @return La position horizontale en pixels
     */
    int getX();

    /**
     * Retourne la coordonnée Y de l'overlay.
     * 
     * @return La position verticale en pixels
     */
    int getY();

    /**
     * Retourne la largeur de l'overlay.
     * 
     * @return La largeur en pixels
     */
    int getWidth();

    /**
     * Retourne la hauteur de l'overlay.
     * 
     * @return La hauteur en pixels
     */
    int getHeight();

    /**
     * Retourne le niveau de transparence de l'overlay.
     * 
     * @return La transparence (0 = transparent, 1 = opaque)
     */
    float getAlpha();

    /**
     * Définit la position de l'overlay.
     * 
     * @param pX La nouvelle coordonnée X
     * @param pY La nouvelle coordonnée Y
     */
    void setPosition(int pX, int pY);

    /**
     * Définit le niveau de transparence de l'overlay.
     * 
     * @param pAlpha Le nouveau niveau de transparence (0-1)
     */
    void setAlpha(float pAlpha);
}