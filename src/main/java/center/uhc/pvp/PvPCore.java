package center.uhc.pvp;

import center.uhc.core.Module;
import center.uhc.pvp.combat.CombatManager;
import center.uhc.pvp.freeze.FreezeManager;
import center.uhc.pvp.listeners.ProjectilePlayerHealthListener;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

public class PvPCore extends Module {

    //All configuration options
    @Getter @Setter private boolean announceLongshot = false;
    @Getter @Setter private double longShotDistance = 20;
    @Getter @Setter private boolean projectilePlayerHealth = true;
    @Getter @Setter private boolean firstBlood = false;

    //Instance
    @Getter private static PvPCore instance;

    //All other data
    @Getter private ArrayList<Player> rodMessageCooldown;

    public PvPCore(JavaPlugin plugin) {
        super(plugin, "PvP Core");
    }

    @Override
    public void onEnable() {
        instance = this;
        rodMessageCooldown = new ArrayList<>();

        //Registering listeners
        getPlugin().getServer().getPluginManager().registerEvents(new ProjectilePlayerHealthListener(), getPlugin());

        //Register modules
        CombatManager combatManager = new CombatManager(getPlugin());
        FreezeManager freezeManager = new FreezeManager(getPlugin());
    }

    public void reset() {
        CombatManager.getInstance().setCombatLastHit(new HashMap<>());
        CombatManager.getInstance().setCombatAssists(new HashMap<>());
        CombatManager.getInstance().setNoDeathMessage(new ArrayList<>());
        CombatManager.getInstance().setFirstBloodAnnounced(false);
    }
}
