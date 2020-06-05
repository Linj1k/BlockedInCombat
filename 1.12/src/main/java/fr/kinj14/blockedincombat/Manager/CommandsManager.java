package fr.kinj14.blockedincombat.Manager;

import fr.kinj14.blockedincombat.Enums.FinishType;
import fr.kinj14.blockedincombat.Enums.GameState;
import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandsManager implements CommandExecutor {
    private Main main = Main.getInstance();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String msg, String[] args) {
        if(cmd.getName().equalsIgnoreCase("blockedincombat.arena") && PlayerManager.hasPermission(sender, "blockedincombat.Arena")){
            if(args.length < 1){return false;}
            String type = args[0];

            if(type.equalsIgnoreCase("build")){
                main.getArenaManager().buildArena();
            }
            if(type.equalsIgnoreCase("clear")){
                main.getArenaManager().clearArena();
            }
            if(type.equalsIgnoreCase("setup")){
                main.getArenaManager().setupArena();
            }
            if(type.equalsIgnoreCase("setupclear")){
                main.getArenaManager().setupClearArena();
            }

            if(sender instanceof Player){
                Player player = (Player) sender;
                Location loc = new Location(Bukkit.getWorld(main.WorldName),1040, 30, 1040, 0f, 90f);
                player.teleport(loc);
            }

            return true;
        }

        if(sender instanceof Player){
            Player player = (Player) sender;
            if(cmd.getName().equalsIgnoreCase("blockedincombat.canbuild") && PlayerManager.hasPermission(sender, "blockedincombat.canbuild")){
                if(main.getPlayersBuild().contains(player)){
                    main.getPlayersBuild().remove(player);
                    player.sendMessage(main.getPrefix()+Lang.COMMANDSÙCANBUILDÙDISABLED.get());
                } else {
                    main.getPlayersBuild().add(player);
                    player.sendMessage(main.getPrefix()+ Lang.COMMANDSÙCANBUILDÙACTIVATED.get());
                }
                return true;
            }
        }

        if(cmd.getName().equalsIgnoreCase("blockedincombat.cancel") && PlayerManager.hasPermission(sender, "blockedincombat.ChangeSettings") && GameState.isState(GameState.GENERATE_MAP)){
            main.FinishGame(FinishType.CANCEL);
            return true;
        }
        return false;
    }
}
