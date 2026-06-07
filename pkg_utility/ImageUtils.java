package pkg_utility;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Image;
import pkg_items.Item;
import java.net.URL;

/**
 * ImageUtils - Classe utilitaire pour le chargement et le redimensionnement
 * des icônes des objets (items) du jeu.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class ImageUtils {

    /**
     * Constructeur par défaut (privé pour classe utilitaire).
     * Empêche l'instanciation.
     */
    private ImageUtils() {
        // Constructeur privé pour une classe utilitaire
    }

    /**
     * Méthode utilitaire pour obtenir une icône à partir d'un Item
     * Cherche une image dans les ressources basée sur le nom de l'item
     * Redimensionne l'image pour l'affichage dans la liste
     * 
     * @param pItem L'item
     * @param pSize Taille souhaitée
     * @return L'icône correspondante ou null si aucune icône n'est trouvée
     */
    public static Icon loadItemIcon(final Item pItem, final int pSize) {
        String vImageName = pItem.getImageName();
        if (vImageName != null && !vImageName.isEmpty()) {
            try {
                URL vImageURL = ImageUtils.class.getResource("/items/" + vImageName);
                if (vImageURL != null) {
                    ImageIcon vOriginalIcon = new ImageIcon(vImageURL);
                    Image vOriginalImage = vOriginalIcon.getImage();
                    Image vResizedImage = vOriginalImage.getScaledInstance(
                            pSize, pSize, Image.SCALE_SMOOTH);
                    return new ImageIcon(vResizedImage);
                }
            } catch (Exception e) {
                System.err.println(Lang.localizableString("error_loading_icon") + " " + vImageName);
            }
        }
        return null;
    }

    /**
     * Redimensionne une image en conservant les proportions, basé sur la hauteur.
     * 
     * @param pIcon         L'icône à redimensionner
     * @param pTargetHeight La hauteur souhaitée
     * @return L'icône redimensionnée
     */
    public static ImageIcon scaleImageToHeight(final ImageIcon pIcon, final int pTargetHeight) {
        if (pIcon == null)
            return null;
        int vOriginalWidth = pIcon.getIconWidth();
        int vOriginalHeight = pIcon.getIconHeight();
        if (vOriginalHeight <= 0)
            return pIcon;
        double vRatio = (double) pTargetHeight / vOriginalHeight;
        int vTargetWidth = (int) (vOriginalWidth * vRatio);
        Image vOriginalImage = pIcon.getImage();
        Image vScaledImage = vOriginalImage.getScaledInstance(vTargetWidth, pTargetHeight, Image.SCALE_SMOOTH);
        return new ImageIcon(vScaledImage);
    }

}