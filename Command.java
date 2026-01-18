/**
 * Cette classe est la classe principale de l'application "Station Abyssale-6".
 * "Station Abyssale-6" est un jeu d'aventure très simple, basé sur du texte.
 * Cette classe conserve les informations concernant une commande entrée par l'utilisateur.
 * Une commande est actuellement constituée de deux chaînes de caractères : un mot de commande et un second
 * mot (par exemple, si la commande était "prendre carte", alors les deux chaînes
 * sont évidemment "prendre" et "carte").
 * La façon dont cela est utilisé est : Les commandes sont déjà vérifiées pour être des mots de commande valides.
 * Si l'utilisateur a entré une commande invalide (un mot qui n'est pas connu), alors le mot de commande est <null>.
 * Si la commande n'avait qu'un seul mot, alors le second mot est <null>.
 * @author Michael Kolling et David J. Barnes
 * @version 1.0 (Février 2002)
 * @modified by Alexander KAZAZYAN
 */

class Command
{
    private String commandWord;
    private String secondWord;

    /**
     * Crée un objet commande. Le premier et le second mot doivent être fournis, mais
     * l'un ou l'autre (ou les deux) peuvent être nuls. Le mot de commande doit être null pour
     * indiquer qu'il s'agit d'une commande non reconnue par ce jeu.
     */
    public Command(final String firstWord,final String secondWord)
    {
        commandWord = firstWord;
        this.secondWord = secondWord;
    }

    /**
     * Retourne le mot de commande (le premier mot) de cette commande. Si la
     * commande n'a pas été comprise, le résultat est null.
     */
    public String getCommandWord()
    {
        return commandWord;
    }

    /**
     * Retourne le second mot de cette commande. Retourne null s'il n'y a pas de
     * second mot.
     */
    public String getSecondWord()
    {
        return secondWord;
    }

    /**
     * Retourne true si cette commande n'a pas été comprise.
     */
    public boolean isUnknown()
    {
        return (commandWord == null);
    }

    /**
     * Retourne true si la commande possède un second mot.
     */
    public boolean hasSecondWord()
    {
        return (secondWord != null);
    }
}