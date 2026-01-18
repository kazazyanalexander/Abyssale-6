
/** 
 * Cette classe est la classe principale de l'application "Station Abyssale-6".
 * "Station Abyssale-6" est un jeu d'aventure très simple, basé sur du texte. 
 * Les utilisateurs peuvent se déplacer dans un décor. C'est tout. 
 * Il devrait vraiment être étendu pour le rendre plus intéressant !
 * Pour jouer à ce jeu, créez une instance de cette classe.
 * Cette classe principale crée les objets d'implémentation nécessaires et lance le jeu.
 * @author Michael Kolling et David J. Barnes
 * @version 2.0 (Jan 2003)
 * @modified by Alexander KAZAZYAN
 */

public class Game
{
    private UserInterface gui;
    private GameEngine engine;

    /**
     * Crée le jeu et initialise sa carte interne.
     */
    public Game() 
    {
        engine = new GameEngine();
        gui = new UserInterface(engine);
        engine.setGUI(gui);
    }
}
