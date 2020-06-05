package fr.kinj14.blockedincombat.SettingsGUI.Configs;

import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Manager.ItemsManager;
import fr.kinj14.blockedincombat.Manager.PlayerManager;
import fr.kinj14.blockedincombat.SettingsGUI.GUI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;

public class GUI_ConfigsChoice extends GUI {
    public GUI_ConfigsChoice(String permission) {
        super("blockedincombat.EditProfiles");
    }

    @Override
    public String getMenuName() {
        return Lang.CONFIGÙGUIÙCONFIGS_CHOICEÙNAME.get();
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

                        String InventoryName = event.getInventory().getName();
                        if (meta.getDisplayName().equalsIgnoreCase(Lang.CONFIGÙGUIÙCONFIGS_CHOICEÙREPLACE.get())) {
                            player.closeInventory();
                            main.getGuiManager().getAnvil().setName(InventoryName);
                            main.getGuiManager().getAnvil().open(player);
                        }
                        if (meta.getDisplayName().equalsIgnoreCase(Lang.CONFIGÙGUIÙCONFIGS_CHOICEÙDELETE.get())) {
                            player.closeInventory();
                            if(main.getSettingsManager().deleteConfig(InventoryName, true)){
                                main.logger.info("["+main.getPrefixDefault()+"] "+player.getName()+"("+player.getUniqueId().toString()+") deleted profile "+InventoryName+".json");
                            }
                        }

                        if (meta.getDisplayName().equalsIgnoreCase(Lang.CONFIGÙGUIÙITEM_BACK.get())) {
                            player.closeInventory();
                            main.getGuiManager().getSettings().open(player);
                        }

                        event.setCancelled(true);
                        return;
                    }
                }
            } else {
                player.sendMessage(main.getPrefix()+ Lang.PLUGINÙNO_PERMISSION.get());
            }
        }
    }

    public void open(Player player, String name) {
        final Inventory inv = Bukkit.createInventory(this, getSlots(), name);

        final ItemStack replaceItem = ItemsManager.buildItemstack(new ItemStack(Material.WOOL, 1, (short)1), Lang.CONFIGÙGUIÙCONFIGS_CHOICEÙREPLACE.get(), new ArrayList<>());
        inv.setItem(11, replaceItem);

        final ItemStack deleteItem = ItemsManager.buildItemstack(new ItemStack(Material.WOOL, 1, (short)14), Lang.CONFIGÙGUIÙCONFIGS_CHOICEÙDELETE.get(), new ArrayList<>());
        inv.setItem(15, deleteItem);

        inv.setItem(26, ItemsManager.buildItemstack(new ItemStack(Material.COMPASS, 1), Lang.CONFIGÙGUIÙITEM_BACK.get(), new ArrayList<>()));
        player.openInventory(inv);
    }

    @Override
    public void setMenuItems(){}

    @Override
    public ItemStack getItem(){return null;}
}