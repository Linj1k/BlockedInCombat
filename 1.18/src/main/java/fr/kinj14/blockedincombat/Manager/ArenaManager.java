package fr.kinj14.blockedincombat.Manager;

import fr.kinj14.blockedincombat.Enums.GameState;
import fr.kinj14.blockedincombat.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ArenaManager {
    protected final Main main = Main.getInstance();
    public final Location StartLocation;
    public final Location EndLocation;
    private final List<Integer> Tasks = new ArrayList<>();

    public ArenaManager() {
        StartLocation = new Location(main.world, 1054, 2, 1054);
        EndLocation = new Location(main.world, 1025, 11, 1025);
        Tasks.clear();
    }

    //Setup Arena
    public void setupArena(){
        buildBlock(Material.BEDROCK, 1025, 1055, 1025,1054, 0, 0);

        //X
        buildWall(Material.BEDROCK, 1024, 1055, 1024,1, 1, 12);
        buildWall(Material.BEDROCK, 1024, 1055, 1055,1, 1, 12);

        //Z
        buildWall(Material.BEDROCK, 1055, 1, 1024,1056, 1, 12);
        buildWall(Material.BEDROCK, 1024, 1, 1024,1055, 1, 12);
    }

    public void setupClearArena(){
        buildBlock(Material.AIR, 1024, 1056, 1024,1055, 0, 12);
    }

    public void buildBlock(Material mat, int xMin, int xMax, int zMin, int zMax, int yMin, int yMax){
        buildBlock(mat, xMin, xMax, zMin, zMax, yMin, yMax, true);
    }

    public void buildBlock(Material mat, int xMin, int xMax, int zMin, int zMax, int Y, int yMax, boolean fakeBoolean){
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
                Block block = main.world.getBlockAt(x,Y,z);
                block.setType(mat);
            }
        }
    }

    public void buildWall(Material mat, int xMin, int xMax, int zMin, int zMax, int yMin, int yMax){
        buildWall(mat, xMin, xMax, zMin, zMax, yMin, yMax, true);
    }

    public void buildWall(Material mat, int xMin, int xMax, int zMin, int zMax, int Y, int yMax, boolean fakeBoolean){
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
                Block block = main.world.getBlockAt(x,Y,zMin);
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
                Block block = main.world.getBlockAt(xMin,Y,z);
                block.setType(mat);
            }
        }
    }

    //Arena
    public void clearArena(){
        stopTasks();
        buildBlock(Material.AIR, 1025, 1055, 1025,1054, 1, 33);
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

        if(Y == 12){
            return;
        }

        long delay = 0;
        int tmpDelay = 5;

        for(int x = xMin; x <= xMax; x++)
        {
            if(!GameState.isState(GameState.GENERATE_MAP)){return;}
            for(int z = zMin; z <= zMax; z++)
            {
                if(!GameState.isState(GameState.GENERATE_MAP)){return;}
                if(x == xMax){
                    buildArena(Y+1);
                    return;
                }
                Block block = main.world.getBlockAt(x,Y,z);
                if(main.getSettingsManager().getConfig().getArenaDelay()){
                    tmpDelay++;
                    if(tmpDelay >= 6){
                        tmpDelay = 0;
                        delay = delay + 1L;
                    }
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
        buildBlock(Material.BEDROCK, (int)EndLocation.getX(), (int)StartLocation.getX()+1, (int)EndLocation.getZ(),(int)StartLocation.getZ(), 12, 12);
    }

    // Create Random Bonus Chest
    public ArrayList<ItemStack> createRandomBonusChest(){
        ArrayList<ItemStack> itemsList = new ArrayList<>();

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for(int i=0; i <= random.nextInt(25); i++){
            ItemStack item = new ItemStack(main.getSettingsManager().getRandomItems(), random.nextInt(7));
            if(main.itemsUtils.ItemIsTool(item)){
                item.setAmount(1);
            }

            itemsList.add(item);
        }

        return itemsList;
    }

    //Setup team Spawn
    public void setupTeam(Location loc, ArrayList<ItemStack> itemsList){
        Location start = new Location(main.world, loc.getX()-1, 0, loc.getZ()-1);
        Location end = new Location(main.world, loc.getX()+2, 0, loc.getZ()+1);

        buildBlock(Material.AIR, (int)start.getX(), (int)end.getX(), (int)start.getZ(),(int)end.getZ(), (int)loc.getY(), 4);

        if(main.getSettingsManager().getConfig().getBonusChest()){
            loc = new Location(main.world, loc.getX(), loc.getY()-1, loc.getZ());
            Block block = main.world.getBlockAt(loc);
            block.setType(Material.CHEST);
            Chest chest = (Chest) block.getState();

            ThreadLocalRandom random = ThreadLocalRandom.current();
            for (ItemStack item: itemsList) {
                chest.getBlockInventory().setItem(random.nextInt(chest.getBlockInventory().getSize()-1), item);
            }
        }
    }

    public void setupTeam(Location loc){
        setupTeam(loc, main.getSettingsManager().getConfig().getBonusChest() ? createRandomBonusChest() : new ArrayList<>());
    }

    public void stopTasks(){
        for(int id : this.Tasks){
            //if(Bukkit.getScheduler().isCurrentlyRunning(id)){
                Bukkit.getScheduler().cancelTask(id);
            //}
        }
        this.Tasks.clear();
    }
}
