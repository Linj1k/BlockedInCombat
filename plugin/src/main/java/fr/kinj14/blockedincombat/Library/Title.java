package fr.kinj14.blockedincombat.Library;

import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Collection;

public class Title {
    private String title;
    private String subtitle;

    /**
     * Create a title with a main message
     * @param title the main message
     */
    public Title(String title) {
        this.title = title;
        subtitle = "";
    }

    /**
     * Create a Title with a main message and a sub-message
     * @param title the main message
     * @param subtitle the sub-message
     */
    public Title(String title, String subtitle) {
        this.title = title;
        this.subtitle = subtitle;
    }

    /**
     * Send the Title to a player
     * @param player the player to send the Title to
     * @param fadeIn the opening fade time
     * @param stay display time
     * @param fadeOut closing time
     */
    public void send(Player player, int fadeIn, int stay, int fadeOut) {
        PlayerConnection connection = ((CraftPlayer) player).getHandle().playerConnection;
        PacketPlayOutTitle packetPlayOutTimes = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TIMES, null, fadeIn * 20, stay * 20, fadeOut * 20);
        connection.sendPacket(packetPlayOutTimes);
        if (subtitle != null) {
            subtitle = subtitle.replaceAll("%player%", player.getDisplayName());
            subtitle = ChatColor.translateAlternateColorCodes('&', subtitle);
            IChatBaseComponent titleSub = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + subtitle + "\"}");
            PacketPlayOutTitle packetPlayOutSubTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, titleSub);
            connection.sendPacket(packetPlayOutSubTitle);
        }
        if (title != null) {
            title = title.replaceAll("%player%", player.getDisplayName());
            title = ChatColor.translateAlternateColorCodes('&', title);
            IChatBaseComponent titleMain = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + title + "\"}");
            PacketPlayOutTitle packetPlayOutTitle = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, titleMain);
            connection.sendPacket(packetPlayOutTitle);
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
}