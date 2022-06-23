package fr.kinj14.blockedincombat.Events;

import fr.kinj14.blockedincombat.Enums.FinishType;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * When the game ends by any means (Equality, Win, Not enough players, Cancel), the event can be canceled
*/
public class FinishEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();
    private final FinishType finishType;
    private boolean isCancelled;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * When the game ends by any means (Equality, Win, Not enough players, Cancel), the event can be canceled
     *
     * @param finishType Endgame type
     */
    public FinishEvent(FinishType finishType) {
        this.finishType = finishType;
        this.isCancelled = false;
    }

    public FinishType getFinishType(){
        return this.finishType;
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
