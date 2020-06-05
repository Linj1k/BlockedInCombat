package fr.kinj14.blockedincombat.Manager;

import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Enums.Teams;
import fr.kinj14.blockedincombat.Main;
import fr.kinj14.blockedincombat.Library.ScoreboardSign;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ScoreboardManager {
    protected final Main main = Main.getInstance();
    public String timerdate = new SimpleDateFormat(Lang.PLUGINÙDATE_FORMAT.get()).format(new Date((new java.sql.Timestamp(Calendar.getInstance().getTime().getTime())).getTime()));
    public Map<Player, ScoreboardSign> scorebaordMap = new HashMap<>();

    public void setup(Player player){
        if(!scorebaordMap.containsKey(player)){
            ScoreboardSign sb = new ScoreboardSign(player, main.getPrefix());
            sb.create();
            scorebaordMap.put(player, sb);
            update(player);
        }
    }

    public void update(Player player){
        //Bukkit.getOnlinePlayers().size()+"/"+Bukkit.getMaxPlayers()
        if(scorebaordMap.containsKey(player)){
            ScoreboardSign sb = scorebaordMap.get(player);
            sb.setLine(0, ChatColor.GRAY+"- "+timerdate+" -");
            sb.setLine(1, Lang.SCOREBOARDÙTIME.get().replace("{time}", "00:00"));
            sb.setLine(2, Lang.SCOREBOARDÙPLAYERS.get().replace("{players}", String.valueOf(Bukkit.getOnlinePlayers().size())).replace("{maxplayers}", String.valueOf(Bukkit.getMaxPlayers())));
            sb.setLine(3, Lang.SCOREBOARDÙPVP.get().replace("{time}", "00:00"));
            sb.setLine(4, "§a ");
            sb.setLine(5, Lang.SCOREBOARDÙTEAM.get().replace("{team}", Teams.spectator.getColoredName()));
            sb.setLine(6, ChatColor.GRAY+"───────────");
            sb.setLine(7, Lang.SCOREBOARDÙIP.get());
        }
    }

    public void updateAll(){
        for(Player player : Bukkit.getOnlinePlayers()){
            update(player);
        }
    }

    public void updatePlayers(Player player, String players){
        if(scorebaordMap.containsKey(player)){
            ScoreboardSign sb = scorebaordMap.get(player);
            sb.setLine(2, Lang.SCOREBOARDÙPLAYERS.get().replace("{players}", players).replace("{maxplayers}", String.valueOf(Bukkit.getMaxPlayers())));
        }
    }

    public void updatePlayersAll(String players){
        for(Player player : Bukkit.getOnlinePlayers()){
            updatePlayers(player, players);
        }
    }

    public void updateTime(Player player, String time){
        if(scorebaordMap.containsKey(player)){
            ScoreboardSign sb = scorebaordMap.get(player);
            sb.setLine(1, Lang.SCOREBOARDÙTIME.get().replace("{time}", time));
        }
    }

    public void updateTimeAll(String time){
        for(Player player : Bukkit.getOnlinePlayers()){
            updateTime(player, time);
        }
    }

    public void updatePVP(Player player, String pvp){
        if(scorebaordMap.containsKey(player)){
            ScoreboardSign sb = scorebaordMap.get(player);
            sb.setLine(3, pvp);
        }
    }

    public void updatePVPAll(String pvp){
        for(Player player : Bukkit.getOnlinePlayers()){
            updatePVP(player, pvp);
        }
    }

    public void updateTeam(Player player, String teamname){
        if(scorebaordMap.containsKey(player)){
            ScoreboardSign sb = scorebaordMap.get(player);
            sb.setLine(5, Lang.SCOREBOARDÙTEAM.get().replace("{team}", teamname));
        }
    }

    public void updateTeamAll(String teamname){
        for(Player player : Bukkit.getOnlinePlayers()){
            updateTeam(player, teamname);
        }
    }

    public void destroy(Player player){
        if(scorebaordMap.containsKey(player)){
            ScoreboardSign sb = scorebaordMap.get(player);
            sb.destroy();
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
            int ping = (((CraftPlayer)player).getHandle()).ping;
            String playername = player.getPlayerListName();
            if(playername.contains(" ")){
                playername = player.getPlayerListName().split(" ")[0];
            } else {
                playername = playername+" ";
            }
            player.setPlayerListName(playername+" "+ChatColor.GREEN+String.valueOf(ping));
        }
    }
}
