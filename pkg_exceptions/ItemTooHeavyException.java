package pkg_exceptions;

/**
 * ItemTooHeavyException - Exception levée lorsqu'un objet est trop lourd pour
 * être transporté.
 * Hérite de GameException.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 **/
public class ItemTooHeavyException extends GameException {
    /**
     * Constructeur de ItemTooHeavyException.
     * Utilise la clé de ressource "error_too_heavy".
     */
    public ItemTooHeavyException() {
        super("error_too_heavy");
    }
}
