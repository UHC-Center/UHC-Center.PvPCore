package center.uhc.pvp.freeze;

import center.uhc.core.Module;
import center.uhc.core.commons.Message;
import center.uhc.core.redis.RedisManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class FreezeManager extends Module {

    public enum FreezeReason {
        STAFF, PANIC
    }

    @Getter private HashMap<Player, FreezeReason> frozenPlayers;

    @Getter private ArrayList<UUID> freezeOnJoin;

    @Getter private static FreezeManager instance;

    public FreezeManager(JavaPlugin plugin) {
        super(plugin, "Freeze Manager");
    }

    @Override
    public void onEnable() {
        instance = this;
        frozenPlayers = new HashMap<>();
        freezeOnJoin = new ArrayList<>();
        registerSelf();

        registerCommand(new FreezeCommand());
        registerCommand(new UnfreezeCommand());
        registerCommand(new PanicCommand());
    }

    public boolean isFrozen(Player player) {
        return frozenPlayers.containsKey(player);
    }

    public FreezeReason getReason(Player player) {
        if (!frozenPlayers.containsKey(player))
            return null;

        return frozenPlayers.get(player);
    }

    public void freeze(Player player, FreezeReason reason, Player staff) {
        frozenPlayers.put(player, reason);
        new FreezeMessageRunnable(player, reason, getPlugin()).runTask(getPlugin());

        if (reason == FreezeReason.STAFF) {
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (p.hasPermission("uc.staff.freeze"))
                    if (staff == null)
                        p.sendMessage("§4§l[FREEZE] §c" + player.getName() + " §7has been automatically frozen.");
                    else
                        if (p != staff)
                            p.sendMessage("§4§l[FREEZE] §c" + player.getName() + " §7has been frozen by §c" + staff.getName() + "§7.");
            }
        } else {
            String msg = "§4§l[PANIC ALERT] §8[" + RedisManager.getInstance().getServiceId().getName() + "] §c" + player.getName() + " §7has activated §c§lPanic Mode§7. Please review the situation immediately!";
            Message.sendToNetwork(msg, "uc.staff.freeze");
            //Message.soundToNetwork(Sound.NOTE_PLING, "uc.staff.freeze");
        }
    }

    public void unFreeze(Player player, Player staff) {
        frozenPlayers.remove(player);
        player.sendMessage(Message.formatSystem(ChatColor.GREEN, "Freeze", "You have been unfrozen."));
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            if (p.hasPermission("uc.staff.freeze") && p != staff)
                p.sendMessage("§4§l[FREEZE] §c" + player.getName() + " §7has been unfrozen by §c" + staff.getName() + "§7.");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void playerMoveEvent(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (isFrozen(player))
            player.teleport(event.getFrom());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();

        if (isFrozen(player)) {
            event.setCancelled(true);
            player.sendMessage(Message.formatSystem(ChatColor.RED, "Error", "You cannot do this whilst frozen!"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();

        if (isFrozen(player)) {
            event.setCancelled(true);
            player.sendMessage(Message.formatSystem(ChatColor.RED, "Error", "You cannot do this whilst frozen!"));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();

            if (isFrozen(player))
                event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDamageOther(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damager = (Player) event.getDamager();
            Player target = (Player) event.getEntity();

            if (isFrozen(damager)) {
                event.setCancelled(true);
                damager.sendMessage(Message.formatSystem(ChatColor.RED, "Error", "You cannot do this whilst frozen!"));
            } else if (isFrozen(target)) {
                event.setCancelled(true);
                damager.sendMessage(Message.formatSystem(ChatColor.RED, "Error", target.getName() + " is currently frozen!"));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onProjectile(ProjectileLaunchEvent event) {
        if (event.getEntity().getShooter() instanceof Player) {
            Player player = (Player) event.getEntity().getShooter();

            if (isFrozen(player)) {
                event.setCancelled(true);
                player.sendMessage(Message.formatSystem(ChatColor.RED, "Error", "You cannot do this whilst frozen!"));
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (isFrozen(player)) {
            String msg = "§c§l[FREEZE] §7" + player.getName() + " §chas logged out whilst frozen!";
            for (Player p : Bukkit.getServer().getOnlinePlayers()) {
                if (p.hasPermission("uc.staff.freeze"))
                    p.sendMessage(msg);
            }
            if (freezeOnJoin.contains(uuid))
                freezeOnJoin.remove(uuid);
            freezeOnJoin.add(uuid);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (freezeOnJoin.contains(uuid)) {
            freeze(player, FreezeReason.STAFF, null);
            freezeOnJoin.remove(uuid);
        }
    }

}
