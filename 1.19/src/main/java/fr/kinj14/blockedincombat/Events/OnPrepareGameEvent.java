package fr.kinj14.blockedincombat.Events;

import fr.kinj14.blockedincombat.Enums.Teams;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

/**
 * Call before the first timer to wait for players in the lobby (First timer), the event can be canceled
*/
public class OnPrepareGameEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private boolean isCancelled;
    private final List<Teams> Teams;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * Call before the first timer to wait for players in the lobby (First timer), the event can be canceled
     *
     * @param teams Team lists
     */
    public OnPrepareGameEvent(List<Teams> teams) {
        this.Teams = teams;
        this.isCancelled = false;
    }

    public List<Teams> getTeams(){
        return this.Teams;
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