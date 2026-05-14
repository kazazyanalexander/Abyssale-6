package pkg_ui_components;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.BorderFactory;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import javax.swing.border.MatteBorder;
import javax.swing.JComponent;

/**
 * 
 * Menu contextuel multiplateforme personnalisé qui contourne le rendu natif
 * du Look and Feel pour l'arrière-plan du menu et chaque élément.
 * Rendu cohérent sur macOS et Windows.
 * Utilisation (remplacement direct de l'ancien modèle addMenuItem) :
 * AppPopupMenu menu = new AppPopupMenu();
 * menu.addItem("Prendre", () -> fairePrendre());
 * menu.addItem("Déposer", () -> faireDéposer());
 * menu.addItem("Équiper", () -> faireÉquiper());
 * menu.show(invocateur, x, y);
 * Couleurs personnalisées :
 * AppPopupMenu menu = new AppPopupMenu(
 * fondÉlément, fondSurvolÉlément, texteÉlément, texteSurvolÉlément,
 * bordureMenu, policeÉlément);
 * 
 * @author Alexandre KAZAZYAN
 * 
 * @version 04/2026
 */
public class AppPopupMenu extends JPopupMenu {

    // ── Palette par défaut ─────────────────────────────────────────────────────

    /** Couleur de fond du menu par défaut. */
    private static final Color COULEUR_FOND_MENU_DEFAUT = new Color(36, 38, 52);

    /** Couleur de fond des éléments de menu par défaut. */
    private static final Color COULEUR_FOND_ELEMENT_DEFAUT = new Color(60, 60, 80);

    /** Couleur de fond au survol des éléments par défaut. */
    private static final Color COULEUR_SURVOL_ELEMENT_DEFAUT = new Color(100, 200, 255);

    /** Couleur du texte des éléments par défaut. */
    private static final Color COULEUR_TEXTE_ELEMENT_DEFAUT = new Color(220, 240, 255);

    /** Couleur du texte au survol des éléments par défaut. */
    private static final Color COULEUR_TEXTE_SURVOL_DEFAUT = Color.BLACK;

    /** Couleur de la bordure du menu par défaut. */
    private static final Color COULEUR_BORDURE_DEFAUT = new Color(110, 150, 200);

    /** Police de caractères des éléments par défaut. */
    private static final Font POLICE_DEFAUT = new Font("SansSerif", Font.PLAIN, 13);

    // ── Configuration d'instance ───────────────────────────────────────────────

    /** Couleur de fond normale des éléments de menu. */
    private final Color aFondElement;

    /** Couleur de fond au survol des éléments de menu. */
    private final Color aFondSurvolElement;

    /** Couleur du texte normale des éléments de menu. */
    private final Color aTexteElement;

    /** Couleur du texte au survol des éléments de menu. */
    private final Color aTexteSurvolElement;

    /** Police de caractères des éléments de menu. */
    private final Font aPoliceElement;

    // ── Constructeurs ──────────────────────────────────────────────────────────

    /**
     * 
     * Crée un AppPopupMenu avec le thème sombre par défaut.
     */
    public AppPopupMenu() {
        this(COULEUR_FOND_ELEMENT_DEFAUT, COULEUR_SURVOL_ELEMENT_DEFAUT,
                COULEUR_TEXTE_ELEMENT_DEFAUT, COULEUR_TEXTE_SURVOL_DEFAUT,
                new MatteBorder(2, 2, 2, 2, COULEUR_BORDURE_DEFAUT),
                POLICE_DEFAUT);
    }

    /**
     * 
     * Crée un AppPopupMenu avec couleurs, bordure et police entièrement
     * personnalisées.
     * 
     * @param pFondElement        Fond normal de chaque élément
     * @param pFondSurvolElement  Fond au survol de chaque élément
     * @param pTexteElement       Couleur normale du texte de chaque élément
     * @param pTexteSurvolElement Couleur du texte au survol
     * @param pBordure            Bordure autour du menu contextuel
     * @param pPolice             Police utilisée pour tous les éléments
     */
    public AppPopupMenu(final Color pFondElement, final Color pFondSurvolElement,
            final Color pTexteElement, final Color pTexteSurvolElement,
            final Border pBordure, final Font pPolice) {
        super();

        this.aFondElement = pFondElement;
        this.aFondSurvolElement = pFondSurvolElement;
        this.aTexteElement = pTexteElement;
        this.aTexteSurvolElement = pTexteSurvolElement;
        this.aPoliceElement = pPolice;

        // Peindre l'arrière-plan du menu nous-mêmes
        setOpaque(true);
        setBackground(COULEUR_FOND_MENU_DEFAUT);
        if (pBordure != null) {
            setBorder(pBordure);
        }
    }

    // ── API publique ───────────────────────────────────────────────────────────

    /**
     * 
     * Ajoute un élément de menu avec une étiquette et une action.
     * C'est la seule méthode nécessaire — remplace l'ancien assistant addMenuItem.
     * 
     * @param pTexte  Étiquette affichée dans le menu
     * @param pAction Runnable exécuté lors du clic sur l'élément
     * @return L'AppMenuItem créé (pour personnalisation supplémentaire si besoin)
     */
    public AppMenuItem addItem(final String pTexte, final Runnable pAction) {
        AppMenuItem vElement = new AppMenuItem(pTexte, aFondElement, aFondSurvolElement,
                aTexteElement, aTexteSurvolElement, aPoliceElement);
        vElement.addActionListener(e -> pAction.run());
        add(vElement);
        return vElement;
    }

    /**
     * 
     * Ajoute un séparateur stylisé entre les groupes d'éléments,
     * utilisant la couleur d'accentuation par défaut.
     */
    public void addDivider() {
        addDivider(COULEUR_BORDURE_DEFAUT, 1, 10);
    }

