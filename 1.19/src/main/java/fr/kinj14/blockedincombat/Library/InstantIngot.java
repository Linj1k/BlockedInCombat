package fr.kinj14.blockedincombat.Library;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Kinj14
 * Allows the replacement of ores by ingots
 *
 */
public class InstantIngot {
    public static void OreToIngot(BlockBreakEvent event, boolean isactivate, boolean fortune, Material ore, Material ingot) {
        Block block = event.getBlock();
        Player p = event.getPlayer();
        if(isactivate && block.getType().equals(ore)) {
            if (p.getInventory().getItemInMainHand().getType() == Material.STONE_PICKAXE || p.getInventory().getItemInMainHand().getType() == Material.IRON_PICKAXE || p.getInventory().getItemInMainHand().getType() == Material.DIAMOND_PICKAXE) {
                if (p.getInventory().getItemInMainHand().hasItemMeta() && p.getInventory().getItemInMainHand().getItemMeta().hasEnchant(Enchantment.SILK_TOUCH)) {
                    return;
                }
                int ItemCount = 1;
                if (fortune && p.getInventory().getItemInMainHand().containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS)) {
                    if (p.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) == 1) {
                        ItemCount = 2;
                    } else if (p.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) == 2) {
                        ItemCount = 3;
                    } else if (p.getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS) == 3) {
                        ItemCount = 4;
                    }
                }
                Location blockLocation = block.getLocation().add(0.15,0.15,0.15);
                block.getWorld().dropItemNaturally(blockLocation, new ItemStack(ingot, ItemCount));
                block.setType(Material.AIR);
                ExperienceOrb orb = event.getPlayer().getWorld().spawn(blockLocation, ExperienceOrb.class);
                orb.setExperience(event.getExpToDrop());

                event.setCancelled(true);
            }
        }
    }
}
