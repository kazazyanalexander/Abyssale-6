package pkg_exceptions;

/**
 * ItemNotPickableException - Exception levée lorsqu'on tente de prendre un
 * objet non prenable.
 * Hérite de GameException.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class ItemNotPickableException extends GameException {
    /**
     * Constructeur de ItemNotPickableException.
     * Utilise la clé de ressource "error_item_not_pickable".
     */
    public ItemNotPickableException() {
        super("error_item_not_pickable");
    }
}
