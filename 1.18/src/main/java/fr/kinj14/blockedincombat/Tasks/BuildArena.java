package fr.kinj14.blockedincombat.Tasks;

import fr.kinj14.blockedincombat.Enums.FinishType;
import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;

public class BuildArena extends BukkitRunnable {
    protected final Main main = Main.getInstance();
    private int timer = 48;

    public BuildArena() {
        if(main.getSettingsManager().getConfig().getArenaDelay()){
            this.timer = 48;
        } else {
            this.timer = 6;
        }
    }

    @Override
    public void run() {
        if(!main.devmode && (main.getTeamsManager().getValidTeams().size() == 1 || !main.getTeamsManager().CheckForStart() || Bukkit.getOnlinePlayers().size() <= 1)) {
            main.FinishGame(FinishType.CANCEL);
            cancel();
            return;
        }

        timer--;

        for(Player pls : Bukkit.getOnlinePlayers()) {
            String timerdate = new SimpleDateFormat(Lang.PLUGIN_TIMERFORMAT.get()).format(timer*1000);
            main.getScoreboardManager().updateTime(pls, timerdate);
            pls.setLevel(timer);
        }

        if(timer == 5 || timer == 3 || timer == 2 || timer == 1) {
            Bukkit.broadcastMessage(main.getPrefix()+ Lang.GAMESTATE_GAMESTART.get().replace("{time}", String.valueOf(timer)));
        }

        if(timer == 0) {
            main.getArenaManager().finishArea();
            main.StartGame();
            cancel();
        }
    }
}
