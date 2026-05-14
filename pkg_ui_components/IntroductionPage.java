package pkg_ui_components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import pkg_utility.Lang;

/**
 * Introduction - Écran d'introduction avec animation de défilement Star Wars
 * style.
 * Cette classe présente la narration d'introduction du jeu avec un effet de
 * défilement et permet de passer rapidement avec n'importe quelle touche ou
 * clic.
 *
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public final class IntroductionPage extends JPanel {

    /** Callback appelé au démarrage du jeu */
    private Runnable aStartGameCallback;

    /** Timer gérant les animations d'introduction */
    private Timer aAnimationTimer;

    /** Label pour le titre de l'introduction */
    private JLabel aTitleLabel;

    /** Zone de texte pour le crawl */
    private TransparentTextArea aCrawlTextArea;

    /** LayeredPane pour superposer les éléments */
    private JLayeredPane aLayeredPane;

    /** Bouton de démarrage */
    private AppButton aStartButton;

    /** Bouton de sortie */
    private AppButton aQuitButton;

    /** Reference to the background panel */
    private JPanel aBackgroundPanel;

    /** Opacité du titre */
    private float aTitleOpacity = 0f;

    /** Nombre de frames passées */
    private int aFrameCount = 0;

    /** Nombre total de frames. 3 secondes à 60 FPS */
    private static final int TOTAL_FRAMES = 180;

    /** Chemin de l'image de fond */
    private static final String BACKGROUND_PATH = "/images/background.gif";

    /**
     * Constructeur de l'écran d'introduction.
     */
    public IntroductionPage() {
        this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(900, 680));
        this.initializeUI();
    }

    /**
     * Définit le callback à appeler lorsque le jeu démarre.
     * 
     * @param pStartGameCallback Le callback de démarrage
     */
    public final void setStartGameCallback(final Runnable pStartGameCallback) {
        this.aStartGameCallback = pStartGameCallback;
    }

    /**
     * Initialise l'interface utilisateur.
     */
    private void initializeUI() {
        // Utiliser un JLayeredPane pour superposer les éléments
        this.aLayeredPane = new JLayeredPane();
        this.aLayeredPane.setPreferredSize(new Dimension(900, 680));

        // Zone de texte pour le crawl
        this.aCrawlTextArea = new TransparentTextArea();
        this.aCrawlTextArea.setBounds(100, 0, 700, 580);
        this.aLayeredPane.add(this.aCrawlTextArea, JLayeredPane.PALETTE_LAYER);

        // Panneau de contrôle en bas
        final JPanel vControlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        vControlPanel.setBounds(0, 600, 900, 80);
        vControlPanel.setOpaque(false); // Transparent pour voir l'arrière-plan

        // Language ComboBox
        vControlPanel.add(this.createLangCombo());

        // Start Game Button
        this.aStartButton = new AppButton(Lang.localizableString("start_game"));
        this.aStartButton.setToolTipText(Lang.localizableString("start_game"));
        this.aStartButton.addActionListener(pE -> this.skipIntroduction());
        vControlPanel.add(this.aStartButton);

        // Quit Button
        this.aQuitButton = new AppButton(Lang.localizableString("quit_game"));
        this.aQuitButton.setToolTipText(Lang.localizableString("quit_game"));
        this.aQuitButton.addActionListener(pE -> System.exit(0));
        vControlPanel.add(this.aQuitButton);

        this.aLayeredPane.add(vControlPanel, JLayeredPane.PALETTE_LAYER);

        this.add(this.aLayeredPane, BorderLayout.CENTER);

        // Configuration de l'arrière-plan
        this.setupBackground();

        // Création des éléments d'animation
        this.createAnimationLayer();

        // Démarrer les animations
        this.startAnimations();
    }

    /**
     * Configure l'image de fond de l'introduction.
     */
    private void setupBackground() {
        this.aBackgroundPanel = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(final Graphics pG) {
                super.paintComponent(pG);
                final URL vBgUrl = this.getClass().getResource(BACKGROUND_PATH);
                if (vBgUrl != null) {
                    final ImageIcon vImageIcon = new ImageIcon(vBgUrl);
                    pG.drawImage(vImageIcon.getImage(), 0, 0, this.getWidth(), this.getHeight(), this);
                } else {
                    // Couleur de secours si l'image est manquante
                    pG.setColor(new Color(0, 5, 26));
                    pG.fillRect(0, 0, this.getWidth(), this.getHeight());
                    System.err.println("⚠ Image de fond non trouvée: " + BACKGROUND_PATH);
                }
            }
        };

        this.aBackgroundPanel.setOpaque(true);
        this.aBackgroundPanel.setBounds(0, 0, 900, 680);
        this.aLayeredPane.add(this.aBackgroundPanel, JLayeredPane.DEFAULT_LAYER);
    }

    /**
     * Crée la couche d'animation contenant le titre.
     */
    private void createAnimationLayer() {
        this.aTitleLabel = new JLabel("ABYSSAL-6");
        this.aTitleLabel.setFont(new Font("Impact", Font.BOLD, 100));
        this.aTitleLabel.setForeground(new Color(255, 232, 31)); // Jaune Star Wars
        this.aTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        this.aTitleLabel.setBounds(0, 150, 900, 150);
        this.aTitleLabel.setVisible(false);
        this.aLayeredPane.add(this.aTitleLabel, JLayeredPane.PALETTE_LAYER);
    }

    /**
     * Crée la combo box de sélection de langue.
     * 
     * @return La combo box configurée
     */
    private JComboBox<String> createLangCombo() {
        final String[] vLanguages = { "EN", "FR", "DE", "CH" };
        final JComboBox<String> vLangCombo = new JComboBox<>(vLanguages);

        // Détermination de l'index initial
        int vIndex = 0;
        final String vCurrentLang = Lang.getInstance().getLanguage();

        switch (vCurrentLang) {
            case "en":
                vIndex = 0;
                break;
            case "fr":
                vIndex = 1;
                break;
            case "de":
                vIndex = 2;
                break;
            case "zh":
                vIndex = 3;
                break;
            default:
                vIndex = 0;
                break;
        }
        vLangCombo.setSelectedIndex(vIndex);

        // Gestionnaire d'événements pour le changement de langue
        vLangCombo.addActionListener(pE -> {
            final int vSelectedIndex = vLangCombo.getSelectedIndex();

            switch (vSelectedIndex) {
                case 0:
                    Lang.getInstance().setLanguage("en", "EN");
                    break;
                case 1:
                    Lang.getInstance().setLanguage("fr", "FR");
                    break;
                case 2:
                    Lang.getInstance().setLanguage("de", "DE");
                    break;
                case 3:
                    Lang.getInstance().setLanguage("zh", "CH");
                    break;
                default:
                    Lang.getInstance().setLanguage("en", "EN");
                    break;
            }

            this.updateAllButtonTexts();
        });

        return vLangCombo;
    }

    /**
     * Met à jour les textes des boutons selon la langue sélectionnée.
     */
    private void updateAllButtonTexts() {
        if (this.aStartButton != null) {
            this.aStartButton.setText(Lang.localizableString("start_game"));
            this.aStartButton.setToolTipText(Lang.localizableString("start_game"));
        }

        if (this.aQuitButton != null) {
            this.aQuitButton.setText(Lang.localizableString("quit_game"));
            this.aQuitButton.setToolTipText(Lang.localizableString("quit_game"));
        }

        if (this.aCrawlTextArea != null) {
            this.aCrawlTextArea.clearText();
            this.showText();
        }
    }

    /**
     * Affiche le texte d'introduction.
     */
    private void showText() {
        final String vIntroText = Lang.localizableString("introduction");
        this.aCrawlTextArea.setText(vIntroText);
    }

    /**
     * Démarre les animations.
     */
    public void startAnimations() {
        this.aFrameCount = 0;
        this.aCrawlTextArea.setText("");
        this.aTitleLabel.setVisible(true);

        this.aAnimationTimer = new Timer(16, pE -> {
            if (this.aFrameCount < TOTAL_FRAMES) {
                final float vProgress = (float) this.aFrameCount / TOTAL_FRAMES;
                this.aTitleOpacity = 1f - vProgress;
                this.applyTitleTransformations();
                this.aFrameCount++;
            } else {
                this.aAnimationTimer.stop();
                this.showText();
            }
        });

        // Démarrer les animations avec un délai
        final Timer vStartDelay = new Timer(1000, pE -> {
            ((Timer) pE.getSource()).stop();
            this.aAnimationTimer.start();
        });
        vStartDelay.setRepeats(false);
        vStartDelay.start();
    }

    /**
     * Applique les transformations au titre (opacité).
     */
    private void applyTitleTransformations() {
        final Color vOriginalColor = new Color(255, 232, 31);
        final Color vTransparentColor = new Color(
                vOriginalColor.getRed(),
                vOriginalColor.getGreen(),
                vOriginalColor.getBlue(),
                (int) (255 * this.aTitleOpacity));
        this.aTitleLabel.setForeground(vTransparentColor);
        this.repaint();
    }

    /**
     * Passe l'introduction et déclenche le callback de démarrage.
     */
    private void skipIntroduction() {
        if (this.aCrawlTextArea != null) {
            this.aCrawlTextArea.stopMusic();
        }

        if (this.aAnimationTimer != null) {
            this.aAnimationTimer.stop();
        }

        if (this.aStartGameCallback != null) {
            this.aStartGameCallback.run();
        }
    }
}