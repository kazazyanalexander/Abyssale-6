import java.util.Set;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Classe Room - une pièce dans un jeu d'aventure.
 * Cette classe fait partie de l'application "Station Abyssale-6".
 * "Station Abyssale-6" est un jeu d'aventure très simple, basé sur du texte.
 * Une "Room" représente un emplacement dans le décor du jeu. Elle est
 * connectée à d'autres pièces via des sorties. Pour chaque sortie existante, la pièce
 * stocke une référence vers la pièce voisine.
 * @author Michael Kolling et David J. Barnes
 * @version 1.0 (Février 2002)
 * @modified by Alexander KAZAZYAN
 */

public class Room
{
    private String description;
    private HashMap exits; // stocke les sorties de cette pièce.
    private String imageName;

    /**
     * Crée une pièce décrite par "description" avec une image donnée. 
     * Initialement, elle n'a pas de sorties. "description" est quelque chose comme 
     * "dans une cuisine" ou "dans une cour ouverte".
     */
    public Room(final String description,final String image) 
    {
        this.description = description;
        exits = new HashMap();
        imageName = image;
    }

    /**
     * Définit une sortie de cette pièce.
     */
    public void setExit(final String direction,final Room neighbor) 
    {
        exits.put(direction, neighbor);
    }

    /**
     * Retourne la description de la pièce (celle qui a été définie dans le
     * constructeur).
     */
    public String getShortDescription()
    {
        return description;
    }

    /**
     * Retourne une longue description de cette pièce, sous la forme :
     *     Vous êtes dans la cuisine.
     *     Sorties : nord ouest
     */
    public String getLongDescription()
    {
        return "Vous êtes " + description + ".\n" + getExitString();
    }

    /**
     * Retourne une chaîne de caractères décrivant les sorties de la pièce, par exemple
     * "Sorties : nord ouest".
     */
    private String getExitString()
    {
        String returnString = "Sorties :";
        Set keys = exits.keySet();
        for(Iterator iter = keys.iterator(); iter.hasNext(); )
            returnString += " " + iter.next();
        return returnString;
    }

    /**
     * Retourne la pièce atteinte si nous allons de cette pièce dans la direction
     * "direction". S'il n'y a pas de pièce dans cette direction, retourne null.
     */
    public Room getExit(final String direction) 
    {
        return (Room)exits.get(direction);
    }

    /**
     * Retourne une chaîne de caractères décrivant le nom de l'image de la pièce
     */
    public String getImageName()
    {
        return imageName;
    }
}
