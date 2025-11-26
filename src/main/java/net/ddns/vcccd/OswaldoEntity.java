package net.ddns.vcccd;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

class OswaldoEntity {
    private ItemStack[] oswaldoArmor = {
            new ItemStack(Material.NETHERITE_BOOTS),
            new ItemStack(Material.NETHERITE_LEGGINGS),
            new ItemStack(Material.NETHERITE_CHESTPLATE),
            new ItemStack(Material.NETHERITE_HELMET)
    };

    public OswaldoEntity(int health, Location local, World world, Main main) {
        Zombie oswaldo = (Zombie) world.spawnEntity(local, EntityType.ZOMBIE);
        oswaldo.getEquipment().setArmorContents(oswaldoArmor);
        oswaldo.getEquipment().setItemInMainHand(new ItemStack(Material.NETHERITE_SWORD));

        oswaldo.setCustomName(ChatColor.translateAlternateColorCodes('&', "&c&lOswaldo"));
        oswaldo.setCustomNameVisible(true);

        oswaldo.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 99999999, 2));
        oswaldo.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health);
        oswaldo.setHealth(health);
        oswaldo.setRemoveWhenFarAway(main.getConfig().getBoolean("OswaldoDeSpawn"));
        oswaldo.setAdult();
    }
}