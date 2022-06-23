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
        if(!main.devmode && (!main.CanStart() || !main.getTeamsManager().CheckForStart() || Bukkit.getOnlinePlayers().size() <= 1)) {
            main.ReturnLobby();
            cancel();
            return;
        }
        timer--;

        if(timer == 0) {
            String[] arenagen = Lang.PLUGIN_ARENAGENERATION.get().split(" \\n ");
            for(Player pls : Bukkit.getOnlinePlayers()) {
                pls.sendTitle(arenagen[0],arenagen[1],0,40,0);
            }
            main.SetupGame();
            cancel();
            return;
        }

        for(Player pls : Bukkit.getOnlinePlayers()) {
            String timerDate = new SimpleDateFormat(Lang.PLUGIN_TIMERFORMAT.get()).format(timer*1000);
            main.getScoreboardManager().updateTime(pls, timerDate);
            pls.setLevel(timer);

            if(timer == 5 && main.getTeamsManager().getPlayersNotInTeam().contains(pls)){
                pls.sendMessage(Main.getPrefix()+Lang.TEAMS_NOTINTEAM.get());
            }

            if(timer <= 5) {
                pls.sendTitle(String.valueOf(timer),"",0,40,0);
            }
        }
    }
}

