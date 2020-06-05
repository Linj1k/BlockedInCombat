package fr.kinj14.blockedincombat.Listeners;

import fr.kinj14.blockedincombat.Enums.GameState;
import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Enums.Teams;
import fr.kinj14.blockedincombat.Main;
import fr.kinj14.blockedincombat.Manager.DelayManager;
import fr.kinj14.blockedincombat.Manager.PlayerManager;
import fr.kinj14.blockedincombat.Manager.TeamsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.logging.Logger;

public class Chat_Listener implements Listener {
    protected final transient Server server;
    protected final Main main = Main.getInstance();
    private DelayManager delay;

    public Chat_Listener() {
        this.server = main.getServer();
        this.delay = new DelayManager();
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event)
    {
        Player player = event.getPlayer();
        String message = FormatChat(player, event.getMessage());

        if(player.isDead() || event.isCancelled()) {
            event.setCancelled(true);
            return;
        }

        if(main.getConfigManager().getBooleanConfig("General.NoChatFlood") && (!player.isOp() || !PlayerManager.hasPermission(player, "blockedincombat.ChangeSettings"))){
            Integer Interval = 40;
            if(delay.CheckDelay(player,Interval)) {
                player.sendMessage(main.getPrefix()+"§4Thanks for not flooding the chat.");
                event.setCancelled(true);
                return;
            }
            delay.AddDelay(player, Interval);
        }

        if(player.getGameMode() == GameMode.SPECTATOR){
            message = Lang.CHATÙSPECTATOR.get()+message;
            for(Player p : Bukkit.getOnlinePlayers()){
                if(p.getGameMode() == GameMode.SPECTATOR){
                    p.sendMessage(message);
                }
            }
            main.logger.info(message);
            event.setCancelled(true);
            return;
        }

        if(GameState.isState(GameState.PLAYING)) {
            if(event.getMessage().startsWith("!")){
                message = Lang.CHATÙGLOBAL.get()+FormatChat(player, event.getMessage().substring(1));
                GlobalMsg(message);
                main.logger.info(message);
                event.setCancelled(true);
                return;
            }

            Teams playerTeam = main.getTeamsManager().getPlayerTeam(player);
            if(playerTeam != null) {
                message = Lang.CHATÙTEAM.get().replace("{teamcolor}", playerTeam.getColor())+message;
                for(Player pteam : playerTeam.getPlayers()) {
                    pteam.sendMessage(message);
                }

                main.logger.info(message);
                event.setCancelled(true);
                return;
            }
        } else {
            message = Lang.CHATÙGLOBAL.get()+message;
            GlobalMsg(message);
            main.logger.info(message);
            event.setCancelled(true);
            return;
        }
    }

    public String FormatChat(Player player, String msg) {
        //{DisplayName},{PlayerName},{Level},{Health},{MaxHealth},{Food},{CustomName},{Gamemode},{Locale},{PlayerListName},{World},{Message}
        String finalmsg = main.getConfigManager().getStringConfig("General.ChatFormat");
        finalmsg = finalmsg.replace("{DisplayName}", player.getDisplayName());
        finalmsg = finalmsg.replace("{PlayerName}", player.getName());
        finalmsg = finalmsg.replace("{Level}", String.valueOf(player.getExp()));
        finalmsg = finalmsg.replace("{Health}", String.valueOf(player.getHealth()));
        finalmsg = finalmsg.replace("{Food}", String.valueOf(player.getFoodLevel()));
        finalmsg = finalmsg.replace("{MaxHealth}", String.valueOf(player.getHealthScale()));
        finalmsg = finalmsg.replace("{CustomName}", String.valueOf(player.getCustomName()));
        finalmsg = finalmsg.replace("{Gamemode}", String.valueOf(player.getGameMode()));
        finalmsg = finalmsg.replace("{Locale}", String.valueOf(player.getLocale()));
        finalmsg = finalmsg.replace("{PlayerListName}", String.valueOf(player.getPlayerListName()));
        finalmsg = finalmsg.replace("{World}", String.valueOf(player.getWorld()));
        finalmsg = finalmsg.replace("{Message}", ChatColor.RESET+msg);
        return finalmsg;
    }

    public void GlobalMsg(String message){
        for(Player p : Bukkit.getServer().getOnlinePlayers()) {
            p.sendMessage(message);
        }
    }
}