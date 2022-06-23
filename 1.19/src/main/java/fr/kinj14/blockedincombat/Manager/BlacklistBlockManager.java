package fr.kinj14.blockedincombat.Manager;

import fr.kinj14.blockedincombat.Main;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class BlacklistBlockManager {
    protected final Main main = Main.getInstance();
    private final List<Location> BlacklistLock = new ArrayList<>();

    public BlacklistBlockManager() {
        BlacklistLock.clear();

        BlacklistLock.add(new Location(main.world, 47, 10, 76));
        BlacklistLock.add(new Location(main.world, 47, 11, 76));
        BlacklistLock.add(new Location(main.world, 47, 9, 76));

        BlacklistLock.add(new Location(main.world, 49, 9, 82));
        BlacklistLock.add(new Location(main.world, 49, 8, 82));
        BlacklistLock.add(new Location(main.world, 49, 10, 82));
        BlacklistLock.add(new Location(main.world, 49, 11, 82));
        BlacklistLock.add(new Location(main.world, 50, 10, 82));
        BlacklistLock.add(new Location(main.world, 48, 10, 82));
    }

    public boolean canBuild(Location loc){
        for(Location l : this.BlacklistLock){
            if(l.getX() == loc.getBlockX() && l.getY() == loc.getBlockY() && l.getZ() == loc.getBlockZ()){
                return true;
            }
        }
        return false;
    }
}
