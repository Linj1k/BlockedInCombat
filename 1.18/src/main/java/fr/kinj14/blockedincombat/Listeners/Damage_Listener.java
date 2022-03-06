package fr.kinj14.blockedincombat.Listeners;

import fr.kinj14.blockedincombat.Enums.GameState;
import fr.kinj14.blockedincombat.Enums.Lang;
import fr.kinj14.blockedincombat.Main;
import fr.kinj14.blockedincombat.Manager.DelayManager;
import fr.kinj14.blockedincombat.Enums.Teams;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

public class Damage_Listener implements Listener {
    protected final Main main = Main.getInstance();
    protected final DelayManager delayManager = new DelayManager();

    @EventHandler
    public void OnDeath(PlayerDeathEvent event) {
        Player player = (Player) event.getEntity();
        Entity killer = player.getKiller();

        event.setDeathMessage(main.getPrefix()+"§4"+event.getDeathMessage());

        player.getInventory().clear();
        player.updateInventory();
        player.spigot().respawn();

        main.getTeamsManager().eliminatePlayer(player);
    }

    @EventHandler
    public void OnDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();

        if(damager instanceof Player){
            Player player = (Player) damager;
            if(!main.getPlayersBuild().contains(player) && !GameState.isState(GameState.PLAYING)){
                event.setCancelled(true);
                return;
            }
        }


        if(victim instanceof Player) {
            Player player = (Player)victim;
            if(damager != null && damager instanceof Player ) {
                event.setCancelled(!PVP(player, (Player)damager));
                return;
            }
        }
    }

    @EventHandler
    public void OnDamage(EntityDamageEvent event) {
        Entity damager = event.getEntity();
        if(damager instanceof Player){
            Player player = (Player) damager;
            if(!main.getPlayersBuild().contains(player) && !GameState.isState(GameState.PLAYING)){
                event.setCancelled(true);
                return;
            }
        }
    }

    @EventHandler
    public void OnPVP(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity victim = event.getEntity();

        if(damager instanceof Player){
            Player killer = (Player) damager;
            if(!main.getPlayersBuild().contains(killer) && !GameState.isState(GameState.PLAYING)){
                event.setCancelled(true);
                return;
            }
        }

        if(victim instanceof Player) {
            Player player = (Player)victim;
            Player killer = player;

            if(player.getHealth() <= event.getDamage()) {
                if(damager instanceof Player) {
                    killer = (Player)damager;
                    event.setCancelled(!PVP(player, killer));
                    return;
                }

                if(damager instanceof Arrow) {
                    Arrow arrow = (Arrow)damager;
                    if(arrow.getShooter() instanceof Player) {
                        killer = (Player)arrow.getShooter();
                        event.setCancelled(!PVP(player, killer));
                        return;
                    }
                }
            }
        }
    }

    public Boolean PVP(Player player, Player killer) {
        if(!main.getSettingsManager().isPVP()) {
            killer.sendMessage(main.getPrefix()+Lang.GAMESTATE_PVPISNOTYETACTIVATED.get());
            return false;
        }

        Teams playerTeam = main.getTeamsManager().getPlayerTeam(player);
        if(!main.getSettingsManager().getConfig().getFriendlyFire() && main.getTeamsManager().playerIsInTeam(killer, playerTeam)) {
            killer.sendMessage(main.getPrefix()+ Lang.GAMESTATE_FRIENDLYFIREDEACTIVATED.get());
            return false;
        }
        return true;
    }

    @EventHandler
    public void onExplosionEvent(ExplosionPrimeEvent event) {
        event.setCancelled(true);
        return;
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        event.setCancelled(true);
        return;
    }
}