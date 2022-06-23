package fr.kinj14.blockedincombat.Events;

import fr.kinj14.blockedincombat.Enums.Teams;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

/**
 * When the game is won by one team
 */
public class WinEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Teams Team;
    private final List<Player> Players;

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    /**
     * When the game is won by one team
     *
     * @param team Winning team
     * @param players The team players
     */
    public WinEvent(Teams team, List<Player> players) {
        this.Team = team;
        this.Players = players;
    }

    public Teams getTeam(){
        return this.Team;
    }

    public List<Player> getPlayers(){
        return this.Players;
    }
}
