package fr.kinj14.blockedincombat.Manager.Settings;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.stream.JsonReader;
import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class SettingsManager {
    protected final Main main = Main.getInstance();
    private final File folder = new File(main.getDataFolder(), "data");
    private Map<String, SettingsSave> Saves = new HashMap<>();
    private Gson gson;

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
        Scoreboard sb = Bukkit.getScoreboardManager().getMainScoreboard();

        Objective obj = sb.getObjective("showhealth");

        if(health){
            if(obj == null){
                String dName = ChatColor.RED + "\u2665";
                obj = sb.registerNewObjective("showhealth", "health");
                obj.setDisplayName(dName);
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

        Bukkit.getWorld(main.WorldName).setGameRuleValue("naturalRegeneration", String.valueOf(UHCMode));
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

    public Material getRandomBlock(){
        List<Material> temp = new ArrayList<>();
        for(Map.Entry<Material, Boolean> mat : getConfig().getBlocks().entrySet()){
            if(mat.getValue()){
                temp.add(mat.getKey());
            }
        }
        return temp.get(ThreadLocalRandom.current().nextInt(temp.size()));
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
            return Lang.CONFIGÙACTIVATED.get();
        } else {
            return Lang.CONFIGÙDISABLED.get();
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
            main.logger.severe(main.getPrefixDefault()+": The number of profiles has been exceeded! (26)");
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
        } catch (JsonIOException e) {
            main.logger.severe(main.getPrefixDefault()+": An error occured while saving "+name+".json");
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            main.logger.severe(main.getPrefixDefault()+": An error occured while saving "+name+".json");
            e.printStackTrace();
            return false;
        }
    }

    public boolean load(String name){
        final File fn = new File(getFolder(), name+".json");
        if(!fn.exists()){
            main.logger.severe(main.getPrefixDefault()+": An error occured while loading "+name+".json (The file was not found!)");
            return false;
        }
        try {
            JsonReader reader = new JsonReader(new FileReader(fn));
            SettingsSave data = gson.fromJson(reader, SettingsSave.class);

            if(data != null){
                getSaves().put(name, data);
                return true;
            }
            main.logger.severe(main.getPrefixDefault()+": An error occured while loading "+name+".json");
            return false;
        } catch (IOException e) {
            main.logger.severe(main.getPrefixDefault()+": An error occured while loading "+name+".json");
            e.printStackTrace();
            return false;
        }
    }

    public void loadAllFiles(){
        String FileList[] = getFolder().list();

        if (FileList != null) {
            getSaves().clear();
            for (int i = 0; i < FileList.length; i++) {
                load(FileList[i].replace(".json", ""));
            }
        } else {
            main.logger.severe(main.getPrefixDefault()+": An error occurred when loading all configurations.");
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
            main.logger.severe(main.getPrefixDefault()+": An error occured while delete "+name+".json (The file was not found!)");
            return false;
        }

        fn.delete();

        if(Reload){
            loadAllFiles();
        }
        main.logger.info(main.getPrefixDefault()+" "+name+".json delete successfully!");
        return true;
    }
}
