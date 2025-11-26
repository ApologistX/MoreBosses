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

class BigBoyEntity {
    private ItemStack[] BigBoyArmor = {
            new ItemStack(Material.GOLDEN_BOOTS),
            new ItemStack(Material.GOLDEN_LEGGINGS),
            new ItemStack(Material.GOLDEN_CHESTPLATE),
            new ItemStack(Material.GOLDEN_HELMET)
    };

    public BigBoyEntity(int health, Location local, World world, Main main) {
        Giant bigBoy = (Giant) world.spawnEntity(local, EntityType.GIANT);
        bigBoy.getEquipment().setArmorContents(BigBoyArmor);
        bigBoy.getEquipment().setItemInMainHand(new ItemStack(Material.GOLDEN_SWORD));

        bigBoy.setCustomName(ChatColor.translateAlternateColorCodes('&', "&c&lBig Boy"));
        bigBoy.setCustomNameVisible(true);

        bigBoy.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 99999999, 2));
        bigBoy.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health);
        bigBoy.setHealth(health);
        bigBoy.setRemoveWhenFarAway(main.getConfig().getBoolean("BigBoyDeSpawn"));
    }
}
