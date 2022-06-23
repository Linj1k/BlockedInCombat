package fr.kinj14.blockedincombat.Manager;

import fr.kinj14.blockedincombat.Listeners.*;
import fr.kinj14.blockedincombat.Main;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class RegistersManager {
    protected final Main main = Main.getInstance();
    protected final PluginManager pluginManager;
    private final CommandsManager cmdManager = new CommandsManager();

    public RegistersManager(){
        pluginManager = main.getServer().getPluginManager();
    }

    public void registersCommands(){
        registerCommand("blockedincombat");
        registerCommand("blockedincombat.cancel");
        registerCommand("blockedincombat.arena", true);
        registerCommand("blockedincombat.canbuild");
        registerCommand("blockedincombat.settings");
        registerCommand("blockedincombat.devmode");
        registerCommand("blockedincombat.stats");
    }

    public void registerCommand(String command_name, boolean TabCompleter){
        PluginCommand cmd = main.getCommand(command_name);

        assert cmd != null;
        cmd.setExecutor(cmdManager);
        if(TabCompleter){ cmd.setTabCompleter(cmdManager); }
    }

    public void registerCommand(String command_name){
        registerCommand(command_name, false);
    }

    public void registersListeners(){
        registerListener(new Player_Listener());
        registerListener(new Block_Listener());
        registerListener(new Damage_Listener());
        registerListener(new GUI_Listener());
        registerListener(new Chat_Listener());
    }

    public void registerListener(Listener l){
        pluginManager.registerEvents(l, main);
    }
}
