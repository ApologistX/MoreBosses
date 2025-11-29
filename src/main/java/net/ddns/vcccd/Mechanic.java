package net.ddns.vcccd;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Map;

/**
 * Base interface for all boss mechanics
 * Mechanics are modular abilities that can be configured via YAML
 */
public interface Mechanic {

    /**
     * Execute the mechanic
     * @param caster The entity using the mechanic (boss)
     * @param target The target entity (usually a player)
     * @param parameters YAML-configured parameters for this mechanic
     */
    void execute(LivingEntity caster, LivingEntity target, Map<String, Object> parameters);

    /**
     * Get the mechanic's identifier (e.g., "arrowrain", "damage", "blackscreen")
     */
    String getId();

    /**
     * Get a description of what this mechanic does
     */
    String getDescription();

    /**
     * Validate parameters before execution
     * @param parameters The parameters to validate
     * @return true if parameters are valid, false otherwise
     */
    boolean validateParameters(Map<String, Object> parameters);

    /**
     * Get default parameters for this mechanic
     */
    Map<String, Object> getDefaultParameters();
}