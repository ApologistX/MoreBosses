package net.ddns.vcccd;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Slime;

class AlbertEntity {
    public AlbertEntity(int size, Location local, World world) {
        Slime albert = (Slime) world.spawnEntity(local, EntityType.SLIME);
        albert.setSize(size);
        albert.setCustomName(ChatColor.YELLOW + "Albert");
        albert.setCustomNameVisible(true);
        albert.setAI(true);
    }
}
