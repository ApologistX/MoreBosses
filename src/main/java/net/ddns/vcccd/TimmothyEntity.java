package net.ddns.vcccd;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

class TimmothyEntity {
    private ItemStack[] timmothyArmor = {
            new ItemStack(Material.DIAMOND_BOOTS),
            new ItemStack(Material.DIAMOND_LEGGINGS),
            new ItemStack(Material.DIAMOND_CHESTPLATE),
            new ItemStack(Material.DIAMOND_HELMET)
    };

    public TimmothyEntity(int health, Location local, World world, Main main) {
        Skeleton timmothy = (Skeleton) world.spawnEntity(local, EntityType.SKELETON);
        timmothy.getEquipment().setArmorContents(timmothyArmor);

        // Give Timmothy the BomBow so he uses it
        ItemStack bomBow = new ItemStack(Material.BOW);
        ItemMeta bowMeta = bomBow.getItemMeta();
        bowMeta.setDisplayName(ChatColor.RED + "BomBow");
        bomBow.setItemMeta(bowMeta);
        timmothy.getEquipment().setItemInMainHand(bomBow);

        timmothy.setCustomName(ChatColor.AQUA + "Timmothy");
        timmothy.setCustomNameVisible(true);

        timmothy.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 99999999, 2));
        timmothy.getAttribute(Attribute.MAX_HEALTH).setBaseValue(health);
        timmothy.setHealth(health);
        timmothy.setRemoveWhenFarAway(main.getConfig().getBoolean("TimmothyDeSpawn"));
    }
}