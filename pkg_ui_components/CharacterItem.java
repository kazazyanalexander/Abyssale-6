package pkg_ui_components;

import pkg_characters.Character;
import pkg_characters.MovingCharacter;
import pkg_utility.Lang;

/**
 * Wrapper pour afficher un personnage dans la liste.
 */
public class CharacterItem {
    /** Le personnage associé au wrapper */
    private final Character aCharacter;

    /**
     * Constructeur du wrapper.
     * 
     * @param pCharacter Le personnage à afficher
     */

    public CharacterItem(final Character pCharacter) {
        this.aCharacter = pCharacter;
    }

    /**
     * Retourne le personnage associé au wrapper
     * 
     * @return Le personnage associé au wrapper
     */
    public Character getCharacter() {
        return this.aCharacter;
    }

    /**
     * Retourne une représentation textuelle du wrapper
     * 
     * @return Une chaîne de caractères formatée
     */
    @Override
    public String toString() {
        String vDesc = Lang.localizableString(this.aCharacter.getName());

        // Ajouter une indication si c'est un MovingCharacter
        if (this.aCharacter instanceof MovingCharacter) {
            vDesc += " (en mouvement)";
        }

        // Ajouter la première ligne de la description
        String vFullDesc = this.aCharacter.getFullDescription();
        if (vFullDesc.contains("\n")) {
            vDesc += " - " + vFullDesc.split("\n")[1].trim();
        }

        return vDesc;
    }
}