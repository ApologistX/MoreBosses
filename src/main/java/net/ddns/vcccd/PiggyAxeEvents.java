package net.ddns.vcccd;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

public class PiggyAxeEvents implements Listener {

    private final JavaPlugin plugin;
    private final Random random = new Random();

    public PiggyAxeEvents(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // Returns a number in [1, maxInclusive]
    private int RNG(int maxInclusive) {
        return random.nextInt(maxInclusive) + 1;
    }

    @EventHandler
    public void onPiggyHit(EntityDamageByEntityEvent event) {
        try {
            // Only care about Piglin Brute -> Player hits
            if (!(event.getDamager() instanceof PiglinBrute piggy)) {
                return;
            }
            if (!(event.getEntity() instanceof Player target)) {
                return;
            }

            // Make sure this is *our* boss
            String expectedName = ChatColor.translateAlternateColorCodes('&', "&c&lPiGgY");
            String actualName = piggy.getCustomName();

            if (actualName == null || !actualName.equals(expectedName)) {
                return;
            }

            // 50% chance, same as RNG(2) == 1
            if (RNG(2) != 1) {
                return;
            }

            // Compute a knockback vector opposite of where the player is looking
            Location loc = target.getLocation();
            Vector dir = loc.getDirection().normalize().multiply(-1); // push backwards
            dir.setY(1.0); // nice upward launch

            // IMPORTANT for 1.21.4:
            // apply AFTER vanilla knockback using a 1-tick delayed task
            Bukkit.getScheduler().runTask(plugin, () -> target.setVelocity(dir));

        } catch (Exception ignored) {
            // Avoid killing the event pipeline on unexpected errors
        }
    }
}
