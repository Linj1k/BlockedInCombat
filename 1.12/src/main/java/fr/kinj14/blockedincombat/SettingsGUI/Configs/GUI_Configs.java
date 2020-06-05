package fr.kinj14.blockedincombat.SettingsGUI.Configs;

import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Manager.ItemsManager;
import fr.kinj14.blockedincombat.Manager.PlayerManager;
import fr.kinj14.blockedincombat.Manager.Settings.SettingsManager;
import fr.kinj14.blockedincombat.Manager.Settings.SettingsSave;
import fr.kinj14.blockedincombat.SettingsGUI.GUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class GUI_Configs extends GUI {
    public GUI_Configs(String permission) {
        super("blockedincombat.LoadProfiles");
    }

    @Override
    public String getMenuName() {
        return Lang.CONFIGÙGUIÙCONFIGSÙNAME.get();
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
                                    //Open New GUI for delete or save
                                    if(PlayerManager.hasPermission(player, "blockedincombat.SaveProfiles")){
                                        main.getGuiManager().getConfigsChoice().open(player, settings.getName());
                                    } else {
                                        player.sendMessage(main.getPrefix()+ Lang.PLUGINÙNO_PERMISSION.get());
                                    }
                                } else if (event.getAction() == InventoryAction.PICKUP_ALL) {
                                    main.getSettingsManager().loadConfig(settings.getName());
                                }
                                break;
                            }
                        }

                        if(meta.getDisplayName().equalsIgnoreCase("Save Profile")){
                            if(PlayerManager.hasPermission(player,"blockedincombat.SaveProfiles")) {
                                player.closeInventory();
                                main.getGuiManager().getAnvil().setName("new");
                                main.getGuiManager().getAnvil().open(player);
                            } else {
                                player.sendMessage(main.getPrefix()+ Lang.PLUGINÙNO_PERMISSION.get());
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
            }
        }
    }

    @Override
    public void setMenuItems(){
        Inventory menu = this.inventory;

        for(Map.Entry<String, SettingsSave> setting : main.getSettingsManager().getSaves().entrySet()){
            final SettingsSave settings = setting.getValue();
            ArrayList<String> description = new ArrayList<>();
            description.add(Lang.CONFIGÙGUIÙSETTINGSÙTABHEALTH.get()+": "+settings.getTabHealth());
            description.add(Lang.CONFIGÙGUIÙSETTINGSÙBONUSCHEST.get()+": "+settings.getBonusChest());
            description.add(Lang.CONFIGÙGUIÙSETTINGSÙAUTOSMELT.get()+": "+settings.getAutoSmelt());
            description.add(Lang.CONFIGÙGUIÙSETTINGSÙAUTOSMELT_FORTUNE.get()+": "+settings.getAutoSmeltFortune());
            description.add(Lang.CONFIGÙGUIÙSETTINGSÙUHCMODE.get()+": "+settings.getUHCMode());
            description.add(Lang.CONFIGÙGUIÙSETTINGSÙFRIENDLYFIRE.get()+": "+settings.getFriendlyFire());
            description.add(Lang.CONFIGÙGUIÙTIMERSÙGAME_TIME.get()+": "+settings.getGameTime());
            description.add(Lang.CONFIGÙGUIÙTIMERSÙCOMBAT_TIME.get()+": "+settings.getCombatTime());
            description.add(Lang.CONFIGÙGUIÙTIMERSÙGLOWING_TIME.get()+": "+settings.getGlowingTime());
            description.add(Lang.CONFIGÙGUIÙSETTINGSÙEXPMULTIPLIER.get()+": "+settings.getExpMultiplier());
            ItemStack configItem = ItemsManager.buildItemstack(new ItemStack(Material.NAME_TAG, 1), setting.getKey(), description);

            final ItemMeta im = configItem.getItemMeta();
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            configItem.setItemMeta(im);

            menu.addItem(configItem);
        }

        menu.setItem(25, ItemsManager.buildItemstack(new ItemStack(Material.BOOK, 1), Lang.CONFIGÙGUIÙCONFIGSÙSAVE_PROFILE.get(), new ArrayList<>()));
        //Return Item
        menu.setItem(26, ItemsManager.buildItemstack(new ItemStack(Material.COMPASS, 1), Lang.CONFIGÙGUIÙITEM_BACK.get(), new ArrayList<>()));
    }

    @Override
    public ItemStack getItem(){
        return ItemsManager.buildItemstack(new ItemStack(Material.NAME_TAG, 1), Lang.CONFIGÙGUIÙCONFIGSÙITEM.get(), new ArrayList<>());
    }
}