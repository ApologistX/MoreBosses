package net.ddns.vcccd;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PigZombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PiggyEntity {

    private static final String PIGGY_NAME =
            ChatColor.translateAlternateColorCodes('&', "&c&lPiGgY");
    private static final String PIGGY_AXE_NAME =
            ChatColor.translateAlternateColorCodes('&', "&c&lPiggy&4&lAxe");
    private static final String PIGGY_AXE_LORE =
            ChatColor.translateAlternateColorCodes('&', "&cThe power of the piggy");

    public PiggyEntity(int health, Location location, World world, Main main) {

        // === Spawn main Piggy entity
        PigZombie piggy = (PigZombie) world.spawnEntity(location, EntityType.ZOMBIFIED_PIGLIN);

        piggy.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health);
        piggy.setHealth(health);

        piggy.setAnger(Integer.MAX_VALUE);
        piggy.setAngry(true);
        piggy.setTarget(null);

        piggy.setBaby(false);
        piggy.setAdult();
        piggy.setAgeLock(true);

        piggy.setCustomName(PIGGY_NAME);
        piggy.setCustomNameVisible(true);
        piggy.setRemoveWhenFarAway(main.getConfig().getBoolean("PiggyDeSpawn"));

        // Remove stray baby piglins spawned with him
        world.getNearbyEntities(location, 1.5, 1.5, 1.5).forEach(entity -> {
            if (entity instanceof PigZombie other && other != piggy && other.isBaby()) {
                other.remove();
            }
        });

        // === Equipment ===
        equipPiggy(piggy);
    }

    private void equipPiggy(PigZombie piggy) {
        EntityEquipment equipment = piggy.getEquipment();
        if (equipment == null) return;

        ItemStack boots = new ItemStack(Material.DIAMOND_BOOTS);
        ItemStack leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
        ItemStack chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
        ItemStack axe = createPiggyAxe();

        equipment.setBoots(boots);
        equipment.setLeggings(leggings);
        equipment.setChestplate(chestplate);
        equipment.setItemInMainHand(axe);

        equipment.setItemInMainHandDropChance(0f);
        equipment.setBootsDropChance(0f);
        equipment.setLeggingsDropChance(0f);
        equipment.setChestplateDropChance(0f);
    }

    private ItemStack createPiggyAxe() {
        ItemStack axe = new ItemStack(Material.GOLDEN_AXE);
        ItemMeta meta = axe.getItemMeta();
        if (meta == null) return axe;

        meta.setDisplayName(PIGGY_AXE_NAME);

        List<String> lore = new ArrayList<>();
        lore.add(PIGGY_AXE_LORE);
        meta.setLore(lore);

        if (meta instanceof Damageable dmg) dmg.setDamage(0);
        meta.setUnbreakable(true);

        axe.setItemMeta(meta);
        return axe;
    }
}
