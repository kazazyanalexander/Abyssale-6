/**
 * Cette classe est la classe principale de l'application "Station Abyssale-6".
 * "Station Abyssale-6" est un jeu d'aventure très simple, basé sur du texte.
 * Cette classe contient une énumération de tous les mots de commande connus du jeu.
 * Elle est utilisée pour reconnaître les commandes au fur et à mesure qu'elles sont tapées.
 * @author Michael Kolling et David J. Barnes
 * @version 1.0 (Février 2002)
 * @modified by Alexander KAZAZYAN
 */

public class CommandWords
{
    // un tableau constant qui contient tous les mots de commande valides
    private static final String validCommands[] = {
            "go", "quit", "help"
        };

    /**
     * Constructeur - initialise les mots de commande.
     */
    public CommandWords()
    {
        // rien à faire pour le moment...
    }

    /**
     * Vérifie si une chaîne de caractères donnée est un mot de commande valide. 
     * Retourne true si c'est le cas, false sinon.
     **/
    public boolean isCommand(final String aString)
    {
        for(int i = 0; i < validCommands.length; i++) {
            if(validCommands[i].equals(aString))
                return true;
        }
        // si on arrive ici, la chaîne n'a pas été trouvée dans les commandes
        return false;
    }

    /**
     * retourne une chaîne de caractères de toutes les commandes valides.
     */
    public String showAll() 
    {
        StringBuffer commands = new StringBuffer();
        for(int i = 0; i < validCommands.length; i++) {
            commands.append(validCommands[i] + "  ");
        }
        return commands.toString();
    }
}
