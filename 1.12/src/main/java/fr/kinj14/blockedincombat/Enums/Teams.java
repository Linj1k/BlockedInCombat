package fr.kinj14.blockedincombat.Enums;

import fr.kinj14.blockedincombat.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public enum Teams {
    blue(Lang.TEAMSÙBLUE.get(), 11, "§9", new Location(null, 1052, 2, 1051)),
    red(Lang.TEAMSÙRED.get(), 14, "§4", new Location(null, 1027, 2, 1051)),
    yellow(Lang.TEAMSÙYELLOW.get(), 4, "§e", new Location(null, 1027, 2, 1027)),
    green(Lang.TEAMSÙGREEN.get(), 5, "§2", new Location(null, 1050, 2, 1029)),
    spectator(Lang.TEAMSÙSPECTATOR.get(), 8, "§7", new Location(null, 0, 2, 0));

    protected final Main main = Main.getInstance();
    private final String name;
    private final byte Data;
    private final String Color;
    private List<Player> players = new ArrayList<>();
    private Location spawn;

    Teams(String name, int Data, String color, Location spawn){
        this.name = name;
        this.Data = (byte) Data;
        this.Color = color;
        spawn.setWorld(Bukkit.getWorld(main.WorldName));
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

    public byte getData() {
        return this.Data;
    }

    public String getColor() {
        return Color;
    }

    public String getColoredName(){ return getColor()+getName()+ChatColor.RESET; }

    public ItemStack getItem(){
        return main.getItemsManager().buildItemstack(new ItemStack(Material.WOOL, 1, getData()), Lang.TEAMSÙITEM.get().replace("{team}", getColoredName()), new ArrayList<>());
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
