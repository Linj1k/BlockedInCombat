package fr.kinj14.blockedincombat.Manager;

import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Enums.Teams;
import fr.kinj14.blockedincombat.Library.FastBoard.FastBoard;
import fr.kinj14.blockedincombat.Library.ReflectionUtils;
import fr.kinj14.blockedincombat.Main;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager {
    protected final Main main = Main.getInstance();
    public String timerdate = new SimpleDateFormat(Lang.PLUGIN_DATEFORMAT.get()).format(new Date((new java.sql.Timestamp(Calendar.getInstance().getTime().getTime())).getTime()));
    public Map<Player, FastBoard> scorebaordMap = new HashMap<>();
    private String yourip = main.getConfigManager().getStringConfig("General.YourIP");

    public void setup(Player player){
        if(!scorebaordMap.containsKey(player)){
            FastBoard sb = new FastBoard(player);
            sb.updateTitle(ChatColor.GOLD+"- BlockedInCombat -");
            scorebaordMap.put(player, sb);
            update(player);
        }
    }

    public void update(Player player){
        //Bukkit.getOnlinePlayers().size()+"/"+Bukkit.getMaxPlayers()
        if(scorebaordMap.containsKey(player)){
            FastBoard sb = scorebaordMap.get(player);
            sb.updateLines(
                    Lang.SCOREBOARD_TIME.get().replace("{time}", "00:00"),
                    Lang.SCOREBOARD_PLAYERS.get().replace("{players}", String.valueOf(Bukkit.getOnlinePlayers().size())).replace("{maxplayers}", String.valueOf(Bukkit.getMaxPlayers())),
                    Lang.SCOREBOARD_PVP.get().replace("{time}", "00:00"),
                    "§a ",
                    Lang.SCOREBOARD_TEAM.get().replace("{team}", Teams.spectator.getColoredName()),
                    ChatColor.GRAY+"───────────",
                    ChatColor.translateAlternateColorCodes('&', this.yourip)
            );
        }
    }

    public void updateAll(){
        for(Player player : Bukkit.getOnlinePlayers()){
            update(player);
        }
    }

    public void updatePlayers(Player player, String players){
        if(scorebaordMap.containsKey(player)){
            FastBoard sb = scorebaordMap.get(player);
            sb.updateLine(1, Lang.SCOREBOARD_PLAYERS.get().replace("{players}", players).replace("{maxplayers}", String.valueOf(Bukkit.getMaxPlayers())));
        }
    }

    public void updatePlayersAll(String players){
        for(Player player : Bukkit.getOnlinePlayers()){
            updatePlayers(player, players);
        }
    }

    public void updateTime(Player player, String time){
        if(scorebaordMap.containsKey(player)){
            FastBoard sb = scorebaordMap.get(player);
            sb.updateLine(0, Lang.SCOREBOARD_TIME.get().replace("{time}", time));
        }
    }

    public void updateTimeAll(String time){
        for(Player player : Bukkit.getOnlinePlayers()){
            updateTime(player, time);
        }
    }

    public void updatePVP(Player player, String pvp){
        if(scorebaordMap.containsKey(player)){
            FastBoard sb = scorebaordMap.get(player);
            sb.updateLine(2, pvp);
        }
    }

    public void updatePVPAll(String pvp){
        for(Player player : Bukkit.getOnlinePlayers()){
            updatePVP(player, pvp);
        }
    }

    public void updateTeam(Player player, String teamname){
        if(scorebaordMap.containsKey(player)){
            FastBoard sb = scorebaordMap.get(player);
            sb.updateLine(4, Lang.SCOREBOARD_TEAM.get().replace("{team}", teamname));
        }
    }

    public void updateTeamAll(String teamname){
        for(Player player : Bukkit.getOnlinePlayers()){
            updateTeam(player, teamname);
        }
    }

    public void destroy(Player player){
        if(scorebaordMap.containsKey(player)){
            FastBoard sb = scorebaordMap.get(player);
            if (sb != null) {
                sb.delete();
            }
            scorebaordMap.remove(player);
        }
    }

    public void clear(){
        for(Player player : Bukkit.getOnlinePlayers()){
            destroy(player);
        }
        scorebaordMap.clear();
    }

    public void updatePingToAllPlayers() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            try {
                Object entityPlayer = ReflectionUtils.getHandle(player);
                int ping = entityPlayer.getClass().getField("ping").getInt(entityPlayer);
                String playername = player.getPlayerListName();
                if(playername.contains(" ")){
                    playername = player.getPlayerListName().split(" ")[0];
                } else {
                    playername = playername+" ";
                }
                player.setPlayerListName(playername+" "+ChatColor.GREEN+String.valueOf(ping));
            } catch (Exception ex) {
            }
        }
    }
}
