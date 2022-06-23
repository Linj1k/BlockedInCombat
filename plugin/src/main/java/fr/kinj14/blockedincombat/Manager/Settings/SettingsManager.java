package fr.kinj14.blockedincombat.Manager.Settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonReader;
import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.RenderType;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SettingsManager {
    protected final Main main = Main.getInstance();
    private final File folder = new File(main.getDataFolder(), "presets");
    private final Map<String, SettingsSave> Saves = new HashMap<>();
    private final Gson gson;

    private boolean PVP = false;
    private boolean Glowing = false;

    private SettingsSave Config;

    public SettingsManager(){
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .disableHtmlEscaping().create();

        folder.mkdir();

        Config = SettingsSave.createDefault();
        if(!new File(folder, Config.getName()+".json").exists()){
            save(Config);
        } else {
            loadAllFiles();
        }

        setTabHealth(Config.getTabHealth());
        setUHCMode(Config.getUHCMode());
    }

    public SettingsSave getConfig() {
        return this.Config;
    }

    public void setConfig(SettingsSave config){
        this.Config = config;
    }

    public void setTabHealth(boolean health) {
        getConfig().setTabHealth(health);
        Scoreboard sb = Objects.requireNonNull(Bukkit.getScoreboardManager()).getMainScoreboard();

        Objective obj = sb.getObjective("showhealth");

        if(health){
            if(obj == null){
                final String renderType = main.getConfigManager().getStringConfig("General.TabHealthRenderType");

                String dName = ChatColor.RED + "\u2665";
                obj = sb.registerNewObjective("showhealth", "health", dName, renderType.equalsIgnoreCase("hearts") ? RenderType.HEARTS : RenderType.INTEGER);
                obj.setDisplaySlot(DisplaySlot.PLAYER_LIST);

                for(Player player : Bukkit.getOnlinePlayers()){
                    player.setHealth(player.getHealth()-0.01);
                }
            }
        } else {
            if(obj != null){
                obj.unregister();
            }
        }
    }

    public void setUHCMode(boolean UHCMode) {
        getConfig().setUHCMode(UHCMode);

        main.world.setGameRule(GameRule.NATURAL_REGENERATION, UHCMode);
    }

    public void setGameTime(int gameTime) {
        if(gameTime <= 0){return;}
        getConfig().setGameTime(gameTime);
    }

    public void setCombatTime(int combatTime) {
        if(combatTime <= 0){return;}
        getConfig().setCombatTime(combatTime);
    }

    public void setGlowingTime(int glowingTime) {
        if(glowingTime <= 0){return;}
        getConfig().setGlowingTime(glowingTime);
    }

    public void setArenaDelay(boolean ArenaDelay) {
        getConfig().setArenaDelay(ArenaDelay);
    }

    public Material getRandomBlock(){
        List<Material> temp = new ArrayList<>();
        for(Map.Entry<Material, Boolean> mat : getConfig().getBlocks().entrySet()){
            if(mat.getValue()){
                temp.add(mat.getKey());
            }
        }
        if (temp.size() > 0){
            return temp.get(ThreadLocalRandom.current().nextInt(temp.size()));
        }

        temp = new ArrayList<>();
        for(Map.Entry<Material, Boolean> mat : SettingsSave.createDefault().getBlocks().entrySet()){
            if(mat.getValue()){
                temp.add(mat.getKey());
            }
        }
        if (temp.size() > 0){
            return temp.get(ThreadLocalRandom.current().nextInt(temp.size()));
        }

        return Material.COBBLESTONE;
    }

    public Material getRandomItems(){
        List<Material> temp = new ArrayList<>();
        for(Map.Entry<Material, Boolean> mat : getConfig().getItems().entrySet()){
            if(mat.getValue()){
                temp.add(mat.getKey());
            }
        }
        return temp.get(ThreadLocalRandom.current().nextInt(temp.size()));
    }

    public boolean isPVP() {
        return PVP;
    }

    public void setPVP(boolean PVP) {
        this.PVP = PVP;
    }

    public boolean isGlowing() {
        return Glowing;
    }

    public void setGlowing(boolean glowing) {
        Glowing = glowing;
    }

    public void setExpMultiplier(int expMultiplier) {
        if(expMultiplier <= 0){return;}
        getConfig().setExpMultiplier(expMultiplier);
    }

    public static String booleanToString(boolean bool){
        if(bool){
            return Lang.CONFIG_ACTIVATED.get();
        } else {
            return Lang.CONFIG_DISABLED.get();
        }
    }

    public File getFolder() {
        return folder;
    }

    public Map<String, SettingsSave> getSaves() {
        return Saves;
    }

    public SettingsSave getSave(String name){
        for(Map.Entry<String, SettingsSave> s : getSaves().entrySet()){
            if(s.getKey().equalsIgnoreCase(name) || s.getValue().getName().equalsIgnoreCase(name)){
                return s.getValue();
            }
        }
        return getConfig();
    }

    public boolean save(SettingsSave save){
        if(getSaves().size() >= 25){
            main.getLogger().severe(Main.PrefixDefault +": The number of profiles has been exceeded! (26)");
            return false;
        }
        if(save == null){return false;}

        String name = save.getName();
        try {
            FileWriter file = new FileWriter(new File(getFolder(), name+".json"));
            gson.toJson(save, file);
            file.close();

            loadAllFiles();
            return true;
        } catch (JsonIOException | IOException e) {
            main.getLogger().severe(Main.PrefixDefault +": An error occured while saving "+name+".json");
            e.printStackTrace();
            return false;
        }
    }

    public boolean load(String name){
        final File fn = new File(getFolder(), name+".json");
        if(!fn.exists()){
            main.getLogger().severe(Main.PrefixDefault +": An error occured while loading "+name+".json (The file was not found!)");
            return false;
        }
        try {
            FileReader FileReader = new FileReader(fn);
            JsonReader reader = new JsonReader(FileReader);
            SettingsSave data = gson.fromJson(reader, SettingsSave.class);

            reader.close();
            FileReader.close();

            if(data != null){
                getSaves().put(name, data);
                return true;
            }
            main.getLogger().severe(Main.PrefixDefault +": An error occured while loading "+name+".json");
            return false;
        } catch (IOException e) {
            main.getLogger().severe(Main.PrefixDefault +": An error occured while loading "+name+".json");
            e.printStackTrace();
            return false;
        }
    }

    public void loadAllFiles(){
        String[] FileList = getFolder().list();

        if (FileList != null) {
            getSaves().clear();
            for (String s : FileList) {
                load(s.replace(".json", ""));
            }
        } else {
            main.getLogger().severe(Main.PrefixDefault +": An error occurred when loading all configurations.");
        }
    }

    public boolean loadConfig(String name){
        for(Map.Entry<String, SettingsSave> s : getSaves().entrySet()){
            if(s.getKey().equalsIgnoreCase(name)){
                setConfig(s.getValue());
                setTabHealth(getConfig().getTabHealth());
                setUHCMode(getConfig().getUHCMode());

                main.getGuiManager().updateAll();
                return true;
            }
        }
        return false;
    }

    public boolean deleteConfig(String name, boolean Reload){
        final File fn = new File(getFolder(), name+".json");
        if(!fn.exists()){
            main.getLogger().severe(Main.PrefixDefault +": An error occured while delete "+name+".json (The file was not found!)");
            return false;
        }
        boolean successful;

        successful = fn.delete();

        if(Reload){
            loadAllFiles();
        }
        main.getLogger().info(Main.PrefixDefault +" "+name+".json delete successfully!");
        return successful;
    }
}
