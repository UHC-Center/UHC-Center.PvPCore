package center.uhc.pvp.listeners;

import center.uhc.core.commons.Message;
import center.uhc.core.commons.PlayerUtils;
import center.uhc.pvp.PvPCore;
import center.uhc.pvp.utils.PvPUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class ProjectilePlayerHealthListener implements Listener {

    PvPCore plugin = PvPCore.getInstance();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onArrow(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;

        if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
            Player damaged = (Player) event.getEntity();
            Projectile arrow = (Projectile) event.getDamager();
            if (arrow.getShooter() instanceof Player) {
                Player damager = (Player) arrow.getShooter();

                if (plugin.isAnnounceLongshot()) {
                    double distance = PlayerUtils.getDistanceFromPlayer(damager, damaged);
                    double distanceRounded = Math.round(distance * 100.0) / 100.0;
                    if (distance >= plugin.getLongShotDistance())
                        Message.broadcast(Message.formatSystem(ChatColor.YELLOW, "Longshot", "§6" + damager.getName() + " §eshot §6" + damaged.getName() + " §efrom §6" + distanceRounded + " blocks §eaway!"));
                }

                if (plugin.isProjectilePlayerHealth()) {
                    double damagedPercent = PvPUtils.getPercentHealth(damaged.getHealth()-event.getFinalDamage(), damaged.getMaxHealth());
                    if (damagedPercent <= 0)
                        return;
                    damagedPercent = Math.round(damagedPercent * 100.0) / 100.0;
                    damager.sendMessage(Message.formatSystem(ChatColor.YELLOW, "PvP", "§6" + damaged.getName() + " §eis now on " + PvPUtils.playerPercentWithColours(damagedPercent)));
                }
            }
        } else if (event.getEntity() instanceof Player && event.getDamager() instanceof FishHook) {
            Player damaged = (Player) event.getEntity();
            FishHook hook = (FishHook) event.getDamager();
            if (hook.getShooter() instanceof Player) {
                Player damager = (Player) hook.getShooter();

                if (plugin.getRodMessageCooldown().contains(damager))
                    return;

                double damagedPercent = PvPUtils.getPercentHealth(damaged);
                damagedPercent = Math.round(damagedPercent * 100.0) / 100.0;
                damager.sendMessage(Message.formatSystem(ChatColor.YELLOW, "PvP", "§6" + damaged.getName() + " §eis now on " + PvPUtils.playerPercentWithColours(damagedPercent)));
                plugin.getRodMessageCooldown().add(damager);
                Bukkit.getServer().getScheduler().runTaskLater(plugin.getPlugin(), () -> plugin.getRodMessageCooldown().remove(damager), (60));
            }
        }
    }
}
