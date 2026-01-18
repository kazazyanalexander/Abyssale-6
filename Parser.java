import java.util.StringTokenizer;

/**
 * Cette classe fait partie de "Station Abyssale-6". "Station Abyssale-6" est un simple
 * jeu d'aventure basé sur du texte.
 * Cet analyseur syntaxique prend l'entrée utilisateur et essaie de l'interpréter comme une commande "Zuul".
 * Chaque fois qu'il est appelé, il prend une ligne sous forme de chaîne de caractères et
 * essaie d'interpréter la ligne comme une commande à deux mots. Il retourne la commande
 * sous forme d'un objet de la classe Command.
 * L'analyseur syntaxique possède un ensemble de mots de commande connus. Il vérifie l'entrée utilisateur par rapport
 * aux commandes connues, et si l'entrée n'est pas l'une des commandes connues, il
 * retourne un objet commande marqué comme commande inconnue.
 * @author Michael Kolling et David J. Barnes
 * @version 2.0 (Jan 2003)
 * @modified by Alexander KAZAZYAN
 */

public class Parser
{

    private CommandWords commands;  // contient tous les mots de commande valides
    /**
     * Crée un nouvel analyseur syntaxique.
     */
    public Parser() 
    {
        commands = new CommandWords();
    }

    /**
     * Obtient une nouvelle commande de l'utilisateur. La commande est lue en
     * analysant la 'inputLine'.
     */
    public Command getCommand(final String inputLine) 
    {
        //String inputLine = "";   // contiendra la ligne d'entrée complète
        String word1;
        String word2;

        StringTokenizer tokenizer = new StringTokenizer(inputLine);

        if(tokenizer.hasMoreTokens())
            word1 = tokenizer.nextToken();      // obtient le premier mot
        else
            word1 = null;
        if(tokenizer.hasMoreTokens())
            word2 = tokenizer.nextToken();      // obtient le second mot
        else
            word2 = null;

        // note : nous ignorons simplement le reste de la ligne d'entrée.

        // Maintenant, vérifie si ce mot est connu. Si c'est le cas, crée une commande
        // avec lui. Sinon, crée une commande "null" (pour une commande inconnue).

        if(commands.isCommand(word1))
            return new Command(word1, word2);
        else
            return new Command(null, word2);
    }

    /**
     * Affiche une liste des mots de commande valides.
     */
    public String showCommands()
    {
        return commands.showAll();
    }
}
