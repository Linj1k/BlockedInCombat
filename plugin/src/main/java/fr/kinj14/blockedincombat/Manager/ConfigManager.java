package fr.kinj14.blockedincombat.Manager;

import fr.kinj14.blockedincombat.Enums.Files;
import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Library.QueryUtils;
import fr.kinj14.blockedincombat.Main;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Kinj14
 * Allows easy management of a configuration file
 *
 */
public class ConfigManager {
    protected final Main main = Main.getInstance();
    protected FileConfiguration cfg;

    public ConfigManager(){
        cfg = main.getConfig();
    }

    public void reloadCfg(){
        this.cfg = main.getConfig();
    }

    public FileConfiguration getCfg(){
        return this.cfg;
    }

    public String getStringConfig(String StringName){
        return cfg.getString(StringName);
    }

    public int getIntConfig(String IntName){
        return cfg.getInt(IntName);
    }

    public Boolean getBooleanConfig(String BooleanName){
        return cfg.getBoolean(BooleanName);
    }

    public Location getLocationConfig(String WorldName, String LocationName){
        return deserializeLocation(WorldName, cfg.getString(LocationName));
    }

    public ConfigurationSection getConfigurationSection(String ConfigurationSectionName){
        return cfg.getConfigurationSection(ConfigurationSectionName);
    }

    //(de)serialize

    public Location deserializeConfigLocation(String World_Name, String configname){
        String[] split = getStringConfig(configname).split(", ");
        return new Location(
                main.world,
                Double.parseDouble(split[0]),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2]),
                Float.parseFloat(split[3]),
                Float.parseFloat(split[4])
        );
    }

    public Location deserializeLocation(String World_Name, String name){
        String[] split = name.split(", ");
        return new Location(
                main.world,
                Double.parseDouble(split[0]),
                Double.parseDouble(split[1]),
                Double.parseDouble(split[2]),
                Float.parseFloat(split[3]),
                Float.parseFloat(split[4])
        );
    }

    public static String serializeLocation(Location loc) {
        return loc.getX() + ", " + loc.getY() + ", " + loc.getZ() + ", " + loc.getYaw() + ", " + loc.getPitch();
    }

    public JSONObject lastCheckUpdate = null;

    public boolean checkForUpdate(){
        Map<String, String> parameters = new HashMap<>();
        String query = new QueryUtils().query_GET("https://api.spiget.org/v2/resources/"+main.resourceID+"/versions/latest", parameters);
        JSONObject json = new JSONObject(query);
        lastCheckUpdate = json;
        return !main.version.equalsIgnoreCase(json.getString("name"));
    }

    public boolean isLatestVersion(){
        return main.version.equalsIgnoreCase(lastCheckUpdate.getString("name"));
    }

    public String getVersionName(){
        return lastCheckUpdate.getString("name");
    }

    public String getDownloadVersion(){
        return "https://www.spigotmc.org/resources/"+main.resourceID+"/download?version="+lastCheckUpdate.getInt("id");
    }

    public boolean RetroCompatibility(){
        Files configFile = Files.CONFIG;
        configFile.setFolder(main.getDataFolder());
        FileConfiguration oldCfg = configFile.getConfig();

        if(!oldCfg.contains("Version") || !oldCfg.getString("Version").equals(main.version)){
            configFile.delete();
            main.saveDefaultConfig();

            FileConfiguration newCfg = configFile.getConfig();

            newCfg.set("General.devmode",oldCfg.getBoolean("General.devmode"));
            newCfg.set("General.UsebStats",oldCfg.getBoolean("General.UsebStats"));
            newCfg.set("General.CheckUpdate",oldCfg.getBoolean("General.CheckUpdate"));
            newCfg.set("General.NoChatFlood",oldCfg.getBoolean("General.NoChatFlood"));
            newCfg.set("General.ChatFormat",oldCfg.getString("General.ChatFormat"));
            newCfg.set("General.PingInTab",oldCfg.getBoolean("General.PingInTab"));
            newCfg.set("General.TabHealthRenderType",oldCfg.getString("General.TabHealthRenderType"));
            newCfg.set("General.PlayerStats",oldCfg.getBoolean("General.PlayerStats"));
            newCfg.set("General.YourIP",oldCfg.getString("General.YourIP"));
            newCfg.set("General.RenameLanguageFile",oldCfg.getBoolean("General.RenameLanguageFile"));

            newCfg.set("Locations.Lobby",oldCfg.getString("Locations.Lobby"));

            newCfg.set("BungeeCord.Server",oldCfg.getString("BungeeCord.Server"));

            try {
                newCfg.save(configFile.getFile());
            } catch (IOException e) {
                e.printStackTrace();
            }

            this.cfg = newCfg;
            main.getLogger().info(main.getPrefix(true)+"The configuration file is too old, Regeneration of the file with a RetroCompatibility");

            // Language
            if(getBooleanConfig("General.RenameLanguageFile")){
                Files lang = Files.LANG;
                lang.setFolder(main.getDataFolder());
                lang.getFile().renameTo(new File(main.getDataFolder(), lang.getFileName().replace("lang", "lang_old")));
                lang.create(true);

                Lang.reload();
            }

            main.getLogger().info(main.getPrefix(true)+"The lang.yml file has been renamed to lang.old_yml to update it! You must remodify the file if you have modified it!");

            return true;
        }
        return false;
    }
}
