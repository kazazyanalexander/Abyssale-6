package pkg_items;

import pkg_characters.Player;
import pkg_utility.Lang;

/**
 * MagicCookie - Représente un cookie magique qui augmente la capacité de
 * transport du joueur.
 * Hérite de la classe GenericItem.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class MagicCookie extends GenericItem {
    /** Version de serialisation automatique */
    private static final long serialVersionUID = 1L;
    /** Le bonus de poids ajouté lorsque le cookie est mangé (en grammes) */
    private static final int WEIGHT_BONUS = 13000; // +13 kg

    /**
     * Constructeur du cookie magique.
     */
    public MagicCookie() {
        super(ItemType.MAGIC_COOKIE);
    }

    /**
     * Applique l'effet du cookie sur le joueur.
     * Augmente la capacité de transport maximale.
     * 
     * @param pPlayer Le joueur qui mange le cookie
     */
    public void applyEffect(Player pPlayer) {
        int newMaxWeight = pPlayer.getMaxWeight() + WEIGHT_BONUS;
        pPlayer.setMaxWeight(newMaxWeight);
    }

    /**
     * Retourne une description de l'effet du cookie, incluant le bonus de poids.
     * 
     * @return Une description de l'effet du cookie
     */
    @Override
    public String getInformation() {
        return Lang.localizableString(super.getName())
                + String.format(Lang.localizableString("weight_bonus"), WEIGHT_BONUS / 1000.0);
    }

    /**
     * Format: MagicCookie{type=MAGIC_COOKIE}
     * 
     * @return Une représentation textuelle du cookie magique
     */
    @Override
    public String toString() {
        return String.format("MagicCookie{type=%s}", getType().name());
    }
}
