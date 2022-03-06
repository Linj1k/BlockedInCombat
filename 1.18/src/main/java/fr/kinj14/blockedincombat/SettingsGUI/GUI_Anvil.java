package fr.kinj14.blockedincombat.SettingsGUI;

import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Main;
import fr.kinj14.blockedincombat.Manager.PlayerManager;
import fr.kinj14.blockedincombat.Manager.Settings.SettingsSave;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;

public class GUI_Anvil {
    protected final Main main = Main.getInstance();
    private String Name;
    private AnvilGUI.Builder builder;
    private String permission;

    public GUI_Anvil(String permission) {
        this.permission = permission;
        builder = new AnvilGUI.Builder()
                .onComplete((player, text) -> {
                    if(!text.contains(" ") && !text.contains("of profile") && !text.contains("name") && !text.isEmpty()) {
                        if(PlayerManager.hasPermission(player, this.permission)){
                            SettingsSave settings = main.getSettingsManager().getConfig();
                            if(getName() != "new"){
                                settings = main.getSettingsManager().getSave(getName());
                                if(main.getSettingsManager().deleteConfig(getName(), false)){
                                    main.logger.info("["+main.getPrefixDefault()+"] "+player.getName()+"("+player.getUniqueId().toString()+") deleted profile "+getName()+".json");
                                }
                            }

                            settings.setName(text);
                            if(main.getSettingsManager().save(settings)){
                                player.sendMessage(main.getPrefix()+ Lang.CONFIG_GUI_CONFIGS_PRESETSAVED.get());
                                main.logger.info("["+main.getPrefixDefault()+"] "+player.getName()+"("+player.getUniqueId().toString()+") created the profile "+text+".json");
                                setName("new");
                            }
                            return AnvilGUI.Response.close();
                        }
                    }
                    return AnvilGUI.Response.text("Incorrect.");
                })
                //.itemLeft(new ItemStack(Material.PAPER))
                .text("Name of profile!")
                .plugin(Main.getInstance());
    }

    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public void open(Player player){
        if(!this.permission.isEmpty()){
            if(!PlayerManager.hasPermission(player, this.permission)){
                player.sendMessage(main.getPrefix()+ Lang.PLUGIN_NOPERMISSION.get());
                return;
            }
        }

        builder.text(getName() != "new" ? getName() : "Name of profile!");
        builder.open(player);
    }
}