package center.uhc.pvp.combat;

import center.uhc.core.commands.ICommand;
import center.uhc.core.commons.Message;
import center.uhc.core.commons.PlayerUtils;
import center.uhc.pvp.utils.PvPUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class HealthCommand implements ICommand {

    @Override
    public String command() {
        return "health";
    }

    @Override
    public String[] alias() {
        return new String[] { "h", "hp" };
    }

    @Override
    public String permission() {
        return null;
    }

    @Override
    public void run(Player player, String[] args) {
        if (args == null || args.length < 1) {
            player.sendMessage(Message.formatSystem(ChatColor.YELLOW, "PvP", "§eYou are on " + PvPUtils.playerPercentWithColours(PvPUtils.getPercentHealth(player))));
            return;
        }

        Player target = PlayerUtils.search(args[0]);
        if (target == null) {
            player.sendMessage(Message.formatSystem(ChatColor.RED, "Error", args[0] + " is not online!"));
            return;
        }

        player.sendMessage(Message.formatSystem(ChatColor.YELLOW, "PvP", "§6" + target.getName() + " §eis on " + PvPUtils.playerPercentWithColours(PvPUtils.getPercentHealth(target))));
    }
}
