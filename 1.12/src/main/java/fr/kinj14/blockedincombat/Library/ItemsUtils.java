package fr.kinj14.blockedincombat.Library;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Kinj14
 * Allows easy management of Items
 *
 */
public class ItemsUtils {
    final List<String> ItemTool = new ArrayList<>();
    final List<String> ItemConsumable = new ArrayList<>();

    public ItemsUtils() {
        ItemTool.add("PICKAXE");
        ItemTool.add("SWORD");
        ItemTool.add("AXE");
        ItemTool.add("HOE");
        ItemTool.add("SPADE");
        ItemTool.add("CHESTPLATE");
        ItemTool.add("BOOTS");
        ItemTool.add("HELMET");
        ItemTool.add("LEGGINGS");
        ItemTool.add("SADDLE");
        ItemTool.add("BARDING");
        ItemTool.add("BUCKET");
        ItemTool.add("COMPASS");
        ItemTool.add("BOW");
        ItemTool.add("WATCH");
        ItemTool.add("BOWL");

        ItemConsumable.add("APPLE");
        ItemConsumable.add("POTATO");
        ItemConsumable.add("BEETROOT");
        ItemConsumable.add("BREAD");
        ItemConsumable.add("CAKE");
        ItemConsumable.add("CARROT");
        ItemConsumable.add("CHORUS");
        ItemConsumable.add("COOKED");
        ItemConsumable.add("COOKIE");
        ItemConsumable.add("MELON");
        ItemConsumable.add("MUSHROOM_SOUP");
        ItemConsumable.add("RAW");
        ItemConsumable.add("RABBIT_STEW");
        ItemConsumable.add("ROTTEN_FLESH");
        ItemConsumable.add("SPIDER_EYE");
    }

    public boolean ItemIsConsumable(ItemStack item){
        for(String i : ItemConsumable){
            if(item.getType().name().contains(i)){
                return true;
            }
        }
        return false;
    }

    public boolean ItemIsTool(ItemStack item){
        for(String i : ItemTool){
            if(item.getType().name().contains(i)){
                return true;
            }
        }
        return false;
    }
}
