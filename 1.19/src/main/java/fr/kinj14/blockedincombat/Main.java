package fr.kinj14.blockedincombat;

import fr.kinj14.blockedincombat.Data.PlayerData;
import fr.kinj14.blockedincombat.Enums.*;
import fr.kinj14.blockedincombat.Events.*;
import fr.kinj14.blockedincombat.Library.*;
import fr.kinj14.blockedincombat.Manager.*;
import fr.kinj14.blockedincombat.Manager.Settings.SettingsManager;
import fr.kinj14.blockedincombat.Manager.Settings.SettingsSave;
import fr.kinj14.blockedincombat.Tasks.*;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import fr.kinj14.blockedincombat.Library.ServerVersion.Version;
import org.bukkit.scoreboard.Objective;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

public class Main extends JavaPlugin {
    //Instance
    private static Main instance = null;
    public static Main getInstance() {
        return instance;
    }

    // Spigot Information
    public final String version = getDescription().getVersion();
    public boolean IsUpToDate = true;
    public final Version minVersion = Version.v1_13_R1;
    public final String resourceID = "79717";

    public final int bStatsID = 7741;

    // Managers
    private ConfigManager ConfigManager;
    private ItemsManager itemsManager;
    private ScoreboardManager scoreboardManager;
    private PlayerManager PlayerManager;
    private TeamsManager TeamsManager;
    private justisr_BungeeCom BungeeComManager;
    private SettingsManager settingsManager;
    private ArenaManager arenaManager;
    private BlacklistBlockManager blacklistBlockManager;
    private GUIManager guiManager;
    public ItemsUtils itemsUtils;

    // Config.yml Values
    public boolean devmode = false;
    public GameState Game_State;
    public String WorldName;
    public Location lobby;
    public boolean BungeeCord = false;
    public String BungeeCord_Server = "";
    public Location ArenaLoc;

    // Variables
    private Logger logger;
    public World world;
    private final List<BukkitRunnable> Timers = new ArrayList<>();
    private final List<Player> PlayersBuild = new ArrayList<>();

