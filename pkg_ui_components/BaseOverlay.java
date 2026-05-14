package pkg_ui_components;

import javax.swing.ImageIcon;

/**
 * BaseOverlay - Implémentation de base de l'interface Overlay.
 *
 * Cette classe abstraite fournit l'implémentation commune à tous les overlays :
 * stockage de l'icône, de la clé d'identification, des dimensions,
 * de la position et de la transparence.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public abstract class BaseOverlay implements Overlay {

    /** Icône de l'overlay (image du personnage ou de l'item). */
    protected final ImageIcon aIcon;

    /** Clé d'identification unique de l'overlay. */
    protected final String aNameKey;

    /** Largeur de l'overlay en pixels. */
    protected final int aWidth;

    /** Hauteur de l'overlay en pixels. */
    protected final int aHeight;

    /** Coordonnée X de l'overlay (position horizontale à l'écran). */
    protected int aX;

    /** Coordonnée Y de l'overlay (position verticale à l'écran). */
    protected int aY;

    /** Niveau de transparence de l'overlay (0 = transparent, 1 = opaque). */
    protected float aAlpha = 0.9f;

    /**
     * Constructeur d'un overlay de base.
     * 
     * @param pIcon    L'icône à afficher
     * @param pNameKey La clé d'identification
     * @param pWidth   La largeur de l'overlay en pixels
     * @param pHeight  La hauteur de l'overlay en pixels
     */
    public BaseOverlay(final ImageIcon pIcon, final String pNameKey,
            final int pWidth, final int pHeight) {
        this.aIcon = pIcon;
        this.aNameKey = pNameKey;
        this.aWidth = pWidth;
        this.aHeight = pHeight;
    }

    @Override
    public ImageIcon getIcon() {
        return this.aIcon;
    }

    @Override
    public String getNameKey() {
        return this.aNameKey;
    }

    @Override
    public int getX() {
        return this.aX;
    }

    @Override
    public int getY() {
        return this.aY;
    }

    @Override
    public int getWidth() {
        return aWidth;
    }

    @Override
    public int getHeight() {
        return this.aHeight;
    }

    @Override
    public float getAlpha() {
        return this.aAlpha;
    }

    @Override
    public void setPosition(int pX, int pY) {
        this.aX = pX;
        this.aY = pY;
    }

    @Override
    public void setAlpha(float pAlpha) {
        this.aAlpha = Math.max(0.0f, Math.min(1.0f, pAlpha));
    }
}