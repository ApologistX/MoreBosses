package net.ddns.vcccd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * GUI for selecting mob type
 */
class MobTypeSelectionGUI {
    private final Main main;

    public MobTypeSelectionGUI(Main main) {
        this.main = main;
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, "Select Mob Type");

        // Hostile mobs
        addMobOption(gui, 0, EntityType.ZOMBIE, "Zombie");
        addMobOption(gui, 1, EntityType.SKELETON, "Skeleton");
        addMobOption(gui, 2, EntityType.CREEPER, "Creeper");
        addMobOption(gui, 3, EntityType.SPIDER, "Spider");
        addMobOption(gui, 4, EntityType.ENDERMAN, "Enderman");
        addMobOption(gui, 5, EntityType.BLAZE, "Blaze");
        addMobOption(gui, 6, EntityType.WITHER_SKELETON, "Wither Skeleton");
        addMobOption(gui, 7, EntityType.PIGLIN_BRUTE, "Piglin Brute");
        addMobOption(gui, 8, EntityType.HOGLIN, "Hoglin");

        addMobOption(gui, 9, EntityType.ZOMBIE_VILLAGER, "Zombie Villager");
        addMobOption(gui, 10, EntityType.HUSK, "Husk");
        addMobOption(gui, 11, EntityType.DROWNED, "Drowned");
        addMobOption(gui, 12, EntityType.STRAY, "Stray");
        addMobOption(gui, 13, EntityType.PHANTOM, "Phantom");
        addMobOption(gui, 14, EntityType.VINDICATOR, "Vindicator");
        addMobOption(gui, 15, EntityType.EVOKER, "Evoker");
        addMobOption(gui, 16, EntityType.PILLAGER, "Pillager");
        addMobOption(gui, 17, EntityType.RAVAGER, "Ravager");

        addMobOption(gui, 18, EntityType.SLIME, "Slime");
        addMobOption(gui, 19, EntityType.MAGMA_CUBE, "Magma Cube");
        addMobOption(gui, 20, EntityType.GHAST, "Ghast");
        addMobOption(gui, 21, EntityType.SHULKER, "Shulker");
        addMobOption(gui, 22, EntityType.GUARDIAN, "Guardian");
        addMobOption(gui, 23, EntityType.ELDER_GUARDIAN, "Elder Guardian");
        addMobOption(gui, 24, EntityType.WITCH, "Witch");
        addMobOption(gui, 25, EntityType.VEX, "Vex");

        // Passive/Neutral mobs
        addMobOption(gui, 27, EntityType.IRON_GOLEM, "Iron Golem");
        addMobOption(gui, 28, EntityType.WOLF, "Wolf");
        addMobOption(gui, 29, EntityType.POLAR_BEAR, "Polar Bear");
        addMobOption(gui, 30, EntityType.PIGLIN, "Piglin");
        addMobOption(gui, 31, EntityType.ZOMBIFIED_PIGLIN, "Zombified Piglin");

        // Back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.YELLOW + "Back to Creator");
        back.setItemMeta(backMeta);
        gui.setItem(49, back);

        player.openInventory(gui);
    }

    private void addMobOption(Inventory gui, int slot, EntityType type, String name) {
        ItemStack item = new ItemStack(Material.SPAWNER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN + name);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Click to select " + name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(slot, item);
    }
}

/**
 * GUI for selecting particle effects
 */
class ParticleSelectionGUI {
    private final Main main;

    public ParticleSelectionGUI(Main main) {
        this.main = main;
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, "Select Particle Effect");

        addParticleOption(gui, 0, Particle.FLAME, Material.BLAZE_POWDER, "Flame");
        addParticleOption(gui, 1, Particle.SMOKE, Material.COBWEB, "Smoke");
        addParticleOption(gui, 2, Particle.HEART, Material.RED_DYE, "Hearts");
        addParticleOption(gui, 3, Particle.CRIT, Material.IRON_SWORD, "Critical");
        addParticleOption(gui, 4, Particle.ENCHANT, Material.ENCHANTED_BOOK, "Enchant");
        addParticleOption(gui, 5, Particle.PORTAL, Material.OBSIDIAN, "Portal");
        addParticleOption(gui, 6, Particle.WITCH, Material.POTION, "Witch Spell");
        addParticleOption(gui, 7, Particle.DUST, Material.REDSTONE, "Redstone");
        addParticleOption(gui, 8, Particle.END_ROD, Material.END_ROD, "End Rod");

        addParticleOption(gui, 9, Particle.DRIPPING_LAVA, Material.LAVA_BUCKET, "Dripping Lava");
        addParticleOption(gui, 10, Particle.DRIPPING_WATER, Material.WATER_BUCKET, "Dripping Water");
        addParticleOption(gui, 11, Particle.CLOUD, Material.WHITE_WOOL, "Cloud");
        addParticleOption(gui, 12, Particle.EXPLOSION, Material.TNT, "Explosion");
        addParticleOption(gui, 13, Particle.SNOWFLAKE, Material.SNOWBALL, "Snowflake");
        addParticleOption(gui, 14, Particle.SOUL, Material.SOUL_SAND, "Soul");
        addParticleOption(gui, 15, Particle.SOUL_FIRE_FLAME, Material.SOUL_TORCH, "Soul Fire");
        addParticleOption(gui, 16, Particle.ELECTRIC_SPARK, Material.LIGHTNING_ROD, "Electric Spark");
        addParticleOption(gui, 17, Particle.GLOW, Material.GLOW_INK_SAC, "Glow");

