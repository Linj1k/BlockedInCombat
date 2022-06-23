package fr.kinj14.blockedincombat.SettingsGUI;

import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Main;
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
import java.util.Objects;

public class GUI_ItemsChest extends GUI {
    public GUI_ItemsChest(String permission) {
        super("blockedincombat.ChangeSettings");
    }

    @Override
    public String getMenuName() {
        return Lang.CONFIG_GUI_ITEMS_NAME.get();
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
        if(event.getWhoClicked() instanceof Player) {
            event.getInventory();
            Player player = (Player) event.getWhoClicked();
            if (PlayerManager.hasPermission(player, this.permission)) {
                final ItemStack item = inventory.getItem(event.getSlot());

                if (item != null) {
                    if (item.hasItemMeta() && Objects.requireNonNull(item.getItemMeta()).hasDisplayName()) {
                        ItemMeta meta = item.getItemMeta();

                        if (meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_ITEMBACK.get())) {
                            player.closeInventory();
                            main.getGuiManager().getSettings().open(player);

                            return;
                        }
                    }

                    for (Map.Entry<Material, Boolean> it : this.settings.getItems().entrySet()) {
                        if (item.getType().name().equalsIgnoreCase(it.getKey().name())) {
                            this.settings.getItems().replace(it.getKey(), !it.getValue());
                            break;
                        }
                    }

                    this.updateInventory();
                }
            } else {
                player.sendMessage(Main.getPrefix() + Lang.PLUGIN_NOPERMISSION.get());
            }

            event.setCancelled(true);
        }
    }

    @Override
    public void setMenuItems(){
        Inventory menu = this.inventory;

        for(Map.Entry<Material, Boolean> item : main.getSettingsManager().getConfig().getItems().entrySet()){
            ItemStack itemItem = ItemsManager.buildItemstack(new ItemStack(item.getKey(), 1), "null", new ArrayList<>(Collections.singletonList(String.valueOf(
                    SettingsManager.booleanToString(item.getValue())
            ))));

            final ItemMeta im = itemItem.getItemMeta();
            assert im != null;
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            itemItem.setItemMeta(im);
            if(item.getValue()){
                itemItem.addUnsafeEnchantment(Enchantment.DURABILITY, 2);
            }

            menu.addItem(itemItem);
        }

        //Return Item
        menu.setItem(26, ItemsManager.buildItemstack(new ItemStack(Material.COMPASS, 1), Lang.CONFIG_GUI_ITEMBACK.get(), new ArrayList<>()));
    }

    @Override
    public ItemStack getItem(){
        return ItemsManager.buildItemstack(new ItemStack(Material.CHEST, 1), Lang.CONFIG_GUI_ITEMS_ITEM.get(), new ArrayList<>());
    }
}