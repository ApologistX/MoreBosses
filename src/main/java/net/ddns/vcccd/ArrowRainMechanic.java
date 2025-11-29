package net.ddns.vcccd;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Arrow Rain Mechanic
 * Rains arrows above target location
 *
 * Parameters:
 * - amount: Number of arrows (default: 20)
 * - spread: How much arrows spread out (default: 45)
 * - velocity: Velocity of arrows (default: 20)
 * - fireticks: Duration hit enemies burn (default: 0)
 * - removedelay: Time arrows stay before disappearing in ticks (default: 200)
 * - canpickup: Can players pick up arrows? (default: false)
 * - follows: Does rain follow target? (default: false)
 */
public class ArrowRainMechanic extends AbstractMechanic {

    private final Random random = new Random();

    public ArrowRainMechanic(Main main) {
        super(main, "arrowrain", "Rains arrows above target location");
    }

    @Override
    public void execute(LivingEntity caster, LivingEntity target, Map<String, Object> parameters) {
        int amount = getInt(parameters, "amount", 20);
        double spread = getDouble(parameters, "spread", 4.5);
        double velocity = getDouble(parameters, "velocity", 2.0);
        int fireTicks = getInt(parameters, "fireticks", 0);
        int removeDelay = getInt(parameters, "removedelay", 200);
        boolean canPickup = getBoolean(parameters, "canpickup", false);
        boolean follows = getBoolean(parameters, "follows", false);

        // Determine target location
        final Location targetLoc = target.getLocation().clone();

        // Spawn arrows over time for visual effect
        new BukkitRunnable() {
            int spawned = 0;

            @Override
            public void run() {
                if (spawned >= amount || caster.isDead()) {
                    cancel();
                    return;
                }

                // Get spawn location (follow target if configured)
                Location spawnLoc;
                if (follows && target.isValid() && !target.isDead()) {
                    spawnLoc = target.getLocation().clone();
                } else {
                    spawnLoc = targetLoc.clone();
                }

                // Random offset within spread
                double offsetX = (random.nextDouble() - 0.5) * spread;
                double offsetZ = (random.nextDouble() - 0.5) * spread;
                spawnLoc.add(offsetX, 15, offsetZ); // 15 blocks above

                // Spawn arrow
                Arrow arrow = spawnLoc.getWorld().spawn(spawnLoc, Arrow.class);
                arrow.setShooter(caster);
                arrow.setVelocity(new Vector(0, -velocity, 0));
                arrow.setPickupStatus(canPickup ? Arrow.PickupStatus.ALLOWED : Arrow.PickupStatus.DISALLOWED);

                if (fireTicks > 0) {
                    arrow.setFireTicks(fireTicks);
                }

                // Schedule arrow removal
                if (removeDelay > 0) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (arrow.isValid() && !arrow.isDead()) {
                                arrow.remove();
                            }
                        }
                    }.runTaskLater(main, removeDelay);
                }

                spawned++;
            }
        }.runTaskTimer(main, 0L, 2L); // Spawn 1 arrow every 2 ticks
    }

    @Override
    public Map<String, Object> getDefaultParameters() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("amount", 20);
        defaults.put("spread", 4.5);
        defaults.put("velocity", 2.0);
        defaults.put("fireticks", 0);
        defaults.put("removedelay", 200);
        defaults.put("canpickup", false);
        defaults.put("follows", false);
        return defaults;
    }
}