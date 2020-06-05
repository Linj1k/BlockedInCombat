package fr.kinj14.blockedincombat.Manager;

import fr.kinj14.blockedincombat.Listeners.*;
import fr.kinj14.blockedincombat.Main;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class RegistersManager {
    protected final Main main = Main.getInstance();
    protected final PluginManager pm;

    public RegistersManager(){
        pm = main.getServer().getPluginManager();
    }

    public void registersCommands(){
        registerCommand("blockedincombat.cancel");
        registerCommand("blockedincombat.arena");
        registerCommand("blockedincombat.canbuild");
    }

    public void registerCommand(String command_name){
        main.getCommand(command_name).setExecutor(new CommandsManager());
    }

    public void registersListeners(){
        registerListener(new Player_Listener());
        registerListener(new Block_Listener());
        registerListener(new Damage_Listener());
        registerListener(new GUI_Listener());
        registerListener(new Chat_Listener());
    }

    public void registerListener(Listener l){
        pm.registerEvents(l, main);
    }
}
