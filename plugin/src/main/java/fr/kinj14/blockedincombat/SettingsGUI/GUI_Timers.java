package fr.kinj14.blockedincombat.SettingsGUI;

import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Manager.ItemsManager;
import fr.kinj14.blockedincombat.Manager.PlayerManager;
import fr.kinj14.blockedincombat.Manager.Settings.SettingsManager;
import fr.kinj14.blockedincombat.Manager.Settings.SettingsSave;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class GUI_Timers extends GUI {
    public GUI_Timers(String permission) {
        super("blockedincombat.ChangeSettings");
    }

    @Override
    public String getMenuName() {
        return Lang.CONFIGÙGUIÙTIMERSÙNAME.get();
    }

    @Override
    public int getSlots() {
        return 9;
    }

    @Override
    public void create() {
        this.inventory = Bukkit.createInventory(this, getSlots(), getMenuName());
    }

    @Override
    public void handleMenu(InventoryClickEvent event) {
        if(event.getWhoClicked() instanceof Player && event.getInventory() instanceof Inventory) {
            Player player = (Player) event.getWhoClicked();
            if (PlayerManager.hasPermission(player, this.permission)) {
                final ItemStack item = inventory.getItem(event.getSlot());

                if (item != null) {
                    if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                        ItemMeta meta = item.getItemMeta();
                        if (meta.getDisplayName().equalsIgnoreCase(Lang.CONFIGÙGUIÙTIMERSÙGAME_TIME.get())) {
                            if (event.getAction() == InventoryAction.PICKUP_ALL) {
                                this.settings.setGameTime(this.settings.getGameTime() + 5);
                            } else if (event.getAction() == InventoryAction.PICKUP_HALF) {
                                this.settings.setGameTime(this.settings.getGameTime() - 5);
                            } else if (event.getAction() == InventoryAction.CLONE_STACK){
                                this.settings.setGameTime(this.settings.getGameTime() + 5);
                            }
                        }
                        if (meta.getDisplayName().equalsIgnoreCase(Lang.CONFIGÙGUIÙTIMERSÙCOMBAT_TIME.get())) {
                            if (event.getAction() == InventoryAction.PICKUP_ALL) {
                                this.settings.setCombatTime(this.settings.getCombatTime() + 1);
                            } else if (event.getAction() == InventoryAction.PICKUP_HALF) {
                                this.settings.setCombatTime(this.settings.getCombatTime() - 1);
                            } else if (event.getAction() == InventoryAction.CLONE_STACK){
                                this.settings.setCombatTime(this.settings.getCombatTime() + 5);
                            }
                        }
                        if (meta.getDisplayName().equalsIgnoreCase(Lang.CONFIGÙGUIÙTIMERSÙGLOWING_TIME.get())) {
                            if (event.getAction() == InventoryAction.PICKUP_ALL) {
                                this.settings.setGlowingTime(this.settings.getGlowingTime() + 1);
                            } else if (event.getAction() == InventoryAction.PICKUP_HALF) {
                                this.settings.setGlowingTime(this.settings.getGlowingTime() - 1);
                            } else if (event.getAction() == InventoryAction.CLONE_STACK){
                                this.settings.setGlowingTime(this.settings.getGlowingTime() + 5);
                            }
                        }

                        if (meta.getDisplayName().equalsIgnoreCase(Lang.CONFIGÙGUIÙITEM_BACK.get())) {
                            player.closeInventory();
                            main.getGuiManager().getSettings().open(player);
                        }

                        this.updateInventory();
                        event.setCancelled(true);
                        return;
                    }
                }
            } else {
                player.sendMessage(main.getPrefix()+ Lang.PLUGINÙNO_PERMISSION.get());
            }
        }
    }

    @Override
    public void setMenuItems(){
        Inventory menu = this.inventory;
        //GameTime Item
        menu.setItem(1, ItemsManager.buildItemstack(new ItemStack(Material.WATCH, 1), Lang.CONFIGÙGUIÙTIMERSÙGAME_TIME.get(), new ArrayList<>(Collections.singletonList(String.valueOf(
                new SimpleDateFormat("mm:ss").format((this.settings.getGameTime()*60)*1000)
        )))));
        //CombatTime Item
        menu.setItem(4, ItemsManager.buildItemstack(new ItemStack(Material.CHEST, 1), Lang.CONFIGÙGUIÙTIMERSÙCOMBAT_TIME.get(), new ArrayList<>(Collections.singletonList(String.valueOf(
                new SimpleDateFormat("mm:ss").format((this.settings.getCombatTime()*60)*1000)
        )))));
        //GlowingTime Item
        menu.setItem(7, ItemsManager.buildItemstack(new ItemStack(Material.DIAMOND_SWORD, 1), Lang.CONFIGÙGUIÙTIMERSÙGLOWING_TIME.get(), new ArrayList<>(Collections.singletonList(String.valueOf(
                new SimpleDateFormat("mm:ss").format((this.settings.getGlowingTime()*60)*1000)
        )))));

        //Return Item
        menu.setItem(8, ItemsManager.buildItemstack(new ItemStack(Material.COMPASS, 1), Lang.CONFIGÙGUIÙITEM_BACK.get(), new ArrayList<>()));
    }

    @Override
    public ItemStack getItem(){
        return ItemsManager.buildItemstack(new ItemStack(Material.WATCH, 1), Lang.CONFIGÙGUIÙTIMERSÙITEM.get(), new ArrayList<>());
    }
}