    /**
     * 
     * Ajoute un séparateur entièrement personnalisable entre les groupes
     * d'éléments.
     * 
     * @param pCouleur   Couleur de la ligne de séparation
     * @param pEpaisseur Épaisseur de la ligne en pixels
     * @param pMargeV    Marge verticale (espace au-dessus et en dessous de la
     *                   ligne)
     */
    public void addDivider(final Color pCouleur, final int pEpaisseur, final int pMargeV) {
        add(new AppSeparator(pCouleur, pEpaisseur, pMargeV, COULEUR_FOND_MENU_DEFAUT));
    }

    // ── Classe interne : AppSeparator ──────────────────────────────────────────

    /**
     * 
     * Composant séparateur personnalisé peint manuellement pour une cohérence
     * multiplateforme totale.
     */
    public static class AppSeparator extends JComponent {
        /** Couleur de la ligne de séparation. */
        private final Color aCouleurLigne;

        /** Épaisseur de la ligne de séparation (en pixels). */
        private final int aEpaisseur;

        /** Marge verticale autour de la ligne de séparation (en pixels). */
        private final int aMargeV;

        /** Couleur de fond du séparateur. */
        private final Color aFond;

        /**
         * Construit un séparateur avec les paramètres spécifiés.
         * 
         * @param pCouleurLigne Couleur de la ligne de séparation
         * @param pEpaisseur    Épaisseur de la ligne en pixels
         * @param pMargeV       Espace vertical au-dessus et en dessous de la ligne
         * @param pFond         Couleur de fond (doit correspondre au fond du menu)
         */
        public AppSeparator(final Color pCouleurLigne, final int pEpaisseur,
                final int pMargeV, final Color pFond) {
            this.aCouleurLigne = pCouleurLigne;
            this.aEpaisseur = pEpaisseur;
            this.aMargeV = pMargeV;
            this.aFond = pFond;
            setPreferredSize(new java.awt.Dimension(0, pEpaisseur + pMargeV * 2));
            setOpaque(false);
        }

        @Override
        protected void paintComponent(final Graphics pG) {
            Graphics2D g2 = (Graphics2D) pG.create();

            // Remplissage de l'arrière-plan
            g2.setColor(aFond);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Ligne de séparation, centrée verticalement avec la marge
            g2.setColor(aCouleurLigne);
            int yVertical = aMargeV + aEpaisseur / 2;
            g2.fillRect(0, yVertical, getWidth(), aEpaisseur);

            g2.dispose();
        }
    }

    // ── Peinture de l'arrière-plan du menu ────────────────────────────────────

    /**
     * 
     * Peint l'arrière-plan du menu contextuel, contournant le Look and Feel natif.
     */
    @Override
    protected void paintComponent(final Graphics pG) {
        Graphics2D g2 = (Graphics2D) pG.create();
        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());
        g2.dispose();
    }

    // ── Classe interne : AppMenuItem ───────────────────────────────────────────

    /**
     * 
     * Élément de menu personnalisé qui se peint entièrement,
     * 
     * contournant le Look and Feel natif.
     * 
     * L'arrière-plan et le texte sont dessinés manuellement pour une cohérence
     * multiplateforme totale.
     */
    public static class AppMenuItem extends JMenuItem {
        /** Couleur de fond normale (état non survolé). */
        private final Color aFondNormal;

        /** Couleur de fond au survol (état survolé). */
        private final Color aFondSurvol;

        /** Couleur du texte normale (état non survolé). */
        private final Color aTexteNormal;

        /** Couleur du texte au survol (état survolé). */
        private final Color aTexteSurvol;

        /** Indique si la souris survole actuellement l'élément de menu. */
        private boolean aSurvolé = false;

        /**
         * 
         * Construit un AppMenuItem avec des couleurs et une police explicites.
         * 
         * @param pTexte       Texte de l'étiquette
         * @param pFondNormal  Fond normal
         * @param pFondSurvol  Fond au survol
         * @param pTexteNormal Couleur normale du texte
         * @param pTexteSurvol Couleur du texte au survol
         * @param pPolice      Police pour l'étiquette
         */
        public AppMenuItem(final String pTexte,
                final Color pFondNormal, final Color pFondSurvol,
                final Color pTexteNormal, final Color pTexteSurvol,
                final Font pPolice) {
            super(pTexte);

            this.aFondNormal = pFondNormal;
            this.aFondSurvol = pFondSurvol;
            this.aTexteNormal = pTexteNormal;
            this.aTexteSurvol = pTexteSurvol;

            // Désactiver tous les hooks de rendu natifs
            setContentAreaFilled(false);
            setOpaque(false);
            setFont(pPolice);
            setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

            // Suivi du survol
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(final MouseEvent e) {
                    aSurvolé = true;
                    repaint();
                }

                @Override
                public void mouseExited(final MouseEvent e) {
                    aSurvolé = false;
                    repaint();
                }
            });
        }

        /**
         * 
         * Peint l'élément de menu depuis zéro, contournant le rendu natif du Look and
         * Feel.
         * 
         * @param pG Contexte graphique pour le dessin
         */
        @Override
        protected void paintComponent(final Graphics pG) {
            Graphics2D g2 = (Graphics2D) pG.create();

            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                    RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Arrière-plan
            g2.setColor(aSurvolé ? aFondSurvol : aFondNormal);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Texte de l'étiquette centré
            g2.setFont(getFont());
            g2.setColor(aSurvolé ? aTexteSurvol : aTexteNormal);
            FontMetrics fm = g2.getFontMetrics();
            String vTexte = getText();
            int xPosition = getInsets().left;
            int yPosition = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(vTexte, xPosition, yPosition);

            g2.dispose();
        }
    }
}