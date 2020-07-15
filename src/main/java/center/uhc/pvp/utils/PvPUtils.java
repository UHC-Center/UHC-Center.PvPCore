package center.uhc.pvp.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class PvPUtils {

    public static double getPercentHealth(Player player) {
        return (player.getHealth() / player.getMaxHealth())*100;
    }

    public static String playerPercentWithColours(double percent) {
        if (percent >= 0 && percent < 20)
            return "" + ChatColor.DARK_RED + percent + "%";
        else if (percent >= 20 && percent < 30)
            return "" + ChatColor.RED + percent + "%";
        else if (percent >= 30 && percent < 50)
            return "" + ChatColor.GOLD + percent + "%";
        else if (percent >= 50 && percent < 80)
            return "" + ChatColor.YELLOW + percent + "%";
        else if (percent >= 80 && percent < 90)
            return "" + ChatColor.GREEN + percent + "%";
        else if (percent >= 90)
            return "" + ChatColor.DARK_GREEN + percent + "%";
        else
            return "" + ChatColor.DARK_AQUA + percent + "%";
    }

}
