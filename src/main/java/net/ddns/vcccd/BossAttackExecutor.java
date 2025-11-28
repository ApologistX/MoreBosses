package net.ddns.vcccd;

import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

/**
 * Executes special attacks for custom bosses
 * This class contains all the implementation logic for boss special attacks
 */
public class BossAttackExecutor {

    private final Main main;
    private final CustomAttackExecutor customAttackExecutor;
    private static final Random random = new Random();

    public BossAttackExecutor(Main main) {
        this.main = main;
        this.customAttackExecutor = new CustomAttackExecutor(main);
    }

    /**
     * Execute a special attack from a boss
     */
    public void executeAttack(String attackId, LivingEntity boss, Player target) {
        // Check if it's a custom attack first
        if (CustomAttackManager.isCustomAttack(attackId)) {
            customAttackExecutor.executeCustomAttack(attackId, boss, target);
            return;
        }

        BossSpecialAttacks.SpecialAttack attack = BossSpecialAttacks.getAttack(attackId);
        if (attack == null || boss.isDead()) return;

        switch (attackId) {
            // MELEE ATTACKS
            case "SLAM_ATTACK":
                executeSlamAttack(boss, attack);
                break;
            case "CLEAVE":
                executeCleave(boss, attack);
                break;
            case "CHARGE":
                executeCharge(boss, target, attack);
                break;

            // RANGED ATTACKS
            case "FIREBALL":
                executeFireball(boss, target, attack);
                break;
            case "LIGHTNING":
                executeLightning(boss, target, attack);
                break;
            case "ARROW_RAIN":
                executeArrowRain(boss, target, attack);
                break;
            case "ENDER_PEARL":
                executeEnderPearlStrike(boss, target, attack);
                break;

            // AOE ATTACKS
            case "POISON_CLOUD":
                executePoisonCloud(boss, attack);
                break;
            case "EXPLOSION":
                executeExplosion(boss, attack);
                break;
            case "FROST_NOVA":
                executeFrostNova(boss, attack);
                break;
            case "SHOCKWAVE":
                executeShockwave(boss, attack);
                break;

            // SUMMON ABILITIES
            case "SUMMON_MINIONS":
                executeSummonMinions(boss, attack);
                break;
            case "SUMMON_WALL":
                executeSummonWall(boss, attack);
                break;

            // BUFF/DEBUFF
            case "REGEN":
                executeRegen(boss, attack);
                break;
            case "ENRAGE":
                executeEnrage(boss, attack);
                break;
            case "WEAKEN":
                executeWeaken(boss, attack);
                break;
            case "BLIND":
                executeBlind(boss, attack);
                break;

            // SPECIAL MECHANICS
            case "PHASE_SHIFT":
                executePhaseShift(boss, attack);
                break;
            case "LIFE_DRAIN":
                executeLifeDrain(boss, attack);
                break;
            case "REFLECT":
                executeReflect(boss, attack);
                break;

            default:
                break;
        }
    }

    // ===== MELEE ATTACKS =====

    private void executeSlamAttack(LivingEntity boss, BossSpecialAttacks.SpecialAttack attack) {
        Location loc = boss.getLocation();
        World world = loc.getWorld();
        if (world == null) return;

        // Visual effects
        world.spawnParticle(Particle.EXPLOSION, loc, 5, 1, 0.5, 1);
        world.playSound(loc, Sound.ENTITY_GENERIC_EXPLODE, 1.0f, 0.5f);

        // Damage and knockback nearby entities
        Collection<Entity> nearby = world.getNearbyEntities(loc, attack.getRange(), attack.getRange(), attack.getRange());
        for (Entity entity : nearby) {
            if (entity instanceof Player && entity != boss) {
                Player player = (Player) entity;
                player.damage(attack.getDamage(), boss);

                // Knockback
                Vector direction = player.getLocation().toVector().subtract(loc.toVector()).normalize();
                direction.setY(0.5);
                player.setVelocity(direction.multiply(1.5));
            }
        }
    }

