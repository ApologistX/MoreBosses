package net.ddns.vcccd;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Damage Mechanic
 * Deals damage to target with various options
 *
 * Parameters:
 * - amount: Damage amount (default: 5.0)
 * - ignorearmor: Ignore armor (default: false)
 * - preventknockback: Prevent knockback (default: false)
 * - preventimmunity: Prevent damage immunity ticks (default: false)
 * - element: Damage element - FIRE, ICE, LIGHTNING (default: null)
 * - tags: Comma-separated tags (default: "")
 */
public class DamageMechanic extends AbstractMechanic {

    public DamageMechanic(Main main) {
        super(main, "damage", "Deals damage to target");
    }

    @Override
    public void execute(LivingEntity caster, LivingEntity target, Map<String, Object> parameters) {
        double amount = getDouble(parameters, "amount", 5.0);
        boolean ignoreArmor = getBoolean(parameters, "ignorearmor", false);
        boolean preventKnockback = getBoolean(parameters, "preventknockback", false);
        boolean preventImmunity = getBoolean(parameters, "preventimmunity", false);
        String element = getString(parameters, "element", null);
        String tags = getString(parameters, "tags", "");

        // Deal damage
        if (ignoreArmor) {
            // Direct health modification
            double newHealth = Math.max(0, target.getHealth() - amount);
            target.setHealth(newHealth);
        } else {
            target.damage(amount, caster);
        }

        // Prevent knockback if requested
        if (preventKnockback && target instanceof Player) {
            target.setVelocity(target.getVelocity().multiply(0));
        }

        // Reset damage immunity
        if (preventImmunity) {
            target.setNoDamageTicks(0);
        }

        // Apply elemental effects
        if (element != null) {
            applyElementalEffect(target, element);
        }
    }

    private void applyElementalEffect(LivingEntity target, String element) {
        switch (element.toUpperCase()) {
            case "FIRE":
                target.setFireTicks(100);
                break;
            case "ICE":
                // Slowness effect handled by PotionEffectMechanic
                break;
            case "LIGHTNING":
                target.getWorld().strikeLightningEffect(target.getLocation());
                break;
        }
    }

    @Override
    public Map<String, Object> getDefaultParameters() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("amount", 5.0);
        defaults.put("ignorearmor", false);
        defaults.put("preventknockback", false);
        defaults.put("preventimmunity", false);
        defaults.put("element", null);
        defaults.put("tags", "");
        return defaults;
    }
}