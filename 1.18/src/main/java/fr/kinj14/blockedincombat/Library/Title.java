package fr.kinj14.blockedincombat.Library;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.Collection;

public class Title {
    private String title;
    private String subtitle;
    private ChatColor color;

    /**
     * Create a title with a main message
     * @param title the main message
     */
    public Title(String title) {
        this.title = title;
        this.subtitle = "";
        this.color = ChatColor.WHITE;
    }

    /**
     * Create a title with a main message
     * @param title the main message
     * @param color the color
     */
    public Title(String title, ChatColor color) {
        this.title = title;
        this.subtitle = "";
        this.color = color;
    }

    /**
     * Create a Title with a main message and a sub-message
     * @param title the main message
     * @param subtitle the sub-message
     */
    public Title(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
        this.color = ChatColor.WHITE;
    }

    /**
     * Create a Title with a main message and a sub-message
     * @param title the main message
     * @param subtitle the sub-message
     * @param color the color
     */
    public Title(String title, String subtitle, ChatColor color) {
        this.title = title;
        this.subtitle = subtitle;
        this.color = color;
    }

    /**
     * Send the Title to a player
     * @param player the player to send the Title to
     * @param fadeIn the opening fade time
     * @param stay display time
     * @param fadeOut closing time
     */
    public void send(Player player, int fadeIn, int stay, int fadeOut) {
        try
        {
            Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\": \"" + this.title + "\",color:" + this.color.name().toLowerCase() + "}");

            Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"), int.class, int.class, int.class);
            Object packet = ((Constructor<?>) titleConstructor).newInstance(getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle, fadeIn, stay, fadeOut);

            sendPacket(player, packet);
        }

        catch (Exception ex)
        {
            //Do something
        }
    }

    /**
     * Send the Title to multiple players
     * @param players players to send the Title to
     * @param fadeIn the opening fade time
     * @param stay display time
     * @param fadeOut closing time
     */
    public void send(Collection<? extends Player> players, int fadeIn, int stay, int fadeOut){
        players.forEach(p -> send(p, fadeIn, stay, fadeOut));
    }

    private void sendPacket(Player player, Object packet)
    {
        try
        {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        }
        catch(Exception ex)
        {
            //Do something
        }
    }

    private Class<?> getNMSClass(String name)
    {
        try
        {
            return Class.forName("net.minecraft.server" + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        }
        catch(ClassNotFoundException ex)
        {
            //Do something
        }
        return null;
    }
}