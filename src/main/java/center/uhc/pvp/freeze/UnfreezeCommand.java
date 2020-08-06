package center.uhc.pvp.freeze;

import center.uhc.core.commands.ICommand;
import center.uhc.core.commons.Message;
import center.uhc.core.commons.PlayerUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class UnfreezeCommand implements ICommand {

    @Override
    public String command() {
        return "unfreeze";
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
            player.sendMessage(Message.formatSystem(ChatColor.RED, "Error", "Correct Usage: /unfreeze <player>"));
            return;
        }

        Player target = PlayerUtils.search(args[0]);
        if (target == null) {
            player.sendMessage(Message.formatSystem(ChatColor.RED, "Error", args[0] + " is not online!"));
            return;
        }

        if (target == player) {
            player.sendMessage(Message.formatSystem(ChatColor.RED, "Error", "You cannot unfreeze yourself, silly!"));
            return;
        }

        if (f.isFrozen(target)) {
            f.unFreeze(target, player);
            player.sendMessage(Message.formatSystem(ChatColor.GREEN, "Freeze", "You unfroze " + target.getName() + "."));
        } else {
            player.sendMessage(Message.formatSystem(ChatColor.RED, "Error", target.getName() + " is not frozen!"));
        }
    }

}
