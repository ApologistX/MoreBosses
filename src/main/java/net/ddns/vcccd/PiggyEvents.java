package net.ddns.vcccd;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PiggyEvents implements Listener {

    private static final String PIGGY_NAME_RAW = "PiGgY";
    private static final String PIGGY_NAME_COLORED =
            ChatColor.translateAlternateColorCodes('&', "&c&lPiGgY");

    private final List<Player> piggyPlayers = new ArrayList<>();
    private final Main main;

    public PiggyEvents(Main main) {
        this.main = main;
    }

    private ItemStack customItem(Material item, String name) {
        ItemStack result = new ItemStack(item);
        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            result.setItemMeta(meta);
        }
        return result;
    }

    private void dropItemAt(LivingEntity entity, ItemStack item) {
        Location location = entity.getLocation();
        World world = entity.getWorld();
        world.dropItem(location, item);
    }

    private void spawnExperienceOrbs(Location location, int totalOrbs, int expPerOrb) {
        World world = location.getWorld();
        if (world == null) return;

        for (int i = 0; i < totalOrbs; i++) {
            ExperienceOrb orb = world.spawn(location, ExperienceOrb.class);
            orb.setExperience(expPerOrb);
        }
    }

    @EventHandler
    public void onPiggyAttacked(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && !piggyPlayers.contains(player)) {
            piggyPlayers.add(player);
        }
    }

    @EventHandler
    public void onPiggyFallDamage(EntityDamageEvent event) {
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL) return;
        if (!(event.getEntity() instanceof PigZombie piggy)) return;
        if (piggy.getCustomName() == null) return;

        String stripped = ChatColor.stripColor(piggy.getCustomName());
        if (stripped != null && stripped.equalsIgnoreCase(PIGGY_NAME_RAW)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerNearPiggy(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        for (org.bukkit.entity.Entity entity : player.getNearbyEntities(10, 10, 10)) {
            if (!(entity instanceof PigZombie piggy)) continue;

            String name = piggy.getCustomName();
            if (name != null && name.equals(PIGGY_NAME_COLORED)) {
                piggy.setTarget(player);
            }
        }
    }

    @EventHandler
    public void onPiggyDeath(EntityDeathEvent event) {
        if (!(event.getEntity() instanceof PigZombie piggy)) return;

        String name = piggy.getCustomName();
        if (name == null || !name.equals(PIGGY_NAME_COLORED)) return;

        // --- DROPS / REWARDS ---
        event.getDrops().clear();
        spawnExperienceOrbs(piggy.getLocation(), 100, 2);

        if (piggy.getEquipment() != null) {
            ItemStack axe = piggy.getEquipment().getItemInMainHand();
            if (axe != null && axe.getType() != Material.AIR) {
                event.getDrops().add(axe.clone());
            }
        }

        if (main.getConfig().getBoolean("AnnounceBossKill")) {
            for (Player player : main.getServer().getOnlinePlayers()) {
                player.sendMessage(main.getPluginPrefix() + "Piggy has been slain!");
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 0f);
            }
        }
    }
}
