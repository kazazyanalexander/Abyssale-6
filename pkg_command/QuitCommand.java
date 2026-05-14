package pkg_command;

import pkg_core.GameEngine;
import pkg_ui_components.AppButton;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import pkg_characters.Player;
import pkg_utility.Lang;

/**
 * QuitCommand - Commande pour quitter le jeu.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class QuitCommand extends Command {
    /**
     * Constructeur par défaut de la commande QuitCommand.
     * Initialise la commande sans paramètre supplémentaire.
     */
    public QuitCommand() {
        // Constructeur par défaut
    }

    /**
     * Exécute la commande pour quitter le jeu, en affichant une confirmation à
     * l'utilisateur.
     * 
     * @param pPlayer     Le joueur exécutant la commande
     * @param pGameEngine Le moteur du jeu
     * @return true si la commande a été exécutée avec succès, false sinon
     */
    @Override
    public boolean execute(Player pPlayer, GameEngine pGameEngine) {

        // Créer un JDialog personnalisé
        JDialog vDialog = new JDialog(pGameEngine.getGui(), Lang.localizableString("quit_confirm_title"), true);
        vDialog.setLayout(new BorderLayout(10, 10));
        vDialog.getContentPane().setBackground(new Color(40, 40, 50));

        JPanel vPanel = new JPanel(new BorderLayout(15, 15));
        vPanel.setBackground(new Color(40, 40, 50));
        vPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Message
        JLabel vMessageLabel = new JLabel(Lang.localizableString("quit_confirm_message"));
        vMessageLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        vMessageLabel.setForeground(new Color(220, 240, 255));
        vMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        vPanel.add(vMessageLabel, BorderLayout.CENTER);

        // Boutons
        JPanel vButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        vButtonPanel.setBackground(new Color(40, 40, 50));

        AppButton vYesButton = new AppButton("✓ " + Lang.localizableString("yes"), new Color(100, 200, 100));

        AppButton vNoButton = new AppButton("✗ " + Lang.localizableString("no"), new Color(200, 80, 80));

        vYesButton.addActionListener(e -> {
            vDialog.dispose();
            pGameEngine.log(Lang.localizableString("end_game"));
            pGameEngine.shutdown();
            System.exit(0);
        });

        vNoButton.addActionListener(e -> {
            vDialog.dispose();
            pGameEngine.log(Lang.localizableString("quit_cancelled"));
        });

        vButtonPanel.add(vYesButton);
        vButtonPanel.add(vNoButton);
        vPanel.add(vButtonPanel, BorderLayout.SOUTH);

        vDialog.add(vPanel);
        vDialog.pack();
        vDialog.setLocationRelativeTo(pGameEngine.getGui());
        vDialog.setVisible(true);

        return false; // Ne pas terminer immédiatement, on attend la réponse
    }

}