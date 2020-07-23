package center.uhc.pvp.freeze;

import center.uhc.core.commons.center.Centered;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class FreezeMessageRunnable extends BukkitRunnable {

    Player player;
    FreezeManager f;
    JavaPlugin p;
    FreezeManager.FreezeReason r;

    public FreezeMessageRunnable(Player player, FreezeManager.FreezeReason r, JavaPlugin p) {
        this.player = player;
        f = FreezeManager.getInstance();
        this.p = p;
        this.r = r;
    }

    @Override
    public void run() {
        if (!f.isFrozen(player)) {
            cancel();
            return;
        }

        if (r == FreezeManager.FreezeReason.STAFF) {
            player.sendMessage(" ");
            player.sendMessage(Centered.create("§4§lYOU ARE FROZEN"));
            player.sendMessage(Centered.create("§7A staff member will contact you soon, please wait"));
            player.sendMessage(" ");
            player.sendMessage(Centered.create("§c§lLogging out will lead to an instant ban."));
            player.sendMessage(" ");
        } else {
            player.sendMessage(" ");
            player.sendMessage(Centered.create("§5§lPANIC MODE"));
            player.sendMessage(Centered.create("§7All online staff members have been notified"));
            player.sendMessage(Centered.create("§7and are on their way"));
            player.sendMessage(" ");
            player.sendMessage(Centered.create("§8No staff online? Ask for help in our discord"));
            player.sendMessage(Centered.create("§8discord.uhc.center"));
            player.sendMessage(" ");
        }

        new FreezeMessageRunnable(player, r, p).runTaskLater(p, (10*20));
    }
}
