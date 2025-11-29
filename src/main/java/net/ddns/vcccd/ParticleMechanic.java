package net.ddns.vcccd;

import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Particle Mechanic
 * Spawns particles at target location
 *
 * Parameters:
 * - particle: Particle type (required)
 * - amount: Number of particles (default: 10)
 * - offsetx: X offset (default: 0.5)
 * - offsety: Y offset (default: 0.5)
 * - offsetz: Z offset (default: 0.5)
 * - speed: Particle speed (default: 0.1)
 */
public class ParticleMechanic extends AbstractMechanic {

    public ParticleMechanic(Main main) {
        super(main, "particle", "Spawns particles");
    }

    @Override
    public void execute(LivingEntity caster, LivingEntity target, Map<String, Object> parameters) {
        String particleName = getString(parameters, "particle", null);

        if (particleName == null) {
            return;
        }

        int amount = getInt(parameters, "amount", 10);
        double offsetX = getDouble(parameters, "offsetx", 0.5);
        double offsetY = getDouble(parameters, "offsety", 0.5);
        double offsetZ = getDouble(parameters, "offsetz", 0.5);
        double speed = getDouble(parameters, "speed", 0.1);

        try {
            Particle particle = Particle.valueOf(particleName.toUpperCase());
            target.getWorld().spawnParticle(particle, target.getLocation().add(0, 1, 0),
                    amount, offsetX, offsetY, offsetZ, speed);
        } catch (IllegalArgumentException e) {
            main.getConsole().sendMessage(main.getPluginPrefix() +
                    org.bukkit.ChatColor.YELLOW + "Invalid particle: " + particleName);
        }
    }

    @Override
    public Map<String, Object> getDefaultParameters() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("particle", "");
        defaults.put("amount", 10);
        defaults.put("offsetx", 0.5);
        defaults.put("offsety", 0.5);
        defaults.put("offsetz", 0.5);
        defaults.put("speed", 0.1);
        return defaults;
    }
}