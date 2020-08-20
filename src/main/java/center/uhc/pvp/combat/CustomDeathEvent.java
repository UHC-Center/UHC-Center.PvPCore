package center.uhc.pvp.combat;

import center.uhc.core.commons.Message;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.ArrayList;

public class CustomDeathEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter private Player player;
    @Getter private EntityDamageEvent.DamageCause deathCause;
    @Getter private Player killer;
    @Getter private ArrayList<Player> assists;
    @Getter private String deathMessage;

    @Getter @Setter private boolean handled;

    public CustomDeathEvent(Player player, EntityDamageEvent.DamageCause deathCause, Player killer, ArrayList<Player> assists, String deathMessage) {
        handled = false;
        this.player = player;
        this.deathCause = (deathCause == null ? EntityDamageEvent.DamageCause.CUSTOM : deathCause);
        this.killer = killer;
        this.assists = assists;
        this.deathMessage = deathMessage;
        if (killer != null) {
            this.assists.remove(killer);
            this.deathMessage = String.format(deathMessage, killer.getName() + (assists.isEmpty() ? "" : " + " + assists.size()));
        }
    }

    @Override public HandlerList getHandlers() { return handlers; }
    public static HandlerList getHandlerList() { return handlers; }

    public String getFormattedDeathMessage() {
        return Message.formatSystem(ChatColor.RED, "Death", deathMessage);
    }

    public void announceDeath() {
        Message.broadcast(Message.formatSystem(ChatColor.RED, "Death", deathMessage));
    }
}
