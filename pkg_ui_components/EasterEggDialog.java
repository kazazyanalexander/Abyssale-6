package pkg_ui_components;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.net.URL;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import pkg_items.Torch;
import pkg_utility.Lang;

/**
 * EasterEggDialog - Boîte de dialogue modale affichant une image secrète
 * (easter egg). L'image apparaît avec un effet de fondu et sa visibilité
 * dépend du niveau de batterie de la torche. La batterie se décharge
 * pendant l'affichage.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class EasterEggDialog extends JDialog {

    /** Timer pour l'animation de fondu */
    private Timer aFadeTimer;

    /** Timer pour la décharge de la batterie */
    private Timer aDischargeTimer;

    /** Niveau de transparence actuel (0 = transparent, 1 = opaque) */
    private float aAlpha = 0f;

    /** Panneau personnalisé pour l'effet de fondu */
    private FadePanel aFadePanel;

    /** Niveau de batterie de la torche (0-100) */
    private int aBatteryLevel;

    /** Label affichant le niveau de batterie */
    private JLabel aBatteryLabel;

    /** Barre de progression de la batterie */
    private JProgressBar aBatteryBar;

    /** Référence vers la torche pour mettre à jour son niveau */
    private Torch aTorch;

    /**
     * Constructeur de la boîte de dialogue de l'easter egg.
     * 
     * @param pOwner La fenêtre parente
     * @param pTorch La torche utilisée (pour connaître le niveau de batterie)
     */
    public EasterEggDialog(final Frame pOwner, final Torch pTorch) {
        super(pOwner, Lang.localizableString("easter_egg_title"), true);

        this.aTorch = pTorch;

        // Récupérer le niveau de batterie de la torche
        if (pTorch != null) {
            this.aBatteryLevel = pTorch.getBatteryLife();
        } else {
            this.aBatteryLevel = 100; // Valeur par défaut
        }

        initializeUI();
        pack();
        setLocationRelativeTo(pOwner);
        setResizable(false);

        // Démarrer l'animation de fondu entrant
        startFadeIn();

        // Démarrer la décharge de la batterie
        startBatteryDischarge();
    }

    /**
     * Initialise l'interface utilisateur.
     */
    private void initializeUI() {
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(30, 30, 40));

        // Panneau principal
        JPanel vMainPanel = new JPanel(new BorderLayout(10, 10));
        vMainPanel.setBackground(new Color(30, 30, 40));
        vMainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Panneau d'image avec effet de fondu
        this.aFadePanel = createFadePanel();
        vMainPanel.add(this.aFadePanel, BorderLayout.CENTER);

        // Panneau d'information sur la batterie
        JPanel vInfoPanel = createInfoPanel();
        vMainPanel.add(vInfoPanel, BorderLayout.NORTH);

        // Panneau de boutons
        JPanel vButtonPanel = createButtonPanel();
        vMainPanel.add(vButtonPanel, BorderLayout.SOUTH);

        add(vMainPanel, BorderLayout.CENTER);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }

    /**
     * Crée le panneau d'information sur la batterie.
     * 
     * @return Le panneau d'information
     */
    private JPanel createInfoPanel() {
        JPanel vPanel = new JPanel(new BorderLayout(10, 5));
        vPanel.setBackground(new Color(30, 30, 40));
        vPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Label d'information sur la batterie
        this.aBatteryLabel = new JLabel();
        this.aBatteryLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        this.aBatteryLabel.setHorizontalAlignment(SwingConstants.CENTER);

        // Barre de progression de la batterie
        this.aBatteryBar = new JProgressBar(0, 100);
        this.aBatteryBar.setValue(this.aBatteryLevel);
        this.aBatteryBar.setStringPainted(true);
        this.aBatteryBar.setPreferredSize(new Dimension(200, 20));

        // Couleur de la barre selon le niveau
        updateBatteryDisplay();

        JPanel vBarPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        vBarPanel.setBackground(new Color(30, 30, 40));
        vBarPanel.add(this.aBatteryBar);

        vPanel.add(this.aBatteryLabel, BorderLayout.NORTH);
        vPanel.add(vBarPanel, BorderLayout.CENTER);

        return vPanel;
    }

    /**
     * Met à jour l'affichage de la batterie.
     */
    private void updateBatteryDisplay() {
        this.aBatteryBar.setValue(this.aBatteryLevel);

        // Mettre à jour la couleur de la barre
        if (this.aBatteryLevel >= 70) {
            this.aBatteryBar.setForeground(new Color(100, 255, 100));
            this.aBatteryLabel.setText(String.format(
                    Lang.localizableString("easter_egg_battery_powerful"), this.aBatteryLevel));
            this.aBatteryLabel.setForeground(new Color(100, 255, 100));
        } else if (this.aBatteryLevel >= 40) {
            this.aBatteryBar.setForeground(new Color(255, 255, 100));
            this.aBatteryLabel.setText(String.format(
                    Lang.localizableString("easter_egg_battery_moderate"), this.aBatteryLevel));
            this.aBatteryLabel.setForeground(new Color(255, 255, 100));
        } else if (this.aBatteryLevel >= 15) {
            this.aBatteryBar.setForeground(new Color(255, 150, 100));
            this.aBatteryLabel.setText(String.format(
                    Lang.localizableString("easter_egg_battery_low"), this.aBatteryLevel));
            this.aBatteryLabel.setForeground(new Color(255, 150, 100));
        } else {
            this.aBatteryBar.setForeground(new Color(255, 80, 80));
            this.aBatteryLabel.setText(String.format(
                    Lang.localizableString("easter_egg_battery_critical"), this.aBatteryLevel));
            this.aBatteryLabel.setForeground(new Color(255, 80, 80));
        }

        // Mettre à jour la visibilité de l'image
        if (this.aFadePanel != null) {
            this.aFadePanel.repaint();
        }
    }

    /**
     * Démarre la décharge de la batterie.
     */
    private void startBatteryDischarge() {
        this.aDischargeTimer = new Timer(500, e -> {
            if (this.aBatteryLevel > 0) {
                this.aBatteryLevel -= 2;
                if (this.aBatteryLevel < 0)
                    this.aBatteryLevel = 0;

                // Mettre à jour la torche
                if (this.aTorch != null) {
                    this.aTorch.setBatteryLife(this.aBatteryLevel);
                }

                updateBatteryDisplay();

                // Si la batterie est à 0, fermer le dialogue
                if (this.aBatteryLevel <= 0) {
                    this.aDischargeTimer.stop();
                    JOptionPane.showMessageDialog(EasterEggDialog.this,
                            Lang.localizableString("easter_egg_torch_depleted"),
                            Lang.localizableString("easter_egg_torch_depleted_title"),
                            JOptionPane.WARNING_MESSAGE);
                    startFadeOut();
                }
            }
        });
        this.aDischargeTimer.start();
    }

    /**
     * Crée le panneau personnalisé avec effet de fondu.
     * 
     * @return Le panneau avec effet de fondu
     */
    private FadePanel createFadePanel() {
        FadePanel vPanel = new FadePanel();
        vPanel.setBackground(new Color(20, 20, 30));
        vPanel.setBorder(BorderFactory.createLineBorder(new Color(100, 150, 200), 2));
        vPanel.setPreferredSize(new Dimension(550, 450));
        return vPanel;
    }

    /**
     * Démarre l'animation de fondu entrant.
     */
    private void startFadeIn() {
        this.aFadeTimer = new Timer(30, e -> {
            this.aAlpha += 0.05f;
            if (this.aAlpha >= 1.0f) {
                this.aAlpha = 1.0f;
                this.aFadeTimer.stop();
            }
            this.aFadePanel.setAlpha(this.aAlpha);
            repaint();
        });
        this.aFadeTimer.start();
    }

    /**
     * Démarre l'animation de fondu sortant avant la fermeture.
     */
    private void startFadeOut() {
        if (this.aDischargeTimer != null && this.aDischargeTimer.isRunning()) {
            this.aDischargeTimer.stop();
        }

        if (this.aFadeTimer != null && this.aFadeTimer.isRunning()) {
            this.aFadeTimer.stop();
        }

        this.aFadeTimer = new Timer(30, e -> {
            this.aAlpha -= 0.05f;
            if (this.aAlpha <= 0f) {
                this.aAlpha = 0f;
                this.aFadeTimer.stop();
                this.dispose();
            }
            this.aFadePanel.setAlpha(this.aAlpha);
            this.aFadePanel.repaint();
        });
        this.aFadeTimer.start();
    }

    /**
     * Crée le panneau des boutons.
     * 
     * @return Le panneau des boutons
     */
    private JPanel createButtonPanel() {
        JPanel vPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        vPanel.setBackground(new Color(30, 30, 40));

        // Bouton Fermer
        AppButton vCloseButton = new AppButton(Lang.localizableString("close"));
        vCloseButton.addActionListener(e -> startFadeOut());
        vCloseButton.setToolTipText(Lang.localizableString("close_tooltip"));

        vPanel.add(vCloseButton);

        return vPanel;
    }

    /**
     * Panneau personnalisé pour l'affichage de l'image avec effet de fondu.
     */
    private class FadePanel extends JPanel {
        /** Image redimensionnée pour s'adapter au panneau */
        private Image aScaledImage;
        /** Niveau de transparence pour l'effet de fondu (0-1) */
        private float aPanelAlpha = 0f;

        /**
         * Constructeur du panneau avec effet de fondu.
         */
        public FadePanel() {
            this.loadAndScaleImage();
            setOpaque(false);
        }

        /**
         * Charge et redimensionne l'image.
         */
        private void loadAndScaleImage() {
            URL vImageURL = getClass().getResource("/images/millman.jpg");
            if (vImageURL != null) {
                this.aScaledImage = new ImageIcon(vImageURL).getImage();
            } else {
                System.err.println(Lang.localizableString("error_image_not_found") + " : millman.jpg");
            }
        }

        /**
         * Définit le niveau de transparence pour l'effet de fondu.
         * 
         * @param pAlpha Le niveau de transparence (0-1)
         */
        public void setAlpha(float pAlpha) {
            this.aPanelAlpha = pAlpha;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D vG2d = (Graphics2D) g.create();
            vG2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            vG2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            // Appliquer l'effet de transparence
            vG2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, aPanelAlpha));

            // Calculer la visibilité en fonction du niveau de batterie
            float vBatteryVisibility = Math.max(0.01f, Math.min(1.0f, aBatteryLevel / 100.0f));

            // Appliquer l'effet de batterie (réduction supplémentaire de la visibilité)
            float vTotalAlpha = aPanelAlpha * vBatteryVisibility;
            vG2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, vTotalAlpha));

            // Dessiner l'image
            if (this.aScaledImage != null) {
                int vImageWidth = this.aScaledImage.getWidth(null);
                int vImageHeight = this.aScaledImage.getHeight(null);
                int vX = (getWidth() - vImageWidth) / 2;
                int vY = (getHeight() - vImageHeight) / 2;
                vG2d.drawImage(this.aScaledImage, vX, vY, this);
            } else {
                // Message d'erreur si l'image n'est pas chargée
                vG2d.setColor(new Color(255, 100, 100));
                vG2d.setFont(new Font("Monospaced", Font.PLAIN, 14));
                String vErrorMsg = Lang.localizableString("error_image_not_found") + " : millman.jpg";
                int vX = (getWidth() - vG2d.getFontMetrics().stringWidth(vErrorMsg)) / 2;
                int vY = getHeight() / 2;
                vG2d.drawString(vErrorMsg, vX, vY);
            }

            vG2d.dispose();
        }
    }

    @Override
    public void dispose() {
        if (this.aDischargeTimer != null && this.aDischargeTimer.isRunning()) {
            this.aDischargeTimer.stop();
        }
        if (this.aFadeTimer != null && this.aFadeTimer.isRunning()) {
            this.aFadeTimer.stop();
        }
        super.dispose();
    }
}