package net.ddns.vcccd;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import java.util.Map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Executes custom user-defined special attacks with pattern support
 */
public class CustomAttackExecutor {

    private final Main main;
    private static final Random random = new Random();

    public CustomAttackExecutor(Main main) {
        this.main = main;
    }

    /**
     * Execute a custom attack
     */
    public void executeCustomAttack(String attackId, LivingEntity boss, Player target) {
        CustomAttackManager.CustomAttack attack = CustomAttackManager.getCustomAttack(attackId);
        if (attack == null || boss.isDead()) return;

        CustomAttackManager.AttackEffects effects = attack.getEffects();
        Location bossLoc = boss.getLocation();
        World world = bossLoc.getWorld();

        // Play sounds
        for (String soundStr : effects.sounds) {
            try {
                Sound sound = Sound.valueOf(soundStr);
                world.playSound(bossLoc, sound, 1.0f, 1.0f);
            } catch (IllegalArgumentException e) {
                main.getConsole().sendMessage(main.getPluginPrefix() +
                        ChatColor.YELLOW + "Invalid sound: " + soundStr);
            }
        }

        // Spawn particles at boss location
        for (String particleStr : effects.particles) {
            try {
                Particle particle = Particle.valueOf(particleStr);
                world.spawnParticle(particle, bossLoc.clone().add(0, 1, 0), 20, 1, 1, 1);
            } catch (IllegalArgumentException e) {
                main.getConsole().sendMessage(main.getPluginPrefix() +
                        ChatColor.YELLOW + "Invalid particle: " + particleStr);
            }
        }

        // Handle projectiles with pattern support
        if (effects.projectileType != null && target != null && target.isOnline()) {
            if (effects.pattern != null) {
                // Use pattern-based spawning
                spawnProjectilesWithPattern(boss, target, attack);
            } else {
                // Use default spawning
                spawnProjectiles(boss, target, attack);
            }
        }

        // Handle AOE effects
        if (effects.aoeRadius > 0) {
            handleAOEEffects(boss, attack);
        }

        // Handle summoning
        if (effects.summonEntity != null) {
            summonEntities(boss, attack);
        }

        // Handle teleport behind
        if (effects.teleportBehind && target != null && target.isOnline()) {
            teleportBehindTarget(boss, target);
        }

        // Handle explosion
        if (effects.createExplosion) {
            world.createExplosion(bossLoc, effects.explosionPower, false, false, boss);
        }

        // Apply self buffs
        if (effects.applyToSelf && !effects.potionEffects.isEmpty()) {
            applyPotionEffects(boss, effects.potionEffects);
        }

        // Execute mechanics if defined (NEW)
        if (attack.hasMechanics()) {
            executeMechanics(boss, target, attack);
        }
    }

    /**
     * Execute all mechanics for this attack (NEW)
     */
    private void executeMechanics(LivingEntity boss, Player target, CustomAttackManager.CustomAttack attack) {
        MechanicManager mechanicManager = main.getMechanicManager();
        if (mechanicManager == null) {
            main.getConsole().sendMessage(main.getPluginPrefix() +
                    ChatColor.RED + "MechanicManager not initialized!");
            return;
        }

        for (Map<String, Object> mechanic : attack.getMechanics()) {
            String mechanicId = (String) mechanic.get("id");
            @SuppressWarnings("unchecked")
            Map<String, Object> parameters = (Map<String, Object>) mechanic.get("parameters");

            if (mechanicId != null) {
                mechanicManager.executeMechanic(mechanicId, boss, target, parameters);
            }
        }
    }

    /**
     * Spawn projectiles with pattern (CIRCLE, SPIRAL, RAIN, etc.)
     */
    private void spawnProjectilesWithPattern(LivingEntity boss, Player target, CustomAttackManager.CustomAttack attack) {
        CustomAttackManager.AttackEffects effects = attack.getEffects();
        CustomAttackManager.AttackPattern pattern = effects.pattern;

        // Capture target's current position (important for circle pattern)
        final Location targetPos = target.getLocation().clone();
        final World world = targetPos.getWorld();

        String patternType = pattern.type.toUpperCase();

        switch (patternType) {
            case "CIRCLE":
                spawnCirclePattern(boss, targetPos, attack, pattern);
                break;

            case "RAIN":
                spawnRainPattern(boss, targetPos, attack, pattern);
                break;

            case "SPIRAL":
                spawnSpiralPattern(boss, targetPos, attack, pattern);
                break;

            case "SCATTER":
                spawnScatterPattern(boss, targetPos, attack, pattern);
                break;

            default:
                main.getConsole().sendMessage(main.getPluginPrefix() +
                        ChatColor.YELLOW + "Unknown pattern type: " + pattern.type + ", using default");
                spawnProjectiles(boss, target, attack);
                break;
        }
    }

