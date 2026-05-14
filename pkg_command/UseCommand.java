package pkg_command;

import pkg_core.GameEngine;
import pkg_gameplay.Door;
import pkg_gameplay.Room;
import pkg_ui_components.EasterEggDialog;
import pkg_characters.Player;
import pkg_items.Item;
import pkg_items.ItemType;
import pkg_items.Torch;
import pkg_utility.Direction;
import pkg_utility.Lang;

/**
 * UseCommand - Commande pour utiliser un objet (clé, etc.).
 * 
 * @author Alexander KAZAZYAN
 * @version 04/2026
 */
public class UseCommand extends Command {

    /** Niveau minimum de batterie pour déclencher l'easter egg */
    private static final int MIN_BATTERY_FOR_EASTER_EGG = 15;

    /**
     * Constructeur par défaut de la commande UseCommand.
     */
    public UseCommand() {
        // Constructeur par défaut
    }

    /**
     * Exécute la commande pour utiliser un objet.
     * 
     * @param pPlayer     Le joueur exécutant la commande
     * @param pGameEngine Le moteur du jeu
     * @return true si la commande a été exécutée avec succès, false sinon
     */
    @Override
    public boolean execute(Player pPlayer, GameEngine pGameEngine) {
        if (!hasSecondWord()) {
            pGameEngine.log(Lang.localizableString("use_what"));
            return false;
        }

        String vItemName = getSecondWord();
        Item vItem = pPlayer.getInventory().getItem(vItemName);

        if (vItem == null) {
            pGameEngine.log(Lang.localizableString("item_not_in_inventory"));
            return false;
        }

        // ===== EASTER EGG : Utiliser la torche sur le pont d'observation =====
        Room vCurrentRoom = pPlayer.getCurrentRoom();

        if (vItem.getType() == ItemType.TORCH &&
                vCurrentRoom.getRoomKey().equals("room_obs")) {

            // Récupérer la torche (qui est un objet de type Torch)
            Torch vTorch = (Torch) vItem;
            int vBatteryLevel = vTorch.getBatteryLife();

            // Vérifier le niveau de batterie
            if (vBatteryLevel < MIN_BATTERY_FOR_EASTER_EGG) {
                // Batterie trop faible - afficher un message et ne pas montrer l'easter egg
                pGameEngine.log(Lang.localizableString("easter_egg_battery_too_low"));
                pGameEngine.log(String.format(
                        Lang.localizableString("easter_egg_battery_level"),
                        vBatteryLevel));
                pGameEngine.log(Lang.localizableString("easter_egg_charge_needed"));
                return false;
            }

            // Batterie suffisante - déclencher l'easter egg
            pGameEngine.log(Lang.localizableString("easter_egg_torch_brandish"));
            pGameEngine.log(Lang.localizableString("easter_egg_light_beam"));
            pGameEngine.log(Lang.localizableString("easter_egg_secret_message"));

            // Afficher le dialogue Easter Egg avec la torche
            EasterEggDialog vEasterEggDialog = new EasterEggDialog(pGameEngine.getGui(), vTorch);
            vEasterEggDialog.setVisible(true);

            return false;
        }

        // Gestion des clés pour les portes
        if (vItem.getType() == ItemType.BLUE_CARD ||
                vItem.getType() == ItemType.DIVING_SUIT ||
                vItem.getType() == ItemType.RED_CARD ||
                vItem.getType() == ItemType.WRENCH) {

            boolean vDoorFound = false;

            for (Direction vDir : Direction.getAll()) {
                String vDirection = vDir.toString();
                if (vCurrentRoom.hasDoor(vDirection)) {
                    Door vDoor = vCurrentRoom.getDoor(vDirection);

                    if (vDoor.getRequiredKey() == vItem.getType()) {
                        vDoorFound = true;

                        if (vDoor.isLocked()) {
                            boolean vSuccess = vCurrentRoom.unlockDoor(vDirection, vItem);
                            if (vSuccess) {
                                pGameEngine.playSound("granted.wav");
                                pGameEngine.log(String.format(
                                        Lang.localizableString("door_unlocked_direction"),
                                        Lang.localizableString(vDirection)));
                            } else {
                                pGameEngine.log(Lang.localizableString("wrong_key"));
                            }
                        } else {
                            boolean vSuccess = vCurrentRoom.lockDoor(vDirection, vItem);
                            if (vSuccess) {
                                pGameEngine.log(String.format(
                                        Lang.localizableString("door_locked_direction"),
                                        Lang.localizableString(vDirection)));
                            } else {
                                pGameEngine.log(Lang.localizableString("wrong_key"));
                            }
                        }
                        break;
                    }
                }
            }

            if (!vDoorFound) {
                pGameEngine.log(Lang.localizableString("no_compatible_door"));
            }

        } else {
            pGameEngine.log(Lang.localizableString("cannot_use_item"));
        }

        return false;
    }
}