package center.uhc.pvp.listeners;

import center.uhc.core.commons.versions.NMSCore;
import center.uhc.core.scoreboard.ScoreboardCreationEvent;
import center.uhc.core.scoreboard.type.Scoreboard;
import center.uhc.pvp.PvPCore;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;

public class TabHealthListener implements Listener {

    @EventHandler
    public void onScoreboard(ScoreboardCreationEvent event) {
        Scoreboard sb = event.getScoreboard();

        Objective name;
        Objective tab;

        if (sb.getBukkitScoreboard().getObjective("PlayerNameHealth") == null) {
            name = sb.getBukkitScoreboard().registerNewObjective("PlayerNameHealth", "dummy");
        } else {
            name = sb.getBukkitScoreboard().getObjective("PlayerNameHealth");
        }

        if (sb.getBukkitScoreboard().getObjective("TabListHealth") == null) {
            tab = sb.getBukkitScoreboard().registerNewObjective("TabListHealth", "dummy");
        } else {
            tab = sb.getBukkitScoreboard().getObjective("TabListHealth");
        }

        name.setDisplaySlot(DisplaySlot.BELOW_NAME);
        name.setDisplayName("" + ChatColor.RED + "❤");

        tab.setDisplaySlot(DisplaySlot.PLAYER_LIST);

        new UpdateRunnable(name, tab, sb).runTaskLater(PvPCore.getInstance().getPlugin(), 1);
    }

    public static class UpdateRunnable extends BukkitRunnable {

        private Objective name;
        private Objective tab;
        private Scoreboard s;

        public UpdateRunnable(Objective name, Objective tab, Scoreboard s) {
            this.name = name;
            this.tab = tab;
            this.s = s;
        }

        @Override
        public void run() {
            if (!s.isActivated() || name.getScoreboard() == null || tab.getScoreboard() == null) {
                cancel();
                return;
            }

            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                int health = (int) Math.floor(((p.getHealth() / 20) * 100) + ((NMSCore.getUtils().getAbsorptionHearts(p) / 20) * 100));

                name.getScore(p.getName()).setScore(health);
                tab.getScore(p.getName()).setScore(health);
            }
            new UpdateRunnable(name, tab, s).runTaskLater(PvPCore.getInstance().getPlugin(), 1);
        }
    }

}