    /**
     * CIRCLE pattern - Projectiles fall in a circle around target's position
     */
    private void spawnCirclePattern(LivingEntity boss, Location targetPos, CustomAttackManager.CustomAttack attack, CustomAttackManager.AttackPattern pattern) {
        CustomAttackManager.AttackEffects effects = attack.getEffects();
        World world = targetPos.getWorld();

        int projectilesPerWave = pattern.meteorsPerWave;
        int waves = pattern.waves;
        double radius = pattern.radius;
        double heightOffset = pattern.heightOffset;
        int intervalTicks = pattern.intervalTicks;

        // Spawn waves
        for (int wave = 0; wave < waves; wave++) {
            final int currentWave = wave;

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (boss.isDead()) {
                        cancel();
                        return;
                    }

                    // Calculate spawn positions in a circle around target
                    for (int i = 0; i < projectilesPerWave; i++) {
                        double angle = (2 * Math.PI / projectilesPerWave) * i;
                        double x = targetPos.getX() + (radius * Math.cos(angle));
                        double z = targetPos.getZ() + (radius * Math.sin(angle));
                        double y = targetPos.getY() + heightOffset;

                        Location spawnLoc = new Location(world, x, y, z);

                        // Direction is straight down
                        Vector direction = new Vector(0, -1, 0);

                        // Spawn projectile
                        spawnSingleProjectile(spawnLoc, direction, boss, attack, world);

                        // Spawn particle at spawn point
                        world.spawnParticle(Particle.FLAME, spawnLoc, 5, 0.1, 0.1, 0.1, 0.01);
                    }
                }
            }.runTaskLater(main, (long) wave * intervalTicks);
        }
    }

    /**
     * RAIN pattern - Projectiles rain down from above in a spread
     */
    private void spawnRainPattern(LivingEntity boss, Location targetPos, CustomAttackManager.CustomAttack attack, CustomAttackManager.AttackPattern pattern) {
        CustomAttackManager.AttackEffects effects = attack.getEffects();
        World world = targetPos.getWorld();

        int projectilesPerWave = pattern.meteorsPerWave;
        int waves = pattern.waves;
        double radius = pattern.radius;
        double heightOffset = pattern.heightOffset;
        int intervalTicks = pattern.intervalTicks;

        for (int wave = 0; wave < waves; wave++) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (boss.isDead()) {
                        cancel();
                        return;
                    }

                    for (int i = 0; i < projectilesPerWave; i++) {
                        // Random position within radius
                        double angle = random.nextDouble() * 2 * Math.PI;
                        double distance = random.nextDouble() * radius;
                        double x = targetPos.getX() + (distance * Math.cos(angle));
                        double z = targetPos.getZ() + (distance * Math.sin(angle));
                        double y = targetPos.getY() + heightOffset;

                        Location spawnLoc = new Location(world, x, y, z);
                        Vector direction = new Vector(0, -1, 0);

                        spawnSingleProjectile(spawnLoc, direction, boss, attack, world);
                        world.spawnParticle(Particle.FLAME, spawnLoc, 3, 0.1, 0.1, 0.1, 0.01);
                    }
                }
            }.runTaskLater(main, (long) wave * intervalTicks);
        }
    }

    /**
     * SPIRAL pattern - Projectiles spawn in a spiraling pattern
     */
    private void spawnSpiralPattern(LivingEntity boss, Location targetPos, CustomAttackManager.CustomAttack attack, CustomAttackManager.AttackPattern pattern) {
        CustomAttackManager.AttackEffects effects = attack.getEffects();
        World world = targetPos.getWorld();

        int totalProjectiles = pattern.meteorsPerWave * pattern.waves;
        double radius = pattern.radius;
        double heightOffset = pattern.heightOffset;
        int intervalTicks = pattern.intervalTicks;

        for (int i = 0; i < totalProjectiles; i++) {
            final int index = i;

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (boss.isDead()) {
                        cancel();
                        return;
                    }

                    // Spiral outward
                    double angle = (index * 0.5);  // Spiral rate
                    double dist = (radius / totalProjectiles) * index;  // Expand outward
                    double x = targetPos.getX() + (dist * Math.cos(angle));
                    double z = targetPos.getZ() + (dist * Math.sin(angle));
                    double y = targetPos.getY() + heightOffset;

                    Location spawnLoc = new Location(world, x, y, z);
                    Vector direction = new Vector(0, -1, 0);

                    spawnSingleProjectile(spawnLoc, direction, boss, attack, world);
                    world.spawnParticle(Particle.FLAME, spawnLoc, 3, 0.1, 0.1, 0.1, 0.01);
                }
            }.runTaskLater(main, (long) index * (intervalTicks / 2));
        }
    }

    /**
     * SCATTER pattern - Random projectiles around target
     */
    private void spawnScatterPattern(LivingEntity boss, Location targetPos, CustomAttackManager.CustomAttack attack, CustomAttackManager.AttackPattern pattern) {
        CustomAttackManager.AttackEffects effects = attack.getEffects();
        World world = targetPos.getWorld();

        int totalProjectiles = pattern.meteorsPerWave;
        double radius = pattern.radius;
        double heightOffset = pattern.heightOffset;

        for (int i = 0; i < totalProjectiles; i++) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (boss.isDead()) {
                        cancel();
                        return;
                    }

                    // Random scattered positions
                    double x = targetPos.getX() + (random.nextDouble() - 0.5) * radius * 2;
                    double z = targetPos.getZ() + (random.nextDouble() - 0.5) * radius * 2;
                    double y = targetPos.getY() + heightOffset;

                    Location spawnLoc = new Location(world, x, y, z);
                    Vector direction = new Vector(0, -1, 0);

                    spawnSingleProjectile(spawnLoc, direction, boss, attack, world);
                    world.spawnParticle(Particle.FLAME, spawnLoc, 3, 0.1, 0.1, 0.1, 0.01);
                }
            }.runTaskLater(main, (long) i * 2);
        }
    }

    /**
     * Spawn a single projectile of the specified type
     */
    private void spawnSingleProjectile(Location spawnLoc, Vector direction, LivingEntity boss, CustomAttackManager.CustomAttack attack, World world) {
        CustomAttackManager.AttackEffects effects = attack.getEffects();

        switch (effects.projectileType.toUpperCase()) {
            case "FIREBALL":
                Fireball fireball = world.spawn(spawnLoc, Fireball.class);
                fireball.setDirection(direction);
                fireball.setYield(effects.explosionPower);
                fireball.setIsIncendiary(effects.setFire);
                fireball.setShooter(boss);
                break;

            case "ARROW":
                Arrow arrow = world.spawn(spawnLoc, Arrow.class);
                arrow.setVelocity(direction.multiply(2));
                arrow.setShooter(boss);
                arrow.setDamage(attack.getDamage());
                if (effects.setFire) {
                    arrow.setFireTicks(effects.fireTicks);
                }
                break;

            case "WITHER_SKULL":
                WitherSkull skull = world.spawn(spawnLoc, WitherSkull.class);
                skull.setDirection(direction);
                skull.setShooter(boss);
                skull.setCharged(effects.explosionPower > 2.0f);
                break;

            case "DRAGON_FIREBALL":
                DragonFireball dragonball = world.spawn(spawnLoc, DragonFireball.class);
                dragonball.setDirection(direction);
                dragonball.setShooter(boss);
                break;

            case "SNOWBALL":
                Snowball snowball = world.spawn(spawnLoc, Snowball.class);
                snowball.setVelocity(direction.multiply(2));
                snowball.setShooter(boss);
                break;

            case "EGG":
                Egg egg = world.spawn(spawnLoc, Egg.class);
                egg.setVelocity(direction.multiply(2));
                egg.setShooter(boss);
                break;

            case "ENDER_PEARL":
                EnderPearl pearl = world.spawn(spawnLoc, EnderPearl.class);
                pearl.setVelocity(direction.multiply(2));
                pearl.setShooter(boss);
                break;

            default:
                main.getConsole().sendMessage(main.getPluginPrefix() +
                        ChatColor.YELLOW + "Unknown projectile type: " + effects.projectileType);
                break;
        }
    }

    /**
     * Spawn projectiles at target (default behavior, no pattern)
     */
    private void spawnProjectiles(LivingEntity boss, Player target, CustomAttackManager.CustomAttack attack) {
        CustomAttackManager.AttackEffects effects = attack.getEffects();
        Location eyeLoc = boss.getEyeLocation();
        Vector direction = target.getEyeLocation().toVector().subtract(eyeLoc.toVector()).normalize();

        for (int i = 0; i < effects.projectileCount; i++) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (boss.isDead() || !target.isOnline()) {
                        cancel();
                        return;
                    }

                    // Recalculate direction for each projectile
                    Vector dir = target.getEyeLocation().toVector().subtract(boss.getEyeLocation().toVector()).normalize();

                    spawnSingleProjectile(boss.getEyeLocation(), dir, boss, attack, boss.getWorld());
                }
            }.runTaskLater(main, i * 5L); // Stagger projectiles
        }
    }

    /**
     * Handle area of effect damage and effects
     */
    private void handleAOEEffects(LivingEntity boss, CustomAttackManager.CustomAttack attack) {
        CustomAttackManager.AttackEffects effects = attack.getEffects();
        Location loc = boss.getLocation();
        World world = loc.getWorld();

        Collection<Entity> nearby = world.getNearbyEntities(loc, effects.aoeRadius, effects.aoeRadius, effects.aoeRadius);

        for (Entity entity : nearby) {
            if (entity instanceof Player && entity != boss) {
                Player player = (Player) entity;

                // Apply damage
                if (attack.getDamage() > 0) {
                    player.damage(attack.getDamage(), boss);
                }

                // Apply knockback
                if (effects.knockback > 0) {
                    Vector direction = player.getLocation().toVector().subtract(loc.toVector()).normalize();
                    direction.setY(0.3);
                    player.setVelocity(direction.multiply(effects.knockback));
                }

                // Set on fire
                if (effects.setFire) {
                    player.setFireTicks(effects.fireTicks);
                }

                // Apply potion effects
                if (!effects.applyToSelf && !effects.potionEffects.isEmpty()) {
                    applyPotionEffects(player, effects.potionEffects);
                }
            }
        }
    }

    /**
     * Summon entities around the boss
     */
    private void summonEntities(LivingEntity boss, CustomAttackManager.CustomAttack attack) {
        CustomAttackManager.AttackEffects effects = attack.getEffects();
        Location loc = boss.getLocation();
        World world = loc.getWorld();

        try {
            EntityType entityType = EntityType.valueOf(effects.summonEntity.toUpperCase());

            for (int i = 0; i < effects.summonCount; i++) {
                Location spawnLoc = loc.clone().add(
                        random.nextDouble() * 6 - 3,
                        0,
                        random.nextDouble() * 6 - 3
                );

                Entity spawned = world.spawnEntity(spawnLoc, entityType);

                if (spawned instanceof LivingEntity) {
                    LivingEntity living = (LivingEntity) spawned;
                    living.setCustomName(ChatColor.GRAY + "Summoned " + entityType.name());
                    living.setCustomNameVisible(true);
                }

                world.spawnParticle(Particle.POOF, spawnLoc, 10);
            }

            world.playSound(loc, Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1.0f, 1.0f);

        } catch (IllegalArgumentException e) {
            main.getConsole().sendMessage(main.getPluginPrefix() +
                    ChatColor.YELLOW + "Invalid entity type: " + effects.summonEntity);
        }
    }

    /**
     * Teleport boss behind target
     */
    private void teleportBehindTarget(LivingEntity boss, Player target) {
        Location originalLoc = boss.getLocation();
        Location behindTarget = target.getLocation().subtract(target.getLocation().getDirection().multiply(2));

        boss.getWorld().spawnParticle(Particle.PORTAL, originalLoc, 50);
        boss.getWorld().playSound(originalLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

        boss.teleport(behindTarget);

        boss.getWorld().spawnParticle(Particle.PORTAL, behindTarget, 50);
        boss.getWorld().playSound(behindTarget, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
    }

    /**
     * Apply potion effects to an entity
     * Format: "EFFECT_NAME:duration:amplifier"
     */
    private void applyPotionEffects(LivingEntity entity, List<String> potionEffects) {
        for (String effectStr : potionEffects) {
            try {
                String[] parts = effectStr.split(":");
                if (parts.length >= 3) {
                    PotionEffectType type = PotionEffectType.getByName(parts[0]);
                    int duration = Integer.parseInt(parts[1]);
                    int amplifier = Integer.parseInt(parts[2]);

                    if (type != null) {
                        entity.addPotionEffect(new PotionEffect(type, duration, amplifier));
                    }
                }
            } catch (Exception e) {
                main.getConsole().sendMessage(main.getPluginPrefix() +
                        ChatColor.YELLOW + "Invalid potion effect format: " + effectStr);
            }
        }
    }
}