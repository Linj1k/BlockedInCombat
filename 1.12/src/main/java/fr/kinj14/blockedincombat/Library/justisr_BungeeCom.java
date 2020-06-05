package fr.kinj14.blockedincombat.Library;

import com.google.common.collect.Sets;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;

/**
 *
 * @author Justis
 * Bungee wrapper for handling incoming and outgoing bungee messages, because plugin messages are a pain in the arse I refuse to work with them without a wrapper thank you. xD
 *
 */
public class justisr_BungeeCom implements PluginMessageListener {

    // The instance for this class, because there should only ever be one.
    private static justisr_BungeeCom com;
    private Plugin plugin;
    protected final Main main = Main.getInstance();

    /**
     * All of the below maps, sets, and strings will remain without data until you send a plugin message to update them.
     * This is to reduce overhead, and running plugin messages that don't need to be run.
     */
    // This server's server name as defined in the bungee config.
    private String serverName;
    // Set of servers.
    private Set<String> servers = new HashSet<>();
    // Map of player uuid's for the Key, and their IP for the value.
    private Map<UUID, String> playerIps = new HashMap<>();
    // Map of server names for the Key, and player count for the value.
    public Map<String, Integer> playerCount = new HashMap<>();
    // Map of server names for the Key, and player names for the value.
    private Map<String, String[]> playerList = new HashMap<>();
    // Map of server names for the Key, and the server IP for the value.
    private Map<String, String> serverIp = new HashMap<>();
    // Map of player names for the Key, and player UUIDs for the value.
    private Map<String, UUID> playerIds = new HashMap<>();

