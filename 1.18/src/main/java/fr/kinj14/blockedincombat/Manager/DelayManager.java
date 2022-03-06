package fr.kinj14.blockedincombat.Manager;

import fr.kinj14.blockedincombat.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class DelayManager {
    protected final Main main = Main.getInstance();
    public Map<Player,Integer> DelayList = new HashMap<>();
    public void AddDelay(Player player, Integer delay) {
        if(CheckDelay(player,delay)) {return;}
        DelayList.put(player, delay);
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
            public void run() {
                if(DelayList.containsKey(player) && DelayList.containsValue(delay)){
                    DelayList.remove(player, delay);
                }
            }
        }, 0L+delay);
    }

    public Boolean CheckDelay(Player player, Integer delay) {
        return DelayList.containsKey(player) && DelayList.containsValue(delay);
    }
}
