package fr.kinj14.blockedincombat.Manager;

import fr.kinj14.blockedincombat.SettingsGUI.*;
import fr.kinj14.blockedincombat.SettingsGUI.Configs.GUI_Configs;
import fr.kinj14.blockedincombat.SettingsGUI.Configs.GUI_ConfigsChoice;

public class GUIManager {
    private final GUI_Settings settings;
    private final GUI_Timers timers;
    private final GUI_Blocks blocks;
    private final GUI_ItemsChest itemsChest;
    private final GUI_Configs configs;
    private final GUI_ConfigsChoice configsChoice;
    private final GUI_Anvil anvil;

    public GUIManager() {
        settings = new GUI_Settings("blockedincombat.ChangeSettings");
        timers = new GUI_Timers("blockedincombat.ChangeSettings");
        blocks = new GUI_Blocks("blockedincombat.ChangeSettings");
        itemsChest = new GUI_ItemsChest("blockedincombat.ChangeSettings");
        configs = new GUI_Configs("blockedincombat.ChangeSettings");
        configsChoice = new GUI_ConfigsChoice("blockedincombat.ChangeSettings");

        anvil = new GUI_Anvil("blockedincombat.ChangeSettings");
    }

    public GUI_Settings getSettings() {
        return settings;
    }

    public GUI_Timers getTimers() {
        return timers;
    }

    public GUI_Blocks getBlocks() {
        return blocks;
    }

    public GUI_ItemsChest getItemsChest() {
        return itemsChest;
    }

    public GUI_Configs getConfigs() {
        return configs;
    }

    public GUI_ConfigsChoice getConfigsChoice() {
        return configsChoice;
    }

    public GUI_Anvil getAnvil() {
        return anvil;
    }

    public void updateAll(){
        getSettings().updateInventory();
        getBlocks().updateInventory();
        getItemsChest().updateInventory();
        getTimers().updateInventory();
        getConfigs().updateInventory();
    }
}
