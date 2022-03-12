package fr.kinj14.blockedincombat.SettingsGUI;

import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Enums.Teams;
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
import java.util.Arrays;
import java.util.Collections;

public class GUI_Settings extends GUI {
    public GUI_Settings(String permission) {
        super("blockedincombat.ChangeSettings");
    }

    @Override
    public String getMenuName() {
        return Lang.CONFIG_GUI_SETTINGS_NAME.get();
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
                        if(meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_SETTINGS_TABHEALTH.get())){
                            main.getSettingsManager().setTabHealth(!this.settings.getTabHealth());
                        }
                        if(meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_SETTINGS_BONUSCHEST.get())){
                            this.settings.setBonusChest(!this.settings.getBonusChest());
                        }
                        if(meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_SETTINGS_SAMELOOTINCHEST.get())){
                            this.settings.setSameLootChest(!this.settings.getSameLootChest());
                        }
                        if(meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_SETTINGS_AUTOSMELT.get())){
                            this.settings.setAutoSmelt(!this.settings.getAutoSmelt());
                        }
                        if(meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_SETTINGS_AUTOSMELTFORTUNE.get())){
                            if(!this.settings.getAutoSmelt()){
                                this.settings.setAutoSmelt(true);
                            }
                            this.settings.setAutoSmeltFortune(!this.settings.getAutoSmeltFortune());
                        }
                        if(meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_SETTINGS_FRIENDLYFIRE.get())){
                            this.settings.setFriendlyFire(!this.settings.getFriendlyFire());
                        }
                        if(meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_SETTINGS_UHCMODE.get())){
                            main.getSettingsManager().setUHCMode(!this.settings.getUHCMode());
                        }
                        if(meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_SETTINGS_EXPMULTIPLIER.get())){
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
                                player.sendMessage(main.getPrefix()+ Lang.PLUGIN_NOPERMISSION.get());
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

                        if(meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_SETTINGS_RANDOMTEAM.get())){
                            player.closeInventory();
                            for(Player p : Bukkit.getOnlinePlayers()){
                                Teams t = main.getTeamsManager().getRandomTeamForPlayer(p);
                                if (t != null){
                                    main.getTeamsManager().switchPlayer(p, t);
                                }
                            }
                        }

                        main.PrepareGame();
                        this.updateInventory();
                        event.setCancelled(true);
                        return;
                    }
                }
            } else {
                player.sendMessage(main.getPrefix()+ Lang.PLUGIN_NOPERMISSION.get());
            }
        }
    }

    @Override
    public void setMenuItems(){
        Inventory menu = this.inventory;

        //TabHealth Item
        menu.setItem(
            0,
            ItemsManager.buildItemstack(
                new ItemStack(Material.GOLDEN_APPLE, 1),
                Lang.CONFIG_GUI_SETTINGS_TABHEALTH.get(),
                new ArrayList<>(Arrays.asList(
                    Lang.CONFIG_GUI_SETTINGS_DESCRIPTIONS_TABHEALTH.get(),
                    SettingsManager.booleanToString(this.settings.getTabHealth())
                ))
            )
        );

        //BonusChest Item
        menu.setItem(
            2,
            ItemsManager.buildItemstack(
                new ItemStack(Material.CHEST, 1),
                Lang.CONFIG_GUI_SETTINGS_BONUSCHEST.get(),
                new ArrayList<>(Arrays.asList(
                    Lang.CONFIG_GUI_SETTINGS_DESCRIPTIONS_BONUSCHEST.get(),
                    SettingsManager.booleanToString(this.settings.getBonusChest())
                ))
            )
        );

        //SameLootInChest Item
        menu.setItem(
                3,
                ItemsManager.buildItemstack(
                        new ItemStack(Material.ENDER_CHEST, 1),
                        Lang.CONFIG_GUI_SETTINGS_SAMELOOTINCHEST.get(),
                        new ArrayList<>(Arrays.asList(
                                Lang.CONFIG_GUI_SETTINGS_DESCRIPTIONS_SAMELOOTINCHEST.get(),
                                SettingsManager.booleanToString(this.settings.getSameLootChest())
                        ))
                )
        );

        //InstantIngot Item
        menu.setItem(
            5,
            ItemsManager.buildItemstack(
                new ItemStack(Material.IRON_INGOT, 1),
                Lang.CONFIG_GUI_SETTINGS_AUTOSMELT.get(),
                new ArrayList<>(Arrays.asList(
                    Lang.CONFIG_GUI_SETTINGS_DESCRIPTIONS_AUTOSMELT.get(),
                    SettingsManager.booleanToString(this.settings.getAutoSmelt())
                ))
            )
        );

        //InstantIngotFortune Item
        ItemStack iif_item = ItemsManager.buildItemstack(
                new ItemStack(Material.IRON_INGOT, 1),
                Lang.CONFIG_GUI_SETTINGS_AUTOSMELTFORTUNE.get(),
                new ArrayList<>(Arrays.asList(
                    Lang.CONFIG_GUI_SETTINGS_DESCRIPTIONS_AUTOSMELTFORTUNE.get(),
                    SettingsManager.booleanToString(this.settings.getAutoSmeltFortune())
                )
            )
        );
        ItemMeta im = iif_item.getItemMeta();
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        iif_item.setItemMeta(im);
        iif_item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS, 3);
        menu.setItem(6, iif_item);

        //FriendlyFire Item
        menu.setItem(
            8,
            ItemsManager.buildItemstack(
                new ItemStack(Material.IRON_SWORD, 1),
                Lang.CONFIG_GUI_SETTINGS_FRIENDLYFIRE.get(),
                new ArrayList<>(Arrays.asList(
                    Lang.CONFIG_GUI_SETTINGS_DESCRIPTIONS_FRIENDLYFIRE.get(),
                    SettingsManager.booleanToString(this.settings.getFriendlyFire())
                ))
            )
        );

        //NaturalRegen Item
        ItemStack hr_item = ItemsManager.buildItemstack(
            new ItemStack(Material.POTION, 1),
            Lang.CONFIG_GUI_SETTINGS_UHCMODE.get(),
            new ArrayList<>(Arrays.asList(
                Lang.CONFIG_GUI_SETTINGS_DESCRIPTIONS_UHCMODE.get(),
                SettingsManager.booleanToString(this.settings.getUHCMode())
            ))
        );
        ItemMeta him = hr_item.getItemMeta();
        him.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        hr_item.setItemMeta(him);
        menu.setItem(18, hr_item);

        //ExpMultiplier Item
        menu.setItem(
            20,
            ItemsManager.buildItemstack(
                new ItemStack(Material.EXPERIENCE_BOTTLE, 1),
                Lang.CONFIG_GUI_SETTINGS_EXPMULTIPLIER.get(),
                new ArrayList<>(Arrays.asList(
                    Lang.CONFIG_GUI_SETTINGS_DESCRIPTIONS_EXPMULTIPLIER.get(),
                    String.valueOf(settings.getExpMultiplier())
                ))
            )
        );

        //Configs Item
        menu.setItem(22, main.getGuiManager().getConfigs().getItem());
        //Timers Item
        menu.setItem(23, main.getGuiManager().getTimers().getItem());
        //Blocks Item
        menu.setItem(24, main.getGuiManager().getBlocks().getItem());
        //ItemsChest Item
        menu.setItem(25, main.getGuiManager().getItemsChest().getItem());

        //RandomTeam Item
        menu.setItem(
            26,
            ItemsManager.buildItemstack(
                new ItemStack(Material.WHITE_BANNER, 1),
                Lang.CONFIG_GUI_SETTINGS_RANDOMTEAM.get(),
                new ArrayList<>(Collections.singletonList(
                        Lang.CONFIG_GUI_SETTINGS_DESCRIPTIONS_RANDOMTEAM.get()
                ))
            )
        );
    }

    @Override
    public ItemStack getItem(){
        return ItemsManager.buildItemstack(new ItemStack(Material.COMPASS, 1), "&b- "+Lang.CONFIG_GUI_SETTINGS_NAME.get()+" -", new ArrayList<>());
    }
}
