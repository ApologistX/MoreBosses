package net.ddns.vcccd;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * AI Controller for custom bosses with special attacks
 * Manages attack cooldowns, targeting, ability rotation, and looping attacks
 */
public class BossAIController {

    private final Main main;
    private final BossAttackExecutor attackExecutor;
    private final Map<UUID, BossAIData> activeBosses = new HashMap<>();

    private static final String PASSIVE_METADATA_KEY = "custom_boss_passive";
    private static final String PROVOKED_METADATA_KEY = "custom_boss_provoked";

    public BossAIController(Main main) {
        this.main = main;
        this.attackExecutor = new BossAttackExecutor(main);
        main.getConsole().sendMessage(main.getPluginPrefix() +
                org.bukkit.ChatColor.GREEN + "BossAIController initialized with attack executor");
    }

    /**
     * Register a boss to be controlled by the AI system
     */
    public void registerBoss(LivingEntity boss, ArrayList<String> specialAttacks) {
        registerBoss(boss, specialAttacks, true, 160); // Default 8 seconds (160 ticks)
    }

    /**
     * Register a boss with loop configuration
     */
    public void registerBoss(LivingEntity boss, ArrayList<String> specialAttacks, boolean loopAttacks, int loopDelay) {
        if (specialAttacks == null || specialAttacks.isEmpty()) {
            main.getConsole().sendMessage(main.getPluginPrefix() +
                    org.bukkit.ChatColor.RED + "Warning: Boss registered with no special attacks");
            return;
        }

        BossAIData aiData = new BossAIData(boss, specialAttacks, loopAttacks, loopDelay);
        activeBosses.put(boss.getUniqueId(), aiData);

        main.getConsole().sendMessage(main.getPluginPrefix() +
                org.bukkit.ChatColor.GREEN + "Registered boss " + boss.getName() + " with " +
                specialAttacks.size() + " attacks (Loop: " + loopAttacks + ", Delay: " + loopDelay + " ticks)");

        // Start AI loop for this boss
        startAILoop(boss.getUniqueId());
    }

    /**
     * Unregister a boss from the AI system (on death/despawn)
     */
    public void unregisterBoss(UUID bossUUID) {
        BossAIData aiData = activeBosses.remove(bossUUID);
        if (aiData != null) {
            if (aiData.aiTask != null) {
                aiData.aiTask.cancel();
            }
            main.getConsole().sendMessage(main.getPluginPrefix() +
                    org.bukkit.ChatColor.YELLOW + "Unregistered boss from AI controller");
        }
    }

    /**
     * Check if entity is a passive boss that hasn't been provoked
     */
    private boolean isPassiveAndNotProvoked(LivingEntity boss) {
        if (!boss.hasMetadata(PASSIVE_METADATA_KEY)) {
            return false;
        }

        boolean isPassive = boss.getMetadata(PASSIVE_METADATA_KEY).get(0).asBoolean();
        if (!isPassive) {
            return false;
        }

        if (!boss.hasMetadata(PROVOKED_METADATA_KEY)) {
            return true; // Passive and not marked provoked yet
        }

        boolean isProvoked = boss.getMetadata(PROVOKED_METADATA_KEY).get(0).asBoolean();
        return !isProvoked; // Return true if NOT provoked
    }

    /**
     * Start the AI decision loop for a boss
     */
    private void startAILoop(UUID bossUUID) {
        BukkitRunnable aiTask = new BukkitRunnable() {
            @Override
            public void run() {
                BossAIData aiData = activeBosses.get(bossUUID);
                if (aiData == null || aiData.boss.isDead()) {
                    unregisterBoss(bossUUID);
                    cancel();
                    return;
                }

                // Process AI tick
                processAITick(aiData);
            }
        };

        // Store task reference
        BossAIData aiData = activeBosses.get(bossUUID);
        if (aiData != null) {
            aiData.aiTask = aiTask;
            // Run every 20 ticks (1 second) for better responsiveness
            aiTask.runTaskTimer(main, 20L, 20L);
        }
    }

    /**
     * Process one AI decision tick
     */
    private void processAITick(BossAIData aiData) {
        LivingEntity boss = aiData.boss;

        // Don't attack if passive and not provoked
        if (isPassiveAndNotProvoked(boss)) {
            return;
        }

        // Reduce cooldowns (using 20 ticks per AI cycle now)
        aiData.tickCooldowns(20);

        // Try to find target if boss is a Mob
        Player target = null;
        if (boss instanceof Mob) {
            Mob mob = (Mob) boss;
            if (mob.getTarget() instanceof Player) {
                target = (Player) mob.getTarget();
            }
        }

        // If no target from mob AI, find nearest player
        if (target == null) {
            target = findNearestPlayer(boss, 30.0);
        }

        // If still no target, skip this tick
        if (target == null) return;

        // Check if we should use loop mode or random mode
        String attackToUse = null;

        if (aiData.loopAttacks) {
            // Loop mode: execute attacks in sequence
            attackToUse = aiData.getNextLoopAttack();
        } else {
            // Random mode: select available attack
            attackToUse = aiData.selectAttack();
        }

        if (attackToUse != null) {
            main.getConsole().sendMessage(main.getPluginPrefix() +
                    org.bukkit.ChatColor.AQUA + "Boss " + boss.getName() + " executing attack: " + attackToUse);
            attackExecutor.executeAttack(attackToUse, boss, target);
            aiData.useAttack(attackToUse);
        }
    }

