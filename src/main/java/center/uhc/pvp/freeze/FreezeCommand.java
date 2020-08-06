package center.uhc.pvp.freeze;

import center.uhc.core.commands.ICommand;
import center.uhc.core.commons.Message;
import center.uhc.core.commons.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class FreezeCommand implements ICommand {

    @Override
    public String command() {
        return "freeze";
    }

    @Override
    public String[] alias() {
        return new String[0];
    }

    @Override
    public String permission() {
        return "uc.staff.freeze";
    }

    @Override
    public void run(Player player, String[] args) {
        FreezeManager f = FreezeManager.getInstance();
        if (args == null || args.length < 1) {
            player.sendMessage(Message.formatSystem(ChatColor.RED, "Error", "Correct Usage: /freeze <player>"));
            return;
        }

        Player target = PlayerUtils.search(args[0]);
        if (target == null) {
            player.sendMessage(Message.formatSystem(ChatColor.RED, "Error", args[0] + " is not online!"));
            return;
        }

        if (target == player) {
            player.sendMessage(Message.formatSystem(ChatColor.RED, "Error", "You cannot freeze yourself, silly!"));
            return;
        }

        if (f.isFrozen(target)) {
            player.sendMessage(Message.formatSystem(ChatColor.RED, "Error", target.getName() + " is already frozen!"));
        } else {
            f.freeze(target, FreezeManager.FreezeReason.STAFF, player);
            player.sendMessage(Message.formatSystem(ChatColor.GREEN, "Freeze", "You froze " + target.getName() + "."));
        }
    }
}
