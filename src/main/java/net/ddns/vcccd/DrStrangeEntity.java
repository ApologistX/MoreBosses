package net.ddns.vcccd;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;


class DrStrangeEntity {
    public DrStrangeEntity(int health, Location local, World world, Main main) {
        Enderman drStrange = (Enderman) world.spawnEntity(local, EntityType.ENDERMAN);

        drStrange.setCustomName(ChatColor.DARK_PURPLE + "Dr. Strange");
        drStrange.setCustomNameVisible(true);

        drStrange.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 99999999, 2));
        drStrange.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 99999999, 1));
        drStrange.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health);
        drStrange.setHealth(health);
        drStrange.setRemoveWhenFarAway(main.getConfig().getBoolean("DrStrangeDeSpawn"));
    }
}
