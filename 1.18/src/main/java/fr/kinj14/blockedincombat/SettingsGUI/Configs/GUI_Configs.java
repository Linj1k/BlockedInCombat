package fr.kinj14.blockedincombat.SettingsGUI.Configs;

import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Manager.ItemsManager;
import fr.kinj14.blockedincombat.Manager.PlayerManager;
import fr.kinj14.blockedincombat.Manager.Settings.SettingsSave;
import fr.kinj14.blockedincombat.SettingsGUI.GUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Map;

public class GUI_Configs extends GUI {
    public GUI_Configs(String permission) {
        super("blockedincombat.LoadProfiles");
    }

    @Override
    public String getMenuName() {
        return Lang.CONFIG_GUI_CONFIGS_NAME.get();
    }

    @Override
    public int getSlots() {
        return InventoryType.CHEST.getDefaultSize();
    }

    @Override
    public void create() {
        this.inventory = Bukkit.createInventory(this, InventoryType.CHEST, getMenuName());
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
                        for(Map.Entry<String, SettingsSave> setting : main.getSettingsManager().getSaves().entrySet()){
                            final SettingsSave settings = setting.getValue();
                            if (meta.getDisplayName().equalsIgnoreCase(settings.getName())) {
                                if(event.getAction() == InventoryAction.PICKUP_HALF){
                                    if(!meta.getDisplayName().equalsIgnoreCase("default")){
                                        //Open New GUI for delete or save
                                        if(PlayerManager.hasPermission(player, "blockedincombat.SaveProfiles")){
                                            main.getGuiManager().getConfigsChoice().open(player, settings.getName());

                                            event.setCancelled(true);
                                            return;
                                        } else {
                                            player.sendMessage(main.getPrefix()+ Lang.PLUGIN_NOPERMISSION.get());
                                        }
                                    } else {
                                        event.setCancelled(true);
                                        return;
                                    }
                                } else if (event.getAction() == InventoryAction.PICKUP_ALL) {
                                    if(main.getSettingsManager().loadConfig(settings.getName())){
                                        player.sendMessage(main.getPrefix()+ Lang.CONFIG_GUI_CONFIGS_PRESETLOADED.get());
                                    } else {
                                        player.sendMessage(main.getPrefix()+ Lang.CONFIG_GUI_CONFIGS_PRESETFAILED.get());
                                    }
                                }

                                player.closeInventory();
                                main.getGuiManager().getSettings().open(player);

                                event.setCancelled(true);
                                return;
                            }
                        }

                        if(meta.getDisplayName().equalsIgnoreCase("Save Profile")){
                            if(PlayerManager.hasPermission(player,"blockedincombat.SaveProfiles")) {
                                player.closeInventory();
                                main.getGuiManager().getAnvil().setName("new");
                                main.getGuiManager().getAnvil().open(player);
                            } else {
                                player.sendMessage(main.getPrefix()+ Lang.PLUGIN_NOPERMISSION.get());
                            }

                            event.setCancelled(true);
                            return;
                        }

                        if (meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_ITEMBACK.get())) {
                            player.closeInventory();
                            main.getGuiManager().getSettings().open(player);

                            event.setCancelled(true);
                            return;
                        }

                        event.setCancelled(true);
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void setMenuItems(){
        Inventory menu = this.inventory;

        for(Map.Entry<String, SettingsSave> setting : main.getSettingsManager().getSaves().entrySet()){
            final SettingsSave settings = setting.getValue();
            ArrayList<String> description = new ArrayList<>();
            description.add(Lang.CONFIG_GUI_SETTINGS_TABHEALTH.get()+": "+settings.getTabHealth());
            description.add(Lang.CONFIG_GUI_SETTINGS_BONUSCHEST.get()+": "+settings.getBonusChest());
            description.add(Lang.CONFIG_GUI_SETTINGS_SAMELOOTINCHEST.get()+": "+settings.getSameLootChest());
            description.add(Lang.CONFIG_GUI_SETTINGS_AUTOSMELT.get()+": "+settings.getAutoSmelt());
            description.add(Lang.CONFIG_GUI_SETTINGS_AUTOSMELTFORTUNE.get()+": "+settings.getAutoSmeltFortune());
            description.add(Lang.CONFIG_GUI_SETTINGS_UHCMODE.get()+": "+settings.getUHCMode());
            description.add(Lang.CONFIG_GUI_SETTINGS_FRIENDLYFIRE.get()+": "+settings.getFriendlyFire());
            description.add(Lang.CONFIG_GUI_TIMERS_ARENADELAY.get()+": "+settings.getArenaDelay());
            description.add(Lang.CONFIG_GUI_TIMERS_GAMETIME.get()+": "+settings.getGameTime());
            description.add(Lang.CONFIG_GUI_TIMERS_COMBATTIME.get()+": "+settings.getCombatTime());
            description.add(Lang.CONFIG_GUI_TIMERS_GLOWINGTIME.get()+": "+settings.getGlowingTime());
            description.add(Lang.CONFIG_GUI_SETTINGS_EXPMULTIPLIER.get()+": "+settings.getExpMultiplier());
            ItemStack configItem = ItemsManager.buildItemstack(new ItemStack(Material.NAME_TAG, 1), setting.getKey(), description);

            final ItemMeta im = configItem.getItemMeta();
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            configItem.setItemMeta(im);

            menu.addItem(configItem);
        }

        menu.setItem(25, ItemsManager.buildItemstack(new ItemStack(Material.BOOK, 1), Lang.CONFIG_GUI_CONFIGS_PRESETSAVE.get(), new ArrayList<>()));
        //Return Item
        menu.setItem(26, ItemsManager.buildItemstack(new ItemStack(Material.COMPASS, 1), Lang.CONFIG_GUI_ITEMBACK.get(), new ArrayList<>()));
    }

    @Override
    public ItemStack getItem(){
        return ItemsManager.buildItemstack(new ItemStack(Material.NAME_TAG, 1), Lang.CONFIG_GUI_CONFIGS_ITEM.get(), new ArrayList<>());
    }
}