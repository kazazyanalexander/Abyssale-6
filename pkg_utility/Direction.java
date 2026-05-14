package pkg_utility;

/**
 * Direction - Énumération des directions possibles dans le jeu.
 * Centralise toutes les directions pour éviter l'utilisation de chaînes
 * de caractères littérales (magic strings) dans le code. Cette énumération
 * offre une meilleure sécurité de type et facilite la maintenance.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public enum Direction {

    /** Direction nord (vers le haut de la carte). */
    NORTH("north"),

    /** Direction sud (vers le bas de la carte). */
    SOUTH("south"),

    /** Direction est (vers la droite de la carte). */
    EAST("east"),

    /** Direction ouest (vers la gauche de la carte). */
    WEST("west"),

    /** Direction vers le haut (niveau supérieur). */
    UP("up"),

    /** Direction vers le bas (niveau inférieur). */
    DOWN("down");

    /** Représentation textuelle de la direction. */
    private final String aDirection;

    /**
     * Constructeur de l'énumération Direction.
     * 
     * @param pDirection La direction sous forme de chaîne de caractères
     */
    Direction(final String pDirection) {
        this.aDirection = pDirection;
    }

    /**
     * Retourne la direction sous forme de chaîne de caractères.
     * 
     * @return La chaîne représentant la direction (ex: "north", "south", etc.)
     */
    @Override
    public String toString() {
        return this.aDirection;
    }

    /**
     * Retourne un tableau contenant toutes les directions de l'énumération.
     * 
     * @return Un tableau de toutes les directions
     */
    public static Direction[] getAll() {
        return values();
    }
}