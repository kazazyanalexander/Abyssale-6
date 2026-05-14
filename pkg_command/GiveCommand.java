package pkg_command;

import pkg_core.GameEngine;
import pkg_gameplay.Room;
import pkg_exceptions.ItemTooHeavyException;
import pkg_characters.Character;
import pkg_characters.Player;
import pkg_items.Item;
import pkg_utility.Lang;

/**
 * GiveCommand - Commande pour donner un objet à un personnage.
 * Gère les réactions, l'aide et les échanges d'objets.
 * Les dialogues de confirmation sont automatiquement acceptés en mode test.
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class GiveCommand extends Command {
    /**
     * Constructeur par défaut de la commande GiveCommand.
     * Initialise la commande sans paramètre supplémentaire.
     */
    public GiveCommand() {
        // Constructeur par défaut
    }

    /**
     * Exécute la commande pour donner un objet à un personnage.
     * 
     * @param pPlayer     Le joueur exécutant la commande
     * @param pGameEngine Le moteur de jeu
     * @return true si la commande a été exécutée avec succès, false sinon
     */
    @Override
    public boolean execute(Player pPlayer, GameEngine pGameEngine) {
        if (!hasSecondWord()) {
            // Sans second mot → utiliser la popup
            pGameEngine.showGivePopup();
            return false;
        }

        String vItemName = getSecondWord();
        Item vItem = pPlayer.getInventory().getItem(vItemName);

        if (vItem == null) {
            pGameEngine.log(Lang.localizableString("item_not_in_inventory"));
            return false;
        }

        Room vCurrentRoom = pPlayer.getCurrentRoom();

        if (!vCurrentRoom.hasCharacters()) {
            pGameEngine.log(Lang.localizableString("no_characters_here"));
            return false;
        }

        // Chercher un personnage qui réagit à cet objet
        boolean vReacted = false;
        for (Character vChar : vCurrentRoom.getCharacters()) {
            String vResponse = vChar.reactToItem(vItem);

            if (vResponse != null) {
                pGameEngine.log(vResponse);
                vReacted = true;

                // Gestion des échanges
                if (vChar.acceptsExchange(vItem)) {
                    handleExchange(pPlayer, pGameEngine, vChar, vItem);
                }
                // Gestion de l'aide (objet requis)
                else if (vChar.getRequiredItem() != null &&
                        vChar.getRequiredItem().getType() == vItem.getType()) {
                    handleHelp(pPlayer, pGameEngine, vChar, vItem);
                }
                // Simple réaction sans échange ni aide
                else {
                    // Juste une réaction, l'objet n'est pas consommé
                    pGameEngine.log(Lang.localizableString("item_not_consumed"));
                }
                break;
            }
        }

        if (!vReacted) {
            pGameEngine.log(Lang.localizableString("no_one_wants_item"));
        }

        return false;
    }

    /**
     * Gère un échange d'objet avec un personnage.
     * En mode test, l'échange est automatiquement accepté sans dialogue.
     * 
     * @param pPlayer     Le joueur
     * @param pGameEngine Le moteur de jeu (pour détecter le mode test)
     * @param pChar       Le personnage
     * @param pGivenItem  L'objet donné par le joueur
     */
    private void handleExchange(final Player pPlayer, final GameEngine pGameEngine,
            final Character pChar, final Item pGivenItem) {

        // Effectuer l'échange
        Item vReceivedItem = pChar.performExchange(pGivenItem);

        if (vReceivedItem != null) {
            try {
                // Utiliser la méthode d'échange qui peut lancer une exception
                boolean vExchangeSuccess = pPlayer.exchangeItems(pGivenItem, vReceivedItem);

                if (vExchangeSuccess) {
                    pGameEngine.log(String.format(
                            Lang.localizableString("exchange_success"),
                            pGivenItem.getInformation(),
                            vReceivedItem.getInformation()));

                    // Mettre à jour l'affichage
                    pGameEngine.log(pPlayer.getInventoryString());

                    // Forcer le rafraîchissement de l'inventaire
                    pGameEngine.refreshInventory();
                } else {
                    pGameEngine.log(Lang.localizableString("exchange_failed"));
                }

            } catch (ItemTooHeavyException e) {
                // L'objet reçu est trop lourd
                pGameEngine.log(Lang.localizableString("cannot_take") + " "
                        + Lang.localizableString(e.getResourceKey()));
                pGameEngine.log(Lang.localizableString("exchange_cancelled"));
                // L'échange est annulé, l'objet donné reste dans l'inventaire
            }
        } else {
            pGameEngine.log(Lang.localizableString("exchange_failed"));
        }
    }

    /**
     * Gère le cas où le joueur donne l'objet requis pour l'aide.
     * 
     * @param pPlayer     Le joueur
     * @param pGameEngine L'interface graphique
     * @param pChar       Le personnage
     * @param pItem       L'objet donné
     */
    private void handleHelp(final Player pPlayer, final GameEngine pGameEngine,
            final Character pChar, final Item pItem) {

        // Retirer l'objet de l'inventaire (il est consommé pour l'aide)
        pPlayer.getInventory().removeItem(pItem.getName());
        pGameEngine.log(Lang.localizableString("item_given"));

        pGameEngine.log(String.format(
                Lang.localizableString("help_received"),
                pChar.getName()));

        // Mettre à jour l'inventaire
        pGameEngine.log(pPlayer.getInventoryString());
    }
}