    @Override
    public void onEnable() {
        instance = this;
        logger = Bukkit.getLogger();

        String BlockedInCombatLoaded = System.getProperty("BlockedInCombatLoaded");
        boolean AlreadyLoaded = BlockedInCombatLoaded == null || BlockedInCombatLoaded.equals("true");
        System.setProperty("BlockedInCombatLoaded", "true");

        // - load the Language -
        Files lang = Files.LANG;
        lang.setFolder(getDataFolder());
        lang.create();

        // Check Version
        logger.info(getPrefix(true)+" "+Lang.PLUGIN_INITIALIZATION.get());
        if(ServerVersion.Version.isCurrentLower(minVersion)){
            logger.severe(getPrefix(true)+" "+Lang.PLUGIN_VERSIONNOTSUPPORTED.get().replace("{version}",minVersion.toString()));
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }

        // - Load the configuration -

        // Load spigot.yml (For settings.bungeecord)
        Files spigotConfigFile = Files.Spigot;
        spigotConfigFile.setFolder(getServer().getWorldContainer());
        FileConfiguration spigotConfig = spigotConfigFile.getConfig();

        // Load server.properties (For level-name)
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("server.properties"));
            WorldName = props.getProperty("level-name");
        } catch (IOException e) {
            WorldName = "world";
            logger.severe(getPrefix(true)+" Unable to retrieve the name of the world (Default: world)");
            e.printStackTrace();
        }
        world = Bukkit.getWorld(WorldName);

        saveDefaultConfig();
        ConfigManager = new ConfigManager();
        ConfigManager.RetroCompatibility();

        if(ConfigManager.getBooleanConfig("General.UsebStats")){
            new Metrics(this, bStatsID);
        }
        if(ConfigManager.getBooleanConfig("General.CheckUpdate")){
            if(ConfigManager.checkForUpdate()){
                IsUpToDate = false;
                logger.warning(getPrefix(true)+" "+Lang.PLUGIN_NOTUPTODATE.get()+" ("+ConfigManager.getDownloadVersion()+")");
            }
        }

        devmode = ConfigManager.getBooleanConfig("General.devmode");
        lobby = ConfigManager.deserializeConfigLocation(WorldName, "Locations.Lobby");
        BungeeCord = spigotConfig.getBoolean("settings.bungeecord");
        BungeeCord_Server = ConfigManager.getStringConfig("BungeeCord.Server");

        logger.info(getPrefix(true)+" Config loaded !");

        // - Load Managers -
        if(BungeeCord){
            BungeeComManager = new justisr_BungeeCom();
            logger.info(getPrefix(true)+" BungeeCord Initialized !");
        }
        TeamsManager = new TeamsManager();
        PlayerManager = new PlayerManager();
        itemsManager = new ItemsManager();
        scoreboardManager = new ScoreboardManager();
        settingsManager = new SettingsManager();
        arenaManager = new ArenaManager();
        blacklistBlockManager = new BlacklistBlockManager();
        guiManager = new GUIManager();
        itemsUtils = new ItemsUtils();

        logger.info(getPrefix(true)+" Managers Loaded !");

        // - Register (Listeners/Commands) -
        RegistersManager register = new RegistersManager();
        register.registersListeners();
        register.registersCommands();

        logger.info(getPrefix(true)+" Registers Loaded !");

        // - Setup World -
        PlayersBuild.clear();
        if(world != null){
            world.setGameRule(GameRule.NATURAL_REGENERATION, true);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            world.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            world.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false);
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
            world.setTime(6000L);
            world.setDifficulty(Difficulty.NORMAL);
            world.setWeatherDuration(999999999);
            world.setSpawnLocation(lobby);

            logger.info(getPrefix(true)+" World Setup !");

            // Initialize Arena
            ArenaLoc = new Location(world, 1040, 30, 1040);
            arenaManager.setupArena();
            arenaManager.clearArena();

            logger.info(getPrefix(true)+" Arena Setup !");
        } else {
            logger.severe(getPrefix(true)+" Unable to setup the world !");
        }

        ReturnLobby();

        // - Ping In Tab -
        if(getConfigManager().getBooleanConfig("General.PingInTab")){
            PingInTab PingInTabRunnable = new PingInTab();
            Timers.add(PingInTabRunnable);
            PingInTabRunnable.runTaskTimer(this, 0, (long)6*20);
        }

        // Load The Players Stats if plugin has already been loaded
        if(AlreadyLoaded){
            for(Player player : Bukkit.getOnlinePlayers()){
                if(!PlayerManager.hasPlayerData(player)){
                    PlayerManager.loadPlayerData(player);
                }
            }
            logger.info(getPrefix(true)+" All players stats has been loaded after a plugin reload! !");
        }

        Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "BlockedInCombat is fully charged and ready");
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

            PlayerData playerData = PlayerManager.getPlayerData(player);
            if(playerData != null){
                playerData.saveData(PlayerManager.getPlayerDataPath(player.getUniqueId()));
            }
        }

        for(BukkitRunnable timer : Timers){
            timer.cancel();
        }
        Timers.clear();

        Objective obj = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard().getObjective("showhealth");
        if(obj != null){ obj.unregister(); }
    }

    public static final String PrefixDefault = "BlockedInCombat";

    public static String getPrefix(boolean noColor){return "["+PrefixDefault+"]";}
    public static String getPrefix(){return "§7[§e"+PrefixDefault+"§7] §r";}

    public ConfigManager getConfigManager() {
        return ConfigManager;
    }

    public boolean canUsePlayersStats(){
        return getConfigManager().getBooleanConfig("General.PlayerStats");
    }

    public ItemsManager getItemsManager() {
        return itemsManager;
    }

    public TeamsManager getTeamsManager() {
        return TeamsManager;
    }

    public PlayerManager getPlayerManager() {
        return PlayerManager;
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
            OnPrepareGameEvent onPrepareGameEvent = new OnPrepareGameEvent(getTeamsManager().getTeams());
            Bukkit.getPluginManager().callEvent(onPrepareGameEvent);
            if (onPrepareGameEvent.isCancelled()) {return;}

            Start start = new Start();
            Timers.add(start);
            start.runTaskTimer(this, 0, 20);
            GameState.setState(GameState.STARTING);

            for(Player player : getTeamsManager().getPlayersNotInTeam()){
                player.sendMessage(getPrefix()+Lang.TEAMS_NOTINTEAM.get());
                getTeamsManager().addPlayerInTeam(player, Teams.spectator);
            }
        }
    }

    public void SetupGame(){
        if(CanStart() && GameState.isState(GameState.STARTING)){
            OnSetupGameEvent onSetupGameEvent = new OnSetupGameEvent();
            Bukkit.getPluginManager().callEvent(onSetupGameEvent);
            if (onSetupGameEvent.isCancelled()) {
                FinishGame(FinishType.CANCEL);
                return;
            }

            BuildArena buildArena = new BuildArena();
            Timers.add(buildArena);
            buildArena.runTaskTimer(this, 0, 20);

            GameState.setState(GameState.GENERATE_MAP);
            getArenaManager().clearArena();
            itemsManager.RemoveAllItems();

            for(Teams team : getTeamsManager().getInValidTeams()){
                getTeamsManager().getTeams().remove(team);
            }

            for(Player player : Bukkit.getOnlinePlayers()){
                player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, Integer.MAX_VALUE, 255, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, Integer.MAX_VALUE, 255, false, false));
                player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 255, false, false));
                player.teleport(new Location(world,1040, 30, 1040, 0f, 90f));
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
        if(GameState.isState(GameState.WAITING)){return;}

        OnStartGameEvent onStartGameEvent = new OnStartGameEvent();
        Bukkit.getPluginManager().callEvent(onStartGameEvent);
        if (onStartGameEvent.isCancelled()) {
            FinishGame(FinishType.CANCEL);
            return;
        }

        SettingsSave settings = getSettingsManager().getConfig();
        boolean sameBonusChest = settings.getBonusChest() && settings.getSameLootChest();
        ArrayList<ItemStack> itemsList = sameBonusChest ? getArenaManager().createRandomBonusChest() : new ArrayList<>();

        for(Teams team : getTeamsManager().getValidTeams()){
            if(sameBonusChest){
                getArenaManager().setupTeam(team.getSpawn(), itemsList);
            } else {
                getArenaManager().setupTeam(team.getSpawn());
            }
        }

        itemsManager.RemoveAllItems();

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            Game game = new Game();
            Timers.add(game);
            game.runTaskTimer(this, 0, 20);
            GameState.setState(GameState.PLAYING);

            for(Teams team : getTeamsManager().getTeams()){
                for(Player player : team.getPlayers()){
                    PlayerManager.clear(player);
                    player.setLevel(0);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, Integer.MAX_VALUE, 255, false, false));
                    if(team != Teams.spectator){
                        player.teleport(team.getSpawn());
                        player.setGameMode(GameMode.SURVIVAL);
                    } else {
                        player.setGameMode(GameMode.SPECTATOR);

                        PlayerManager.teleportInArena(player);
                    }
                    player.sendTitle(Lang.PLUGIN_GOSTARTGAME.get(),Lang.PLUGIN_GOSTARTGAMETEXT.get(),5,40,5);
                }
            }
        }, 20L);
    }

    public void FinishGame(FinishType type){
        if(GameState.isState(GameState.FINISH)){return;}

        FinishEvent finishEvent = new FinishEvent(type);
        Bukkit.getPluginManager().callEvent(finishEvent);
        if (finishEvent.isCancelled()) {return;}

        arenaManager.stopTasks();
        StopTimers();
        GameState.setState(GameState.FINISH);

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            Finish finish = new Finish(type == FinishType.CANCEL ? 3 : 10);
            Timers.add(finish);
            finish.runTaskTimer(this, 0, 20);
        }, 20L);

        switch (type){
            case EQUALITY:
                scoreboardManager.broadcastTitle(Lang.FINISH_EQUALITY.get(),"",10,70,10);
                Bukkit.broadcastMessage(getPrefix()+Lang.FINISH_EQUALITY.get());
                break;
            case TEAMS:
                Teams team = getTeamsManager().getValidTeams().get(0);

                WinEvent winEvent = new WinEvent(team, team.getPlayers());
                Bukkit.getPluginManager().callEvent(winEvent);

                String langText = Lang.FINISH_TEAMS.get().replace("{team}", team.getColoredName());

                scoreboardManager.broadcastTitle(langText,"",10,70,10);
                Bukkit.broadcastMessage(getPrefix()+langText);

                for(Player player : Bukkit.getOnlinePlayers()){
                    if(team.getPlayers().contains(player)){
                        FireWorks.SpawnFireWorks(player.getLocation());
                        if(canUsePlayersStats()){
                            PlayerData playerStats = getPlayerManager().getPlayerData(player);
                            if(playerStats != null){
                                playerStats.Wins = playerStats.Wins + 1;
                            }
                        }
                    } else if(getTeamsManager().getPlayerTeam(player) != null && canUsePlayersStats()) {
                        PlayerData playerStats = getPlayerManager().getPlayerData(player);
                        if(playerStats != null){
                            playerStats.Losts = playerStats.Losts + 1;
                        }
                    }
                }
                break;
            case CANCEL:
                Bukkit.broadcastMessage(getPrefix()+Lang.FINISH_CANCEL.get());
                break;
            case NOPLAYERS:
                scoreboardManager.broadcastTitle(Lang.FINISH_NOPLAYERS.get(),"",10,70,10);
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
