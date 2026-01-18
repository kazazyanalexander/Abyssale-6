/**
 * Cette classe fait partie de l'application "Station Abyssale-6".
 * "Station Abyssale-6" est un jeu d'aventure très simple, basé sur du e.
 * Cette classe crée toutes les pièces, crée l'analyseur syntaxique et démarre
 * le jeu. Elle évalue et exécute également les commandes que
 * l'analyseur syntaxique retourne.
 * @author Michael Kolling et David J. Barnes
 * @version 1.0 (Jan 2003)
 * @modified by Alexander KAZAZYAN
 */
public class GameEngine
{
    private Parser parser;
    private Room currentRoom;
    private UserInterface gui;

    /**
     * Constructeur pour les objets de la classe GameEngine
     */
    public GameEngine()
    {
        parser = new Parser();
        createRooms();
    }

    public void setGUI(UserInterface userInterface)
    {
        gui = userInterface;
        printWelcome();
    }

    /**
     * Affiche le message d'accueil pour le joueur.
     */
    private void printWelcome()
    {
        gui.print("\n");
        gui.println("Bienvenue dans Station Abyssale-6 !");
        gui.println("Station Abyssale-6 est un nouveau jeu d'aventure incroyablement ennuyeux.");
        gui.println("Tapez 'help' si vous avez besoin d'aide.");
        gui.print("\n");
        gui.println(currentRoom.getLongDescription());
        gui.showImage(currentRoom.getImageName());
    }

    /**
     * Crée toutes les pièces et relie leurs sorties entre elles.
     */
    private void createRooms()
    {
        Room outside, theatre, pub, lab, office;

        // crée les pièces
        outside = new Room("à l'extérieur de l'entrée principale de l'université", "outside.gif");
        theatre = new Room("dans un amphithéâtre", "castle.gif");
        pub = new Room("dans le pub du campus", "courtyard.gif");
        lab = new Room("dans un laboratoire informatique", "stairs.gif");
        office = new Room("le bureau de l'administration informatique", "dungeon.gif");

        // initialise les sorties des pièces
        outside.setExit("east", theatre);
        outside.setExit("south", lab);
        outside.setExit("west", pub);

        theatre.setExit("west", outside);

        pub.setExit("east", outside);

        lab.setExit("north", outside);
        lab.setExit("east", office);

        office.setExit("west", lab);

        currentRoom = outside; // commence le jeu à l'extérieur
    }

    /**
     * Traite (c'est-à-dire exécute) la commande donnée.
     * Si cette commande met fin au jeu, true est retourné, sinon false est
     * retourné.
     */
    public void interpretCommand(String commandLine)
    {
        gui.println(commandLine);
        Command command = parser.getCommand(commandLine);

        if(command.isUnknown()) {
            gui.println("Je ne comprends pas ce que vous voulez dire...");
            return;
        }

        String commandWord = command.getCommandWord();
        if (commandWord.equals("help"))
            printHelp();
        else if (commandWord.equals("go"))
            goRoom(command);
        else if (commandWord.equals("quit")) {
            if(command.hasSecondWord())
                gui.println("Quitter quoi ?");
            else
                endGame();
        }
    }

    // implémentations des commandes utilisateur :

    /**
     * Affiche des informations d'aide.
     * Ici, nous affichons un message stupide et cryptique ainsi qu'une liste des
     * mots de commande.
     */
    private void printHelp()
    {
        gui.println("Vous êtes perdu. Vous êtes seul. Vous errez");
        gui.println("autour du campus de la péninsule de Monash Uni." + "\n");
        gui.print("Vos mots de commande sont : " + parser.showCommands());
    }

    /**
     * Essaie d'aller dans une direction. S'il y a une sortie, entre dans la nouvelle
     * pièce, sinon affiche un message d'erreur.
     */
    private void goRoom(Command command)
    {
        if(!command.hasSecondWord()) {
            // s'il n'y a pas de second mot, nous ne savons pas où aller...
            gui.println("Aller où ?");
            return;
        }

        String direction = command.getSecondWord();

        // Essaie de quitter la pièce actuelle.
        Room nextRoom = currentRoom.getExit(direction);

        if (nextRoom == null)
            gui.println("Il n'y a pas de porte !");
        else {
            currentRoom = nextRoom;
            gui.println(currentRoom.getLongDescription());
            if(currentRoom.getImageName() != null)
                gui.showImage(currentRoom.getImageName());
        }
    }

    private void endGame()
    {
        gui.println("Merci d'avoir joué. Au revoir.");
        gui.enable(false);
    }

}
