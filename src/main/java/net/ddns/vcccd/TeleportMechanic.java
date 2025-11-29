package net.ddns.vcccd;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

/**
 * Teleport Mechanic
 * Teleports caster behind target
 *
 * Parameters:
 * - behind: Teleport behind target (default: false)
 * - distance: Distance behind target (default: 2.0)
 */
public class TeleportMechanic extends AbstractMechanic {

    public TeleportMechanic(Main main) {
        super(main, "teleport", "Teleports caster");
    }

    @Override
    public void execute(LivingEntity caster, LivingEntity target, Map<String, Object> parameters) {
        boolean behind = getBoolean(parameters, "behind", false);
        double distance = getDouble(parameters, "distance", 2.0);

        if (behind) {
            // Teleport behind target
            Vector direction = target.getLocation().getDirection().normalize();
            Location behindTarget = target.getLocation().subtract(direction.multiply(distance));

            // Particles and sound at old location
            caster.getWorld().spawnParticle(Particle.PORTAL, caster.getLocation(), 50);
            caster.getWorld().playSound(caster.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

            // Teleport
            caster.teleport(behindTarget);

            // Particles and sound at new location
            caster.getWorld().spawnParticle(Particle.PORTAL, behindTarget, 50);
            caster.getWorld().playSound(behindTarget, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        }
    }

    @Override
    public Map<String, Object> getDefaultParameters() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("behind", false);
        defaults.put("distance", 2.0);
        return defaults;
    }
}