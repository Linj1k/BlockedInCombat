package fr.kinj14.blockedincombat.Data;

import fr.kinj14.blockedincombat.Main;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Player data (game time, game won, kills, deaths)
 */
public class PlayerData implements Serializable {
    private static transient final long serialVersionUID = -1410012206529286330L;
    public float TimePlayed;
    public int Wins;
    public int Losts;
    public int Kills;
    public int Deaths;
    public int GamePlayed;

    public PlayerData(float timePlayed, int wins, int losts, int kills, int deaths, int GamePlayed) {
        this.TimePlayed = timePlayed;
        this.Wins = wins;
        this.Losts = losts;
        this.Kills = kills;
        this.Deaths = deaths;
        this.GamePlayed = GamePlayed;
    }

    // Can be used for loading
    public PlayerData(PlayerData loadedData) {
        this.TimePlayed = loadedData.TimePlayed;
        this.Wins = loadedData.Wins;
        this.Losts = loadedData.Losts;
        this.Kills = loadedData.Kills;
        this.Deaths = loadedData.Deaths;
        this.GamePlayed = loadedData.GamePlayed;
    }

    public boolean saveData(String filePath) {
        try {
            BukkitObjectOutputStream out = new BukkitObjectOutputStream(new GZIPOutputStream(new FileOutputStream(filePath)));
            out.writeObject(this);
            out.close();
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    public static PlayerData loadData(String filePath) {
        try {
            BukkitObjectInputStream in = new BukkitObjectInputStream(new GZIPInputStream(new FileInputStream(filePath)));
            Object obj = in.readObject();
            if(obj instanceof PlayerData){
                PlayerData data = (PlayerData) obj;
                in.close();
                return data;
            } else {
                Main main = Main.getInstance();
                main.getLogger().severe(main.getPrefix(true)+"Unable to load player stats as they are outdated, It should be renamed to .dat_old");
                File dataFile = new File(filePath);
                if(dataFile.exists() && !dataFile.isDirectory()){
                    dataFile.renameTo(new File(filePath.replace(".dat",".dat_old")));
                }
            }
            in.close();
            return null;
        } catch (ClassNotFoundException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }
}
