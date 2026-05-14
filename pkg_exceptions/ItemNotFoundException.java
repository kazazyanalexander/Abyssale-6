package pkg_exceptions;

/**
 * ItemNotFoundException - Exception levée lorsqu'un objet demandé n'est pas
 * trouvé.
 * Hérite de GameException.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class ItemNotFoundException extends GameException {
    /**
     * Constructeur de ItemNotFoundException.
     * Utilise la clé de ressource "error_item_not_found".
     */
    public ItemNotFoundException() {
        super("error_item_not_found");
    }
}
