package pkg_ui_components;

import javax.swing.ImageIcon;

/**
 * CharacterOverlay - Overlay représentant un personnage.
 * 
 * Cette classe gère l'affichage des personnages (PNJ) sur le panneau
 * de transition. Les personnages sont affichés en bas à droite et
 * peuvent avoir des dimensions variables (redimensionnement proportionnel).
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class CharacterOverlay extends BaseOverlay {

    /**
     * Constructeur d'un overlay de personnage.
     * 
     * @param pIcon    L'icône du personnage
     * @param pNameKey La clé d'identification du personnage
     * @param pWidth   La largeur de l'overlay en pixels
     * @param pHeight  La hauteur de l'overlay en pixels
     */
    public CharacterOverlay(final ImageIcon pIcon, final String pNameKey,
            final int pWidth, final int pHeight) {
        super(pIcon, pNameKey, pWidth, pHeight);
    }

    /**
     * Retourne une description textuelle de l'overlay.
     * 
     * @return La description du personnage
     */
    @Override
    public String toString() {
        return "CharacterOverlay{name=" + this.aNameKey + ", pos=(" + this.aX + "," + this.aY + ")}";
    }
}