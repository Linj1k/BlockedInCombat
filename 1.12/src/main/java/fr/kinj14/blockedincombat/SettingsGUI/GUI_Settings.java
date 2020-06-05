package fr.kinj14.blockedincombat.SettingsGUI;

import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Manager.ItemsManager;
import fr.kinj14.blockedincombat.Manager.PlayerManager;
import fr.kinj14.blockedincombat.Manager.Settings.SettingsManager;
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

public class GUI_Settings extends GUI {
    public GUI_Settings(String permission) {
        super("blockedincombat.ChangeSettings");
    }

    @Override
    public String getMenuName() {
        return Lang.CONFIGÙGUIÙSETTINGSÙNAME.get();
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
        if(event.getWhoClicked() instanceof Player && event.getInventory() instanceof Inventory){
            Player player = (Player) event.getWhoClicked();
            if(PlayerManager.hasPermission(player, this.permission)){
                final ItemStack item = inventory.getItem(event.getSlot());

                if(item != null){
                    if(item.hasItemMeta() && item.getItemMeta().hasDisplayName()){
                        ItemMeta meta = item.getItemMeta();
                        if(meta.getDisplayName().equalsIgnoreCase(Lang.CONFIGÙGUIÙSETTINGSÙTABHEALTH.get())){
                            main.getSettingsManager().setTabHealth(!this.settings.getTabHealth());
                        }
                        if(meta.getDisplayName().equalsIgnoreCase(Lang.CONFIGÙGUIÙSETTINGSÙBONUSCHEST.get())){
                            this.settings.setBonusChest(!this.settings.getBonusChest());
                        }
                        if(meta.getDisplayName().equalsIgnoreCase(Lang.CONFIGÙGUIÙSETTINGSÙAUTOSMELT.get())){
                            this.settings.setAutoSmelt(!this.settings.getAutoSmelt());
                        }
                        if(meta.getDisplayName().equalsIgnoreCase(Lang.CONFIGÙGUIÙSETTINGSÙAUTOSMELT_FORTUNE.get())){
                            this.settings.setAutoSmeltFortune(!this.settings.getAutoSmeltFortune());
                        }
                        if(meta.getDisplayName().equalsIgnoreCase(Lang.CONFIGÙGUIÙSETTINGSÙFRIENDLYFIRE.get())){
                            this.settings.setFriendlyFire(!this.settings.getFriendlyFire());
                        }
                        if(meta.getDisplayName().equalsIgnoreCase(Lang.CONFIGÙGUIÙSETTINGSÙUHCMODE.get())){
                            main.getSettingsManager().setUHCMode(!this.settings.getUHCMode());
                        }
                        if(meta.getDisplayName().equalsIgnoreCase(Lang.CONFIGÙGUIÙSETTINGSÙEXPMULTIPLIER.get())){
                            if(event.getAction() == InventoryAction.PICKUP_ALL){
                                this.settings.setExpMultiplier(this.settings.getExpMultiplier()+1);
                            } else if(event.getAction() == InventoryAction.PICKUP_HALF){
                                this.settings.setExpMultiplier(this.settings.getExpMultiplier()-1);
                            } else if (event.getAction() == InventoryAction.CLONE_STACK){
                                this.settings.setExpMultiplier(this.settings.getExpMultiplier() + 5);
                            }
                        }

                        if(meta.getDisplayName().equalsIgnoreCase(main.getGuiManager().getConfigs().getItemName())){
                            if(PlayerManager.hasPermission(player, "blockedincombat.LoadProfile")){
                                player.closeInventory();
                                main.getGuiManager().getConfigs().open(player);
                            } else {
                                player.sendMessage(main.getPrefix()+ Lang.PLUGINÙNO_PERMISSION.get());
                            }
                        }

                        if(meta.getDisplayName().equalsIgnoreCase(main.getGuiManager().getTimers().getItemName())){
                            player.closeInventory();
                            main.getGuiManager().getTimers().open(player);
                        }

                        if(meta.getDisplayName().equalsIgnoreCase(main.getGuiManager().getBlocks().getItemName())){
                            player.closeInventory();
                            main.getGuiManager().getBlocks().open(player);
                        }

                        if(meta.getDisplayName().equalsIgnoreCase(main.getGuiManager().getItemsChest().getItemName())){
                            player.closeInventory();
                            main.getGuiManager().getItemsChest().open(player);
                        }

                        if(meta.getDisplayName().equalsIgnoreCase(Lang.CONFIGÙGUIÙSETTINGSÙRANDOMTEAM.get())){
                            player.closeInventory();
                            for(Player p : Bukkit.getOnlinePlayers()){
                                main.getTeamsManager().addPlayerInTeam(p, main.getTeamsManager().getRandomTeamForPlayer(p));
                            }
                        }

                        main.PrepareGame();
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

        //TabHealth Item
        menu.setItem(0, ItemsManager.buildItemstack(new ItemStack(Material.GOLDEN_APPLE, 1), Lang.CONFIGÙGUIÙSETTINGSÙTABHEALTH.get(), new ArrayList<>(Collections.singletonList(
                SettingsManager.booleanToString(this.settings.getTabHealth())
        ))));

        //BonusChest Item
        menu.setItem(2, ItemsManager.buildItemstack(new ItemStack(Material.CHEST, 1), Lang.CONFIGÙGUIÙSETTINGSÙBONUSCHEST.get(), new ArrayList<>(Collections.singletonList(
                SettingsManager.booleanToString(this.settings.getBonusChest())
        ))));

        //InstantIngot Item
        menu.setItem(4, ItemsManager.buildItemstack(new ItemStack(Material.IRON_INGOT, 1), Lang.CONFIGÙGUIÙSETTINGSÙAUTOSMELT.get(), new ArrayList<>(Collections.singletonList(
                SettingsManager.booleanToString(this.settings.getAutoSmelt())
        ))));

        //InstantIngotFortune Item
        ItemStack instantingotfortune_item = ItemsManager.buildItemstack(new ItemStack(Material.IRON_INGOT, 1), Lang.CONFIGÙGUIÙSETTINGSÙAUTOSMELT_FORTUNE.get(), new ArrayList<>(Collections.singletonList(
                SettingsManager.booleanToString(this.settings.getAutoSmeltFortune())
        )));
        ItemMeta im = instantingotfortune_item.getItemMeta();
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        instantingotfortune_item.setItemMeta(im);
        instantingotfortune_item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 3);

        menu.setItem(6, instantingotfortune_item);

        //FriendlyFire Item
        menu.setItem(8, ItemsManager.buildItemstack(new ItemStack(Material.IRON_SWORD, 1), Lang.CONFIGÙGUIÙSETTINGSÙFRIENDLYFIRE.get(), new ArrayList<>(Collections.singletonList(
                SettingsManager.booleanToString(this.settings.getFriendlyFire())
        ))));

        //NaturalRegen Item
        ItemStack healthregen_item = ItemsManager.buildItemstack(new ItemStack(Material.POTION, 1), Lang.CONFIGÙGUIÙSETTINGSÙUHCMODE.get(), new ArrayList<>(Collections.singletonList(
                SettingsManager.booleanToString(this.settings.getUHCMode())
        )));
        ItemMeta him = healthregen_item.getItemMeta();
        him.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        healthregen_item.setItemMeta(him);
        menu.setItem(18, healthregen_item);

        //ExpMultiplier Item
        ItemStack ExpMultiplierItem = ItemsManager.buildItemstack(new ItemStack(Material.EXP_BOTTLE, 1), Lang.CONFIGÙGUIÙSETTINGSÙEXPMULTIPLIER.get(), new ArrayList<>(Collections.singletonList(String.valueOf(settings.getExpMultiplier()))));
        menu.setItem(20, ExpMultiplierItem);

        //Configs Item
        menu.setItem(22, main.getGuiManager().getConfigs().getItem());
        //Timers Item
        menu.setItem(23, main.getGuiManager().getTimers().getItem());
        //Blocks Item
        menu.setItem(24, main.getGuiManager().getBlocks().getItem());
        //ItemsChest Item
        menu.setItem(25, main.getGuiManager().getItemsChest().getItem());

        //RandomTeam Item
        menu.setItem(26, ItemsManager.buildItemstack(new ItemStack(Material.BANNER, 1), Lang.CONFIGÙGUIÙSETTINGSÙRANDOMTEAM.get(), new ArrayList<>()));
    }

    @Override
    public ItemStack getItem(){
        return ItemsManager.buildItemstack(new ItemStack(Material.COMPASS, 1), Lang.CONFIGÙGUIÙSETTINGSÙITEM.get(), new ArrayList<>());
    }
}
