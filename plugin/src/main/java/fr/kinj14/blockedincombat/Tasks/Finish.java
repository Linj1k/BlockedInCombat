package fr.kinj14.blockedincombat.Tasks;

import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;

public class Finish extends BukkitRunnable {
    protected final Main main = Main.getInstance();
    private int timer;

    public Finish(int customTime) {
        this.timer = customTime;
    }

    @Override
    public void run() {
        timer--;

        for(Player pls : Bukkit.getOnlinePlayers()) {
            String timerdate = new SimpleDateFormat(Lang.PLUGIN_TIMERFORMAT.get()).format(timer*1000);
            main.getScoreboardManager().updateTime(pls, timerdate);
            pls.setLevel(timer);
        }

        if(timer == 0) {
            if(main.BungeeCord){
                for(Player player : Bukkit.getOnlinePlayers()){
                    main.getBungeeComManager().sendConnect(player, main.BungeeCord_Server);
                }
            }
            main.ReturnLobby();
            cancel();
            return;
        }
    }
}
