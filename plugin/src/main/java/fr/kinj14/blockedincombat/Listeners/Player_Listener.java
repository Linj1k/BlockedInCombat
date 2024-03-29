package fr.kinj14.blockedincombat.Listeners;

import fr.kinj14.blockedincombat.Data.PlayerData;
import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Main;
import fr.kinj14.blockedincombat.Manager.DelayManager;
import fr.kinj14.blockedincombat.Enums.GameState;
import fr.kinj14.blockedincombat.Manager.PlayerManager;
import fr.kinj14.blockedincombat.Enums.Teams;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.UUID;

public class Player_Listener implements Listener {
    protected final Main main = Main.getInstance();

    private final DelayManager delayManager = new DelayManager();

    @EventHandler
    public void OnJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(Main.getPrefix()+Lang.PLUGIN_JOINMSG.get().replace("{player}", player.getName()));

        main.getPlayerManager().loadPlayerData(player);

        PlayerManager.setupLobby(player);

        if(player.isOp()){
            if(!main.getConfigManager().isLatestVersion()){
                TextComponent message = new TextComponent("\n"+ Main.getPrefix()+"A new version is available! Click here! ("+main.getConfigManager().getVersionName()+")\n");
                message.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, main.getConfigManager().getDownloadVersion() ) );
                player.spigot().sendMessage( message );
            }
        }
    }

    @EventHandler
    public void OnQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        UUID uuid = player.getUniqueId();
        if(main.getPlayerManager().getPlayersStats().containsKey(uuid)){
            PlayerData playerData = main.getPlayerManager().getPlayersStats().get(uuid);
            playerData.TimePlayed = PlayerManager.getTimePlayed(player);
            playerData.saveData(PlayerManager.getPlayerDataPath(uuid));
            main.getLogger().info(Main.getPrefix(true)+" Save of "+player.getName()+"("+ uuid +")'s data");
        }

        event.setQuitMessage(Main.getPrefix()+Lang.PLUGIN_LEAVEMSG.get().replace("{player}", player.getName()));
        
        main.getTeamsManager().removePlayerInTeam(player, main.getTeamsManager().getPlayerTeam(player));

        main.getScoreboardManager().destroy(player);

        main.getScoreboardManager().updatePlayersAll(String.valueOf(Bukkit.getOnlinePlayers().size()-1));

        main.getPlayersBuild().remove(player);

        main.CheckWin();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void OnPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Location from = event.getFrom();
        final Location to = event.getTo();

        assert to != null;
        if(PlayerManager.hasMovedOneBLock(from, to) && GameState.isState(GameState.GENERATE_MAP)){
            final Block bTo = Objects.requireNonNull(to.getWorld()).getBlockAt(to);
            if(bTo.getLocation().distance(main.ArenaLoc) >= 10){
                Integer Interval = 10;
                if(delayManager.CheckDelay(player,Interval)) {return;}
                delayManager.AddDelay(player, Interval);

                player.teleport(main.ArenaLoc);
                player.sendMessage(Main.getPrefix()+Lang.GAMESTATE_LEAVEAREA.get());
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Block block = event.getClickedBlock();

        if(GameState.isState(GameState.PLAYING)){return;}
        Integer Interval = 20; // In Tick (20 = 1 second)
        if(delayManager.CheckDelay(player,Interval)) {event.setCancelled(true);return;}

        if(event.getAction() == Action.RIGHT_CLICK_BLOCK && block != null){
            if(block.getLocation().getBlockX() == 47 && block.getLocation().getBlockY() == 10 && block.getLocation().getBlockZ() == 76){
                player.sendMessage(Lang.PLUGIN_DESCRIPTION.get());
                delayManager.AddDelay(player, Interval);

                event.setCancelled(true);
                return;
            }

            if(block.getLocation().getBlockX() == 49 && block.getLocation().getBlockY() == 10 && block.getLocation().getBlockZ() == 82){
                TextComponent message = new TextComponent("\n§bClick here to subscribe to the map creator! (PingiPuck)");
                message.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, "https://www.youtube.com/c/PingiPuck?sub_confirmation=1" ) );
                player.spigot().sendMessage( message );

                TextComponent message2 = new TextComponent("§bClick here to see the creator of the plugin! (Kinj14)");
                message2.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, "https://github.com/Linj1k" ) );
                player.spigot().sendMessage( message2 );
                delayManager.AddDelay(player, Interval);

                event.setCancelled(true);
                return;
            }
        }

        if((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) && item != null && item.hasItemMeta()){
            if(main.itemsUtils.BlockIsWool(item.getType())){
                for(Teams team : Teams.values()){
                    if(Objects.requireNonNull(item.getItemMeta()).getDisplayName().contains(team.getName())){
                        main.getTeamsManager().switchPlayer(player, team);

                        main.PrepareGame();

                        delayManager.AddDelay(player, Interval);
                        event.setCancelled(true);
                        return;
                    }
                }
            }

            if(Objects.requireNonNull(item.getItemMeta()).getDisplayName().equalsIgnoreCase(Objects.requireNonNull(main.getGuiManager().getSettings().getItem().getItemMeta()).getDisplayName())){
                main.getGuiManager().getSettings().open(player);
                delayManager.AddDelay(player, Interval);
                event.setCancelled(true);
                return;
            }

            if(main.BungeeCord && item.getItemMeta().getDisplayName().contains("Lobby!")){
                main.getBungeeComManager().sendConnect(player, main.BungeeCord_Server);
                delayManager.AddDelay(player, Interval);

                event.setCancelled(true);
            }

        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        event.getRightClicked();
        if(event.getRightClicked() instanceof Player && player.getGameMode() == GameMode.SPECTATOR){
            Player target = (Player)event.getRightClicked();
            Inventory inv = target.getInventory();

            player.openInventory(inv);

            //InventoryUtils.updateInventory(main, player, "Test");
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(main.getPlayersBuild().contains(player)){return;}
        if(!GameState.isState(GameState.PLAYING)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if(main.getPlayersBuild().contains(player)){return;}

        if(!GameState.isState(GameState.PLAYING)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerPickup(EntityPickupItemEvent event) {
        if(event.getEntity() instanceof Player){
            Player player = (Player)event.getEntity();
            if(main.getPlayersBuild().contains(player)){return;}
            if(!GameState.isState(GameState.PLAYING)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerExpChange(PlayerExpChangeEvent event) {
        if(!GameState.isState(GameState.PLAYING)) {
            event.setAmount(0);
            return;
        }
        event.setAmount((int)(event.getAmount() * (main.getSettingsManager().getConfig().getExpMultiplier()*1D)));
    }

    /**
     * Handles food level changes.
     * @param event The event.
     */
    @EventHandler (priority = EventPriority.HIGH)
    public void onFoodLevelChange (FoodLevelChangeEvent event) {
        if (event.getEntityType () != EntityType.PLAYER || GameState.isState(GameState.PLAYING)) return;
        event.setCancelled (true);
    }
}
