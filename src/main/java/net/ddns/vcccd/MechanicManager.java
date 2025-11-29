package net.ddns.vcccd;

import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Manages registration and execution of all mechanics
 */
public class MechanicManager {

    private final Main main;
    private final Map<String, Mechanic> mechanics = new HashMap<>();

    public MechanicManager(Main main) {
        this.main = main;
        registerDefaultMechanics();
    }

    /**
     * Register all default mechanics
     */
    private void registerDefaultMechanics() {
        // Register each mechanic
        registerMechanic(new ArrowRainMechanic(main));
        registerMechanic(new DamageMechanic(main));
        registerMechanic(new BlackScreenMechanic(main));
        registerMechanic(new CommandMechanic(main));
        registerMechanic(new TeleportMechanic(main));
        registerMechanic(new PotionEffectMechanic(main));
        registerMechanic(new SoundMechanic(main));
        registerMechanic(new ParticleMechanic(main));
        registerMechanic(new SummonMechanic(main));
        registerMechanic(new ExplosionMechanic(main));

        main.getConsole().sendMessage(main.getPluginPrefix() +
                ChatColor.GREEN + "Registered " + mechanics.size() + " mechanics");
    }

    /**
     * Register a mechanic
     */
    public void registerMechanic(Mechanic mechanic) {
        mechanics.put(mechanic.getId().toLowerCase(), mechanic);
        main.getConsole().sendMessage(main.getPluginPrefix() +
                ChatColor.GRAY + "  - Registered mechanic: " + mechanic.getId());
    }

    /**
     * Get a mechanic by ID
     */
    public Mechanic getMechanic(String id) {
        return mechanics.get(id.toLowerCase());
    }

    /**
     * Execute a mechanic by ID
     */
    public boolean executeMechanic(String mechanicId, LivingEntity caster, LivingEntity target, Map<String, Object> parameters) {
        Mechanic mechanic = getMechanic(mechanicId);

        if (mechanic == null) {
            main.getConsole().sendMessage(main.getPluginPrefix() +
                    ChatColor.YELLOW + "Unknown mechanic: " + mechanicId);
            return false;
        }

        // Merge default parameters with provided parameters
        Map<String, Object> finalParams = new HashMap<>(mechanic.getDefaultParameters());
        if (parameters != null) {
            finalParams.putAll(parameters);
        }

        // Validate parameters
        if (!mechanic.validateParameters(finalParams)) {
            main.getConsole().sendMessage(main.getPluginPrefix() +
                    ChatColor.RED + "Invalid parameters for mechanic: " + mechanicId);
            return false;
        }

        // Execute mechanic
        try {
            mechanic.execute(caster, target, finalParams);
            return true;
        } catch (Exception e) {
            main.getConsole().sendMessage(main.getPluginPrefix() +
                    ChatColor.RED + "Error executing mechanic " + mechanicId + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get all registered mechanics
     */
    public Map<String, Mechanic> getAllMechanics() {
        return new HashMap<>(mechanics);
    }
}