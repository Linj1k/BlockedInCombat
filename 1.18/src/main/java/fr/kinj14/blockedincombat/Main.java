package fr.kinj14.blockedincombat;

import fr.kinj14.blockedincombat.Enums.*;
import fr.kinj14.blockedincombat.Library.*;
import fr.kinj14.blockedincombat.Manager.*;
import fr.kinj14.blockedincombat.Manager.Settings.SettingsManager;
import fr.kinj14.blockedincombat.Tasks.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import fr.kinj14.blockedincombat.Library.ServerVersion.Version;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
    //Instance
    private static Main instance = null;

    public static Main getInstance() {
        return instance;
    }

    // Spigot Information
    public String version = "1.0.3";
    public Version minVersion = Version.v1_13_R1;
    public String resourceID = "79717";

    public int bStatsID = 7741;

    //Manager
    private ConfigManager ConfigManager;
    private ItemsManager itemsManager;
    private fr.kinj14.blockedincombat.Manager.ScoreboardManager scoreboardManager;
    private TeamsManager TeamsManager;
    private justisr_BungeeCom BungeeComManager;
    private SettingsManager settingsManager;
    private ArenaManager arenaManager;
    private BlacklistBlockManager blacklistBlockManager;
    private GUIManager guiManager;

    // Config.yml Values
    public boolean devmode = false;
    public GameState Game_State;
    public String WorldName;
    public Logger logger;
    public Location lobby;
    public boolean BungeeCord = false;
    public String BungeeCord_Server = "";
    private final List<BukkitRunnable> Timers = new ArrayList<>();
    private final List<Player> PlayersBuild = new ArrayList<>();
    public Location ArenaLoc;

    @Override
    public void onEnable() {
        logger = Bukkit.getLogger();
        instance = this;
        System.setProperty("BlockedInCombatLoaded", "true");

        //load the Language
        Files lang = Files.LANG;
        lang.create();

        logger.info(getPrefix(true)+Lang.PLUGIN_INITIALIZATION.get());
        if(ServerVersion.Version.isCurrentLower(minVersion)){
            logger.severe(getPrefix(true)+Lang.PLUGIN_VERSIONNOTSUPPORTED.get().replace("{version}",minVersion.toString()));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // Load the configuration.
        saveDefaultConfig();
        ConfigManager = new ConfigManager();

        if(ConfigManager.getBooleanConfig("General.UsebStats")){
            new Metrics(this, bStatsID);
        }
        if(ConfigManager.getBooleanConfig("General.CheckUpdate")){
            if(ConfigManager.checkForUpdate()){
                logger.info(getPrefix(true)+"The plugin is not up to date! ("+ConfigManager.getDownloadVersion()+")");
            }
        }

        devmode = ConfigManager.getBooleanConfig("General.devmode");
        WorldName = ConfigManager.getStringConfig("General.WorldName");
        lobby = ConfigManager.deserializeConfigLocation(WorldName, "Locations.Lobby");
        BungeeCord = ConfigManager.getBooleanConfig("BungeeCord.Support");
        BungeeCord_Server = ConfigManager.getStringConfig("BungeeCord.Server");

        //Load Managers
        if(BungeeCord){
            BungeeComManager = new justisr_BungeeCom();
        }
        TeamsManager = new TeamsManager();
        itemsManager = new ItemsManager();
        scoreboardManager = new ScoreboardManager();
        settingsManager = new SettingsManager();
        arenaManager = new ArenaManager();
        blacklistBlockManager = new BlacklistBlockManager();
        guiManager = new GUIManager();

        PlayersBuild.clear();

        //Register (Listeners/Commands)
        RegistersManager register = new RegistersManager();
        register.registersListeners();
        register.registersCommands();

        //Setup World
        World w = Bukkit.getWorld(WorldName);
        if(w != null){
            w.setGameRule(GameRule.NATURAL_REGENERATION, true);
            w.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            w.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            w.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            w.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
            w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            w.setTime(6000L);
            w.setDifficulty(Difficulty.NORMAL);
            w.setWeatherDuration(999999999);
            w.setSpawnLocation(lobby);
        }

        ArenaLoc = new Location(Bukkit.getWorld(WorldName), 1040, 30, 1040);
        arenaManager.setupArena();
        arenaManager.clearArena();

        ReturnLobby();

        // Ping In Tab
        if(getConfigManager().getBooleanConfig("General.PingInTab")){
            int UpdatePingDelay = getConfigManager().getIntConfig("UpdatePing");
            if(UpdatePingDelay <= 0){
                UpdatePingDelay = 3;
            }
            Loop LoopRunnable = new Loop();
            Timers.add(LoopRunnable);
            LoopRunnable.runTaskTimer(this, 0, (long)UpdatePingDelay*20);
        }
    }

    @Override
    public void onDisable() {
        if(arenaManager != null){
            arenaManager.clearArena();
            arenaManager.setupClearArena();
        }

        for(Player player : Bukkit.getOnlinePlayers()){
            getScoreboardManager().destroy(player);
            player.setDisplayName(player.getName());
            player.setPlayerListName(player.getName());
        }

        for(BukkitRunnable timer : Timers){
            timer.cancel();
        }
        Timers.clear();
    }

    public String getPrefixDefault(){return "BlockedInCombat";}

    public String getPrefix(boolean noColor){return "["+getPrefixDefault()+"]";}
    public String getPrefix(){return "§7[§e"+getPrefixDefault()+"§7] §r";}

    public ConfigManager getConfigManager() {
        return ConfigManager;
    }

    public ItemsManager getItemsManager() {
        return itemsManager;
    }

    public TeamsManager getTeamsManager() {
        return TeamsManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public justisr_BungeeCom getBungeeComManager() {
        return BungeeComManager;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public BlacklistBlockManager getBlacklistBlockManager() {
        return blacklistBlockManager;
    }

    public List<Player> getPlayersBuild() {
        return PlayersBuild;
    }

    public GUIManager getGuiManager() {
        return guiManager;
    }

    public void StopTimers(){
        for(BukkitRunnable run : Timers){
            run.cancel();
        }

        String TimerDate = new SimpleDateFormat("mm:ss").format(0);
        for(Player player : Bukkit.getOnlinePlayers()) {
            getScoreboardManager().updateTime(player, TimerDate);
            player.setLevel(0);
        }
        Timers.clear();
    }

    public void PrepareGame(){
        if(CanStart() && GameState.isState(GameState.WAITING)){
            Start start = new Start();
            Timers.add(start);
            start.runTaskTimer(this, 0, 20);
            GameState.setState(GameState.STARTING);

            for(Player player : getTeamsManager().getPlayersNotInTeam()){
                player.setGameMode(GameMode.SPECTATOR);
                getTeamsManager().addPlayerInTeam(player, Teams.spectator);
            }
        }
    }

    public void SetupGame(){
        if(CanStart() && GameState.isState(GameState.STARTING)){
            BuildArena buildArena = new BuildArena();
            Timers.add(buildArena);
            buildArena.runTaskTimer(this, 0, 20);

            GameState.setState(GameState.GENERATE_MAP);
            getArenaManager().clearArena();
            ItemsManager.RemoveAllItems();

            for(Teams team : getTeamsManager().getInValidTeams()){
                getTeamsManager().getTeams().remove(team);
            }

            for(Player player : Bukkit.getOnlinePlayers()){
                player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 9999, 255, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 9999, 255, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 9999, 255, false, false));
                player.teleport(new Location(Bukkit.getWorld(WorldName),1040, 30, 1040, 0f, 90f));
                player.getInventory().clear();
                player.updateInventory();
                if(PlayerManager.hasPermission(player, "blockedincombat.ChangeSettings")){
                    TextComponent message = new TextComponent(Lang.GAMESTATE_GAMESTARTEDCANCEL.get());
                    message.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/cancel" ) );
                    player.spigot().sendMessage( message );
                }
            }

            Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> getArenaManager().buildArena(), 20L);
        } else {
            GameState.setState(GameState.WAITING);
        }
    }

    public void StartGame(){
        if(GameState.isState(GameState.WAITING)){
            return;
        }
        for(Teams team : getTeamsManager().getValidTeams()){
            getArenaManager().setupTeam(team.getSpawn());
        }

        ItemsManager.RemoveAllItems();

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            Game game = new Game();
            Timers.add(game);
            game.runTaskTimer(this, 0, 20);
            GameState.setState(GameState.PLAYING);

            for(Teams team : getTeamsManager().getTeams()){
                for(Player player : team.getPlayers()){
                    PlayerManager.clear(player);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 255, false, false));
                    if(team != Teams.spectator){
                        player.teleport(team.getSpawn());
                        player.setGameMode(GameMode.SURVIVAL);
                    } else {
                        player.setGameMode(GameMode.SPECTATOR);

                        PlayerManager.teleportInArena(player);
                    }
                }
            }
        }, 20L);
    }

    public void FinishGame(FinishType type){
        if(GameState.isState(GameState.FINISH)){return;}
        arenaManager.stopTasks();
        StopTimers();
        GameState.setState(GameState.FINISH);

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            Finish finish = new Finish();
            Timers.add(finish);
            finish.runTaskTimer(this, 0, 20);
        }, 20L);

        switch (type){
            case EQUALITY:
                Bukkit.broadcastMessage(getPrefix()+Lang.FINISH_EQUALITY.get());
                break;
            case TEAMS:
                Teams team = getTeamsManager().getValidTeams().get(0);
                Bukkit.broadcastMessage(getPrefix()+Lang.FINISH_TEAMS.get().replace("{team}", team.getColoredName()));

                for(Player player : team.getPlayers()){
                    FireWorks.SpawnFireWorks(player.getLocation());
                }
                break;
            case CANCEL:
                Bukkit.broadcastMessage(getPrefix()+Lang.FINISH_CANCEL.get());
                break;
            case NOPLAYERS:
                Bukkit.broadcastMessage(getPrefix()+Lang.FINISH_NOPLAYERS.get());
                break;
        }
    }

    public void ReturnLobby(){
        GameState.setState(GameState.WAITING);
        getSettingsManager().setPVP(false);
        getTeamsManager().setGlowingPlayers(false);
        getTeamsManager().setup();

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            for(Player player : Bukkit.getOnlinePlayers()){
                player.setLevel(0);
                PlayerManager.setupLobby(player);
                getScoreboardManager().updateTime(player, "00:00");
                getScoreboardManager().updatePVP(player, Lang.SCOREBOARD_PVP.get().replace("{time}", "00:00"));
            }
        }, 20L);
    }

    public void CheckWin(){
        if(!GameState.isState(GameState.PLAYING)){return;}
        if (!this.devmode){
            if(getTeamsManager().getPlayersCount() == 1) {
                FinishGame(FinishType.TEAMS);
                return;
            }

            if(getTeamsManager().getValidTeams().size() == 1){
                FinishGame(FinishType.TEAMS);
                return;
            }
        }

        if(getTeamsManager().getValidTeams().size() == 0){
            FinishGame(FinishType.EQUALITY);
        }
    }

    public boolean CanStart(){
        if (this.devmode){
            return !GameState.isState(GameState.PLAYING) && !GameState.isState(GameState.FINISH);
        } else{
            return getTeamsManager().CheckForStart() && !GameState.isState(GameState.PLAYING) && !GameState.isState(GameState.FINISH);
        }
    }
}
