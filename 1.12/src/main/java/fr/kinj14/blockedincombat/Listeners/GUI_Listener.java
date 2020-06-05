package fr.kinj14.blockedincombat.Listeners;

import fr.kinj14.blockedincombat.SettingsGUI.GUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class GUI_Listener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        InventoryHolder holder = event.getInventory().getHolder();
        if (holder instanceof GUI) {
            event.setCancelled(true);
            if (event.getCurrentItem() == null) {
                return;
            }
            ((GUI) holder).handleMenu(event);
        }
    }
}
