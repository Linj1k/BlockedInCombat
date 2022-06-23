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

public class GUI_Blocks extends GUI {
    public GUI_Blocks(String permission) {
        super("blockedincombat.ChangeSettings");
    }

    @Override
    public String getMenuName() {
        return Lang.CONFIG_GUI_BLOCKS_NAME.get();
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
                    ItemMeta meta = item.getItemMeta();
                    if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                        if (meta.getDisplayName().equalsIgnoreCase(Lang.CONFIG_GUI_ITEMBACK.get())) {
                            player.closeInventory();
                            main.getGuiManager().getSettings().open(player);

                            event.setCancelled(true);
                            return;
                        }
                    }

                    for(Map.Entry<Material, Boolean> block : this.settings.getBlocks().entrySet()){
                        if (item.getType().name().equalsIgnoreCase(block.getKey().name())) {
                            this.settings.getBlocks().replace(block.getKey(), !block.getValue());
                            break;
                        }
                    }

                    this.updateInventory();
                }
            } else {
                player.sendMessage(main.getPrefix()+ Lang.PLUGIN_NOPERMISSION.get());
            }

            event.setCancelled(true);
            return;
        }
    }

    @Override
    public void setMenuItems(){
        Inventory menu = this.inventory;

        for(Map.Entry<Material, Boolean> block : main.getSettingsManager().getConfig().getBlocks().entrySet()){
            ItemStack blockItem = ItemsManager.buildItemstack(new ItemStack(block.getKey(), 1), "null", new ArrayList<>(Collections.singletonList(String.valueOf(
                    SettingsManager.booleanToString(block.getValue())
            ))));

            final ItemMeta im = blockItem.getItemMeta();
            im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            blockItem.setItemMeta(im);
            if(block.getValue()){
                blockItem.addUnsafeEnchantment(Enchantment.SILK_TOUCH, 1);
            }

            menu.addItem(blockItem);
        }

        //Return Item
        menu.setItem(26, ItemsManager.buildItemstack(new ItemStack(Material.COMPASS, 1), Lang.CONFIG_GUI_ITEMBACK.get(), new ArrayList<>()));
    }

    @Override
    public ItemStack getItem(){
        return ItemsManager.buildItemstack(new ItemStack(Material.DIRT, 1), Lang.CONFIG_GUI_BLOCKS_ITEM.get(), new ArrayList<>());
    }
}