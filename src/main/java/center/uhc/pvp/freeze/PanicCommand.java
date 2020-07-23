package center.uhc.pvp.freeze;

import center.uhc.core.commands.ICommand;
import center.uhc.core.commons.Message;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PanicCommand implements ICommand {

    @Override
    public String command() {
        return "panic";
    }

    @Override
    public String[] alias() {
        return new String[0];
    }

    @Override
    public String permission() {
        return null;
    }

    @Override
    public void run(Player player, String[] args) {
        FreezeManager f = FreezeManager.getInstance();

        if (f.isFrozen(player)) {
            player.sendMessage(Message.formatSystem(ChatColor.RED, "Error", "You are already frozen!"));
            return;
        }

        player.sendMessage(Message.formatSystem(ChatColor.GREEN, "Panic", "Panic mode activated."));
        f.freeze(player, FreezeManager.FreezeReason.PANIC, null);
    }
}
