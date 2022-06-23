package fr.kinj14.blockedincombat.Library;

import fr.kinj14.blockedincombat.Main;
import org.bukkit.Material;
import org.bukkit.enchantments.EnchantmentTarget;
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
    protected final Main main = Main.getInstance();

    final List<String> ItemTool = new ArrayList<>();
    final List<Material> WoolType = new ArrayList<>();

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

        for (Material m : Material.values()) {
            if (m.toString().contains("WOOL")){
                WoolType.add(m);
            }
        }
    }

    public boolean ItemIsConsumable(ItemStack item){
        return item.getType().isEdible();
    }

    public boolean ItemIsTool(ItemStack item){
        Material mat = item.getType();
        if (EnchantmentTarget.TOOL.includes(mat) || EnchantmentTarget.WEAPON.includes(mat) || EnchantmentTarget.ARMOR.includes(mat) || EnchantmentTarget.BOW.includes(mat)) {
            return true;
        } else {
            for (String i : ItemTool) {
                if (mat.name().contains(i)) {
                    return true;
                }
            }
            return false;
        }
    }

    public boolean BlockIsWool(Material m){
        return WoolType.contains(m) || m.toString().contains("WOOL");
    }
}
