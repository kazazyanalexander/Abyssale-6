package pkg_ui_components;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import pkg_characters.Character;
import pkg_characters.MovingCharacter;
import pkg_utility.Lang;

/**
 * CharacterCallout - Callout temporaire centré sur le panneau d'image.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class CharacterCallout extends JWindow {

    /** Largeur du callout (en pixels). */
    private static final int CALLOUT_WIDTH = 350;

    /** Hauteur du callout (en pixels). */
    private static final int CALLOUT_HEIGHT = 120;

    /** Durée d'affichage du callout en millisecondes (2 secondes). */
    private static final int DISPLAY_TIME = 2000; // 2 secondes

    /** Niveau d'opacité actuel du callout (0 = transparent, 1 = opaque). */
    private float aOpacity = 1.0f;

    /** Timer gérant l'animation de fondu pour la disparition du callout. */
    private Timer aFadeTimer;

    /** Panneau cible sur lequel le callout est centré. */
    private final TransitionPanel aTargetPanel;

    /**
     * Constructeur du callout centré sur le panneau d'image.
     * 
     * @param pParent      La fenêtre parente
     * @param pTargetPanel Le panneau d'image sur lequel centrer
     * @param pCharacter   Le personnage rencontré
     */
    public CharacterCallout(final Frame pParent, final TransitionPanel pTargetPanel,
            final Character pCharacter) {
        super(pParent);
        this.aTargetPanel = pTargetPanel;

        JPanel vContentPanel = createContentPanel(pCharacter);
        setContentPane(vContentPanel);

        // Positionnement centré sur le panneau d'image
        centerOnTarget();

        // S'assurer que le callout reste au-dessus
        setAlwaysOnTop(true);

        // Timer pour la disparition avec fondu
        this.aFadeTimer = new Timer(50, e -> {
            this.aOpacity -= 0.1f;
            if (this.aOpacity <= 0) {
                this.aOpacity = 0;
                this.aFadeTimer.stop();
                dispose();
            }
            setOpacity(this.aOpacity);
        });

        // Timer pour déclencher le fondu après DISPLAY_TIME
        Timer vDisplayTimer = new Timer(DISPLAY_TIME, e -> {
            this.aFadeTimer.start();
        });
        vDisplayTimer.setRepeats(false);
        vDisplayTimer.start();

        // Animation d'apparition
        setOpacity(0f);
        setVisible(true);

        Timer vAppearTimer = new Timer(20, new ActionListener() {
            private float vOpacity = 0f;

            @Override
            public void actionPerformed(ActionEvent e) {
                vOpacity += 0.1f;
                if (vOpacity >= 1.0f) {
                    vOpacity = 1.0f;
                    ((Timer) e.getSource()).stop();
                }
                setOpacity(vOpacity);
            }
        });
        vAppearTimer.start();
    }

    /**
     * Centre le callout sur le panneau d'image.
     */
    private void centerOnTarget() {
        if (this.aTargetPanel == null || getParent() == null)
            return;

        // Obtenir la position du panneau d'image par rapport à la fenêtre
        Point vPanelLocation = this.aTargetPanel.getLocationOnScreen();
        int vPanelWidth = this.aTargetPanel.getWidth();
        int vPanelHeight = this.aTargetPanel.getHeight();

        // Calculer la position centrée
        int vX = vPanelLocation.x + (vPanelWidth - CALLOUT_WIDTH) / 2;
        int vY = vPanelLocation.y + (vPanelHeight - CALLOUT_HEIGHT) / 2;

        setBounds(vX, vY, CALLOUT_WIDTH, CALLOUT_HEIGHT);
    }

    /**
     * Crée le contenu du callout.
     * 
     * @param pCharacter Le personnage
     * @return Le panneau de contenu
     */
    private JPanel createContentPanel(final Character pCharacter) {
        JPanel vPanel = new JPanel(new BorderLayout(10, 5)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D vG2 = (Graphics2D) g.create();
                vG2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Fond arrondi semi-transparent
                vG2.setColor(new Color(20, 20, 30, 220));
                vG2.fill(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), 25, 25));

                // Bordure colorée selon le type de personnage
                Color vBorderColor;
                if (pCharacter instanceof MovingCharacter) {
                    vBorderColor = new Color(255, 200, 100, 220); // Orange pour les mobiles
                } else {
                    vBorderColor = new Color(100, 200, 255, 220); // Bleu pour les fixes
                }

                vG2.setColor(vBorderColor);
                vG2.setStroke(new BasicStroke(3));
                vG2.draw(new RoundRectangle2D.Double(2, 2, getWidth() - 4, getHeight() - 4, 22, 22));

                vG2.dispose();
            }
        };

        vPanel.setOpaque(false);
        vPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Icône de personnage (plus grande pour le centrage)
        JLabel vIconLabel = new JLabel("👤");
        vIconLabel.setFont(new Font("SansSerif", Font.PLAIN, 48));

        if (pCharacter instanceof MovingCharacter) {
            vIconLabel.setForeground(new Color(255, 200, 100)); // Orange pour les mobiles
        } else {
            vIconLabel.setForeground(new Color(150, 255, 150)); // Vert pour les fixes
        }

        vIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        vPanel.add(vIconLabel, BorderLayout.WEST);

        // Texte
        JPanel vTextPanel = new JPanel(new BorderLayout(5, 5));
        vTextPanel.setOpaque(false);

        JLabel vNameLabel = new JLabel(Lang.localizableString(pCharacter.getName()));
        vNameLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
        vNameLabel.setForeground(new Color(220, 240, 255));
        vNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        vTextPanel.add(vNameLabel, BorderLayout.NORTH);

        String vGreeting = pCharacter.getFullDescription();
        if (vGreeting.isEmpty()) {
            vGreeting = Lang.localizableString("character_silent"); // Message par défaut si aucune description n'est
                                                                    // fournie
        }

        JLabel vMessageLabel = new JLabel("<html><center>" + vGreeting + "</center></html>");
        vMessageLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        vMessageLabel.setForeground(new Color(200, 210, 230));
        vMessageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        vTextPanel.add(vMessageLabel, BorderLayout.CENTER);

        // Indication pour parler
        JLabel vHintLabel = new JLabel(Lang.localizableString("talk_hint"));
        vHintLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        vHintLabel.setForeground(new Color(150, 150, 180));
        vHintLabel.setHorizontalAlignment(SwingConstants.CENTER);
        vTextPanel.add(vHintLabel, BorderLayout.SOUTH);

        vPanel.add(vTextPanel, BorderLayout.CENTER);

        return vPanel;
    }

}