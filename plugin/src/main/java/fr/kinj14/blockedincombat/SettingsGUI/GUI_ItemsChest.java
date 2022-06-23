package fr.kinj14.blockedincombat.SettingsGUI;

import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Manager.ItemsManager;
import fr.kinj14.blockedincombat.Manager.PlayerManager;
import fr.kinj14.blockedincombat.Manager.Settings.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class GUI_ItemsChest extends GUI {
    public GUI_ItemsChest(String permission) {
        super("blockedincombat.ChangeSettings");
    }

    @Override
    public String getMenuName() {
        return Lang.CONFIGÙGUIÙITEMSÙNAME.get();
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
                        for(Map.Entry<Material, Boolean> it : this.settings.getItems().entrySet()){
                            if (meta.getDisplayName().equalsIgnoreCase(it.getKey().name())) {
                                this.settings.getItems().replace(it.getKey(), !it.getValue());
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

        for(Map.Entry<Material, Boolean> item : main.getSettingsManager().getConfig().getItems().entrySet()){
            ItemStack itemItem = ItemsManager.buildItemstack(new ItemStack(item.getKey(), 1), item.getKey().name(), new ArrayList<>(Collections.singletonList(String.valueOf(
                    SettingsManager.booleanToString(item.getValue())
            ))));

            final ItemMeta im = itemItem.getItemMeta();
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemItem.setItemMeta(im);
            if(item.getValue()){
                itemItem.addUnsafeEnchantment(Enchantment.DURABILITY, 2);
            }

            menu.addItem(itemItem);
        }

        //Return Item
        menu.setItem(26, ItemsManager.buildItemstack(new ItemStack(Material.COMPASS, 1), Lang.CONFIGÙGUIÙITEM_BACK.get(), new ArrayList<>()));
    }

    @Override
    public ItemStack getItem(){
        return ItemsManager.buildItemstack(new ItemStack(Material.CHEST, 1), Lang.CONFIGÙGUIÙITEMSÙITEM.get(), new ArrayList<>());
    }
}