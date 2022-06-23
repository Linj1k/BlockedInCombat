package fr.kinj14.blockedincombat.Tasks;

import fr.kinj14.blockedincombat.Enums.FinishType;
import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Main;
import fr.kinj14.blockedincombat.Manager.Settings.SettingsSave;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;

public class Game extends BukkitRunnable {
    protected final Main main = Main.getInstance();
    private int gameTime;
    private int pvpTime;
    private int glowingTime;
    private boolean showGlowing = false;

    public Game() {
        final SettingsSave settings = main.getSettingsManager().getConfig();
        gameTime = settings.getGameTime() * 60;
        pvpTime = settings.getCombatTime() * 60;
        glowingTime = settings.getGlowingTime() * 60;
    }

    @Override
    public void run() {
        gameTime--;

        if(pvpTime > 0 && !main.getSettingsManager().isPVP()){
            pvpTime--;
        }

        if(showGlowing && glowingTime > 0){
            glowingTime--;
        }

        if(!main.devmode && (!main.getTeamsManager().CheckForStart() || Bukkit.getOnlinePlayers().size() <= 1)) {
            main.FinishGame(FinishType.NOPLAYERS);
            cancel();
            return;
        }

        for(Player pls : Bukkit.getOnlinePlayers()) {
            main.getScoreboardManager().updateTime(pls, new SimpleDateFormat(Lang.PLUGIN_TIMERFORMAT.get()).format(gameTime*1000));
            if(showGlowing){
                main.getScoreboardManager().updatePVP(pls, Lang.SCOREBOARD_GLOWING.get().replace("{time}", new SimpleDateFormat(Lang.PLUGIN_TIMERFORMAT.get()).format(glowingTime*1000)));
            } else {
                main.getScoreboardManager().updatePVP(pls, Lang.SCOREBOARD_PVP.get().replace("{time}", new SimpleDateFormat(Lang.PLUGIN_TIMERFORMAT.get()).format(pvpTime*1000)));
            }
        }

        if(pvpTime == 0 && !main.getSettingsManager().isPVP()){
            //Activate PVP;
            showGlowing = true;
            main.getSettingsManager().setPVP(true);
            Bukkit.broadcastMessage(main.getPrefix()+ Lang.GAMESTATE_PVPACTIVATE.get());
        }

        if(glowingTime == 0 && !main.getSettingsManager().isGlowing()){
            //Activate Glowing;
            main.getTeamsManager().setGlowingPlayers(true);
            Bukkit.broadcastMessage(main.getPrefix()+Lang.GAMESTATE_GLOWINGACTIVATE.get());
        }

        if(gameTime == 0) {
            main.FinishGame(FinishType.EQUALITY);
            cancel();
            return;
        }
    }
}
