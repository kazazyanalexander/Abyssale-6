import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.URL;
import java.awt.image.*;

/**
 * Cette classe implémente une interface utilisateur graphique simple avec une zone de saisie de texte,
 * une zone de sortie de texte et une image optionnelle.
 * @author Michael Kolling
 * @version 1.0 (Jan 2003)
 * @modified by Alexander KAZAZYAN
 */
public class UserInterface implements ActionListener
{
    private GameEngine engine;
    private JFrame myFrame;
    private JTextField entryField;
    private JTextArea log;
    private JLabel image;
    private MusicPlayer music;

    /**
     * Construit une UserInterface. Un moteur de jeu (un objet traitant et exécutant les commandes du jeu) est
     * nécessaire en paramètre.
     * @param gameEngine L'objet GameEngine implémentant la logique du jeu.
     */
    public UserInterface(final GameEngine gameEngine)
    {
        engine = gameEngine;
        createGUI();
    }

    /**
     * Affiche du texte dans la zone de texte.
     */
    public void print(final String text)
    {
        log.append(text);
        log.setCaretPosition(log.getDocument().getLength());
    }

    /**
     * Affiche du texte dans la zone de texte, suivi d'un saut de ligne.
     */
    public void println(final String text)
    {
        log.append(text + "\n");
        log.setCaretPosition(log.getDocument().getLength());
    }

    /**
     * Affiche un fichier image dans l'interface.
     */
    public void showImage(final String imageName)
    {
        URL imageURL = this.getClass().getClassLoader().getResource("images/" + imageName);
        if(imageURL == null)
            System.out.println("image non trouvée");
        else {
            ImageIcon icon = new ImageIcon(imageURL);
            image.setIcon(icon);
            myFrame.pack();
        }
    }

    /**
     * Active ou désactive la saisie dans le champ de saisie.
     */
    public void enable(final boolean on)
    {
        entryField.setEditable(on);
        if(!on)
            entryField.getCaret().setBlinkRate(0);
    }

    /**
     * Configure l'interface utilisateur graphique.
     */
    private void createGUI()
    {
        myFrame = new JFrame("Abyssale-6");
        entryField = new JTextField(34);

        log = new JTextArea();
        log.setEditable(false);
        JScrollPane listScroller = new JScrollPane(log);
        listScroller.setPreferredSize(new Dimension(200, 200));
        listScroller.setMinimumSize(new Dimension(100,100));

        JPanel panel = new JPanel();
        image = new JLabel();

        panel.setLayout(new BorderLayout());
        panel.add(image, BorderLayout.NORTH);
        panel.add(listScroller, BorderLayout.CENTER);
        panel.add(entryField, BorderLayout.SOUTH);

        myFrame.getContentPane().add(panel, BorderLayout.CENTER);

        // ajoute des écouteurs d'événements à certains composants
        myFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    System.exit(0);
                }
            });

        entryField.addActionListener(this);

        myFrame.pack();
        myFrame.setVisible(true);
        entryField.requestFocus();

        this.music = new MusicPlayer();
        this.music.playBackgroundMusic("audio/theme.wav");
    }

    public void doorSound(){
        this.music.playSFX("audio/door.wav");
    }

    /**
     * Interface ActionListener pour le champ de texte de saisie.
     */
    public void actionPerformed(final ActionEvent e)
    {
        // pas besoin de vérifier le type d'action pour le moment.
        // il n'y a qu'une seule action possible : la saisie de texte
        processCommand();
    }

    /**
     * Une commande a été entrée. Lit la commande et fait ce qui est
     * nécessaire pour la traiter.
     */
    private void processCommand()
    {
        boolean finished = false;
        String input = entryField.getText();
        entryField.setText("");

        engine.interpretCommand(input);
    }
}
