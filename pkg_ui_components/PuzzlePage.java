package pkg_ui_components;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.net.URL;
import java.util.Random;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import pkg_utility.Lang;

/**
 * Page de puzzle pour le simulateur de panneau de contrôle pannel.
 * Avec voltmètre analogique dynamique et simulation de circuit.
 * Contient 8 interrupteurs à bascule et un bouton rouge circulaire.
 * L'utilisateur doit configurer correctement les interrupteurs selon
 * un problème de circuit électrique, puis appuyer sur le bouton.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public final class PuzzlePage extends JPanel {
    // ── Constantes pour les dimensions des icônes ───────────────────────────────

    /** Largeur des icônes en pixels. */
    private static final int ICON_WIDTH = 38;

    /** Hauteur des icônes en pixels. */
    private static final int ICON_HEIGHT = 56;

    // ── Coordonnées du Voltmètre (affichage de la tension) ──────────────────────

    /** Coordonnée X du centre du voltmètre. */
    private static final int V_CENTER_X = 638;

    /** Coordonnée Y du centre du voltmètre. */
    private static final int V_CENTER_Y = 296;

    /** Coordonnée X de la position zéro de l'aiguille. */
    private static final int V_ZERO_X = 605;

    /** Coordonnée Y de la position zéro de l'aiguille. */
    private static final int V_ZERO_Y = 270;

    /** Coordonnée X de la position maximale de l'aiguille. */
    private static final int V_MAX_X = 671;

    /** Coordonnée Y de la position maximale de l'aiguille. */
    private static final int V_MAX_Y = 270;

    // ── Coordonnées et dimensions du bouton rouge circulaire ────────────────────

    /** Coordonnée X du centre du bouton rouge. */
    private static final int CIRCLE_CENTER_X = 226;

    /** Coordonnée Y du centre du bouton rouge. */
    private static final int CIRCLE_CENTER_Y = 430;

    /** Diamètre du bouton rouge en pixels. */
    private static final int CIRCLE_DIAMETER = 60;

    /** Rayon du bouton rouge en pixels. */
    private static final int CIRCLE_RADIUS = CIRCLE_DIAMETER / 2;

    // ── Composants graphiques ───────────────────────────────────────────────────

    /** Indique si le cercle est actuellement en surbrillance (survolé). */
    private boolean aCircleHighlighted = false;

    /** Image de fond du panneau de contrôle. */
    private Image aBackgroundImage;

    // ── Interrupteurs et données du circuit ─────────────────────────────────────

    /** Tableau des 8 interrupteurs à bascule du circuit. */
    private final CustomToggleSwitch[] aSwitches = new CustomToggleSwitch[8];

    /** Valeurs des résistances du circuit (en ohms). */
    private final double[] aResistors = { 300, 800, 900, 600, 800, 900, 600, 800 };

    /** Tensions des sources du circuit (en volts). */
    private final double[] aVSources = { 9.0, 9.0, 9.0, 1.5, 1.5, 1.5, 0.0, 0.0 };

    /** Tension actuelle mesurée en sortie (en volts). */
    private double aCurrentVoltage = 0.0;

    /**
     * Callback notifié lorsque le joueur valide sa solution (true = succès, false =
     * échec).
     */
    private Consumer<Boolean> aPuzzleListener;

    /**
     * Constructeur principal de la page de puzzle.
     * Initialise l'interface utilisateur avec tous les composants.
     * 
     */
    public PuzzlePage() {
        // Configuration du layout et de la taille
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(900, 680));

        // Initialisation des ressources et composants
        this.loadResources();
        this.setupInteractionListeners();
        this.setupSwitches();

        // Création et configuration du label d'instructions
        this.setupInstructionLabel();
        this.calculateVoltage(); // Calcul initial
    }

    /**
     * Configure le label d'instructions avec mise en forme HTML.
     * Le texte est aligné à droite et dispose d'un wrapping automatique.
     */
    private void setupInstructionLabel() {
        // Texte formaté en HTML pour le wrapping et l'alignement
        final String instructionText = "<html><div style='text-align: left; width: 600px;'>" +
                Lang.localizableString("puzzle") + "</div></html>";

        final JLabel instructionLabel = new JLabel(instructionText);
        instructionLabel.setForeground(Color.GREEN);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        instructionLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        instructionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 20));

        add(instructionLabel, BorderLayout.SOUTH);
    }

    /**
     * Définit le listener qui sera notifié lorsque l'utilisateur
     * valide sa solution en appuyant sur le bouton rouge.
     * 
     * @param listener Le Consumer qui recevra le résultat (true = correct, false =
     *                 incorrect)
     */
    public final void setPuzzleListener(final Consumer<Boolean> listener) {
        this.aPuzzleListener = listener;
    }

    /**
     * Charge les ressources graphiques nécessaires (image de fond).
     * Cette méthode est appelée une seule fois lors de l'initialisation.
     */
    private void loadResources() {
        try {
            final URL imageUrl = getClass().getResource("/images/schema.jpeg");
            if (imageUrl != null) {
                this.aBackgroundImage = new ImageIcon(imageUrl).getImage();
            }
        } catch (final Exception e) {
            System.err.println("Image de fond non trouvée : " + e.getMessage());
        }
    }

    /**
     * Charge et redimensionne une icône à partir du système de ressources.
     * 
     * @param image Le nom du fichier image (sans chemin)
     * @return L'ImageIcon redimensionné aux dimensions ICON_WIDTH x ICON_HEIGHT
     */
    private ImageIcon getImageIcon(final String image) {
        final URL imageUrl = getClass().getResource("/images/" + image);
        if (imageUrl == null) {
            return new ImageIcon();
        }

        final Image original = new ImageIcon(imageUrl).getImage();
        final Image scaled = original.getScaledInstance(
                ICON_WIDTH, ICON_HEIGHT, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    /**
     * Initialise et positionne les 8 interrupteurs à bascule.
     * Chaque interrupteur est initialisé avec un état aléatoire ON/OFF.
     */
    private void setupSwitches() {
        final JPanel switchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        switchPanel.setOpaque(false);
        switchPanel.setBorder(BorderFactory.createEmptyBorder(150, 150, 0, 0));

        final ImageIcon onIcon = getImageIcon("on.jpeg");
        final ImageIcon offIcon = getImageIcon("off.jpeg");
        final Random random = new Random(); // Générateur d'aléatoire final

        for (int i = 0; i < this.aSwitches.length; i++) {
            this.aSwitches[i] = new CustomToggleSwitch(onIcon, offIcon);
            this.aSwitches[i].setOn(random.nextBoolean());

            // Recalculer la tension à chaque changement d'état
            this.aSwitches[i].addActionListener(e -> calculateVoltage());

            switchPanel.add(this.aSwitches[i]);
        }
        add(switchPanel, BorderLayout.CENTER);
    }

    /**
     * Configure les listeners d'interaction pour la souris.
     * Gère les clics sur le bouton circulaire et le survol.
     */
    private void setupInteractionListeners() {
        // Listener pour les clics de souris
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(final MouseEvent e) {
                if (isPointInCircle(e.getX(), e.getY()))
                    checkSolution();
            }
        });

        // Listener pour le mouvement de la souris (survol)
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(final MouseEvent e) {
                aCircleHighlighted = isPointInCircle(e.getX(), e.getY());
                setCursor(
                        aCircleHighlighted ? Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
                                : Cursor.getDefaultCursor());
                repaint();
            }
        });
    }

    /**
     * Dessine l'aiguille du voltmètre en fonction de la tension calculée.
     * 
     * @param g2d Le contexte graphique 2D pour le dessin
     */
    private void drawVoltmeterArrow(final Graphics2D g2d) {
        // 1. Définir les coordonnées de référence (celles basées sur votre image
        // originale 900x680)
        final double REF_WIDTH = 900.0;
        final double REF_HEIGHT = 680.0;

        // 2. Calculer les ratios d'échelle actuels
        double scaleX = getWidth() / REF_WIDTH;
        double scaleY = getHeight() / REF_HEIGHT;

        // 3. Appliquer l'échelle aux points du voltmètre
        int curCenterX = (int) (V_CENTER_X * scaleX);
        int curCenterY = (int) (V_CENTER_Y * scaleY);
        int curZeroX = (int) (V_ZERO_X * scaleX);
        int curZeroY = (int) (V_ZERO_Y * scaleY);
        int curMaxX = (int) (V_MAX_X * scaleX);
        int curMaxY = (int) (V_MAX_Y * scaleY);

        // 4. Calcul de l'interpolation pour la tension
        double ratio = Math.min(this.aCurrentVoltage / 10.0, 1.0);
        int targetX = (int) (curZeroX + (curMaxX - curZeroX) * ratio);
        int targetY = (int) (curZeroY + (curMaxY - curZeroY) * ratio);

        // 5. Dessin avec les coordonnées mises à l'échelle
        g2d.setColor(Color.RED);
        g2d.setStroke(new BasicStroke((float) (2.5f * scaleX))); // Même l'épaisseur s'adapte
        g2d.drawLine(curCenterX, curCenterY, targetX, targetY);

        g2d.setColor(Color.DARK_GRAY);
        int dotSize = (int) (8 * scaleX);
        g2d.fillOval(curCenterX - dotSize / 2, curCenterY - dotSize / 2, dotSize, dotSize);
    }

    /**
     * Vérifie si un point donné se trouve à l'intérieur du cercle du bouton rouge.
     * Utilise la formule de distance euclidienne.
     * 
     * @param x Coordonnée X du point
     * @param y Coordonnée Y du point
     * @return true si le point est dans le cercle, false sinon
     */
    private boolean isPointInCircle(final int x, final int y) {
        final double distance = Math.sqrt(
                Math.pow(x - CIRCLE_CENTER_X, 2) +
                        Math.pow(y - CIRCLE_CENTER_Y, 2));
        return distance <= CIRCLE_RADIUS;
    }

    /**
     * Vérifie la solution finale du puzzle.
     * Notifie le aPuzzleListener avec le résultat.
     */
    private void checkSolution() {
        // System.err.println("CurrentVoltage : " + this.aCurrentVoltage);
        // Exemple de condition de victoire basée sur la tension (ex: entre 3.2V et
        // 3.4V)
        boolean isCorrect = (this.aCurrentVoltage >= 3.2 && this.aCurrentVoltage <= 3.4);
        if (this.aPuzzleListener != null) {
            this.aPuzzleListener.accept(isCorrect);
        }
    }

    /**
     * Calcule la tension dynamiquement selon la loi des nœuds (Théorème de
     * Millman).
     */
    private void calculateVoltage() {
        double numerator = 0; // Somme des (V / this.aResistors)
        double denominator = 0; // Somme des (1 / this.aResistors)
        boolean anySwitchOn = false;

        for (int i = 0; i < this.aSwitches.length; i++) {
            if (this.aSwitches[i] != null && this.aSwitches[i].isOn()) {
                numerator += this.aVSources[i] / this.aResistors[i];
                denominator += 1.0 / this.aResistors[i];
                anySwitchOn = true;
            }
        }

        this.aCurrentVoltage = anySwitchOn ? (numerator / denominator) : 0.0;
        repaint();
    }

    /**
     * Méthode de rendu graphique personnalisée.
     * Dessine l'image de fond et éventuellement la surbrillance du cercle.
     * 
     * @param pG Le contexte graphique pour le dessin
     */
    @Override
    protected final void paintComponent(final Graphics pG) {
        super.paintComponent(pG);
        final Graphics2D g2d = (Graphics2D) pG.create();

        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // Dessin de l'image de fond si disponible
        if (this.aBackgroundImage != null) {
            g2d.drawImage(this.aBackgroundImage, 0, 0, getWidth(), getHeight(), this);
        }

        drawVoltmeterArrow(g2d);
        // Option : dessiner une surbrillance pour le cercle
        if (this.aCircleHighlighted) {
            g2d.setColor(new Color(255, 0, 0, 50)); // Rouge semi-transparent
            g2d.fillOval(
                    CIRCLE_CENTER_X - CIRCLE_RADIUS,
                    CIRCLE_CENTER_Y - CIRCLE_RADIUS,
                    CIRCLE_DIAMETER,
                    CIRCLE_DIAMETER);
        }
        g2d.dispose();
    }

}