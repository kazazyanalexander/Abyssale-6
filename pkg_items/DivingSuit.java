package pkg_items;

import pkg_utility.Lang;

/**
 * Write a description of class ttt here.
 *
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class DivingSuit extends Item {
    /** Niveau d'oxygène du scaphandre (en pourcentage) */
    private int aOxygen;

    /**
     * Crée un nouvel objet de type DivingSuit.
     */
    public DivingSuit() {
        this(60);
    }

    /**
     * Crée un nouvel objet de type DivingSuit avec un niveau d'oxygène spécifié.
     * 
     * @param pOxygen Le niveau d'oxygène initial du scaphandre (en pourcentage)
     */
    public DivingSuit(int pOxygen) {
        super(ItemType.DIVING_SUIT);
        this.aOxygen = pOxygen;
    }

    /**
     * Retourne une description de l'objet, incluant le niveau d'oxygène.
     *
     * @return Une description de l'objet
     */
    @Override
    public String getInformation() {
        return Lang.localizableString(super.getName()) + this.aOxygen + "%";
    }

    /**
     * Définit le niveau d'oxygène (pour la restauration).
     * 
     * @param pLevel Le niveau d'oxygène à définir (en pourcentage, entre 0 et 100)
     */
    public void setOxygenLevel(final int pLevel) {
        this.aOxygen = Math.min(100, Math.max(0, pLevel));
    }
}
