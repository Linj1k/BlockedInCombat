package fr.kinj14.blockedincombat.SettingsGUI.Configs;

import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Main;
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
import java.util.Objects;

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
        if(event.getWhoClicked() instanceof Player) {
            event.getInventory();
            Player player = (Player) event.getWhoClicked();
            if (PlayerManager.hasPermission(player, this.permission)) {
                final ItemStack item = event.getCurrentItem();

                if (item != null) {
                    if (item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasDisplayName()) {
                        ItemMeta meta = item.getItemMeta();

                        String InventoryName = event.getView().getTitle();
                        if (PlayerManager.hasPermission(player, "blockedincombat.EditProfiles")) {
                            if (meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_CONFIGS_PRESETSAVE.get())) {
                                SettingsSave settings = main.getSettingsManager().getConfig();
                                settings.setName(InventoryName);

                                if (main.getSettingsManager().save(settings)) {
                                    player.sendMessage(Main.getPrefix() + Lang.CONFIG_GUI_CONFIGS_PRESETSAVED.get());
                                    main.getLogger().info("[" + Main.PrefixDefault + "] " + player.getName() + "(" + player.getUniqueId() + ") created the profile " + InventoryName + ".json");
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
                                if (main.getSettingsManager().deleteConfig(InventoryName, true)) {
                                    player.sendMessage(Main.getPrefix() + Lang.CONFIG_GUI_CONFIGSCHOICE_PRESETDELETED.get());
                                    main.getLogger().info("[" + Main.PrefixDefault + "] " + player.getName() + "(" + player.getUniqueId() + ") deleted profile " + InventoryName + ".json");
                                } else {
                                    player.sendMessage(Main.getPrefix() + Lang.CONFIG_GUI_CONFIGSCHOICE_PRESETDELETEFAILED.get());
                                }
                            }
                        } else {
                            player.sendMessage(Main.getPrefix() + Lang.PLUGIN_NOPERMISSION.get());
                        }

                        if (meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_ITEMBACK.get())) {
                            player.closeInventory();
                            main.getGuiManager().getSettings().open(player);
                        }

                        event.setCancelled(true);
                    }
                }
            } else {
                player.sendMessage(Main.getPrefix() + Lang.PLUGIN_NOPERMISSION.get());
            }
        }
    }

    public void open(Player player, String name) {
        final Inventory inv = Bukkit.createInventory(this, getSlots(), name);

        //Save
        inv.setItem(
            10,
            ItemsManager.buildItemstack(new ItemStack(Material.GREEN_WOOL, 1), Lang.CONFIG_GUI_CONFIGS_PRESETSAVE.get(), new ArrayList<>())
        );

        //Rename
        inv.setItem(
            13,
            ItemsManager.buildItemstack(new ItemStack(Material.WHITE_WOOL, 1), Lang.CONFIG_GUI_CONFIGSCHOICE_RENAME.get(), new ArrayList<>())
        );

        //Delete
        inv.setItem(
            16,
            ItemsManager.buildItemstack(new ItemStack(Material.RED_WOOL, 1), Lang.CONFIG_GUI_CONFIGSCHOICE_DELETE.get(), new ArrayList<>())
        );

        //Back
        inv.setItem(
            26,
            ItemsManager.buildItemstack(new ItemStack(Material.COMPASS, 1), Lang.CONFIG_GUI_ITEMBACK.get(), new ArrayList<>())
        );

        player.openInventory(inv);
    }

    @Override
    public ItemStack getItem(){return null;}
}