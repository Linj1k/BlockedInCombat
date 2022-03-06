package fr.kinj14.blockedincombat.Library;

import fr.kinj14.blockedincombat.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.json.JSONObject;

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
                Bukkit.getWorld(World_Name),
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
                Bukkit.getWorld(World_Name),
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
}
