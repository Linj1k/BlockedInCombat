package fr.kinj14.blockedincombat.Manager;

import fr.kinj14.blockedincombat.Enums.GameState;
import fr.kinj14.blockedincombat.Enums.Teams;
import fr.kinj14.blockedincombat.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerManager {
    protected final Main main = Main.getInstance();

    public static void setupLobby(Player player){
        final Main main = Main.getInstance();

        player.setBedSpawnLocation(main.lobby, true);
        if(!GameState.isState(GameState.WAITING)){
            player.setGameMode(GameMode.SPECTATOR);
            main.getTeamsManager().addPlayerInTeam(player, Teams.spectator);

            Player randomPlayer = main.getTeamsManager().getRandomPlayerInTeam(main.getTeamsManager().getRandomTeam());
            if(randomPlayer != null){
                player.teleport(randomPlayer.getLocation());
            } else {
                player.teleport(new Location(Bukkit.getWorld(main.WorldName), 1040, 4, 1040));
            }
        } else {
            // Spawn
            player.teleport(main.lobby);
            player.setGameMode(GameMode.ADVENTURE);

            clear(player);
            main.getItemsManager().LobbyItems(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 255, false, false), true);
        }

        main.getScoreboardManager().updatePlayersAll(String.valueOf(Bukkit.getOnlinePlayers().size()));

        main.getScoreboardManager().setup(player);
    }

    public static boolean hasMovedOneBlock(Location from, Location to){
        final Location bFrom = from.getWorld().getBlockAt(from).getLocation();
        final Location bTo = to.getWorld().getBlockAt(to).getLocation();
        return bFrom != bTo;
    }

    public static void clear(Player player){
        for(PotionEffect effect : player.getActivePotionEffects())
        {
            player.removePotionEffect(effect.getType());
        }
        player.setFireTicks(0);
        player.setFoodLevel(20);
        player.setHealth(20);
        player.setExp(0f);
        //clear player armor
        ItemStack[] emptyArmor = new ItemStack[4];
        for(int i=0 ; i<emptyArmor.length ; i++){
            emptyArmor[i] = new ItemStack(Material.AIR);
        }
        player.getInventory().setArmorContents(emptyArmor);
        player.getInventory().clear();
    }

    public static void teleportInArena(Player player){
        Main main = Main.getInstance();
        Player randomPlayer = main.getTeamsManager().getRandomPlayerInTeam(main.getTeamsManager().getRandomTeam());
        if(randomPlayer != null){
            player.teleport(randomPlayer.getLocation());
        } else {
            player.teleport(new Location(Bukkit.getWorld(main.WorldName), 1040, 4, 1040));
        }
    }

    public static boolean hasMovedOneBLock(Location from, Location to){
        final Location bFrom = from.getWorld().getBlockAt(from).getLocation();
        final Location bTo = to.getWorld().getBlockAt(to).getLocation();
        return bFrom != bTo;
    }

    public static boolean hasPermission(CommandSender sender, String permission){
        if(sender.hasPermission("blockedincombat.*") || sender.hasPermission(permission)){
            return true;
        }
        return false;
    }
}
