package fr.kinj14.blockedincombat.SettingsGUI.Configs;

import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Manager.ItemsManager;
import fr.kinj14.blockedincombat.Manager.PlayerManager;
import fr.kinj14.blockedincombat.Manager.Settings.SettingsSave;
import fr.kinj14.blockedincombat.SettingsGUI.GUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class GUI_ConfigsChoice extends GUI {
    public GUI_ConfigsChoice(String permission) {
        super("blockedincombat.EditProfiles");
    }

    @Override
    public String getMenuName() {
        return Lang.CONFIG_GUI_CONFIGSCHOICE_NAME.get();
    }

    @Override
    public int getSlots() {
        return InventoryType.CHEST.getDefaultSize();
    }

    @Override
    public void create() {}

    @Override
    public void handleMenu(InventoryClickEvent event) {
        if(event.getWhoClicked() instanceof Player && event.getInventory() instanceof Inventory) {
            Player player = (Player) event.getWhoClicked();
            if (PlayerManager.hasPermission(player, this.permission)) {
                final ItemStack item = event.getCurrentItem();

                if (item != null) {
                    if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                        ItemMeta meta = item.getItemMeta();

                        String InventoryName = event.getView().getTitle();
                        if(PlayerManager.hasPermission(player, "blockedincombat.EditProfiles")){
                            if (meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_CONFIGS_SAVEPROFILE.get())) {
                                SettingsSave settings = main.getSettingsManager().getConfig();
                                settings.setName(InventoryName);

                                if(main.getSettingsManager().save(settings)){
                                    player.sendMessage(main.getPrefix()+ Lang.CONFIG_GUI_CONFIGS_PRESETSAVED.get());
                                    main.logger.info("["+main.getPrefixDefault()+"] "+player.getName()+"("+player.getUniqueId().toString()+") created the profile "+InventoryName+".json");
                                }

                                player.closeInventory();
                                main.getGuiManager().getSettings().open(player);
                            }
                            if (meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_CONFIGSCHOICE_RENAME.get())) {
                                player.closeInventory();
                                main.getGuiManager().getAnvil().setName(InventoryName);
                                main.getGuiManager().getAnvil().open(player);
                            }
                            if (meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_CONFIGSCHOICE_DELETE.get())) {
                                player.closeInventory();
                                if(main.getSettingsManager().deleteConfig(InventoryName, true)){
                                    player.sendMessage(main.getPrefix()+ Lang.CONFIG_GUI_CONFIGSCHOICE_PRESETDELETED.get());
                                    main.logger.info("["+main.getPrefixDefault()+"] "+player.getName()+"("+player.getUniqueId().toString()+") deleted profile "+InventoryName+".json");
                                } else {
                                    player.sendMessage(main.getPrefix()+ Lang.CONFIG_GUI_CONFIGSCHOICE_PRESETDELETEFAILED.get());
                                }
                            }
                        } else {
                            player.sendMessage(main.getPrefix()+ Lang.PLUGIN_NOPERMISSION.get());
                        }

                        if (meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_ITEMBACK.get())) {
                            player.closeInventory();
                            main.getGuiManager().getSettings().open(player);
                        }

                        event.setCancelled(true);
                        return;
                    }
                }
            } else {
                player.sendMessage(main.getPrefix()+ Lang.PLUGIN_NOPERMISSION.get());
            }
        }
    }

    public void open(Player player, String name) {
        final Inventory inv = Bukkit.createInventory(this, getSlots(), name);

        final ItemStack saveItem = ItemsManager.buildItemstack(new ItemStack(Material.GREEN_WOOL, 1), Lang.CONFIG_GUI_CONFIGS_SAVEPROFILE.get(), new ArrayList<>());
        inv.setItem(10, saveItem);

        final ItemStack replaceItem = ItemsManager.buildItemstack(new ItemStack(Material.WHITE_WOOL, 1), Lang.CONFIG_GUI_CONFIGSCHOICE_RENAME.get(), new ArrayList<>());
        inv.setItem(13, replaceItem);

        final ItemStack deleteItem = ItemsManager.buildItemstack(new ItemStack(Material.RED_WOOL, 1), Lang.CONFIG_GUI_CONFIGSCHOICE_DELETE.get(), new ArrayList<>());
        inv.setItem(16, deleteItem);

        inv.setItem(26, ItemsManager.buildItemstack(new ItemStack(Material.COMPASS, 1), Lang.CONFIG_GUI_ITEMBACK.get(), new ArrayList<>()));
        player.openInventory(inv);
    }

    @Override
    public void setMenuItems(){}

    @Override
    public ItemStack getItem(){return null;}
}