package center.uhc.pvp.combat;

import center.uhc.core.Module;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

public class CombatManager extends Module {

    @Getter @Setter private HashMap<Player, Player> combatLastHit;
    @Getter @Setter private HashMap<Player, ArrayList<Player>> combatAssists;
    @Getter @Setter ArrayList<String> noDeathMessage;

    @Getter @Setter private boolean firstBloodAnnounced = false;

    @Getter private static CombatManager instance;

    public CombatManager(JavaPlugin plugin) {
        super(plugin, "Combat Manager");
    }

    @Override
    public void onEnable() {
        instance = this;
        registerSelf();
        combatLastHit = new HashMap<>();
        combatAssists = new HashMap<>();
        noDeathMessage = new ArrayList<>();
        registerCommand(new HealthCommand());
    }

    //Damage Events
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerHit(EntityDamageByEntityEvent event) {
        if (event.isCancelled())
            return;

        if (event.getEntity() instanceof Player && event.getDamager() instanceof Player) {
            Player damaged = (Player) event.getEntity();
            Player damager = (Player) event.getDamager();

            getCombatLastHit().remove(damaged);
            getCombatLastHit().put(damaged, damager);

            Bukkit.getServer().getScheduler().runTaskLater(getPlugin(), () -> getCombatLastHit().remove(damaged), 140);

            addAssist(damaged, damager);

        } else if (event.getEntity() instanceof Player && event.getDamager() instanceof Projectile) {
            Player damaged = (Player) event.getEntity();
            Projectile proj = (Projectile) event.getDamager();
            if (proj.getShooter() instanceof Player) {
                Player damager = (Player) proj.getShooter();

                getCombatLastHit().remove(damaged);
                getCombatLastHit().put(damaged, damager);

                Bukkit.getServer().getScheduler().runTaskLater(getPlugin(), () -> getCombatLastHit().remove(damaged), 140);

                addAssist(damaged, damager);
            } else {
                getCombatLastHit().remove(damaged);
            }
        }
    }

    //Death Events
    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;

        Player player = (Player) event.getEntity();

        EntityDamageEvent.DamageCause cause = (player.getLastDamageCause() == null ? EntityDamageEvent.DamageCause.CUSTOM : player.getLastDamageCause().getCause());

        if (cause == EntityDamageEvent.DamageCause.FALL) {
            if (getCombatLastHit().containsKey(player)) {
                Player killer = getCombatLastHit().get(player);
                firstBlood(killer);
                Bukkit.getServer().getPluginManager().callEvent(new CustomDeathEvent(player, cause, killer, getAssists(player), "" + player.getName() + " was thrown off a cliff by %s"));
            } else {
                Bukkit.getServer().getPluginManager().callEvent(new CustomDeathEvent(player, cause, null, getAssists(player), "" + player.getName() + " fell to their death"));
            }
            noDeathMessage.add(player.getName());
        } else if (cause == EntityDamageEvent.DamageCause.FIRE || cause == EntityDamageEvent.DamageCause.FIRE_TICK || cause == EntityDamageEvent.DamageCause.LAVA) {
            if (getCombatLastHit().containsKey(player)) {
                Player killer = getCombatLastHit().get(player);
                firstBlood(killer);
                Bukkit.getServer().getPluginManager().callEvent(new CustomDeathEvent(player, cause, killer, getAssists(player), "" + player.getName() + " was burnt to a crisp whilst fighting %s"));
            } else {
                Bukkit.getServer().getPluginManager().callEvent(new CustomDeathEvent(player, cause, null, getAssists(player), "" + player.getName() + " burnt to death"));
            }
            noDeathMessage.add(player.getName());
        } else if (cause == EntityDamageEvent.DamageCause.DROWNING) {
            if (getCombatLastHit().containsKey(player)) {
                Player killer = getCombatLastHit().get(player);
                firstBlood(killer);
                Bukkit.getServer().getPluginManager().callEvent(new CustomDeathEvent(player, cause, killer, getAssists(player), "" + player.getName() + " drowned to death whilst fighting %s"));
            } else {
                Bukkit.getServer().getPluginManager().callEvent(new CustomDeathEvent(player, cause, null, getAssists(player), "" + player.getName() + " drowned"));
            }
            noDeathMessage.add(player.getName());
        } else if (cause == EntityDamageEvent.DamageCause.SUFFOCATION) {
            if (getCombatLastHit().containsKey(player)) {
                Player killer = getCombatLastHit().get(player);
                firstBlood(killer);
                Bukkit.getServer().getPluginManager().callEvent(new CustomDeathEvent(player, cause, killer, getAssists(player), "" + player.getName() + " suffocated whilst fighting %s"));
            } else {
                Bukkit.getServer().getPluginManager().callEvent(new CustomDeathEvent(player, cause, null, getAssists(player), "" + player.getName() + " suffocated"));
            }
            noDeathMessage.add(player.getName());
        } else if (cause == EntityDamageEvent.DamageCause.FALLING_BLOCK) {
            if (getCombatLastHit().containsKey(player)) {
                Player killer = getCombatLastHit().get(player);
                firstBlood(killer);
                Bukkit.getServer().getPluginManager().callEvent(new CustomDeathEvent(player, cause, killer, getAssists(player), "" + player.getName() + " was crushed whilst fighting " + killer.getName()));
            } else {
                Bukkit.getServer().getPluginManager().callEvent(new CustomDeathEvent(player, cause, null, getAssists(player), "" + player.getName() + " was crushed by a falling block"));
            }
            noDeathMessage.add(player.getName());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(PlayerDeathEvent event) {
        String msg = event.getDeathMessage();
        event.setDeathMessage("");

        if (noDeathMessage.contains(event.getEntity().getName())) {
            noDeathMessage.remove(event.getEntity().getName());
            return;
        }

        if (event.getEntity().getLastDamageCause().getCause() == null) {
            Bukkit.getServer().getPluginManager().callEvent(new CustomDeathEvent(event.getEntity(), EntityDamageEvent.DamageCause.CUSTOM, null, getAssists(event.getEntity()), "" + event.getEntity().getName() + " died"));
        } else {
            if (event.getEntity().getKiller() != null) {
                firstBlood(event.getEntity().getKiller());
                Bukkit.getServer().getPluginManager().callEvent(new CustomDeathEvent(event.getEntity(), event.getEntity().getLastDamageCause().getCause(), event.getEntity().getKiller(), getAssists(event.getEntity()), "" + event.getEntity().getName() + " was killed by %s"));
            } else {
                Bukkit.getServer().getPluginManager().callEvent(new CustomDeathEvent(event.getEntity(), event.getEntity().getLastDamageCause().getCause(), null, getAssists(event.getEntity()), msg));
            }
        }
    }

    private void addAssist(Player player, Player damager) {
        if (!getCombatAssists().containsKey(player))
            getCombatAssists().put(player, new ArrayList<>());

        if (getCombatAssists().get(player).contains(damager))
            return;

        getCombatAssists().get(player).add(damager);

        Bukkit.getServer().getScheduler().runTaskLater(getPlugin(), () -> getCombatAssists().get(player).remove(damager), 400);
    }

    public ArrayList<Player> getAssists(Player player) {
        if (!getCombatAssists().containsKey(player))
            return new ArrayList<>();

        return getCombatAssists().get(player);
    }

    public void firstBlood(Player player) {
        if (firstBloodAnnounced)
            return;

        firstBloodAnnounced = true;
        Bukkit.getServer().getPluginManager().callEvent(new FirstBloodEvent(player));
    }
}
