package net.ddns.vcccd;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Summon Mechanic
 * Summons entities around the caster
 *
 * Parameters:
 * - entity: Entity type to summon (required)
 * - amount: Number to summon (default: 1)
 * - spread: Spread radius (default: 3.0)
 */
public class SummonMechanic extends AbstractMechanic {

    private final Random random = new Random();

    public SummonMechanic(Main main) {
        super(main, "summon", "Summons entities");
    }

    @Override
    public void execute(LivingEntity caster, LivingEntity target, Map<String, Object> parameters) {
        String entityName = getString(parameters, "entity", null);

        if (entityName == null) {
            return;
        }

        int amount = getInt(parameters, "amount", 1);
        double spread = getDouble(parameters, "spread", 3.0);

        try {
            EntityType entityType = EntityType.valueOf(entityName.toUpperCase());

            for (int i = 0; i < amount; i++) {
                Location spawnLoc = caster.getLocation().clone().add(
                        (random.nextDouble() - 0.5) * spread * 2,
                        0,
                        (random.nextDouble() - 0.5) * spread * 2
                );

                Entity spawned = caster.getWorld().spawnEntity(spawnLoc, entityType);

                if (spawned instanceof LivingEntity) {
                    LivingEntity living = (LivingEntity) spawned;
                    living.setCustomName(ChatColor.GRAY + "Summoned " + entityType.name());
                    living.setCustomNameVisible(true);
                }

                caster.getWorld().spawnParticle(Particle.POOF, spawnLoc, 10);
            }
        } catch (IllegalArgumentException e) {
            main.getConsole().sendMessage(main.getPluginPrefix() +
                    ChatColor.YELLOW + "Invalid entity type: " + entityName);
        }
    }

    @Override
    public Map<String, Object> getDefaultParameters() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("entity", "");
        defaults.put("amount", 1);
        defaults.put("spread", 3.0);
        return defaults;
    }
}