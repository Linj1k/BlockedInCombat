package fr.kinj14.blockedincombat.Enums;

import fr.kinj14.blockedincombat.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashMap;
import java.util.Map;

public enum Lang {
    PLUGINÙINITIALIZATION,
    PLUGINÙDISABLE,
    PLUGINÙVERSION_NOT_SUPPORTED,
    PLUGINÙDESCRIPTION,
    PLUGINÙJOINMSG,
    PLUGINÙLEAVEMSG,
    PLUGINÙLOBBY_ITEM,
    PLUGINÙDATE_FORMAT,
    PLUGINÙTIMER_FORMAT,
    PLUGINÙNO_PERMISSION,

    CONFIGÙACTIVATED,
    CONFIGÙDISABLED,

    CONFIGÙGUIÙITEM_BACK,

    CONFIGÙGUIÙSETTINGSÙNAME,
    CONFIGÙGUIÙSETTINGSÙITEM,
    CONFIGÙGUIÙSETTINGSÙTABHEALTH,
    CONFIGÙGUIÙSETTINGSÙBONUSCHEST,
    CONFIGÙGUIÙSETTINGSÙAUTOSMELT,
    CONFIGÙGUIÙSETTINGSÙAUTOSMELT_FORTUNE,
    CONFIGÙGUIÙSETTINGSÙFRIENDLYFIRE,
    CONFIGÙGUIÙSETTINGSÙUHCMODE,
    CONFIGÙGUIÙSETTINGSÙEXPMULTIPLIER,
    CONFIGÙGUIÙSETTINGSÙRANDOMTEAM,

    CONFIGÙGUIÙTIMERSÙNAME,
    CONFIGÙGUIÙTIMERSÙITEM,
    CONFIGÙGUIÙTIMERSÙGAME_TIME,
    CONFIGÙGUIÙTIMERSÙCOMBAT_TIME,
    CONFIGÙGUIÙTIMERSÙGLOWING_TIME,

    CONFIGÙGUIÙBLOCKSÙNAME,
    CONFIGÙGUIÙBLOCKSÙITEM,

    CONFIGÙGUIÙITEMSÙNAME,
    CONFIGÙGUIÙITEMSÙITEM,

    CONFIGÙGUIÙCONFIGSÙNAME,
    CONFIGÙGUIÙCONFIGSÙITEM,
    CONFIGÙGUIÙCONFIGSÙSAVE_PROFILE,

    CONFIGÙGUIÙCONFIGS_CHOICEÙNAME,
    CONFIGÙGUIÙCONFIGS_CHOICEÙREPLACE,
    CONFIGÙGUIÙCONFIGS_CHOICEÙDELETE,

    CHATÙGLOBAL,
    CHATÙTEAM,
    CHATÙSPECTATOR,

    COMMANDSÙCANBUILDÙACTIVATED,
    COMMANDSÙCANBUILDÙDISABLED,

    GAMESTATEÙLEAVE_AREA,
    GAMESTATEÙGAME_STARTED_CANCEL,
    GAMESTATEÙGAME_START,
    GAMESTATEÙPVP_ACTIVATE,
    GAMESTATEÙGLOWING_ACTIVATE,
    GAMESTATEÙPVP_IS_NOT_YET_ACTIVATED,
    GAMESTATEÙFRIENDLYFIRE_DEACTIVATED,

    FINISHÙEQUALITY,
    FINISHÙTEAMS,
    FINISHÙCANCEL,
    FINISHÙNOPLAYERS,

    TEAMSÙBLUE,
    TEAMSÙRED,
    TEAMSÙYELLOW,
    TEAMSÙGREEN,
    TEAMSÙSPECTATOR,
    TEAMSÙJOINMSG,
    TEAMSÙITEM,

    SCOREBOARDÙTIME,
    SCOREBOARDÙPLAYERS,
    SCOREBOARDÙPVP,
    SCOREBOARDÙGLOWING,
    SCOREBOARDÙTEAM,
    SCOREBOARDÙIP,

    SUPPORTÙBUNGEECORDÙREDIRECTED,;

    private static final Map<Lang, String> VALUES = new HashMap<>();

    static{
        for (Lang lang : values()){
            VALUES.put(lang, lang.getFromFile());
        }

        Main.getInstance().logger.info("["+Main.getInstance().getPrefixDefault()+"] Lang file read successfully!");
    }

    public String get(){
        return VALUES.get(this);
    }

    private String getFromFile(){
        FileConfiguration config = Files.LANG.getConfig();
        String key = name().replace('Ù','.').toLowerCase();
        String value = config.getString(key);

        if(value == null){
            value = "";
        }

        return ChatColor.translateAlternateColorCodes('&', value);
    }
}
