package net.ddns.vcccd;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Handles passive boss behavior - prevents attacking until provoked
 */
public class PassiveBossHandler implements Listener {

    private final Main main;
    private static final String PASSIVE_METADATA_KEY = "custom_boss_passive";
    private static final String PROVOKED_METADATA_KEY = "custom_boss_provoked";

    public PassiveBossHandler(Main main) {
        this.main = main;
    }

    /**
     * Mark an entity as a passive custom boss
     */
    public static void markAsPassive(LivingEntity entity, Main main) {
        entity.setMetadata(PASSIVE_METADATA_KEY, new FixedMetadataValue(main, true));
        entity.setMetadata(PROVOKED_METADATA_KEY, new FixedMetadataValue(main, false));
    }

    /**
     * Check if entity is a passive custom boss
     */
    private boolean isPassiveBoss(Entity entity) {
        return entity.hasMetadata(PASSIVE_METADATA_KEY) &&
                entity.getMetadata(PASSIVE_METADATA_KEY).get(0).asBoolean();
    }

    /**
     * Check if passive boss has been provoked
     */
    private boolean isProvoked(Entity entity) {
        return entity.hasMetadata(PROVOKED_METADATA_KEY) &&
                entity.getMetadata(PROVOKED_METADATA_KEY).get(0).asBoolean();
    }

    /**
     * Mark boss as provoked (will now fight back)
     */
    private void provoke(LivingEntity entity) {
        entity.setMetadata(PROVOKED_METADATA_KEY, new FixedMetadataValue(main, true));
    }

    /**
     * Prevent passive bosses from targeting players unless provoked
     */
    @EventHandler
    public void onEntityTarget(EntityTargetEvent event) {
        Entity entity = event.getEntity();

        // Check if this is a passive custom boss
        if (!isPassiveBoss(entity)) {
            return;
        }

        // If already provoked, allow normal targeting
        if (isProvoked(entity)) {
            return;
        }

        // If targeting a player and not provoked, cancel it
        if (event.getTarget() instanceof Player) {
            event.setCancelled(true);

            // Clear the target to be safe
            if (entity instanceof Mob) {
                ((Mob) entity).setTarget(null);
            }
        }
    }

    /**
     * When a passive boss is damaged, mark it as provoked
     */
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();

        // Check if a passive boss was hit
        if (!isPassiveBoss(damaged)) {
            return;
        }

        // Already provoked, no need to do anything
        if (isProvoked(damaged)) {
            return;
        }

        // Mark as provoked so it can fight back
        if (damaged instanceof LivingEntity) {
            provoke((LivingEntity) damaged);

            // Set the attacker as target if it's a mob
            if (damaged instanceof Mob && event.getDamager() instanceof LivingEntity) {
                ((Mob) damaged).setTarget((LivingEntity) event.getDamager());
            }
        }
    }
}