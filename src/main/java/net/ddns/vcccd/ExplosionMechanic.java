package net.ddns.vcccd;

import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Explosion Mechanic
 * Creates an explosion at the target's location
 *
 * Parameters:
 * - power: Explosion power (default: 2.0)
 * - setfire: Whether to set fire (default: false)
 * - breakblocks: Whether to break blocks (default: false)
 */
public class ExplosionMechanic extends AbstractMechanic {

    public ExplosionMechanic(Main main) {
        super(main, "explosion", "Creates an explosion");
    }

    @Override
    public void execute(LivingEntity caster, LivingEntity target, Map<String, Object> parameters) {
        float power = (float) getDouble(parameters, "power", 2.0);
        boolean setFire = getBoolean(parameters, "setfire", false);
        boolean breakBlocks = getBoolean(parameters, "breakblocks", false);

        target.getWorld().createExplosion(
                target.getLocation(),
                power,
                setFire,
                breakBlocks,
                caster
        );
    }

    @Override
    public Map<String, Object> getDefaultParameters() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("power", 2.0);
        defaults.put("setfire", false);
        defaults.put("breakblocks", false);
        return defaults;
    }
}