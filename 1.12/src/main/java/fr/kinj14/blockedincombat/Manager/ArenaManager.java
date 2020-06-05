package fr.kinj14.blockedincombat.Manager;

import fr.kinj14.blockedincombat.Enums.GameState;
import fr.kinj14.blockedincombat.Library.ItemsUtils;
import fr.kinj14.blockedincombat.Main;
import fr.kinj14.blockedincombat.Manager.Settings.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ArenaManager {
    protected final Main main = Main.getInstance();
    public Location StartLocation;
    public Location EndLocation;
    public boolean CancelTask = false;
    private List<Integer> Tasks = new ArrayList<>();

    public ArenaManager() {
        StartLocation = new Location(Bukkit.getWorld(main.WorldName), 1054, 2, 1054);
        EndLocation = new Location(Bukkit.getWorld(main.WorldName), 1025, 11, 1025);
        Tasks.clear();
    }

    //Setup Arena
    public void setupArena(){
        buildBlock(Material.BEDROCK, 1025, 1055, 1025,1055, 0, 0);

        //X
        buildWall(Material.BEDROCK, 1024, 1055, 1024,1, 1, 12);
        buildWall(Material.BEDROCK, 1024, 1055, 1055,1, 1, 12);

        //Z
        buildWall(Material.BEDROCK, 1055, 1, 1024,1055, 1, 12);
        buildWall(Material.BEDROCK, 1024, 1, 1024,1055, 1, 12);
    }

    public void setupClearArena(){
        buildBlock(Material.AIR, 1025, 1055, 1025,1055, 0, 0);

        //X
        buildWall(Material.AIR, 1024, 1055, 1024,1, 1, 12);
        buildWall(Material.AIR, 1024, 1055, 1055,1, 1, 12);

        //Z
        buildWall(Material.AIR, 1055, 1, 1024,1055, 1, 12);
        buildWall(Material.AIR, 1024, 1, 1024,1055, 1, 12);
    }

    public void buildBlock(Material mat, int xMin, int xMax, int zMin, int zMax, int yMin, int yMax){
        buildBlock(mat, xMin, xMax, zMin, zMax, yMin, yMax, true);
    }

    public void buildBlock(Material mat, int xMin, int xMax, int zMin, int zMax, int Y, int yMax, boolean fakeBoolean){
        World world = Bukkit.getWorld(main.WorldName);

        if(Y == yMax+1){
            return;
        }

        for(int x = xMin; x <= xMax; x++)
        {
            for(int z = zMin; z <= zMax; z++)
            {
                if(x == xMax){
                    buildBlock( mat, xMin, xMax, zMin, zMax, Y+1, yMax);
                    return;
                }
                Block block = world.getBlockAt(x,Y,z);
                block.setType(mat);
            }
        }
    }

    public void buildWall(Material mat, int xMin, int xMax, int zMin, int zMax, int yMin, int yMax){
        buildWall(mat, xMin, xMax, zMin, zMax, yMin, yMax, true);
    }

    public void buildWall(Material mat, int xMin, int xMax, int zMin, int zMax, int Y, int yMax, boolean fakeBoolean){
        World world = Bukkit.getWorld(main.WorldName);

        if(Y == yMax+1){
            return;
        }

        if(xMax > 0){
            for(int x = xMin; x <= xMax; x++)
            {
                if(x == xMax){
                    buildWall(mat, xMin, xMax, zMin, zMax, Y+1, yMax);
                    return;
                }
                buildBlock( mat, xMin, xMax, zMin, zMax, Y+1, yMax);
                Block block = world.getBlockAt(x,Y,zMin);
                block.setType(mat);
            }
        }

        if(zMax > 0){
            for(int z = zMin; z <= zMax; z++)
            {
                if(z == zMax){
                    buildWall(mat, xMin, xMax, zMin, zMax, Y+1, yMax);
                    return;
                }
                buildBlock( mat, xMin, xMax, zMin, zMax, Y+1, yMax);
                Block block = world.getBlockAt(xMin,Y,z);
                block.setType(mat);
            }
        }
    }

    //Arena
    public void clearArena(){
        stopTasks();
        clearArena((int) StartLocation.getY()-1);
    }

    public void clearArena(int Y){
        int xMin = (int)EndLocation.getX();
        int xMax = (int)StartLocation.getX()+1;
        int zMin = (int)EndLocation.getZ();
        int zMax = (int)StartLocation.getZ();

        World world = Bukkit.getWorld(main.WorldName);

        if(Y >= 13){
            return;
        }

        for(int x = xMin; x <= xMax; x++)
        {
            for(int z = zMin; z <= zMax; z++)
            {
                if(x == xMax){
                    clearArena(Y+1);
                    return;
                }
                Block block = world.getBlockAt(x,Y,z);
                block.setType(Material.AIR);
            }
        }
    }

    public void buildArena(){
        stopTasks();
        buildArena((int) StartLocation.getY()-1);
    }

    public void buildArena(int Y){
        int xMin = (int)EndLocation.getX();
        int xMax = (int)StartLocation.getX()+1;
        int zMin = (int)EndLocation.getZ();
        int zMax = (int)StartLocation.getZ();

        World world = Bukkit.getWorld(main.WorldName);

        if(Y == 12){
            return;
        }

        long delay = 0;

        for(int x = xMin; x <= xMax; x++)
        {
            if(!GameState.isState(GameState.GENERATE_MAP)){break;}
            for(int z = zMin; z <= zMax; z++)
            {
                if(!GameState.isState(GameState.GENERATE_MAP)){break;}
                if(x == xMax){
                    buildArena(Y+1);
                    return;
                }
                delay = delay + 1L;
                Block block = world.getBlockAt(x,Y,z);
                if(main.getConfigManager().getBooleanConfig("General.ArenaDelay")){
                    int task = Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
                        if(GameState.isState(GameState.GENERATE_MAP)){
                            block.setType(main.getSettingsManager().getRandomBlock());
                        } else {
                            return;
                        }
                    }, delay);
                    Tasks.add(task);
                } else {
                    if(GameState.isState(GameState.GENERATE_MAP)){
                        block.setType(main.getSettingsManager().getRandomBlock());
                    } else {
                        return;
                    }
                }
            }
        }
    }

    public void finishArea(){
        int xMin = (int)EndLocation.getX();
        int xMax = (int)StartLocation.getX()+1;
        int zMin = (int)EndLocation.getZ();
        int zMax = (int)StartLocation.getZ();

        World world = Bukkit.getWorld(main.WorldName);

        for(int x = xMin; x <= xMax; x++)
        {
            for(int z = zMin; z <= zMax; z++)
            {
                Block block = world.getBlockAt(x,12,z);
                block.setType(Material.BEDROCK);
            }
        }
    }

    //Setup team Spawn
    public void setupTeam(Location loc){
        setupTeam(loc, (int)loc.getY());

        if(main.getSettingsManager().getConfig().getBonusChest()){
            World world = Bukkit.getWorld(main.WorldName);
            loc = new Location(world, loc.getX(), loc.getY()-1, loc.getZ());
            Block block = world.getBlockAt(loc);
            block.setType(Material.CHEST);
            Chest chest = (Chest) block.getState();
            ThreadLocalRandom random = ThreadLocalRandom.current();
            for(int i=0; i <= random.nextInt(chest.getBlockInventory().getSize()); i++){
                int randomAmount = random.nextInt(7);

                ItemStack item = new ItemStack(main.getSettingsManager().getRandomItems(), randomAmount);
                if(new ItemsUtils().ItemIsTool(item)){
                    item.setAmount(1);
                }

                chest.getBlockInventory().setItem(random.nextInt(chest.getBlockInventory().getSize()), item);
            }
        }
    }

    public void setupTeam(Location loc, int Y){
        World world = Bukkit.getWorld(main.WorldName);
        Location start = new Location(world, loc.getX()-1, 0, loc.getZ()-1);
        Location end = new Location(world, loc.getX()+2, 0, loc.getZ()+1);
        int xMin = (int)start.getX();
        int xMax = (int)end.getX();
        int zMin = (int)start.getZ();
        int zMax = (int)end.getZ();

        if(Y >= 4){
            return;
        }

        for(int x = xMin; x <= xMax; x++)
        {
            for(int z = zMin; z <= zMax; z++)
            {
                if(x == xMax){
                    setupTeam(loc, Y+1);
                    return;
                }
                Block block = world.getBlockAt(x,Y,z);
                block.setType(Material.AIR);
            }
        }
    }

    private void stopTasks(){
        for(int id : this.Tasks){
            if(Bukkit.getScheduler().isCurrentlyRunning(id)){
                Bukkit.getScheduler().cancelTask(id);
            }
        }
        this.Tasks.clear();
    }
}
