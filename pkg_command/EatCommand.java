package pkg_command;

import pkg_core.GameEngine;
import pkg_characters.Player;
import pkg_items.Item;
import pkg_items.MagicCookie;
import pkg_utility.Lang;

/**
 * EatCommand - Commande pour manger un objet comestible.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class EatCommand extends Command {
    /**
     * Constructeur par défaut de la commande EatCommand.
     * Initialise la commande sans paramètre supplémentaire.
     */
    public EatCommand() {
        // Constructeur par défaut
    }

    /**
     * Exécute la commande pour manger un objet comestible.
     * 
     * @param pPlayer     Le joueur exécutant la commande
     * @param pGameEngine Le moteur de jeu
     * @return true si la commande a été exécutée avec succès, false sinon
     */
    @Override
    public boolean execute(Player pPlayer, GameEngine pGameEngine) {
        if (!hasSecondWord()) {
            pGameEngine.log(Lang.localizableString("eat_error"));
            return false;
        }

        String vItemName = getSecondWord();
        Item vItem = pPlayer.eatItem(vItemName);

        if (vItem == null) {
            pGameEngine.log(Lang.localizableString("item_not_in_room"));
            return false;
        }

        pGameEngine.refreshInventory(); // Rafraîchir les overlays

        // Vérifier si c'est un cookie magique
        if (vItem instanceof MagicCookie) {
            MagicCookie vCookie = (MagicCookie) vItem;

            // Sauvegarder l'ancienne capacité pour l'affichage
            int oldMaxWeight = pPlayer.getMaxWeight();

            // Appliquer l'effet (double la capacité)
            vCookie.applyEffect(pPlayer);

            // Afficher le message de succès
            pGameEngine.log(Lang.localizableString("ate_magic_cookie"));
            pGameEngine.log(Lang.localizableString("weight_doubled") + " " +
                    oldMaxWeight + "g → " + pPlayer.getMaxWeight() + "g\n");

            // Jouer un son magique
            pGameEngine.playSound("magic.wav");

        } else {
            pGameEngine.log(Lang.localizableString("cannot_eat") + " " + vItem.getInformation());
        }

        return false;
    }
}
