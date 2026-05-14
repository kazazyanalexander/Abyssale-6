import pkg_ui_components.IntroductionPage;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import pkg_core.GameEngine;
import pkg_utility.Lang;

/**
 * Game - Point d'entrée principal du jeu "Station Abyssale-6".
 * Affiche d'abord l'écran d'introduction, puis lance le jeu.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class Game {
    /** Référence au moteur de jeu. */
    private GameEngine aEngine;
    /** Fenêtre d'introduction. */
    private JFrame aIntroFrame;
    /** Mode test headless. */
    private boolean aIsHeadlessTest = false;
    /** Script de test en mode headless. */
    private String aTestScriptPath = null;
    /** Mode skip intro. */
    private boolean aSkipIntro = false;

    /**
     * Constructeur par défaut de la classe Game.
     * Lance automatiquement le jeu avec l'écran d'introduction.
     */
    public Game() {
        this(false, null, false);
    }

    /**
     * Constructeur avec options de configuration.
     * Permet de configurer le mode de lancement du jeu.
     * 
     * @param pSkipIntro  Si true, saute l'écran d'introduction
     * @param pTestScript Chemin du script de test (null pour mode normal)
     * @param pIsHeadless Si true, active le mode headless pour les tests
     */
    private Game(boolean pSkipIntro, String pTestScript, boolean pIsHeadless) {
        this.aSkipIntro = pSkipIntro;
        this.aTestScriptPath = pTestScript;
        this.aIsHeadlessTest = pIsHeadless;

        // Configuration pour un look plus moderne
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Lancement du jeu
        this.start();
    }

    /**
     * Démarre le jeu avec la configuration actuelle.
     */
    private void start() {
        // Vérifier le mode test headless
        if (this.aIsHeadlessTest && this.aTestScriptPath != null) {
            boolean vSuccess = runHeadlessTest(this.aTestScriptPath);
            System.exit(vSuccess ? 0 : 1);
            return;
        }

        // Lancement normal de l'interface graphique
        SwingUtilities.invokeLater(() -> {
            if (this.aSkipIntro) {
                this.startGame();
            } else {
                this.showIntroduction();
            }
        });
    }

    /**
     * Méthode principale pour la compatibilité avec le lancement en ligne de
     * commande.
     * 
     * @param args Arguments de la ligne de commande:
     *             --test fichier : Lance un test headless
     *             --skip-intro : Saute l'écran d'introduction
     */
    public static void main(String[] args) {
        boolean vSkipIntro = false;
        String vTestScript = null;
        boolean vIsHeadless = false;

        // Analyser les arguments
        for (int i = 0; i < args.length; i++) {
            if ("--test".equals(args[i]) && i + 1 < args.length) {
                vTestScript = args[i + 1];
                vIsHeadless = true;
                i++;
            } else if ("--skip-intro".equals(args[i])) {
                vSkipIntro = true;
            }
        }

        // Créer et lancer le jeu via le constructeur
        new Game(vSkipIntro, vTestScript, vIsHeadless);
    }

    /**
     * Exécute un test en mode headless à partir d'un script de test.
     * Ce mode permet de tester la logique du jeu sans dépendre de l'interface
     * graphique.
     * 
     * @param pScriptPath Chemin vers le fichier de test
     * @return true si le test réussit, false sinon
     */
    private static boolean runHeadlessTest(String pScriptPath) {
        // Activer le mode headless pour éviter toute dépendance graphique
        System.setProperty("java.awt.headless", "true");

        // Créer le moteur de jeu et les objets nécessaires sans interface graphique
        GameEngine vEngine = new GameEngine(true); // Utiliser le constructeur existant pour le chargement

        // Pour l'instant, nous supposons que runTestFile affiche la sortie sur la
        // console et retourne true en cas de succès
        return vEngine.runTestFile(pScriptPath);
    }

    /**
     * Affiche l'écran d'introduction.
     */
    private void showIntroduction() {
        this.aIntroFrame = new JFrame(Lang.localizableString("intro_title"));
        this.aIntroFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.aIntroFrame.setSize(900, 680);
        this.aIntroFrame.setLocationRelativeTo(null);
        this.aIntroFrame.setResizable(false);

        IntroductionPage vIntroPage = new IntroductionPage();
        vIntroPage.setStartGameCallback(() -> startGame());

        this.aIntroFrame.add(vIntroPage);
        this.aIntroFrame.setVisible(true);
    }

    /**
     * Lance le jeu principal après l'introduction.
     */
    private void startGame() {
        // Fermer la fenêtre d'introduction
        if (this.aIntroFrame != null) {
            this.aIntroFrame.dispose();
            this.aIntroFrame = null;
        }

        // Créer et lancer le moteur de jeu
        this.aEngine = new GameEngine();
    }
}