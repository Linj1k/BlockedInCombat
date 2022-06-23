package fr.kinj14.blockedincombat.Tasks;

import fr.kinj14.blockedincombat.Main;
import org.bukkit.scheduler.BukkitRunnable;

public class PingInTab extends BukkitRunnable {
    protected final Main main = Main.getInstance();

    @Override
    public void run(){
        if(main.getConfigManager().getBooleanConfig("General.PingInTab")) {
            main.getScoreboardManager().updatePingToAllPlayers();
        } else {
            cancel();
        }
    }
}