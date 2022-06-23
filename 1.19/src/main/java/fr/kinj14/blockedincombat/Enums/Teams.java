package fr.kinj14.blockedincombat.Enums;

import fr.kinj14.blockedincombat.Main;
import fr.kinj14.blockedincombat.Manager.ItemsManager;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public enum Teams {
    blue(Lang.TEAMS_BLUE.get(), "§9", Material.BLUE_WOOL, new Location(null, 1052, 2, 1051)),
    red(Lang.TEAMS_RED.get(), "§4", Material.RED_WOOL, new Location(null, 1027, 2, 1051)),
    yellow(Lang.TEAMS_YELLOW.get(), "§e", Material.YELLOW_WOOL, new Location(null, 1027, 2, 1027)),
    green(Lang.TEAMS_GREEN.get(), "§2", Material.GREEN_WOOL, new Location(null, 1050, 2, 1029)),
    spectator(Lang.TEAMS_SPECTATOR.get(), "§7", Material.GRAY_WOOL, new Location(null, 0, 2, 0));

    private final String name;
    private final String Color;
    private final Material Wool;
    private final List<Player> players = new ArrayList<>();
    private final Location spawn;

    Teams(String name, String color, Material Wool, Location spawn){
        this.name = name;
        this.Color = color;
        this.Wool = Wool;
        spawn.setWorld(Main.getInstance().world);
        this.spawn = spawn;
    }

    public static Teams getByName(String name){
        for(Teams team : Teams.values()){
            if(team.getColoredName().equalsIgnoreCase(name) || team.getName().equalsIgnoreCase(name)){
                return team;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return Color;
    }

    public Material getWool() {
        return Wool;
    }

    public String getColoredName(){ return getColor()+getName()+ChatColor.RESET; }

    public ItemStack getItem(){
        return ItemsManager.buildItemstack(new ItemStack(getWool(), 1), Lang.TEAMS_ITEM.get().replace("{team}", getColoredName()), new ArrayList<>());
    }

    public List<Player> getPlayers(){
        return players;
    }

    public boolean containsPlayer(Player player){
        return players.contains(player);
    }

    public boolean addPlayer(Player player){
        if(!containsPlayer(player)){
            players.add(player);
            String name = getColor()+player.getName();
            player.setPlayerListName(name);
            player.setDisplayName(name);
            player.setCustomName(name);
            return true;
        }
        return false;
    }

    public boolean removePlayer(Player player){
        if(containsPlayer(player)){
            players.remove(player);
            String name = player.getName();
            player.setPlayerListName(name);
            player.setDisplayName(name);
            player.setCustomName(name);
            return true;
        }
        return false;
    }

    public Location getSpawn() {
        return spawn;
    }
}