    private void executeCleave(LivingEntity boss, BossSpecialAttacks.SpecialAttack attack) {
        Location loc = boss.getLocation();
        World world = loc.getWorld();
        if (world == null) return;

        Vector direction = loc.getDirection().normalize();

        // Particle arc effect
        for (int i = -30; i <= 30; i += 10) {
            Vector particleDir = rotateVector(direction, i);
            Location particleLoc = loc.clone().add(particleDir.multiply(2)).add(0, 1, 0);
            world.spawnParticle(Particle.SWEEP_ATTACK, particleLoc, 1);
        }

        world.playSound(loc, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.0f, 1.0f);

        // Damage in arc
        Collection<Entity> nearby = world.getNearbyEntities(loc, attack.getRange(), attack.getRange(), attack.getRange());
        for (Entity entity : nearby) {
            if (entity instanceof Player && entity != boss) {
                // Check if player is in front arc
                Vector toPlayer = entity.getLocation().toVector().subtract(loc.toVector()).normalize();
                if (direction.dot(toPlayer) > 0.5) {
                    ((Player) entity).damage(attack.getDamage(), boss);
                }
            }
        }
    }

    private void executeCharge(LivingEntity boss, Player target, BossSpecialAttacks.SpecialAttack attack) {
        if (target == null || !target.isOnline()) return;

        Location bossLoc = boss.getLocation();
        Location targetLoc = target.getLocation();
        World world = bossLoc.getWorld();

        // Calculate charge direction
        Vector direction = targetLoc.toVector().subtract(bossLoc.toVector()).normalize();
        direction.setY(0.2);

        // Visual warning
        world.spawnParticle(Particle.ANGRY_VILLAGER, bossLoc.add(0, 2, 0), 3);
        world.playSound(bossLoc, Sound.ENTITY_RAVAGER_ROAR, 1.0f, 1.0f);

        // Execute charge after delay
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (boss.isDead() || ticks++ > 30) {
                    cancel();
                    return;
                }

                boss.setVelocity(direction.clone().multiply(0.8));
                world.spawnParticle(Particle.CLOUD, boss.getLocation(), 2);

                // Check for collision
                Collection<Entity> nearby = world.getNearbyEntities(boss.getLocation(), 1.5, 1.5, 1.5);
                for (Entity entity : nearby) {
                    if (entity instanceof Player && entity != boss) {
                        ((Player) entity).damage(attack.getDamage(), boss);
                        cancel();
                        return;
                    }
                }
            }
        }.runTaskTimer(main, 10L, 1L);
    }

    // ===== RANGED ATTACKS =====

    private void executeFireball(LivingEntity boss, Player target, BossSpecialAttacks.SpecialAttack attack) {
        if (target == null || !target.isOnline()) return;

        Location loc = boss.getEyeLocation();
        Vector direction = target.getEyeLocation().toVector().subtract(loc.toVector()).normalize();

        Fireball fireball = boss.getWorld().spawn(loc, Fireball.class);
        fireball.setDirection(direction);
        fireball.setYield(2.0f);
        fireball.setIsIncendiary(false);
        fireball.setShooter(boss);

        boss.getWorld().playSound(loc, Sound.ENTITY_GHAST_SHOOT, 1.0f, 1.0f);
    }

    private void executeLightning(LivingEntity boss, Player target, BossSpecialAttacks.SpecialAttack attack) {
        if (target == null || !target.isOnline()) return;

        Location targetLoc = target.getLocation();
        World world = targetLoc.getWorld();

        // Warning particles
        world.spawnParticle(Particle.ELECTRIC_SPARK, targetLoc.clone().add(0, 5, 0), 20, 0.5, 2, 0.5);

        // Strike after delay
        new BukkitRunnable() {
            @Override
            public void run() {
                world.strikeLightning(targetLoc);
                target.damage(attack.getDamage(), boss);
            }
        }.runTaskLater(main, 20L);
    }

    private void executeArrowRain(LivingEntity boss, Player target, BossSpecialAttacks.SpecialAttack attack) {
        if (target == null || !target.isOnline()) return;

        Location targetLoc = target.getLocation();
        World world = targetLoc.getWorld();

        boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0f, 0.8f);

        // Spawn multiple arrows
        for (int i = 0; i < 10; i++) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    Location spawnLoc = targetLoc.clone().add(
                            random.nextDouble() * 6 - 3,
                            10,
                            random.nextDouble() * 6 - 3
                    );

                    Arrow arrow = world.spawn(spawnLoc, Arrow.class);
                    arrow.setVelocity(new Vector(0, -1, 0));
                    arrow.setShooter(boss);
                    arrow.setDamage(attack.getDamage());
                }
            }.runTaskLater(main, i * 2L);
        }
    }

    private void executeEnderPearlStrike(LivingEntity boss, Player target, BossSpecialAttacks.SpecialAttack attack) {
        if (target == null || !target.isOnline()) return;

        Location originalLoc = boss.getLocation();
        Location behindTarget = target.getLocation().subtract(target.getLocation().getDirection().multiply(2));

        // Teleport effect
        boss.getWorld().spawnParticle(Particle.PORTAL, originalLoc, 50);
        boss.getWorld().playSound(originalLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

        boss.teleport(behindTarget);

        boss.getWorld().spawnParticle(Particle.PORTAL, behindTarget, 50);
        boss.getWorld().playSound(behindTarget, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);

        // Strike after teleport
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!boss.isDead() && target.isOnline()) {
                    target.damage(attack.getDamage(), boss);
                }
            }
        }.runTaskLater(main, 5L);
    }

    // ... (rest of the methods remain exactly the same)
    // I'm only showing the changes at the top - keep all other methods as they are

    private void executePoisonCloud(LivingEntity boss, BossSpecialAttacks.SpecialAttack attack) {
        Location loc = boss.getLocation();
        World world = loc.getWorld();
        if (world == null) return;

        AreaEffectCloud cloud = world.spawn(loc, AreaEffectCloud.class);
        cloud.setRadius((float) attack.getRange());
        cloud.setDuration(100);
        cloud.setRadiusPerTick(-0.01f);
        cloud.addCustomEffect(new PotionEffect(PotionEffectType.POISON, 60, 1), true);
        cloud.setParticle(Particle.FALLING_SPORE_BLOSSOM);

        world.playSound(loc, Sound.ENTITY_LINGERING_POTION_THROW, 1.0f, 1.0f);
    }

    private void executeExplosion(LivingEntity boss, BossSpecialAttacks.SpecialAttack attack) {
        Location loc = boss.getLocation();
        World world = loc.getWorld();

        world.createExplosion(loc, 3.0f, false, false, boss);
        world.spawnParticle(Particle.EXPLOSION, loc, 10, 2, 2, 2);

        Collection<Entity> nearby = world.getNearbyEntities(loc, attack.getRange(), attack.getRange(), attack.getRange());
        for (Entity entity : nearby) {
            if (entity instanceof Player && entity != boss) {
                ((Player) entity).damage(attack.getDamage(), boss);
            }
        }
    }

    private void executeFrostNova(LivingEntity boss, BossSpecialAttacks.SpecialAttack attack) {
        Location loc = boss.getLocation();
        World world = loc.getWorld();

        world.spawnParticle(Particle.SNOWFLAKE, loc, 100, attack.getRange(), 1, attack.getRange());
        world.playSound(loc, Sound.BLOCK_GLASS_BREAK, 1.0f, 0.5f);

        Collection<Entity> nearby = world.getNearbyEntities(loc, attack.getRange(), attack.getRange(), attack.getRange());
        for (Entity entity : nearby) {
            if (entity instanceof Player && entity != boss) {
                Player player = (Player) entity;
                player.damage(attack.getDamage(), boss);
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 3));
                player.setFreezeTicks(200);
            }
        }
    }

    private void executeShockwave(LivingEntity boss, BossSpecialAttacks.SpecialAttack attack) {
        Location loc = boss.getLocation();
        World world = loc.getWorld();

        world.playSound(loc, Sound.ENTITY_WARDEN_SONIC_BOOM, 1.0f, 1.0f);

        new BukkitRunnable() {
            double radius = 1;
            @Override
            public void run() {
                if (radius > attack.getRange()) {
                    cancel();
                    return;
                }

                for (int i = 0; i < 36; i++) {
                    double angle = 2 * Math.PI * i / 36;
                    Location particleLoc = loc.clone().add(
                            Math.cos(angle) * radius,
                            0.1,
                            Math.sin(angle) * radius
                    );
                    world.spawnParticle(Particle.SONIC_BOOM, particleLoc, 1);
                }

                Collection<Entity> nearby = world.getNearbyEntities(loc, radius + 1, 3, radius + 1);
                for (Entity entity : nearby) {
                    if (entity instanceof Player && entity.getLocation().distance(loc) <= radius + 1) {
                        ((Player) entity).damage(attack.getDamage(), boss);
                        Vector knockback = entity.getLocation().toVector().subtract(loc.toVector()).normalize();
                        knockback.setY(0.3);
                        entity.setVelocity(knockback.multiply(1.2));
                    }
                }

                radius += 1.5;
            }
        }.runTaskTimer(main, 0L, 3L);
    }

    private void executeSummonMinions(LivingEntity boss, BossSpecialAttacks.SpecialAttack attack) {
        Location loc = boss.getLocation();
        World world = loc.getWorld();

        world.spawnParticle(Particle.SOUL, loc, 20, 2, 1, 2);
        world.playSound(loc, Sound.ENTITY_EVOKER_PREPARE_SUMMON, 1.0f, 1.0f);

        for (int i = 0; i < 3; i++) {
            Location spawnLoc = loc.clone().add(
                    random.nextDouble() * 4 - 2,
                    0,
                    random.nextDouble() * 4 - 2
            );

            Zombie minion = world.spawn(spawnLoc, Zombie.class);
            minion.setCustomName(ChatColor.GRAY + "Minion");
            minion.setCustomNameVisible(true);
            minion.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).setBaseValue(20);
            minion.setHealth(20);

            world.spawnParticle(Particle.POOF, spawnLoc, 10);
        }
    }

    private void executeSummonWall(LivingEntity boss, BossSpecialAttacks.SpecialAttack attack) {
        Location loc = boss.getLocation();
        World world = loc.getWorld();
        Vector direction = loc.getDirection().normalize();
        Vector perpendicular = new Vector(-direction.getZ(), 0, direction.getX()).normalize();

        world.playSound(loc, Sound.BLOCK_STONE_PLACE, 1.0f, 0.5f);

        ArrayList<Location> wallBlocks = new ArrayList<>();
        for (int x = -3; x <= 3; x++) {
            for (int y = 0; y < 3; y++) {
                Location blockLoc = loc.clone()
                        .add(direction.clone().multiply(3))
                        .add(perpendicular.clone().multiply(x))
                        .add(0, y, 0);

                if (blockLoc.getBlock().getType() == Material.AIR) {
                    blockLoc.getBlock().setType(Material.STONE_BRICKS);
                    wallBlocks.add(blockLoc);
                }
            }
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Location blockLoc : wallBlocks) {
                    blockLoc.getBlock().setType(Material.AIR);
                }
            }
        }.runTaskLater(main, 200L);
    }

    private void executeRegen(LivingEntity boss, BossSpecialAttacks.SpecialAttack attack) {
        boss.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 2));
        boss.getWorld().spawnParticle(Particle.HEART, boss.getLocation().add(0, 2, 0), 10);
        boss.getWorld().playSound(boss.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.5f);
    }

    private void executeEnrage(LivingEntity boss, BossSpecialAttacks.SpecialAttack attack) {
        double healthPercent = boss.getHealth() / boss.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();

        if (healthPercent < 0.3) {
            boss.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 200, 1));
            boss.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1));
            boss.getWorld().spawnParticle(Particle.ANGRY_VILLAGER, boss.getLocation().add(0, 2, 0), 20);
            boss.getWorld().playSound(boss.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1.0f, 0.8f);
        }
    }

    private void executeWeaken(LivingEntity boss, BossSpecialAttacks.SpecialAttack attack) {
        Location loc = boss.getLocation();
        World world = loc.getWorld();

        world.spawnParticle(Particle.WITCH, loc, 30, attack.getRange(), 1, attack.getRange());
        world.playSound(loc, Sound.ENTITY_WITCH_CELEBRATE, 1.0f, 0.7f);

        Collection<Entity> nearby = world.getNearbyEntities(loc, attack.getRange(), attack.getRange(), attack.getRange());
        for (Entity entity : nearby) {
            if (entity instanceof Player) {
                ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 100, 1));
            }
        }
    }

    private void executeBlind(LivingEntity boss, BossSpecialAttacks.SpecialAttack attack) {
        Location loc = boss.getLocation();
        World world = loc.getWorld();

        world.spawnParticle(Particle.SQUID_INK, loc, 50, attack.getRange(), 1, attack.getRange());
        world.playSound(loc, Sound.ENTITY_SQUID_SQUIRT, 1.0f, 1.0f);

        Collection<Entity> nearby = world.getNearbyEntities(loc, attack.getRange(), attack.getRange(), attack.getRange());
        for (Entity entity : nearby) {
            if (entity instanceof Player) {
                ((Player) entity).addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
            }
        }
    }

    private void executePhaseShift(LivingEntity boss, BossSpecialAttacks.SpecialAttack attack) {
        Location loc = boss.getLocation();
        World world = loc.getWorld();

        boss.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 60, 0));
        boss.setInvulnerable(true);

        world.spawnParticle(Particle.PORTAL, loc, 100, 1, 1, 1);
        world.playSound(loc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);

        new BukkitRunnable() {
            int teleports = 0;
            @Override
            public void run() {
                if (teleports++ >= 3 || boss.isDead()) {
                    boss.setInvulnerable(false);
                    cancel();
                    return;
                }

                Location newLoc = loc.clone().add(
                        random.nextDouble() * 20 - 10,
                        0,
                        random.nextDouble() * 20 - 10
                );
                newLoc.setY(world.getHighestBlockYAt(newLoc) + 1);

                world.spawnParticle(Particle.PORTAL, boss.getLocation(), 50);
                boss.teleport(newLoc);
                world.spawnParticle(Particle.PORTAL, newLoc, 50);
                world.playSound(newLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 0.5f);
            }
        }.runTaskTimer(main, 20L, 20L);
    }

    private void executeLifeDrain(LivingEntity boss, BossSpecialAttacks.SpecialAttack attack) {
        Location loc = boss.getLocation();
        World world = loc.getWorld();

        world.spawnParticle(Particle.CRIMSON_SPORE, loc, 50, attack.getRange(), 1, attack.getRange());
        world.playSound(loc, Sound.ENTITY_WITHER_AMBIENT, 1.0f, 1.5f);

        Collection<Entity> nearby = world.getNearbyEntities(loc, attack.getRange(), attack.getRange(), attack.getRange());
        double totalDrained = 0;

        for (Entity entity : nearby) {
            if (entity instanceof Player) {
                Player player = (Player) entity;
                player.damage(attack.getDamage(), boss);
                totalDrained += attack.getDamage();

                world.spawnParticle(Particle.DAMAGE_INDICATOR,
                        player.getLocation().add(0, 1, 0), 5);
            }
        }

        if (totalDrained > 0) {
            double maxHealth = boss.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
            double newHealth = Math.min(boss.getHealth() + totalDrained, maxHealth);
            boss.setHealth(newHealth);
            world.spawnParticle(Particle.HEART, loc.add(0, 2, 0), 10);
        }
    }

    private void executeReflect(LivingEntity boss, BossSpecialAttacks.SpecialAttack attack) {
        boss.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 2));
        boss.getWorld().spawnParticle(Particle.WAX_OFF, boss.getLocation().add(0, 1, 0), 30, 0.5, 1, 0.5);
        boss.getWorld().playSound(boss.getLocation(), Sound.BLOCK_BEACON_POWER_SELECT, 1.0f, 2.0f);

        boss.setMetadata("reflect_active", new org.bukkit.metadata.FixedMetadataValue(main, true));

        new BukkitRunnable() {
            @Override
            public void run() {
                boss.removeMetadata("reflect_active", main);
            }
        }.runTaskLater(main, 100L);
    }

    private Vector rotateVector(Vector v, double degrees) {
        double rad = Math.toRadians(degrees);
        double cos = Math.cos(rad);
        double sin = Math.sin(rad);
        return new Vector(
                v.getX() * cos - v.getZ() * sin,
                v.getY(),
                v.getX() * sin + v.getZ() * cos
        );
    }
}