    /**
     * Find the nearest player within range
     */
    private Player findNearestPlayer(LivingEntity boss, double maxRange) {
        Player nearest = null;
        double nearestDistance = maxRange;

        for (Player player : boss.getWorld().getPlayers()) {
            if (player.getGameMode() == org.bukkit.GameMode.CREATIVE ||
                    player.getGameMode() == org.bukkit.GameMode.SPECTATOR) {
                continue;
            }

            double distance = boss.getLocation().distance(player.getLocation());
            if (distance < nearestDistance) {
                nearest = player;
                nearestDistance = distance;
            }
        }

        return nearest;
    }

    /**
     * Inner class to store AI data for each boss
     */
    private class BossAIData {
        private final LivingEntity boss;
        private final ArrayList<String> specialAttacks;
        private final Map<String, Integer> attackCooldowns;
        private final Random random = new Random();
        private BukkitRunnable aiTask;

        // Loop mode settings
        private final boolean loopAttacks;
        private final int loopDelay;
        private int currentAttackIndex = 0;
        private int ticksUntilNextAction = 0; // Unified timer for both loop delay and between attacks

        // Attack frequency settings (only for random mode)
        private static final int BASE_COOLDOWN = 100; // 5 seconds in ticks
        private static final int GLOBAL_COOLDOWN = 40; // 2 seconds between any attacks
        private static final int LOOP_ATTACK_DELAY = 40; // 2 seconds between attacks in loop mode

        private int globalCooldown = 0;

        public BossAIData(LivingEntity boss, ArrayList<String> specialAttacks, boolean loopAttacks, int loopDelay) {
            this.boss = boss;
            this.specialAttacks = new ArrayList<>(specialAttacks);
            this.attackCooldowns = new HashMap<>();
            this.loopAttacks = loopAttacks;
            this.loopDelay = loopDelay;

            // Initialize all attack cooldowns (only used in random mode)
            for (String attack : specialAttacks) {
                attackCooldowns.put(attack, 0);
            }

            // Log what attacks were registered
            main.getConsole().sendMessage(main.getPluginPrefix() +
                    org.bukkit.ChatColor.GRAY + "Attacks registered: " + String.join(", ", specialAttacks));
        }

        /**
         * Tick down all cooldowns
         */
        public void tickCooldowns(int ticksPassed) {
            if (loopAttacks) {
                // In loop mode, tick down the unified timer
                if (ticksUntilNextAction > 0) {
                    ticksUntilNextAction = Math.max(0, ticksUntilNextAction - ticksPassed);
                }
            } else {
                // In random mode, use the cooldown system
                if (globalCooldown > 0) {
                    globalCooldown = Math.max(0, globalCooldown - ticksPassed);
                }

                for (String attack : attackCooldowns.keySet()) {
                    int cooldown = attackCooldowns.get(attack);
                    if (cooldown > 0) {
                        attackCooldowns.put(attack, Math.max(0, cooldown - ticksPassed));
                    }
                }
            }
        }

        /**
         * Get the next attack in loop sequence
         */
        public String getNextLoopAttack() {
            if (specialAttacks.isEmpty()) {
                return null;
            }

            // If we're still cooling down, don't attack
            if (ticksUntilNextAction > 0) {
                return null;
            }

            // Get current attack
            String attack = specialAttacks.get(currentAttackIndex);

            // Move to next attack
            currentAttackIndex++;

            // If we've finished all attacks, reset and apply loop delay
            if (currentAttackIndex >= specialAttacks.size()) {
                currentAttackIndex = 0;
                ticksUntilNextAction = loopDelay; // Apply the full loop delay
                main.getConsole().sendMessage(main.getPluginPrefix() +
                        org.bukkit.ChatColor.GRAY + "Attack loop completed, waiting " + loopDelay + " ticks");
            } else {
                // Between attacks in the sequence, use shorter delay
                ticksUntilNextAction = LOOP_ATTACK_DELAY;
            }

            return attack;
        }

        /**
         * Select an attack to use (returns null if none available)
         */
        public String selectAttack() {
            // Check global cooldown
            if (globalCooldown > 0) {
                return null;
            }

            // Get all attacks that are off cooldown
            ArrayList<String> availableAttacks = new ArrayList<>();
            for (String attack : specialAttacks) {
                if (attackCooldowns.get(attack) <= 0) {
                    availableAttacks.add(attack);
                }
            }

            // If no attacks available, return null
            if (availableAttacks.isEmpty()) {
                return null;
            }

            // Select random attack from available
            return availableAttacks.get(random.nextInt(availableAttacks.size()));
        }

        /**
         * Mark an attack as used and set its cooldown
         */
        public void useAttack(String attackId) {
            if (loopAttacks) {
                // In loop mode, the timer is already set in getNextLoopAttack()
                // No need to do anything here
            } else {
                // In random mode, set cooldowns
                BossSpecialAttacks.SpecialAttack attack = BossSpecialAttacks.getAttack(attackId);

                int cooldown = BASE_COOLDOWN;
                if (attack != null) {
                    switch (attack.getCategory()) {
                        case MELEE:
                            cooldown = 60; // 3 seconds
                            break;
                        case RANGED:
                            cooldown = 80; // 4 seconds
                            break;
                        case AOE:
                            cooldown = 120; // 6 seconds
                            break;
                        case SUMMON:
                            cooldown = 300; // 15 seconds
                            break;
                        case BUFF:
                        case DEBUFF:
                            cooldown = 200; // 10 seconds
                            break;
                        case SPECIAL:
                            cooldown = 240; // 12 seconds
                            break;
                    }
                }
                attackCooldowns.put(attackId, cooldown);
                globalCooldown = GLOBAL_COOLDOWN;
            }
        }
    }
}