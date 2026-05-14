package pkg_ui_components;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;
import javax.swing.border.Border;
import javax.swing.BorderFactory;

/**
 * 
 * Bouton multiplateforme personnalisé qui contourne le rendu natif
 * du Look and Feel. Rendu cohérent sur macOS et Windows en redéfinissant
 * paintComponent.
 * Utilisation :
 * AppButton btn = new AppButton("REGARDER");
 * AppButton btn = new AppButton("ALLER", fondNormal, fondSurvol, couleurTexte,
 * couleurTexteSurvol, bordure);
 * 
 * @author Alexandre KAZAZYAN
 * @version 04/2026
 */
public class AppButton extends JButton {

    // ── Palette par défaut (correspond aux constantes de GameGUI) ─────────────

    /** Couleur de fond par défaut. */
    private static final Color FOND_DEFAUT = new Color(70, 90, 110);

    /** Couleur de fond au survol par défaut. */
    private static final Color FOND_SURVOL_DEFAUT = new Color(110, 150, 200);

    /** Couleur du texte par défaut. */
    private static final Color TEXTE_DEFAUT = Color.WHITE;

    /** Couleur du texte au survol par défaut. */
    private static final Color TEXTE_SURVOL_DEFAUT = Color.BLACK;

    /** Couleur de fond désactivé par défaut. */
    private static final Color FOND_DESACTIVE_DEFAUT = new Color(45, 50, 60);

    /** Couleur du texte désactivé par défaut. */
    private static final Color TEXTE_DESACTIVE_DEFAUT = new Color(100, 110, 120);

    // ── Couleurs d'instance ───────────────────────────────────────────────────

    /** Couleur de fond normale du bouton. */
    private Color aFondNormal;

    /** Couleur de fond au survol du bouton. */
    private Color aFondSurvol;

    /** Couleur du texte normal du bouton. */
    private Color aTexteNormal;

    /** Couleur du texte au survol du bouton. */
    private Color aTexteSurvol;

    /** Couleur de fond du bouton désactivé. */
    private Color aFondDesactive;

    /** Couleur du texte du bouton désactivé. */
    private Color aTexteDesactive;

    /** Indique si la souris survole actuellement le bouton. */
    private boolean aSurvole = false;

    // ── Constructeurs ─────────────────────────────────────────────────────────

    /**
     * 
     * Crée un AppButton avec les couleurs par défaut.
     * 
     * @param pTexte Étiquette affichée sur le bouton
     */
    public AppButton(final String pTexte) {
        this(pTexte,
                FOND_DEFAUT, FOND_SURVOL_DEFAUT,
                TEXTE_DEFAUT, TEXTE_SURVOL_DEFAUT,
                null);
    }

    /**
     * Crée un AppButton avec un texte et une couleur d'accentuation.
     * Utilise les valeurs par défaut :
     * - fond normal : new Color(60, 60, 80)
     * - fond au survol : la couleur d'accentuation
     * - texte normal : Color.WHITE
     * - texte au survol : Color.BLACK
     * - bordure : ligne de la couleur d'accentuation (épaisseur 2) avec un padding
     * de 10,25,10,25
     * 
     * @param pTexte       Étiquette du bouton
     * @param pAccentColor Couleur d'accentuation (utilisée pour le survol et la
     *                     bordure)
     */
    public AppButton(final String pTexte, final Color pAccentColor) {
        this(pTexte,
                new Color(60, 60, 80), pAccentColor,
                Color.WHITE, Color.BLACK,
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(pAccentColor, 2),
                        BorderFactory.createEmptyBorder(10, 25, 10, 25)));
    }

    /**
     * 
     * Crée un AppButton avec des couleurs entièrement personnalisées
     * et une bordure optionnelle.
     * 
     * @param pTexte       Étiquette affichée sur le bouton
     * @param pFondNormal  Couleur de fond à l'état normal
     * @param pFondSurvol  Couleur de fond au survol
     * @param pTexteNormal Couleur du texte à l'état normal
     * @param pTexteSurvol Couleur du texte au survol
     * @param pBordure     Bordure à appliquer (passer {@code null} pour aucune)
     */
    public AppButton(final String pTexte,
            final Color pFondNormal, final Color pFondSurvol,
            final Color pTexteNormal, final Color pTexteSurvol,
            final Border pBordure) {
        super(pTexte);

        this.aFondNormal = pFondNormal;
        this.aFondSurvol = pFondSurvol;
        this.aTexteNormal = pTexteNormal;
        this.aTexteSurvol = pTexteSurvol;
        this.aFondDesactive = FOND_DESACTIVE_DEFAUT;
        this.aTexteDesactive = TEXTE_DESACTIVE_DEFAUT;

        // Désactiver tous les hooks de peinture natifs
        this.setContentAreaFilled(false);
        this.setFocusPainted(false);
        setOpaque(false);

        if (pBordure != null) {
            setBorder(pBordure);
        }

        // Suivi du survol
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(final MouseEvent e) {
                if (isEnabled()) {
                    aSurvole = true;
                    repaint();
                }
            }

            @Override
            public void mouseExited(final MouseEvent e) {
                aSurvole = false;
                repaint();
            }
        });
    }

    // ── Peinture ─────────────────────────────────────────────────────────────

    /**
     * 
     * Peint le bouton complètement depuis zéro, contournant le Look and Feel natif.
     * Cela garantit un rendu identique sur Windows, macOS et Linux.
     */
    @Override
    protected void paintComponent(final Graphics pG) {
        Graphics2D g2 = (Graphics2D) pG.create();

        // Rendu fluide du texte
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Choisir les couleurs selon l'état
        Color vFond;
        Color vTexte;

        if (!isEnabled()) {
            vFond = this.aFondDesactive;
            vTexte = this.aTexteDesactive;
        } else if (this.aSurvole) {
            vFond = this.aFondSurvol;
            vTexte = this.aTexteSurvol;
        } else {
            vFond = this.aFondNormal;
            vTexte = this.aTexteNormal;
        }

        // Remplir l'arrière-plan
        g2.setColor(vFond);
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Dessiner l'étiquette centrée
        g2.setFont(getFont());
        g2.setColor(vTexte);
        FontMetrics fm = g2.getFontMetrics();
        String vTexteBouton = getText();
        int xPosition = (getWidth() - fm.stringWidth(vTexteBouton)) / 2;
        int yPosition = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(vTexteBouton, xPosition, yPosition);

        g2.dispose();
    }

}
