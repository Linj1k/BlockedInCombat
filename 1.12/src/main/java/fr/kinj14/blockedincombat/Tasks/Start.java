package fr.kinj14.blockedincombat.Tasks;

import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;

public class Start extends BukkitRunnable {
    protected final Main main = Main.getInstance();
    private int timer = 15;

    @Override
    public void run() {
        if(!main.CanStart() || !main.getTeamsManager().CheckForStart() || Bukkit.getOnlinePlayers().size() <= 1) {
            main.ReturnLobby();
            cancel();
            return;
        }
        timer--;

        for(Player pls : Bukkit.getOnlinePlayers()) {
            String timerdate = new SimpleDateFormat(Lang.PLUGINÙTIMER_FORMAT.get()).format(timer*1000);
            main.getScoreboardManager().updateTime(pls, timerdate);
            pls.setLevel(timer);
        }

        if(timer == 0) {
            main.SetupGame();
            cancel();
        }
    }
}

