package net.ddns.vcccd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.PiglinBrute;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PiggyEntity {

    private ItemStack PiggySword = new ItemStack(Material.GOLDEN_AXE);
    private List<String> PiggyLore = new ArrayList<String>();

    private ItemStack[] apperal = {
            new ItemStack(Material.NETHERITE_BOOTS),
            new ItemStack(Material.NETHERITE_LEGGINGS),
            new ItemStack(Material.NETHERITE_CHESTPLATE),
            new ItemStack(Material.NETHERITE_HELMET)
    };

    public PiggyEntity(int health, Location location, World world, Main main) {
        // Use PiglinBrute instead of deprecated PigZombie
        PiglinBrute OurPiggy = (PiglinBrute) world.spawnEntity(location, EntityType.PIGLIN_BRUTE);

        OurPiggy.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health);
        OurPiggy.setHealth(health);

        ItemMeta PiggySwordMeta = PiggySword.getItemMeta();
        PiggySwordMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&c&lPiggy&4&lAxe"));
        this.PiggyLore.add(ChatColor.translateAlternateColorCodes('&', "&cThe power of the piggy"));
        PiggySwordMeta.setLore(PiggyLore);
        this.PiggySword.setItemMeta(PiggySwordMeta);

        EntityEquipment equipment = OurPiggy.getEquipment();
        if (equipment != null) {
            equipment.setBoots(apperal[0]);
            equipment.setLeggings(apperal[1]);
            equipment.setChestplate(apperal[2]);
            equipment.setHelmet(apperal[3]);

            equipment.setItemInMainHand(PiggySword);

            equipment.setItemInMainHandDropChance(1.0f); // 100% chance to drop the axe
            equipment.setHelmetDropChance(0.0f); // Don't drop armor
            equipment.setChestplateDropChance(0.0f);
            equipment.setLeggingsDropChance(0.0f);
            equipment.setBootsDropChance(0.0f);
        }

        OurPiggy.setCustomName(ChatColor.translateAlternateColorCodes('&', "&c&lPiGgY"));
        OurPiggy.setCustomNameVisible(true);

        OurPiggy.setRemoveWhenFarAway(main.getConfig().getBoolean("PiggyDeSpawn"));
    }
}
