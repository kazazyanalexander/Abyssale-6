package pkg_ui_components;

import pkg_core.GameEngine;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import pkg_utility.Lang;

/**
 * ReactorPuzzleDialog - Boîte de dialogue modale pour le puzzle du réacteur.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class ReactorPuzzleDialog extends JDialog {

    /** Moteur de jeu principal */
    private final GameEngine aEngine;

    /** Page du puzzle */
    private final PuzzlePage aPuzzlePage;

    /**
     * Constructeur de la boîte de dialogue du puzzle.
     * 
     * @param pOwner  La fenêtre parente
     * @param pEngine Le moteur de jeu
     */
    public ReactorPuzzleDialog(final Frame pOwner, final GameEngine pEngine) {
        super(pOwner, Lang.localizableString("puzzle_title"), true);
        this.aEngine = pEngine;

        // Créer la page de puzzle
        this.aPuzzlePage = new PuzzlePage();

        this.initializeUI();
        pack();
        setLocationRelativeTo(pOwner);
        setResizable(false);
    }

    /**
     * Initialise l'interface utilisateur.
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(30, 30, 40));
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        // Panneau principal
        JPanel vMainPanel = new JPanel(new BorderLayout(10, 10));
        vMainPanel.setBackground(new Color(30, 30, 40));
        vMainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Configurer le listener pour le résultat du puzzle
        this.aPuzzlePage.setPuzzleListener(result -> {
            if (result) {
                // Puzzle résolu avec succès
                dispose();
                this.showVictoryOptions();
            } else {
                // Mauvaise configuration → Game Over
                dispose();
                this.showGameOverOptions();
            }
        });

        vMainPanel.add(this.aPuzzlePage, BorderLayout.CENTER);

        // Panneau des boutons
        JPanel vButtonPanel = createButtonPanel();
        vMainPanel.add(vButtonPanel, BorderLayout.SOUTH);

        add(vMainPanel, BorderLayout.CENTER);

        // Empêcher la fermeture avec la croix
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                showExitConfirmation();
            }
        });
    }

    /**
     * Crée le panneau des boutons.
     * 
     * @return Le panneau des boutons
     */
    private JPanel createButtonPanel() {
        JPanel vPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        vPanel.setBackground(new Color(30, 30, 40));

        // Bouton Annuler
        AppButton vCancelButton = new AppButton(Lang.localizableString("cancel"));
        vCancelButton.addActionListener(e -> showExitConfirmation());

        vPanel.add(vCancelButton);

        return vPanel;
    }

    /**
     * Affiche la confirmation de sortie.
     */
    private void showExitConfirmation() {
        int vConfirm = JOptionPane.showConfirmDialog(
                this,
                Lang.localizableString("puzzle_confirm_exit"),
                Lang.localizableString("confirm"),
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (vConfirm == JOptionPane.YES_OPTION) {
            dispose();
            this.showGameOverOptions();
        }
    }

    /**
     * Affiche les options après une victoire.
     */
    private void showVictoryOptions() {
        this.aEngine.handleVictory();
    }

    /**
     * Affiche les options après un game over.
     */
    private void showGameOverOptions() {
        // D'abord afficher le message de game over
        this.aEngine.handleGameOver();
    }

}