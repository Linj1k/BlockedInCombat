package fr.kinj14.blockedincombat.Manager;

import fr.kinj14.blockedincombat.Data.PlayerData;
import fr.kinj14.blockedincombat.Enums.FinishType;
import fr.kinj14.blockedincombat.Enums.GameState;
import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Main;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CommandsManager implements CommandExecutor, TabExecutor {
    protected final Main main = Main.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if(cmd.getName().equalsIgnoreCase("blockedincombat")){
            sender.sendMessage(
                    Main.getPrefix(),
                    Lang.COMMANDS_BLOCKEDINCOMBAT_VERSION.get()+" "+(!main.IsUpToDate ? ChatColor.RED : ChatColor.GREEN)+main.version+ChatColor.WHITE+(!main.IsUpToDate ? " ("+Lang.PLUGIN_NOTUPTODATE.get()+")" : ""),
                    Lang.COMMANDS_BLOCKEDINCOMBAT_CREATEDBY.get()+" "+ChatColor.GOLD+"Kinj14"+ChatColor.WHITE
            );
            return true;
        }

        if(cmd.getName().equalsIgnoreCase("blockedincombat.arena") && PlayerManager.hasPermission(sender, "blockedincombat.Arena")){
            if(args.length < 1){return false;}
            String type = args[0];

            if(type.equalsIgnoreCase("build")){
                GameState.setState(GameState.GENERATE_MAP);
                main.getArenaManager().clearArena();
                main.getArenaManager().buildArena();
            }else if(type.equalsIgnoreCase("clear")){
                GameState.setState(GameState.WAITING);
                main.getArenaManager().clearArena();
            }else if(type.equalsIgnoreCase("setup")){
                main.getArenaManager().setupArena();
            }else if(type.equalsIgnoreCase("setupclear")){
                main.getArenaManager().setupClearArena();
            }else if(type.equalsIgnoreCase("finish")){
                main.getArenaManager().finishArea();
            }

            if(sender instanceof Player){
                Player player = (Player) sender;

                if(type.equalsIgnoreCase("tp")){
                    player.setGameMode(GameMode.CREATIVE);
                    Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> player.setFlying(true), 5L);
                }

                Location loc = new Location(main.world,1040, 30, 1040, 0f, 90f);
                player.teleport(loc);
            }

            return true;
        }

        if(sender instanceof Player){
            Player player = (Player) sender;

            if(cmd.getName().equalsIgnoreCase("blockedincombat.stats")){
                if(!main.canUsePlayersStats()){player.sendMessage(Main.getPrefix()+ChatColor.RED+Lang.COMMANDS_STATS_NOTENABLED.get());}
                Player target = player;
                if(args.length > 0){
                    Player argPlayer = Bukkit.getPlayer(args[0]);
                    if(argPlayer != null){
                        target = argPlayer;
                    }
                }

                PlayerData targetData = main.getPlayerManager().getPlayerData(target);
                if(targetData != null){
                    player.sendMessage(
                            Main.getPrefix(),
                            ChatColor.GOLD+Lang.COMMANDS_STATS_TEXT.get().replace("{playername}",target.getName()),
                            ChatColor.BLUE+Lang.COMMANDS_STATS_TIMEPLAYED.get().replace("{time}", String.valueOf(Math.round(PlayerManager.getTimePlayed(target)/3600))),
                            ChatColor.BLUE+Lang.COMMANDS_STATS_WINS.get()+ChatColor.GREEN+targetData.Wins,
                            ChatColor.BLUE+Lang.COMMANDS_STATS_LOSTS.get()+ChatColor.GREEN+targetData.Losts,
                            ChatColor.BLUE+Lang.COMMANDS_STATS_KILLS.get()+ChatColor.GREEN+targetData.Kills,
                            ChatColor.BLUE+Lang.COMMANDS_STATS_DEATHS.get()+ChatColor.GREEN+targetData.Deaths,
                            ""
                    );
                } else {
                    player.sendMessage(ChatColor.RED+Lang.COMMANDS_STATS_CANTFINDDATA.get());
                }
                return true;
            }

            if(cmd.getName().equalsIgnoreCase("blockedincombat.settings") && PlayerManager.hasPermission(sender, "blockedincombat.ChangeSettings")){
                main.getGuiManager().getSettings().open(player);
                return true;
            }

            if(cmd.getName().equalsIgnoreCase("blockedincombat.canbuild") && PlayerManager.hasPermission(sender, "blockedincombat.canbuild")){
                if(main.getPlayersBuild().contains(player)){
                    main.getPlayersBuild().remove(player);
                    PlayerManager.setupLobby(player);
                    player.sendMessage(Main.getPrefix()+Lang.COMMANDS_CANBUILD_DISABLED.get());
                } else {
                    main.getPlayersBuild().add(player);
                    player.setGameMode(GameMode.CREATIVE);
                    PlayerManager.clear(player);
                    player.sendMessage(Main.getPrefix()+ Lang.COMMANDS_CANBUILD_ACTIVATED.get());
                }
                return true;
            }
        }

        if(cmd.getName().equalsIgnoreCase("blockedincombat.cancel") && PlayerManager.hasPermission(sender, "blockedincombat.ChangeSettings") && (!GameState.isState(GameState.WAITING) && !GameState.isState(GameState.FINISH))){
            main.FinishGame(FinishType.CANCEL);
            return true;
        }

        if(cmd.getName().equalsIgnoreCase("blockedincombat.devmode") && PlayerManager.hasPermission(sender, "blockedincombat.*")){
            main.devmode = !main.devmode;
            sender.sendMessage(Main.getPrefix()+Lang.COMMANDS_DEVMODE_TEXT.get().replace("{mode}", main.devmode ? Lang.COMMANDS_DEVMODE_ACTIVATE.get() : Lang.COMMANDS_DEVMODE_DISABLE.get()));
            return true;
        }
        return false;
    }

    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args){
        if(cmd.getName().equalsIgnoreCase("blockedincombat.arena")){
            List<String> l = new ArrayList<>();

            if (args.length==1){
                l.add("tp");
                l.add("setup");
                l.add("setupclear");
                l.add("build");
                l.add("finish");
                l.add("clear");
            }

            return l;
        }
        return null;
    }
}
