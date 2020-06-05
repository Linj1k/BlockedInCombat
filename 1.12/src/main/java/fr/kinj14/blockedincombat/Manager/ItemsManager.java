package fr.kinj14.blockedincombat.Manager;

import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Enums.Teams;
import fr.kinj14.blockedincombat.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemsManager {
    protected final Main main = Main.getInstance();

    public static ItemStack buildItemstack(ItemStack is, String displayName, ArrayList<String> description){
        final ItemMeta im = is.getItemMeta();

        //SetItemMeta
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));
        if(!description.isEmpty()){
            for(String de : description){
                description.set(description.indexOf(de), ChatColor.translateAlternateColorCodes('&', de));
            }
            im.setLore(description);
        }
        im.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS, ItemFlag.HIDE_UNBREAKABLE);

        is.setItemMeta(im);

        return is;
    }

    public static ItemStack changeName(ItemStack is, String displayName){
        final ItemMeta im = is.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

        is.setItemMeta(im);

        return is;
    }

    public static ItemStack getReturnToLobbyItem(){
        return buildItemstack(new ItemStack(Material.NETHER_STAR, 1), Lang.PLUGINÙLOBBY_ITEM.get(), new ArrayList<>());
    }

    public void LobbyItems(Player player){
        //Blue
        player.getInventory().addItem(Teams.blue.getItem());

        //Red
        player.getInventory().addItem(Teams.red.getItem());

        //Yellow
        player.getInventory().addItem(Teams.yellow.getItem());

        //Green
        player.getInventory().addItem(Teams.green.getItem());

        //Spectator
        player.getInventory().addItem(Teams.spectator.getItem());

        int SettingsSlot = 8;
        if(main.BungeeCord){
            player.getInventory().setItem(8, getReturnToLobbyItem());
            SettingsSlot = 7;
        }

        if(PlayerManager.hasPermission(player, "blockedincombat.ChangeSettings")){
            player.getInventory().setItem(SettingsSlot, main.getGuiManager().getSettings().getItem());
        }

        player.updateInventory();
    }

    public static void RemoveAllItems(){
        World world = Bukkit.getWorld(Main.getInstance().WorldName);

        for(Entity ent : world.getEntities()){
            if(ent instanceof Item){
                ent.remove();
            }
        }
    }
}
