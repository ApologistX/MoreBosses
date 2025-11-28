package net.ddns.vcccd;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 * Handles damage reflection for bosses with the REFLECT special attack active
 */
public class BossReflectListener implements Listener {

    private final Main main;

    public BossReflectListener(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onBossDamaged(EntityDamageByEntityEvent event) {
        // Check if a player is attacking a living entity
        if (!(event.getEntity() instanceof LivingEntity)) return;
        if (!(event.getDamager() instanceof Player)) return;

        LivingEntity boss = (LivingEntity) event.getEntity();
        Player attacker = (Player) event.getDamager();

        // Check if boss has reflect active
        if (boss.hasMetadata("reflect_active")) {
            // Reflect 50% of damage back to attacker
            double reflectedDamage = event.getDamage() * 0.5;
            attacker.damage(reflectedDamage);

            // Visual feedback
            attacker.getWorld().spawnParticle(
                    org.bukkit.Particle.CRIT,
                    attacker.getLocation().add(0, 1, 0),
                    10,
                    0.5, 0.5, 0.5
            );
        }
    }
}