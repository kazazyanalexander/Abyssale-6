package pkg_items;

import pkg_utility.Lang;

/**
 * Write a description of class Torch here.
 *
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class Torch extends Item {
    /** Durée de vie de la batterie en pourcentage */
    private int aBatteryLife;

    /**
     * Constructeur du torch.
     */
    public Torch() {
        this(100);
    }

    /**
     * Constructeur du torch avec une durée de vie spécifiée.
     *
     * @param pBatteryLife Durée de vie de la batterie en pourcentage
     */
    public Torch(int pBatteryLife) {
        super(ItemType.TORCH); // On lie l'instance au type TORCH
        this.aBatteryLife = pBatteryLife;
    }

    /**
     * Met à jour la durée de vie de la batterie.
     *
     * @param pBatteryLife Nouvelle durée de vie de la batterie en pourcentage
     */
    public void setBatteryLife(int pBatteryLife) {
        this.aBatteryLife = Math.max(0, Math.min(100, pBatteryLife));
    }

    /**
     * Retourne la durée de vie actuelle de la batterie.
     *
     * @return La durée de vie de la batterie
     */
    public int getBatteryLife() {
        return this.aBatteryLife;
    }

    /**
     * Retourne une description de l'objet, incluant son type et sa durée de vie.
     *
     * @return Une description de l'objet
     */
    @Override
    public String getInformation() {
        return Lang.localizableString(super.getName()) + " " + this.aBatteryLife + "%";
    }

}
