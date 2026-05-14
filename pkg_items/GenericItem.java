package pkg_items;

import pkg_utility.Lang;

/**
 * GenericItem - Représente un objet standard sans propriétés dynamiques.
 * Utilisé pour les cartes d'accès, clés, etc.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class GenericItem extends Item {

    /**
     * Constructeur pour un objet générique.
     * 
     * @param pType Le type d'objet provenant de l'énumération ItemType.
     */
    public GenericItem(final ItemType pType) {
        super(pType);
    }

    /**
     * @return Une chaîne vide ou une description simple car pas d'état spécial.
     */
    @Override
    public String getInformation() {
        return Lang.localizableString(super.getName());
    }
}
