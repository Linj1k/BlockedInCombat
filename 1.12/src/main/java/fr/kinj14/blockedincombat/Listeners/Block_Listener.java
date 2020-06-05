package fr.kinj14.blockedincombat.Listeners;

import fr.kinj14.blockedincombat.Enums.GameState;
import fr.kinj14.blockedincombat.Library.InstantIngot;
import fr.kinj14.blockedincombat.Main;
import fr.kinj14.blockedincombat.Manager.Settings.SettingsSave;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class Block_Listener implements Listener {
    protected final Main main = Main.getInstance();

    @EventHandler
    public void OnPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if((!main.getPlayersBuild().contains(player) && !GameState.isState(GameState.PLAYING)) || main.getBlacklistBlockManager().canBuild(block.getLocation())){
            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void OnBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();

        if((!main.getPlayersBuild().contains(player) && !GameState.isState(GameState.PLAYING)) || main.getBlacklistBlockManager().canBuild(block.getLocation())){
            event.setCancelled(true);
            return;
        }

        final SettingsSave settings = main.getSettingsManager().getConfig();
        boolean AutoSmelt = settings.getAutoSmelt();
        boolean AutoSmeltFortune = settings.getAutoSmeltFortune();
        if(AutoSmelt) {
            InstantIngot.OreToIngot(event, AutoSmelt, AutoSmeltFortune, Material.IRON_ORE, Material.IRON_INGOT);
            InstantIngot.OreToIngot(event, AutoSmelt, AutoSmeltFortune, Material.GOLD_ORE, Material.GOLD_INGOT);
        }
    }
}