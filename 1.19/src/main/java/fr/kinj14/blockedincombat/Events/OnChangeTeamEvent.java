package fr.kinj14.blockedincombat.Events;

import fr.kinj14.blockedincombat.Enums.Teams;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * Calling when a player changes team, the event can be canceled
*/
public class OnChangeTeamEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player Player;
    private final Teams newTeam;
    private final Teams lastTeam;
    private boolean isCancelled;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Calling when a player changes team, the event can be canceled
     *
     * @param player The Player concerned
     * @param newTeam The new player's team
     * @param lastTeam The player's old team, May be null !
     */
    public OnChangeTeamEvent(Player player, Teams newTeam, Teams lastTeam) {
        this.Player = player;
        this.newTeam = newTeam;
        this.lastTeam = lastTeam;
        this.isCancelled = false;
    }

    public Player getPlayer(){
        return this.Player;
    }

    public Teams getNewTeam(){
        return this.newTeam;
    }

    public Teams getLastTeam(){
        return this.lastTeam;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }
}
