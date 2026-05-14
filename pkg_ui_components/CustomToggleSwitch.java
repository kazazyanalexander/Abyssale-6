package pkg_ui_components;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * Composant réutilisable d'interrupteur à bascule qui alterne entre deux
 * icônes.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public final class CustomToggleSwitch extends JPanel {
    /** État actuel de l'interrupteur (true = ON, false = OFF) */
    private boolean aIsOn = false;

    /** Icône pour l'état ON (activé) */
    private final ImageIcon aOnIcon;

    /** Icône pour l'état OFF (désactivé) */
    private final ImageIcon aOffIcon;

    /** Label pour afficher l'icône actuelle */
    private final JLabel aIconLabel;

    /**
     * Constructeur de l'interrupteur personnalisé.
     * 
     * @param pOnIcon  Icône à afficher lorsque l'interrupteur est ON (final)
     * @param pOffIcon Icône à afficher lorsque l'interrupteur est OFF (final)
     */
    public CustomToggleSwitch(final ImageIcon pOnIcon, final ImageIcon pOffIcon) {
        // Initialisation des icônes
        this.aOnIcon = pOnIcon;
        this.aOffIcon = pOffIcon;

        // Configuration des propriétés du panneau
        setLayout(new BorderLayout());
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setOpaque(false); // Permet d'afficher l'arrière-plan du conteneur parent

        // Initialisation avec l'état OFF
        this.aIconLabel = new JLabel(pOffIcon);
        this.aIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(this.aIconLabel, BorderLayout.CENTER);

        // Basculer l'état au clic
        addMouseListener(new MouseAdapter() {
            @Override
            public final void mouseClicked(final MouseEvent e) {
                toggle();
            }
        });
    }

    /**
     * Bascule l'état de l'interrupteur (ON ↔ OFF).
     * Méthode finale - comportement fixe.
     */
    public final void toggle() {
        this.setOn(!this.aIsOn);
        fireActionEvent();
    }

    /**
     * Retourne l'état actuel de l'interrupteur.
     * 
     * @return true si l'interrupteur est ON, false si OFF (final)
     */
    public final boolean isOn() {
        return this.aIsOn;
    }

    /**
     * Définit manuellement l'état de l'interrupteur.
     * 
     * @param pState true pour ON, false pour OFF (final)
     */
    public final void setOn(final boolean pState) {
        this.aIsOn = pState;
        this.aIconLabel.setIcon(this.aIsOn ? this.aOnIcon : this.aOffIcon);
        repaint(); // Force le redessin du composant
    }

    /**
     * Permet aux classes externes d'écouter les changements d'état.
     * 
     * @param pListener L'écouteur à ajouter (final)
     */
    public final void addActionListener(final ActionListener pListener) {
        this.listenerList.add(ActionListener.class, pListener);
    }

    /**
     * Déclenche un événement pour tous les écouteurs enregistrés.
     * Méthode finale - comportement de notification fixe.
     */
    private final void fireActionEvent() {
        final ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                this.aIsOn ? "ON" : "OFF");

        // Notifier tous les écouteurs
        for (final ActionListener vListeneristener : this.listenerList.getListeners(ActionListener.class)) {
            vListeneristener.actionPerformed(event);
        }
    }
}