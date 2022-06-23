package fr.kinj14.blockedincombat.Manager;

import fr.kinj14.blockedincombat.Data.PlayerData;
import fr.kinj14.blockedincombat.Enums.GameState;
import fr.kinj14.blockedincombat.Enums.Teams;
import fr.kinj14.blockedincombat.Library.ServerVersion;
import fr.kinj14.blockedincombat.Main;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class PlayerManager {
    protected final Main main = Main.getInstance();
    public static String PlayersDataPath = Main.getInstance().getDataFolder().getPath()+"/playersdata/";
    private HashMap<UUID, PlayerData> PlayersStats = new HashMap<>();

    public PlayerManager() {
        // Create PlayersDataFolder in Plugin Data Folder
        File PlayersDataFolder = new File(PlayersDataPath);
        if(!PlayersDataFolder.isDirectory()){PlayersDataFolder.mkdir();}
    }

    public HashMap<UUID, PlayerData> getPlayersStats(){
        return this.PlayersStats;
    }

    public static String getPlayerDataPath(UUID uuid){
        return PlayersDataPath+uuid.toString()+".dat";
    }

    public void loadPlayerData(Player player){
        UUID uuid = player.getUniqueId();
        if(!main.getPlayerManager().getPlayersStats().containsKey(uuid)){
            final String PlayerDataPath = PlayerManager.getPlayerDataPath(uuid);
            File f = new File(PlayerDataPath);
            if(f.exists() && !f.isDirectory()) {
                main.getPlayerManager().getPlayersStats().put(uuid, PlayerData.loadData(PlayerDataPath));
                main.getLogger().info(main.getPrefix(true)+" Loading of "+player.getName()+"("+uuid.toString()+")'s data");
            } else {
                main.getPlayerManager().getPlayersStats().put(uuid, new PlayerData(0,0,0,0,0,0));
                main.getLogger().info(main.getPrefix(true)+" Creation of the data of "+player.getName()+"("+uuid.toString()+")");
            }
        }
    }

    public boolean hasPlayerData(Player player){
        return getPlayersStats().containsKey(player.getUniqueId());
    }

    public PlayerData getPlayerData(Player player){
        final UUID uuid = player.getUniqueId();
        if(getPlayersStats().containsKey(uuid)){
            return main.getPlayerManager().getPlayersStats().get(uuid);
        }
        return null;
    }

    public static int getTimePlayed(Player player) {
        if (!ServerVersion.Version.isCurrentLower(ServerVersion.Version.v1_14_R1)) {
            return player.getStatistic(Statistic.valueOf("PLAY_ONE_MINUTE")) / 20;
        }
        return player.getStatistic(Statistic.valueOf("PLAY_ONE_TICK")) / 20;
    }

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
                player.teleport(new Location(main.world, 1040, 4, 1040));
            }
        } else {
            // Spawn
            player.teleport(main.lobby);
            player.setGameMode(GameMode.ADVENTURE);

            clear(player);
            main.getItemsManager().LobbyItems(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 255, false, false));
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
            player.teleport(new Location(main.world, 1040, 4, 1040));
        }
    }

    public static boolean hasMovedOneBLock(Location from, Location to){
        final Location bFrom = from.getWorld().getBlockAt(from).getLocation();
        final Location bTo = to.getWorld().getBlockAt(to).getLocation();
        return bFrom != bTo;
    }

    public static boolean hasPermission(CommandSender sender, String permission){
        return sender.hasPermission("blockedincombat.*") || sender.hasPermission(permission);
    }
}
