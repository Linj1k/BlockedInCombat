package fr.kinj14.blockedincombat.Listeners;

import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Library.InventoryUtils;
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
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Player_Listener implements Listener {
    protected final Main main = Main.getInstance();

    private DelayManager delayManager = new DelayManager();

    @EventHandler
    public void OnJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        event.setJoinMessage(main.getPrefix()+Lang.PLUGINÙJOINMSG.get().replace("{player}", player.getName()));

        PlayerManager.setupLobby(player);

        if(player.isOp()){
            if(!main.getConfigManager().isLatestVersion()){
                TextComponent message = new TextComponent("\n"+main.getPrefix()+"A new version is available! Click here! ("+main.getConfigManager().getVersionName()+")\n");
                message.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, main.getConfigManager().getDownloadVersion() ) );
                player.spigot().sendMessage( message );
            }
        }
    }

    @EventHandler
    public void OnQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        event.setQuitMessage(main.getPrefix()+Lang.PLUGINÙLEAVEMSG.get().replace("{player}", player.getName()));
        
        main.getTeamsManager().removePlayerInTeam(player, main.getTeamsManager().getPlayerTeam(player));

        main.getScoreboardManager().destroy(player);

        main.getScoreboardManager().updatePlayersAll(String.valueOf(Bukkit.getOnlinePlayers().size()-1));

        if(main.getPlayersBuild().contains(player)){
            main.getPlayersBuild().remove(player);
        }

        main.CheckWin();
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void OnPlayerMove(PlayerMoveEvent event) {
        final Player player = event.getPlayer();
        final Location from = event.getFrom();
        final Location to = event.getTo();

        if(PlayerManager.hasMovedOneBLock(from, to) && GameState.isState(GameState.GENERATE_MAP)){
            final Block bTo = to.getWorld().getBlockAt(to);
            if(bTo.getLocation().distance(main.ArenaLoc) >= 10){
                Integer Interval = 10;
                if(delayManager.CheckDelay(player,Interval)) {return;}
                delayManager.AddDelay(player, Interval);

                player.teleport(main.ArenaLoc);
                player.sendMessage(main.getPrefix()+Lang.GAMESTATEÙLEAVE_AREA.get());
            }
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        Block block = event.getClickedBlock();

        if(event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK && !GameState.isState(GameState.PLAYING)){
            Integer Interval = 20; // In Tick (20 = 1 second)
            if(delayManager.CheckDelay(player,Interval)) {event.setCancelled(true);return;}
            delayManager.AddDelay(player, Interval);
            if(item != null && item.hasItemMeta()) {

                if(item.getType() == Material.WOOL){
                    for(Teams team : Teams.values()){
                        if(item.getItemMeta().getDisplayName().contains(team.getColoredName())){
                            main.getTeamsManager().switchPlayer(player, team);

                            main.PrepareGame();

                            event.setCancelled(true);
                            return;
                        }
                    }
                }

                if(item.getItemMeta().getDisplayName().equalsIgnoreCase(main.getGuiManager().getSettings().getItem().getItemMeta().getDisplayName())){
                    main.getGuiManager().getSettings().open(player);
                    event.setCancelled(true);
                    return;
                }

                if(main.BungeeCord && item.getItemMeta().getDisplayName().contains("Lobby!")){
                    main.getBungeeComManager().sendConnect(player, "lobby1");

                    event.setCancelled(true);
                    return;
                }
            }

            if(block != null){
                if(block.getLocation().getBlockX() == 47 && block.getLocation().getBlockY() == 10 && block.getLocation().getBlockZ() == 76){
                    player.sendMessage(main.getPrefix()+ Lang.PLUGINÙDESCRIPTION.get());

                    event.setCancelled(true);
                    return;
                }

                if(block.getLocation().getBlockX() == 49 && block.getLocation().getBlockY() == 10 && block.getLocation().getBlockZ() == 82){
                    TextComponent message = new TextComponent("\n§bClick here to subscribe to the map creator! (PingiPuck)");
                    message.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, "https://www.youtube.com/c/PingiPuck?sub_confirmation=1" ) );
                    player.spigot().sendMessage( message );

                    TextComponent message2 = new TextComponent("\n§bClick here to see the creator of the plugin! (Kinj14)");
                    message2.setClickEvent( new ClickEvent( ClickEvent.Action.OPEN_URL, "https://orerun.ovh" ) );
                    player.spigot().sendMessage( message2 );

                    event.setCancelled(true);
                    return;
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntity(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        if(event.getRightClicked() != null && event.getRightClicked() instanceof Player && player.getGameMode() == GameMode.SPECTATOR){
            Player target = (Player)event.getRightClicked();
            Inventory inv = target.getInventory();

            player.openInventory(inv);

            InventoryUtils.updateChestName(player, target.getDisplayName());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if(main.getPlayersBuild().contains(player)){return;}
        if(!GameState.isState(GameState.PLAYING)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if(main.getPlayersBuild().contains(player)){return;}

        if(!GameState.isState(GameState.PLAYING)) {
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onPlayerPickup(EntityPickupItemEvent event) {
        if(event.getEntity() instanceof Player){
            Player player = (Player)event.getEntity();
            if(main.getPlayersBuild().contains(player)){return;}
            if(!GameState.isState(GameState.PLAYING)) {
                event.setCancelled(true);
                return;
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
}
