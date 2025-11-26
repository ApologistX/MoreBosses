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

class GortEntity {
    private ItemStack[] gortArmor = {
            new ItemStack(Material.IRON_BOOTS),
            new ItemStack(Material.IRON_LEGGINGS),
            new ItemStack(Material.IRON_CHESTPLATE),
            new ItemStack(Material.IRON_HELMET)
    };

    public GortEntity(int health, Location local, World world, Main main) {
        Vindicator gort = (Vindicator) world.spawnEntity(local, EntityType.VINDICATOR);
        gort.getEquipment().setArmorContents(gortArmor);
        gort.getEquipment().setItemInMainHand(new ItemStack(Material.IRON_HOE));

        gort.setCustomName(ChatColor.translateAlternateColorCodes('&', "&eGort The Serf"));
        gort.setCustomNameVisible(true);

        gort.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 99999999, 2));
        gort.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health);
        gort.setHealth(health);
        gort.setRemoveWhenFarAway(main.getConfig().getBoolean("GortDeSpawn"));
    }
}
