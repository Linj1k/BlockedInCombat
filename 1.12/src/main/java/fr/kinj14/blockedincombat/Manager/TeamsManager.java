package fr.kinj14.blockedincombat.Manager;

import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Enums.Teams;
import fr.kinj14.blockedincombat.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class TeamsManager {
    protected final Main main = Main.getInstance();
    private List<Teams> teamsList = new ArrayList<>();

    public TeamsManager() {
        setup();
    }

    public void setup(){
        teamsList.clear();
        for(Teams team : Teams.values()){
            teamsList.add(team);
        }
    }

    public List<Teams> getTeams() {
        return teamsList;
    }

    public Teams getTeam(Teams teams){
        if(teams == null){return null;}
        for(Teams team : teamsList){
            if(teams.getName() == team.getName()){
                return team;
            }
        }
        return null;
    }

    public Teams getPlayerTeam(Player player){
        for(Teams team : getValidTeams()){
            for(Player p : team.getPlayers()){
                if(p.getUniqueId() == player.getUniqueId()){
                    return team;
                }
            }
        }
        return null;
    }

    public Teams getPlayerTeam_ANYTEAM(Player player){
        for(Teams team : getTeams()){
            for(Player p : team.getPlayers()){
                if(p.getUniqueId() == player.getUniqueId()){
                    return team;
                }
            }
        }
        return null;
    }

    public boolean playerIsInTeam(Player player, Teams teams){
        for(Teams team : getValidTeams()){
            if(team.getName() == teams.getName()){
                for(Player p : team.getPlayers()){
                    if(p.getUniqueId() == player.getUniqueId()){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean canAddPlayerInTeam(Player player, Teams teams){
        return !getTeam(teams).containsPlayer(player);
    }

    public boolean canRemovePlayerInTeam(Player player, Teams teams){
        if(player == null || teams == null || getTeam(teams) == null){return false;}
        return getTeam(teams).containsPlayer(player);
    }

    public void addPlayerInTeam(Player player, Teams teams){
        if(canAddPlayerInTeam(player, teams)){
            Teams team = getTeam(teams);
            if(team.addPlayer(player)){
                main.getScoreboardManager().updateTeam(player, team.getColoredName());
                Bukkit.broadcastMessage(main.getPrefix()+ Lang.TEAMSÙJOINMSG.get().replace("{player}", player.getDisplayName()).replace("{team}", team.getColoredName()));
            }
        }
    }

    public void removePlayerInTeam(Player player, Teams teams){
        if(canRemovePlayerInTeam(player, teams)){
            Teams team = getTeam(teams);
            if(team.removePlayer(player)){
                main.getScoreboardManager().updateTeam(player, team.getName());
            }
        }
    }

    public void switchPlayer(Player player, Teams team){
        for(Teams t : getTeams()){
            if(t.getName() != team.getName() && t.containsPlayer(player)){
                t.removePlayer(player);
            }
        }

        addPlayerInTeam(player, team);
    }

    public void eliminatePlayer(Player player){
        Teams team = getPlayerTeam(player);
        if(team != null){
            team.removePlayer(player);
            if(team.getPlayers().size() <= 0){
                getTeams().remove(team);
            }
        }

        player.setGameMode(GameMode.SPECTATOR);
        Player randomPlayer = getRandomPlayerInTeam(getRandomTeam());
        if(randomPlayer != null){
            player.teleport(randomPlayer.getLocation());
        } else {
            player.teleport(new Location(Bukkit.getWorld(main.WorldName), 1040, 4, 1040));
        }
        addPlayerInTeam(player, Teams.spectator);

        main.CheckWin();
    }

    public void setGlowingPlayers(boolean glowing){
        main.getSettingsManager().setGlowing(glowing);
        if(glowing){
            for(Teams team : getValidTeams()){
                for(Player player : team.getPlayers()){
                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 1, true, false));
                }
            }
        } else {
            for(Player player : Bukkit.getOnlinePlayers()){
                if(player.hasPotionEffect(PotionEffectType.GLOWING)){
                    player.removePotionEffect(PotionEffectType.GLOWING);
                }
            }
        }
    }


    // Utils
    public Teams getRandomTeamForPlayer(Player player) {
        List<Teams> rteam = new ArrayList<>();
        for(Teams team : getTeamsNoSpec()) {
            if(!team.containsPlayer(player) && team != null) {
                rteam.add(team);
            }
        }

        return rteam.get(ThreadLocalRandom.current().nextInt(rteam.size()));
    }

    public Teams getRandomTeam() {
        List<Teams> validTeams = getTeamsNoSpec();
        if(validTeams != null && validTeams.size() > 0){
            Teams randomTeam = getTeams().get(ThreadLocalRandom.current().nextInt(validTeams.size()));
            if(randomTeam != null){
                return randomTeam;
            }
        }
        return null;
    }

    public Player getRandomPlayerInTeam(Teams team){
        if(team != null && getTeam(team) != null){
            int randomNumber = ThreadLocalRandom.current().nextInt(getTeam(team).getPlayers().size());
            return team.getPlayers().get(randomNumber);
        }
        return null;
    }

    public int getPlayersCountInTeam(Teams team) {
        return getTeam(team).getPlayers().size();
    }

    public int getPlayersCount() {
        int count = 0;
        for(Teams team : getTeams()){
            if(team != Teams.spectator) {
                count = count + team.getPlayers().size();
            }
        }
        return count;
    }

    public List<Teams> getValidTeams(){
        List<Teams> teams = new ArrayList<>();
        for(Teams team : getTeams()){
            if(team != Teams.spectator && team.getPlayers().size() > 0 && team != null) {
                teams.add(team);
            }
        }
        return teams;
    }

    public List<Teams> getInValidTeams(){
        List<Teams> teams = new ArrayList<>();
        for(Teams team : getTeams()){
            if(team != Teams.spectator && team.getPlayers().size() <= 0 && team != null) {
                teams.add(team);
            }
        }
        return teams;
    }

    public List<Teams> getTeamsNoSpec(){
        List<Teams> teams = new ArrayList<>();
        for(Teams team : getTeams()){
            if(team != Teams.spectator && team != null) {
                teams.add(team);
            }
        }
        return teams;
    }

    public int getTeamsCount(){
        int count = 0;
        for(Teams team : getTeams()){
            if(team != Teams.spectator && team.getPlayers().size() > 0) {
                count = count + 1;
            }
        }
        return count;
    }

    public boolean CheckForStart() {
        return getTeamsCount() >= 2;
    }

    public List<Player> getPlayersNotInTeam(){
        List<Player> players = new ArrayList<>();
        for(Player player : Bukkit.getOnlinePlayers()){
            if(getPlayerTeam_ANYTEAM(player) == null){
                players.add(player);
            }
        }
        return players;
    }
}