        // No particle option
        ItemStack none = new ItemStack(Material.BARRIER);
        ItemMeta noneMeta = none.getItemMeta();
        noneMeta.setDisplayName(ChatColor.RED + "No Particles");
        ArrayList<String> noneLore = new ArrayList<>();
        noneLore.add(ChatColor.GRAY + "Remove particle effects");
        noneMeta.setLore(noneLore);
        none.setItemMeta(noneMeta);
        gui.setItem(45, none);

        // Back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.YELLOW + "Back to Creator");
        back.setItemMeta(backMeta);
        gui.setItem(49, back);

        player.openInventory(gui);
    }

    private void addParticleOption(Inventory gui, int slot, Particle particle, Material icon, String name) {
        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + name);
        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Particle: " + particle.name());
        meta.setLore(lore);
        item.setItemMeta(meta);
        gui.setItem(slot, item);
    }
}

/**
 * GUI for selecting special attacks
 */
class SpecialAttacksSelectionGUI {
    private final Main main;

    public SpecialAttacksSelectionGUI(Main main) {
        this.main = main;
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 54, "Select Special Attacks");

        int slot = 0;

        // Add preset attacks from BossSpecialAttacks
        for (String attackId : BossSpecialAttacks.getAllAttacks().keySet()) {
            ItemStack attackItem = BossSpecialAttacks.createAttackMenuItem(attackId);
            if (attackItem != null && slot < 45) {
                gui.setItem(slot, attackItem);
                slot++;
            }
        }

        // Add custom attacks from CustomAttackManager
        for (String attackId : CustomAttackManager.getCustomAttacks().keySet()) {
            CustomAttackManager.CustomAttack customAttack = CustomAttackManager.getCustomAttack(attackId);
            ItemStack attackItem = CustomAttackManager.createAttackItem(customAttack);
            if (attackItem != null && slot < 45) {
                gui.setItem(slot, attackItem);
                slot++;
            }
        }


        // Back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.YELLOW + "Back to Creator");
        back.setItemMeta(backMeta);
        gui.setItem(49, back);

        player.openInventory(gui);
    }
}

/**
 * GUI for natural spawn settings
 */
class NaturalSpawnSettingsGUI {
    private final Main main;

    public NaturalSpawnSettingsGUI(Main main) {
        this.main = main;
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "Natural Spawn Settings");

        CustomBossCreatorGUI.CustomBossBuilder builder = CustomBossCreatorGUI.getBuildSession(player.getUniqueId());
        if (builder == null) {
            player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Error: No active build session.");
            return;
        }

        // Toggle natural spawning
        ItemStack toggle = new ItemStack(builder.isNaturalSpawning() ? Material.LIME_DYE : Material.GRAY_DYE);
        ItemMeta toggleMeta = toggle.getItemMeta();
        toggleMeta.setDisplayName(ChatColor.GOLD + "Natural Spawning: " +
                (builder.isNaturalSpawning() ? ChatColor.GREEN + "ENABLED" : ChatColor.RED + "DISABLED"));
        ArrayList<String> toggleLore = new ArrayList<>();
        toggleLore.add(ChatColor.GRAY + "Click to toggle");
        toggleMeta.setLore(toggleLore);
        toggle.setItemMeta(toggleMeta);
        gui.setItem(10, toggle);

        // Spawn rarity
        ItemStack rarity = new ItemStack(Material.CLOCK);
        ItemMeta rarityMeta = rarity.getItemMeta();
        rarityMeta.setDisplayName(ChatColor.GOLD + "Spawn Rarity");
        ArrayList<String> rarityLore = new ArrayList<>();
        rarityLore.add(ChatColor.GRAY + "Current: 1 in " + builder.getSpawnRarity());
        rarityLore.add(ChatColor.YELLOW + "Click to adjust");
        rarityMeta.setLore(rarityLore);
        rarity.setItemMeta(rarityMeta);
        gui.setItem(12, rarity);

        // World selection
        ItemStack worlds = new ItemStack(Material.GRASS_BLOCK);
        ItemMeta worldsMeta = worlds.getItemMeta();
        worldsMeta.setDisplayName(ChatColor.GOLD + "Spawn Worlds");
        ArrayList<String> worldsLore = new ArrayList<>();
        worldsLore.add(ChatColor.GRAY + "Configure which worlds");
        worldsLore.add(ChatColor.GRAY + "this boss can spawn in");
        worldsMeta.setLore(worldsLore);
        worlds.setItemMeta(worldsMeta);
        gui.setItem(14, worlds);

        // Back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.YELLOW + "Back to Creator");
        back.setItemMeta(backMeta);
        gui.setItem(22, back);

        player.openInventory(gui);
    }
}