package fr.kinj14.blockedincombat.SettingsGUI;

import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Main;
import fr.kinj14.blockedincombat.Manager.ItemsManager;
import fr.kinj14.blockedincombat.Manager.PlayerManager;
import fr.kinj14.blockedincombat.Manager.Settings.SettingsManager;
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
import java.util.Objects;

public class GUI_Timers extends GUI {
    public GUI_Timers(String permission) {
        super("blockedincombat.ChangeSettings");
    }

    @Override
    public String getMenuName() {
        return Lang.CONFIG_GUI_TIMERS_NAME.get();
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
        if(event.getWhoClicked() instanceof Player) {
            event.getInventory();
            Player player = (Player) event.getWhoClicked();
            if (PlayerManager.hasPermission(player, this.permission)) {
                final ItemStack item = inventory.getItem(event.getSlot());

                if (item != null) {
                    if (item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasDisplayName()) {
                        ItemMeta meta = item.getItemMeta();
                        if (meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_TIMERS_ARENADELAY.get())) {
                            main.getSettingsManager().setArenaDelay(!this.settings.getArenaDelay());
                        }
                        if (meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_TIMERS_GAMETIME.get())) {
                            if (event.getAction() == InventoryAction.PICKUP_ALL) {
                                this.settings.setGameTime(this.settings.getGameTime() + 5);
                            } else if (event.getAction() == InventoryAction.PICKUP_HALF) {
                                this.settings.setGameTime(this.settings.getGameTime() - 5);
                            } else if (event.getAction() == InventoryAction.CLONE_STACK) {
                                this.settings.setGameTime(this.settings.getGameTime() + 5);
                            }
                        }
                        if (meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_TIMERS_COMBATTIME.get())) {
                            if (event.getAction() == InventoryAction.PICKUP_ALL) {
                                this.settings.setCombatTime(this.settings.getCombatTime() + 1);
                            } else if (event.getAction() == InventoryAction.PICKUP_HALF) {
                                this.settings.setCombatTime(this.settings.getCombatTime() - 1);
                            } else if (event.getAction() == InventoryAction.CLONE_STACK) {
                                this.settings.setCombatTime(this.settings.getCombatTime() + 5);
                            }
                        }
                        if (meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_TIMERS_GLOWINGTIME.get())) {
                            if (event.getAction() == InventoryAction.PICKUP_ALL) {
                                this.settings.setGlowingTime(this.settings.getGlowingTime() + 1);
                            } else if (event.getAction() == InventoryAction.PICKUP_HALF) {
                                this.settings.setGlowingTime(this.settings.getGlowingTime() - 1);
                            } else if (event.getAction() == InventoryAction.CLONE_STACK) {
                                this.settings.setGlowingTime(this.settings.getGlowingTime() + 5);
                            }
                        }

                        if (meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_ITEMBACK.get())) {
                            player.closeInventory();
                            main.getGuiManager().getSettings().open(player);

                            event.setCancelled(true);
                            return;
                        }

                        this.updateInventory();
                        event.setCancelled(true);
                    }
                }
            } else {
                player.sendMessage(Main.getPrefix() + Lang.PLUGIN_NOPERMISSION.get());
            }
        }
    }

    @Override
    public void setMenuItems(){
        Inventory menu = this.inventory;
        //ArenaDelay Item
        ArrayList<String> arenaItemDesc = new ArrayList<>();
        arenaItemDesc.add(Lang.CONFIG_GUI_TIMERS_ARENADELAYDESCRIPTION.get());
        arenaItemDesc.add(SettingsManager.booleanToString(this.settings.getArenaDelay()));

        menu.setItem(0, ItemsManager.buildItemstack(new ItemStack(Material.BEDROCK, 1), Lang.CONFIG_GUI_TIMERS_ARENADELAY.get(), arenaItemDesc));

        //GameTime Item
        menu.setItem(3, ItemsManager.buildItemstack(new ItemStack(Material.CLOCK, 1), Lang.CONFIG_GUI_TIMERS_GAMETIME.get(), new ArrayList<>(Collections.singletonList(String.valueOf(
                new SimpleDateFormat("mm:ss").format((this.settings.getGameTime()*60)*1000)
        )))));
        //CombatTime Item
        menu.setItem(4, ItemsManager.buildItemstack(new ItemStack(Material.DIAMOND_SWORD, 1), Lang.CONFIG_GUI_TIMERS_COMBATTIME.get(), new ArrayList<>(Collections.singletonList(String.valueOf(
                new SimpleDateFormat("mm:ss").format((this.settings.getCombatTime()*60)*1000)
        )))));
        //GlowingTime Item
        menu.setItem(5, ItemsManager.buildItemstack(new ItemStack(Material.SPECTRAL_ARROW, 1), Lang.CONFIG_GUI_TIMERS_GLOWINGTIME.get(), new ArrayList<>(Collections.singletonList(String.valueOf(
                new SimpleDateFormat("mm:ss").format((this.settings.getGlowingTime()*60)*1000)
        )))));

        //Return Item
        menu.setItem(8, ItemsManager.buildItemstack(new ItemStack(Material.COMPASS, 1), Lang.CONFIG_GUI_ITEMBACK.get(), new ArrayList<>()));
    }

    @Override
    public ItemStack getItem(){
        return ItemsManager.buildItemstack(new ItemStack(Material.CLOCK, 1), Lang.CONFIG_GUI_TIMERS_ITEM.get(), new ArrayList<>());
    }
}