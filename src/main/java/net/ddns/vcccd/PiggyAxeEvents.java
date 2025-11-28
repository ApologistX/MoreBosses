package net.ddns.vcccd;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PiggyAxeEvents implements Listener {

    private static final String PIGGY_AXE_NAME =
            ChatColor.translateAlternateColorCodes('&', "&c&lPiggy&4&lAxe");
    private static final String PIGGY_NAME =
            ChatColor.translateAlternateColorCodes('&', "&c&lPiGgY");

    private static final long PLAYER_COOLDOWN = 7_000L;
    private static final long PIGGY_COOLDOWN  = 6_500L;

    private static final Set<UUID> EXPLOSION_IMMUNE = new HashSet<>();

    private final JavaPlugin plugin;
    private final Map<UUID, Long> abilityCooldown = new HashMap<>();

    public PiggyAxeEvents(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    // ===== Ability / cooldown logic =====

    private boolean isOnCooldown(UUID id, long cooldownMs) {
        long now = System.currentTimeMillis();
        Long last = abilityCooldown.get(id);
        return last != null && (now - last < cooldownMs);
    }

    private void putOnCooldown(UUID id) {
        abilityCooldown.put(id, System.currentTimeMillis());
    }

    public static boolean isExplosionImmune(UUID id) {
        return EXPLOSION_IMMUNE.contains(id);
    }

    private void grantExplosionImmunity(UUID id) {
        EXPLOSION_IMMUNE.add(id);
        new BukkitRunnable() {
            @Override
            public void run() {
                EXPLOSION_IMMUNE.remove(id);
            }
        }.runTaskLater(plugin, 50L); 
    }

    private boolean isPiggyAxe(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        return meta != null
                && meta.hasDisplayName()
                && PIGGY_AXE_NAME.equals(meta.getDisplayName());
    }

    private void fireballAndKnockback(PigZombie piggy, Player target) {
        Vector dir = target.getLocation().toVector().subtract(piggy.getLocation().toVector());
        dir.setY(0);

        if (dir.lengthSquared() == 0) return;

        dir.normalize();

        Vector knockback = dir.clone().multiply(1.5);
        target.setVelocity(knockback);

        SmallFireball fireball = piggy.launchProjectile(SmallFireball.class, dir);
        fireball.setIsIncendiary(false);
        fireball.setYield(0);
    }

    private void rocketEffect(Entity damager, Entity victim) {
        Location loc = victim.getLocation();

        Firework fw = (Firework) loc.getWorld().spawnEntity(loc, EntityType.FIREWORK_ROCKET);
        FireworkMeta meta = fw.getFireworkMeta();
        meta.setPower(0);
        fw.setFireworkMeta(meta);

        fw.addPassenger(victim);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!fw.isDead()) {
                    fw.detonate();
                }

                victim.getWorld().createExplosion(
                        victim.getLocation(),
                        1F,
                        false,
                        false
                );
            }
        }.runTaskLater(plugin, 6L);
    }

    private void particleEffect(Entity entity) {
        Location location = entity.getLocation();

        for (int i = 0; i < 360; i += 18) {
            double xOffset = 3 * Math.cos(Math.toRadians(i));
            double zOffset = 3 * Math.sin(Math.toRadians(i));

            double x = location.getX() + xOffset;
            double y = location.getY();
            double z = location.getZ() + zOffset;

            entity.getWorld().spawnParticle(Particle.FIREWORK, x, y, z, 1);
            entity.getWorld().spawnParticle(Particle.FLAME, x, y, z, 1);
        }
    }

    // ===== Event handlers =====

    @EventHandler
    public void onExplosionDamage(EntityDamageEvent event) {
        EntityDamageEvent.DamageCause cause = event.getCause();
        if (cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION ||
                cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {

            UUID victimId = event.getEntity().getUniqueId();
            if (isExplosionImmune(victimId)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPiggyAxeAttack(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity victim  = event.getEntity();

        // Player using Piggy Axe
        if (damager instanceof Player player) {

            ItemStack hand = player.getInventory().getItemInMainHand();
            if (!isPiggyAxe(hand)) return;

            UUID id = player.getUniqueId();
            if (isOnCooldown(id, PLAYER_COOLDOWN)) return;

            putOnCooldown(id);
            particleEffect(player);
            rocketEffect(player, victim);
            return;
        }

        // PigZombie Piggy using Piggy Axe
        if (damager instanceof PigZombie piggy) {

            String customName = piggy.getCustomName();
            if (customName == null || !PIGGY_NAME.equals(customName)) return;

            EntityEquipment eq = piggy.getEquipment();
            if (eq == null) return;

            ItemStack hand = eq.getItemInMainHand();
            if (!isPiggyAxe(hand)) return;

            UUID id = piggy.getUniqueId();
            if (isOnCooldown(id, PIGGY_COOLDOWN)) return;

            putOnCooldown(id);
            grantExplosionImmunity(id);
            particleEffect(piggy);
            rocketEffect(piggy, victim);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (!(victim instanceof Player target)) return;
                    if (piggy.isDead() || !piggy.isValid()) return;

                    fireballAndKnockback(piggy, target);
                }
            }.runTaskLater(plugin, 20L);
        }
    }
}