    /**
     * Initiate the bungee com, and register channels.
     */
    public justisr_BungeeCom() {
        com = this;
        this.plugin = Main.getInstance();
        plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "BungeeCord", this);
        plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    }

    /**
     * Receive method, determines the incoming message and runs the appropriate updating method as a result.
     */
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("BungeeCord")) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String subchannel = in.readUTF();
        switch (Channel.fromString(subchannel)) {
            case GET_SERVER:
                receiveServerName(in.readUTF());
                break;
            case GET_SERVERS:
                final String[] servers = in.readUTF().split(", ");
                receiveServers(servers);
                break;
            case IP:
                receiveIP(player, in.readUTF(), in.readInt());
                break;
            case PLAYER_COUNT:
                final String server = in.readUTF();
                final int playerCount = in.readInt();
                receivePlayerCount(server, playerCount);
                break;
            case PLAYER_LIST:
                receivePlayerList(in.readUTF(), in.readUTF().split(", "));
                break;
            case SERVER_IP:
                receiveServerIP(in.readUTF(), in.readUTF(), in.readShort());
                break;
            case UUID:
                receivePlayerUUID(player.getName(), in.readUTF());
                break;
            case UUID_OTHER:
                receivePlayerUUID(in.readUTF(), in.readUTF());
                break;
            default:
                byte[] data = new byte[in.readShort()];
                in.readFully(data);
                receiveForward(player, subchannel, new String(data).substring(2));
        }
    }

    /**
     * Update server name
     * @param name to update it to.
     */
    private void receiveServerName(String name) {
        com.serverName = name;
    }

    /**
     * @return server name
     */
    public static String getServerName() {
        return com.serverName;
    }

    /**
     * Update server set
     * @param servers to update it to.
     */
    private void receiveServers(String[] servers) {
        com.servers = Sets.newHashSet(servers);
    }

    /**
     * @return the server set.
     */
    public static Set<String> getServers() {
        return Collections.unmodifiableSet(com.servers);
    }

    /**
     * Add/update a player to the ip list.
     * @param player
     * @param ip
     * @param port
     */
    private void receiveIP(Player player, String ip, int port) {
        com.playerIps.put(player.getUniqueId(), ip + ":" + port);
    }

    /**
     * Get the ip of a player
     * @param name of the player
     * @return player's ip.
     * Null if player's ip has not been gathered.
     */
    public static String getPlayerIp(String name) {
        return (String) com.playerIps.get(name);
    }

    /**
     * Update a server's player count.
     * @param server
     * @param count
     */
    private void receivePlayerCount(String server, int count) {
        com.playerCount.put(server, Integer.valueOf(count));
    }

    /**
     * @param server
     * @return player count for given server.
     */
    public static int getPlayerCount(String server) {
        return ((Integer) com.playerCount.get(server)).intValue();
    }

    /**
     * Receive list of player names for a given server
     * @param server
     * @param playerList
     */
    private void receivePlayerList(String server, String[] playerList) {
        com.playerList.put(server, playerList);
    }

    /**
     * @param server to get player names from
     * @return list of player names for the given server.
     */
    public static String[] getPlayerList(String server) {
        return (String[]) com.playerList.get(server);
    }

    /**
     * Update ip for the given server
     * @param server
     * @param ip
     * @param port
     */
    private void receiveServerIP(String server, String ip, short port) {
        com.serverIp.put(server, ip + ":" + port);
    }

    /**
     * Update the UUID for a given player
     * @param player
     * @param id
     */
    private void receivePlayerUUID(String player, String id) {
        com.playerIds.put(player, UUID.fromString(id));
    }

    /**
     * Do whatever you want here.
     *
     * @param player May be relevant if the forward was to that specific player
     * @param subChannel Sub channel name.
     * @param message The message received.
     *
     * TODO: Fill this in with stuff I wanna do. :P
     */
    private void receiveForward(Player player, String subChannel, String message) {

    }

    /**
     * Send a player to another server
     * @param player to send
     * @param server to send the player to
     */
    public void sendConnect(Player player, String server) {
        player.sendMessage(main.getPrefix()+ Lang.SUPPORTÙBUNGEECORDÙREDIRECTED.get().replace("{servername}", server));
        out(Channel.CONNECT, player, server);
    }

    /**
     * Send another player to a server
     * @param player to connect
     * @param server to connect the player to
     */
    public static void sendConnectOther(String player, String server) {
        out(Channel.CONNECT_OTHER, null, player, server);
    }

    /**
     * Send for the player's ip
     * @param player who's ip to get
     */
    public static void sendIP(Player player) {
        out(Channel.IP, player);
    }

    /**
     * Send for the player count to this server
     * @param server to get the player count from
     */
    public static void sendPlayerCount(String server) {
        out(Channel.PLAYER_COUNT, null, server);
    }

    /**
     * Send for the total player count to all the servers
     */
    public static void sendPlayerCountAll() {
        out(Channel.PLAYER_COUNT, null, "ALL");
    }

    /**
     * Send for the player list to a specific server
     * @param server to get the player list from
     */
    public static void sendPlayerList(String server) {
        out(Channel.PLAYER_LIST, null, server);
    }

    /**
     * Send for player list from all the servers
     */
    public static void sendPlayerListAll() {
        out(Channel.PLAYER_LIST, null, "ALL");
    }

    /**
     * Send for server list
     */
    public static void sendGetServers() {
        out(Channel.GET_SERVERS, null);
    }

    /**
     * Send a player a message
     * @param player
     * @param message
     */
    public static void sendMessage(String player, String message) {
        out(Channel.MESSAGE, null, player, message);
    }

    /**
     * Send for the server name
     */
    public static void sendGetServer() {
        out(Channel.GET_SERVER, null);
    }

    /**
     * Send a specific server a message on a specific subchannel
     * Remember, the sending and receiving server(s) need to have a player online
     * @param server
     * @param subChannel
     * @param message
     */
    public static void sendForward(String server, String subChannel, String message) {
        out(Channel.FORWARD, null, server, subChannel, message);
    }

    /**
     * Send a message on a specific subchannel to all online servers exept for the one sending the message
     * Remember, the sending and receiving server(s) need to have a player online
     * @param subChannel
     * @param message
     */
    public static void sendForwardOnline(String subChannel, String message) {
        out(Channel.FORWARD, null, "ONLINE", subChannel, message);
    }

    /**
     * Send a message on a specific subchannel to all servers exept for the one sending the message
     * Remember, the sending and receiving server(s) need to have a player online
     * @param subChannel
     * @param message
     */
    public static void sendForwardAll(String subChannel, String message) {
        out(Channel.FORWARD, null, "ALL", subChannel, message);
    }

    /**
     * Send a message on a specific subchannel to the server with a specific player
     * Remember, the sending and receiving server(s) need to have a player online
     * @param player
     * @param subChannel
     * @param message
     */
    public static void sendForwardToPlayer(String player, String subChannel, String message) {
        out(Channel.FORWARD_TO_PLAYER, null, player, subChannel, message);
    }

    /**
     * Send for the uuid of a specific player
     * @param player
     */
    public static void sendUUID(Player player) {
        out(Channel.UUID, player);
    }

    /**
     * Send for the UUID of a specific player
     * @param player
     */
    public static void sendUUIDOther(String player) {
        out(Channel.UUID_OTHER, null, player);
    }

    /**
     * Send for the server ip of a specific server
     * @param server
     */
    public static void sendServerIP(String server) {
        out(Channel.SERVER_IP, null, server);
    }

    /**
     * Kick a player for a specific reason.
     * @param player
     * @param reason
     */
    public static void sendKickPlayer(String player, String reason) {
        out(Channel.KICK_PLAYER, null, player, reason);
    }

    /**
     * Handles the outgoing messages.
     * @param subChannel Subchannel of the message, if there is one.
     * @param p player to send the message to.
     * @param args
     */
    private static void out(Channel subChannel, Player p, String... args) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF(subChannel.toString());
        if ((subChannel.equals(Channel.FORWARD)) || (subChannel.equals(Channel.FORWARD_TO_PLAYER))) {
            out.writeUTF(args[0]);
            out.writeUTF(args[1]);
            try {
                ByteArrayOutputStream msgbytes = new ByteArrayOutputStream();
                new DataOutputStream(msgbytes).writeUTF(args[2]);
                out.writeShort(msgbytes.toByteArray().length);
                out.write(msgbytes.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            for (String arg : args)
                out.writeUTF(arg);
        }
        if (p == null){
            Bukkit.getServer().sendPluginMessage(com.plugin, "BungeeCord", out.toByteArray());
        } else{
            p.sendPluginMessage(com.plugin, "BungeeCord", out.toByteArray());
        }
    }

    /**
     *
     * @author Justis
     * Enum for all of the bungee channels.
     */
    static enum Channel {

        CONNECT("Connect"),

        CONNECT_OTHER("ConnectOther"),

        IP("IP"),

        PLAYER_COUNT("PlayerCount"),

        PLAYER_LIST("PlayerList"),

        GET_SERVERS("GetServers"),

        MESSAGE("Message"),

        GET_SERVER("GetServer"),

        FORWARD("Forward"),

        FORWARD_TO_PLAYER("ForwardToPlayer"),

        UUID("UUID"),

        UUID_OTHER("UUIDOther"),

        SERVER_IP("ServerIP"),

        KICK_PLAYER("KickPlayer"),

        OTHER("");

        private String string;

        /**
         * You better know how an enum works.... -.-
         * @param string
         */
        private Channel(String string) {
            this.string = string;
        }

        /**
         * Convert the channel to it's string value.
         */
        public String toString() {
            return this.string;
        }

        /**
         * Get the enum value from a string
         * @param string to get the enum value from.
         * @return enum value of the string
         * OTHER if it is empty or does not match any of bungee's channels.
         */
        public static Channel fromString(String string) {
            for (Channel ch : values()) {
                if (ch.toString().equalsIgnoreCase(string))
                    return ch;
            }
            return OTHER;
        }
    }
}