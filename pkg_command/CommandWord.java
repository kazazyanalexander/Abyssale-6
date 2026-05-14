package pkg_command;

import java.util.HashMap;
import java.util.Map;

/**
 * CommandWord - Énumération des mots de commande avec association directe.
 * Cette énumération centralise tous les mots de commande valides du jeu et
 * assure la correspondance entre la chaîne saisie par l'utilisateur et
 * l'objet Command correspondant à exécuter.
 * 
 * @author Alexander KAZAZYAN
 * @version 05/2026
 */
public enum CommandWord {
    /** Commande de déplacement. */
    GO("go", new GoCommand()),
    /** Commande pour quitter le jeu. */
    QUIT("quit", new QuitCommand()),
    /** Commande pour afficher l'aide. */
    HELP("help", new HelpCommand()),
    /** Commande pour observer l'environnement. */
    LOOK("look", new LookCommand()),
    /** Commande pour changer la langue. */
    LANG("lang", new LangCommand()),
    /** Commande pour manger un objet. */
    EAT("eat", new EatCommand()),
    /** Commande pour revenir en arrière. */
    BACK("back", new BackCommand()),
    /** Commande pour exécuter un fichier de test. */
    TEST("test", new TestCommand()),
    /** Commande pour prendre un objet. */
    TAKE("take", new TakeCommand()),
    /** Commande pour déposer un objet. */
    DROP("drop", new DropCommand()),
    /** Commande pour afficher l'inventaire. */
    INVENTORY("inventory", new InventoryCommand()),
    /** Commande pour charger le téléporteur. */
    CHARGE("charge", new ChargeCommand()),
    /** Commande pour déclencher le téléporteur. */
    FIRE("fire", new FireCommand()),
    /** Commande pour utiliser un objet. */
    USE("use", new UseCommand()),
    /** Commande pour contrôler l'aléatoire (mode test). */
    ALEA("alea", new AleaCommand()),
    /** Commande pour parler à un personnage. */
    TALK("talk", new TalkCommand()),
    /** Commande pour donner un objet à un personnage. */
    GIVE("give", new GiveCommand()),
    /** Commande pour sauvegarder la partie. */
    SAVE("save", new SaveXMLCommand()),
    /** Commande pour charger une partie. */
    LOAD("load", new LoadXMLCommand()),
    /** Commande inconnue ou non reconnue. */
    UNKNOWN("?", new UnknownCommand());

    /**
     * Chaîne représentant la commande.
     */
    private final String aCommandString;
    /**
     * Objet Command associé à cette commande.
     */
    private final Command aCommand;

    /**
     * * Mapping statique pour la recherche par String (fromString).
     * On garde ce petit bloc statique car c'est le moyen le plus rapide
     * de transformer une saisie utilisateur en Enum.
     */
    private static final Map<String, CommandWord> STRING_TO_COMMAND = new HashMap<>();

    static {
        for (CommandWord vCmd : values()) {
            if (vCmd != UNKNOWN) {
                STRING_TO_COMMAND.put(vCmd.aCommandString, vCmd);
            }
        }
    }

    /**
     * Constructeur de l'énumération.
     * 
     * @param pCommandString La chaîne représentant la commande.
     * @param pCommand       L'objet Command associé à cette commande.
     */
    private CommandWord(final String pCommandString, final Command pCommand) {
        this.aCommandString = pCommandString;
        this.aCommand = pCommand;
    }

    /**
     * Retourne la représentation textuelle de la commande.
     * 
     * @return L'objet Command associé à ce mot.
     */
    public Command getCommand() {
        return this.aCommand;
    }

    @Override
    public String toString() {
        return this.aCommandString;
    }

    /**
     * Convertit une chaîne en CommandWord.
     * 
     * @param pString La chaîne à convertir.
     * @return L'objet CommandWord correspondant à la chaîne
     */
    public static CommandWord fromString(final String pString) {
        if (pString == null)
            return UNKNOWN;
        CommandWord vResult = STRING_TO_COMMAND.get(pString.toLowerCase());
        return vResult != null ? vResult : UNKNOWN;
    }

    /**
     * Retourne la liste de toutes les commandes valides.
     * 
     * @return Une chaîne de caractères contenant toutes les commandes
     */
    public static String getAllCommands() {
        return String.join(" ", STRING_TO_COMMAND.keySet());
    }
}