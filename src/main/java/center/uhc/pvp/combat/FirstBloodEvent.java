package center.uhc.pvp.combat;

import center.uhc.core.commons.Message;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class FirstBloodEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter private Player player;

    public FirstBloodEvent(Player player) {
        this.player = player;
    }

    @Override public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }

    public void announce() {
        Message.broadcast(Message.formatSystem(ChatColor.RED, "PvP", "§4" + player.getName() + " §cdrew first blood."));
    }
}
