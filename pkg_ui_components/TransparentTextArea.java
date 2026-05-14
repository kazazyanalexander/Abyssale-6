package pkg_ui_components;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import pkg_utility.MusicPlayer;

/**
 * Zone de texte transparente avec effet machine à écrire pour l'affichage de la
 * narration du jeu.
 * 
 * Ce composant se superpose aux arrière-plans du jeu tout en maintenant la
 * lisibilité
 * avec un overlay sombre semi-transparent et du texte vert de style terminal.
 * 
 * Caractéristiques :
 * - Effet d'animation machine à écrire pour une narration immersive
 * - Arrière-plan semi-transparent pour la lisibilité du texte sur les images
 * - Police monospace verte de style terminal
 * - Défilement automatique à l'apparition du texte
 * - Effets sonores optionnels pour la frappe
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public final class TransparentTextArea extends JScrollPane {
    /** Zone de texte principale pour l'affichage (final) */
    private final JTextArea aTextArea;

    /** Timer pour l'effet machine à écrire (final) */
    private final Timer aTimer;

    /** Texte complet à afficher (final après initialisation) */
    private String aFullText;

    /** Position du caractère courant dans l'animation */
    private int aCurrentChar = 0;

    /** Lecteur de musique pour les effets sonores (final) */
    private final MusicPlayer aMusic;

    /** Constante pour l'opacité de l'arrière-plan finale */
    private static final float BACKGROUND_OPACITY_FINAL = 0.8f;

    /** Constante pour la couleur d'arrière-plan finale */
    private static final Color BACKGROUND_COLOR_FINAL = new Color(0, 0, 0, 0);

    /** Constante pour la couleur du curseur finale */
    private static final Color CARET_COLOR_FINAL = Color.GREEN;

    /** Constante pour la couleur du texte finale */
    private static final Color TEXT_COLOR_FINAL = Color.GREEN;

    /** Constante pour la police de texte finale */
    private static final Font TEXT_FONT_FINAL = new Font("Monospaced", Font.BOLD, 16);

    /** Constante pour la bordure intérieure finale */
    private static final EmptyBorder TEXT_BORDER_FINAL = new EmptyBorder(20, 20, 20, 20);

    /** Constante pour le délai du timer final (en ms) */
    private static final int TIMER_DELAY_FINAL = 50;

    /** Constante pour le fichier son de la machine à écrire finale */
    private static final String TYPEWRITER_SOUND_FINAL = "typewriter.wav";

    /**
     * Constructeur créant une zone de texte transparente avec effet machine à
     * écrire.
     * Configuration finale de tous les composants.
     */
    public TransparentTextArea() {
        // Initialisation du lecteur de musique (version finale)
        this.aMusic = new MusicPlayer();

        // Création de la zone de texte personnalisée
        this.aTextArea = new JTextArea() {
            @Override
            protected final void paintComponent(final Graphics g) {
                final Graphics2D g2d = (Graphics2D) g;

                // Application de la transparence (valeur finale)
                g2d.setComposite(AlphaComposite.getInstance(
                        AlphaComposite.SRC_OVER,
                        BACKGROUND_OPACITY_FINAL));

                // Arrière-plan subtil pour rendre le texte lisible sur les images
                g2d.setColor(BACKGROUND_COLOR_FINAL);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                super.paintComponent(g);
            }
        };

        // Configuration de la zone de texte (valeurs finales)
        this.configureTextArea();

        // Configuration du ScrollPane transparent
        this.configureScrollPane();

        // Configuration du timer pour l'effet machine à écrire (version finale)
        this.aTimer = createTypewriterTimer();
    }

    /**
     * Configure les propriétés de la zone de texte.
     * Méthode finale - configuration fixe de la zone de texte.
     */
    private final void configureTextArea() {
        // Désactivation de la sélection et de l'édition
        this.aTextArea.setEditable(false);
        this.aTextArea.setHighlighter(null); // Supprime la possibilité de sélectionner le texte
        this.aTextArea.setFocusable(false); // Empêche l'apparition du curseur

        // Configuration de l'apparence
        this.aTextArea.setCaretColor(CARET_COLOR_FINAL);
        this.aTextArea.setLineWrap(true);
        this.aTextArea.setOpaque(false);
        this.aTextArea.setWrapStyleWord(true);
        this.aTextArea.setForeground(TEXT_COLOR_FINAL);
        this.aTextArea.setFont(TEXT_FONT_FINAL);
        this.aTextArea.setBorder(TEXT_BORDER_FINAL);
    }

    /**
     * Configure le ScrollPane pour qu'il soit transparent.
     * Méthode finale - configuration fixe du ScrollPane.
     */
    private final void configureScrollPane() {
        // Utilisation de setViewportView au lieu de add() pour la compatibilité
        setViewportView(this.aTextArea);
        setOpaque(false);
        getViewport().setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder());
    }

    /**
     * Crée et configure le timer pour l'effet machine à écrire.
     * 
     * @return Le timer configuré (final)
     */
    private final Timer createTypewriterTimer() {
        return new Timer(TIMER_DELAY_FINAL, e -> {
            if (this.aCurrentChar < this.aFullText.length()) {
                final char c = this.aFullText.charAt(this.aCurrentChar);
                this.aTextArea.append(String.valueOf(c));
                this.aCurrentChar++;

                // Défilement automatique vers le bas
                this.aTextArea.setCaretPosition(this.aTextArea.getDocument().getLength());
            } else {
                this.aTimer.stop();
                this.aMusic.stopMusic();
            }
        });
    }

    /**
     * Affiche le texte avec l'effet d'animation machine à écrire.
     * 
     * @param text Le texte à afficher (final)
     */
    public final void setText(final String text) {
        // Réinitialisation de l'animation
        this.aFullText = text;
        this.aTextArea.setText("");
        this.aCurrentChar = 0;

        // Démarrage de l'animation et du son (version finale)
        this.aTimer.start();
        this.aMusic.stopMusic();
        this.aMusic.playBackgroundMusic(TYPEWRITER_SOUND_FINAL);
    }

    /**
     * Arrête la musique et les effets sonores.
     * Méthode finale - comportement d'arrêt fixe.
     */
    public final void stopMusic() {
        this.aMusic.stopMusic();
    }

    /**
     * Efface le texte affiché.
     * Méthode finale - comportement d'effacement fixe.
     */
    public final void clearText() {
        this.aTextArea.setText("");
        this.aFullText = "";
        this.aCurrentChar = 0;
        this.aTimer.stop();
        this.aMusic.stopMusic();
    }

}