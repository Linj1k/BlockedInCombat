package fr.kinj14.blockedincombat.SettingsGUI;

import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Main;
import fr.kinj14.blockedincombat.Manager.ItemsManager;
import fr.kinj14.blockedincombat.Manager.Settings.SettingsSave;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public abstract class GUI implements InventoryHolder {
    protected final Main main = Main.getInstance();
    protected SettingsSave settings;
    protected Inventory inventory;
    protected String permission;

    public GUI(String permission){
        this.permission = permission;
        create();
        settings = main.getSettingsManager().getConfig();
    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract void handleMenu(InventoryClickEvent e);

    public abstract void create();

    public void open(Player player) {
        if(!this.permission.isEmpty()){
            if(!player.hasPermission(this.permission)){
                player.sendMessage(main.getPrefix()+ Lang.PLUGINÙNO_PERMISSION.get());
                return;
            }
        }

        updateInventory();
        //open the inventory for the player
        player.openInventory(getInventory());
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    public void setMenuItems(){}

    public ItemStack getItem(){
        return ItemsManager.buildItemstack(new ItemStack(Material.COMPASS, 1), ChatColor.AQUA+"- Settings -", new ArrayList<>());
    }

    public String getItemName(){
        return getItem().getItemMeta().getDisplayName();
    }

    public void updateInventory(){
        settings = main.getSettingsManager().getConfig();
        inventory.clear();
        this.setMenuItems();
        for(Entity ent : inventory.getViewers()){
            if(ent instanceof Player){
                Player player = (Player) ent;
                player.updateInventory();
            }

        }
    }
}