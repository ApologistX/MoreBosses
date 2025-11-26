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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomBossCreatorGUI {

    private final Main main;
    private static Map<UUID, CustomBossBuilder> activeBuildSessions = new HashMap<>();
    private static Map<UUID, String> editingSessions = new HashMap<>(); // Track which boss is being edited

    public CustomBossCreatorGUI(Main main) {
        this.main = main;
    }

    public void openCreatorGUI(Player player) {
        // Initialize or retrieve build session
        if (!activeBuildSessions.containsKey(player.getUniqueId())) {
            activeBuildSessions.put(player.getUniqueId(), new CustomBossBuilder());
        }

        Inventory creatorMenu = Bukkit.createInventory(null, 54, "Custom Boss Creator");

        // Basic Settings (Top Row)
        creatorMenu.setItem(0, createMenuItem(Material.NAME_TAG, ChatColor.GOLD + "Set Boss Name",
                "Click to name your boss"));
        creatorMenu.setItem(1, createMenuItem(Material.ZOMBIE_HEAD, ChatColor.GOLD + "Choose Mob Type",
                "Select the entity type"));
        creatorMenu.setItem(2, createMenuItem(Material.EXPERIENCE_BOTTLE, ChatColor.GOLD + "Set Size",
                "Adjust mob size (slimes/magma cubes)"));
        creatorMenu.setItem(3, createMenuItem(Material.DIAMOND_SWORD, ChatColor.GOLD + "Set Aggression",
                "Configure mob behavior"));
        creatorMenu.setItem(4, createMenuItem(Material.HEART_OF_THE_SEA, ChatColor.GOLD + "Set Health",
                "Configure boss health"));

        // Visual Effects (Second Row)
        creatorMenu.setItem(9, createMenuItem(Material.BLAZE_POWDER, ChatColor.AQUA + "Particle Effects",
                "Add persistent particles"));
        creatorMenu.setItem(10, createMenuItem(Material.ENDER_PEARL, ChatColor.AQUA + "Custom Model Data",
                "Set custom model (WIP)"));

        // Equipment (Third Row)
        creatorMenu.setItem(18, createMenuItem(Material.DIAMOND_HELMET, ChatColor.GREEN + "Helmet Slot",
                "Set helmet equipment"));
        creatorMenu.setItem(19, createMenuItem(Material.DIAMOND_CHESTPLATE, ChatColor.GREEN + "Chestplate Slot",
                "Set chestplate equipment"));
        creatorMenu.setItem(20, createMenuItem(Material.DIAMOND_LEGGINGS, ChatColor.GREEN + "Leggings Slot",
                "Set leggings equipment"));
        creatorMenu.setItem(21, createMenuItem(Material.DIAMOND_BOOTS, ChatColor.GREEN + "Boots Slot",
                "Set boots equipment"));
        creatorMenu.setItem(22, createMenuItem(Material.DIAMOND_SWORD, ChatColor.GREEN + "Main Hand",
                "Set main hand item"));
        creatorMenu.setItem(23, createMenuItem(Material.SHIELD, ChatColor.GREEN + "Off Hand",
                "Set off hand item"));

        // Abilities & Minions (Fourth Row)
        creatorMenu.setItem(27, createMenuItem(Material.ENCHANTED_BOOK, ChatColor.LIGHT_PURPLE + "Special Attacks",
                "Configure boss abilities (WIP)"));
        creatorMenu.setItem(28, createMenuItem(Material.SPAWNER, ChatColor.LIGHT_PURPLE + "Minion Spawning",
                "Set minion config (WIP)"));
        creatorMenu.setItem(29, createMenuItem(Material.POTION, ChatColor.LIGHT_PURPLE + "Potion Effects",
                "Add status effects (WIP)"));

        // World Settings (Fifth Row)
        creatorMenu.setItem(36, createMenuItem(Material.GRASS_BLOCK, ChatColor.YELLOW + "Natural Spawning",
                "Enable world spawn settings"));
        creatorMenu.setItem(37, createMenuItem(Material.BARRIER, ChatColor.YELLOW + "WorldGuard Region",
                "Set region restrictions (WIP)"));
        creatorMenu.setItem(38, createMenuItem(Material.WOODEN_AXE, ChatColor.YELLOW + "WorldEdit Structure",
                "Link structure spawn (WIP)"));

        // Loot Configuration (Bottom Left)
        creatorMenu.setItem(45, createMenuItem(Material.CHEST, ChatColor.GOLD + "Death Drops",
                "Configure drop table (WIP)"));

        // Save/Cancel (Bottom Right)
        creatorMenu.setItem(49, createMenuItem(Material.EMERALD_BLOCK, ChatColor.GREEN + "" + ChatColor.BOLD + "SAVE BOSS",
                "Save this custom boss"));
        creatorMenu.setItem(53, createMenuItem(Material.REDSTONE_BLOCK, ChatColor.RED + "" + ChatColor.BOLD + "CANCEL",
                "Discard changes"));

        // Fill empty slots with gray panes
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);

        for (int i = 0; i < 54; i++) {
            if (creatorMenu.getItem(i) == null) {
                creatorMenu.setItem(i, filler);
            }
        }

        player.openInventory(creatorMenu);
    }

    private ItemStack createMenuItem(Material material, String name, String... lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);

        if (lore.length > 0) {
            ArrayList<String> loreList = new ArrayList<>();
            for (String line : lore) {
                loreList.add(ChatColor.GRAY + line);
            }
            meta.setLore(loreList);
        }

        item.setItemMeta(meta);
        return item;
    }

    public static CustomBossBuilder getBuildSession(UUID playerUUID) {
        return activeBuildSessions.get(playerUUID);
    }

    public static void removeBuildSession(UUID playerUUID) {
        activeBuildSessions.remove(playerUUID);
        editingSessions.remove(playerUUID); // Also clear edit session
    }

    /**
     * Set a build session for editing an existing boss
     */
    public static void setEditSession(UUID playerUUID, CustomBossBuilder builder, String originalBossName) {
        activeBuildSessions.put(playerUUID, builder);
        editingSessions.put(playerUUID, originalBossName);
    }

    /**
     * Get the name of the boss being edited (null if creating new)
     */
    public static String getEditingBossName(UUID playerUUID) {
        return editingSessions.get(playerUUID);
    }

    /**
     * Clear edit session tracking
     */
    public static void clearEditSession(UUID playerUUID) {
        editingSessions.remove(playerUUID);
    }

    // Inner class to track boss creation progress
    public static class CustomBossBuilder {
        private String bossName = "Unnamed Boss";
        private EntityType entityType = EntityType.ZOMBIE;
        private int size = 1; // For slimes/magma cubes
        private AggressionType aggressionType = AggressionType.AGGRESSIVE;
        private double health = 100.0;
        private Particle particleEffect = null;
        private int customModelData = 0;

        // Equipment
        private ItemStack helmet = null;
        private ItemStack chestplate = null;
        private ItemStack leggings = null;
        private ItemStack boots = null;
        private ItemStack mainHand = null;
        private ItemStack offHand = null;

        // Abilities
        private ArrayList<String> specialAttacks = new ArrayList<>();
        private ArrayList<MinionConfig> minions = new ArrayList<>();
        private ArrayList<String> potionEffects = new ArrayList<>();

        // World Settings
        private boolean naturalSpawning = false;
        private int spawnRarity = 1000; // 1 in X chance
        private ArrayList<String> spawnWorlds = new ArrayList<>();
        private String worldGuardRegion = null; // WIP
        private String worldEditStructure = null; // WIP

        // Loot
        private ArrayList<ItemStack> deathDrops = new ArrayList<>();

        // Getters and Setters
        public String getBossName() { return bossName; }
        public void setBossName(String name) { this.bossName = name; }

        public EntityType getEntityType() { return entityType; }
        public void setEntityType(EntityType type) { this.entityType = type; }

        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }

        public AggressionType getAggressionType() { return aggressionType; }
        public void setAggressionType(AggressionType type) { this.aggressionType = type; }

        // Keep backward compatibility for spawning logic
        public boolean isAggressive() { return aggressionType.isAggressive(); }
        public boolean hasAI() { return aggressionType.hasAI(); }

        public double getHealth() { return health; }
        public void setHealth(double health) { this.health = health; }

        public Particle getParticleEffect() { return particleEffect; }
        public void setParticleEffect(Particle particle) { this.particleEffect = particle; }

        public int getCustomModelData() { return customModelData; }
        public void setCustomModelData(int data) { this.customModelData = data; }

        public ItemStack getHelmet() { return helmet; }
        public void setHelmet(ItemStack helmet) { this.helmet = helmet; }

        public ItemStack getChestplate() { return chestplate; }
        public void setChestplate(ItemStack chestplate) { this.chestplate = chestplate; }

        public ItemStack getLeggings() { return leggings; }
        public void setLeggings(ItemStack leggings) { this.leggings = leggings; }

        public ItemStack getBoots() { return boots; }
        public void setBoots(ItemStack boots) { this.boots = boots; }

        public ItemStack getMainHand() { return mainHand; }
        public void setMainHand(ItemStack mainHand) { this.mainHand = mainHand; }

        public ItemStack getOffHand() { return offHand; }
        public void setOffHand(ItemStack offHand) { this.offHand = offHand; }

        public ArrayList<String> getSpecialAttacks() { return specialAttacks; }
        public void addSpecialAttack(String attack) { this.specialAttacks.add(attack); }
        public void removeSpecialAttack(String attack) { this.specialAttacks.remove(attack); }

        public ArrayList<MinionConfig> getMinions() { return minions; }
        public void addMinion(MinionConfig minion) { this.minions.add(minion); }

        public ArrayList<String> getPotionEffects() { return potionEffects; }
        public void addPotionEffect(String effect) { this.potionEffects.add(effect); }

        public boolean isNaturalSpawning() { return naturalSpawning; }
        public void setNaturalSpawning(boolean spawning) { this.naturalSpawning = spawning; }

        public int getSpawnRarity() { return spawnRarity; }
        public void setSpawnRarity(int rarity) { this.spawnRarity = rarity; }

        public ArrayList<String> getSpawnWorlds() { return spawnWorlds; }
        public void addSpawnWorld(String world) { this.spawnWorlds.add(world); }

        public String getWorldGuardRegion() { return worldGuardRegion; }
        public void setWorldGuardRegion(String region) { this.worldGuardRegion = region; }

        public String getWorldEditStructure() { return worldEditStructure; }
        public void setWorldEditStructure(String structure) { this.worldEditStructure = structure; }

        public ArrayList<ItemStack> getDeathDrops() { return deathDrops; }
        public void addDeathDrop(ItemStack item) { this.deathDrops.add(item); }
    }

    // Minion configuration helper class
    public static class MinionConfig {
        private EntityType minionType;
        private int spawnAmount;
        private int spawnInterval; // In ticks
        private double healthPercent; // Boss health % to trigger spawn

        public MinionConfig(EntityType type, int amount, int interval, double healthPercent) {
            this.minionType = type;
            this.spawnAmount = amount;
            this.spawnInterval = interval;
            this.healthPercent = healthPercent;
        }

        public EntityType getMinionType() { return minionType; }
        public int getSpawnAmount() { return spawnAmount; }
        public int getSpawnInterval() { return spawnInterval; }
        public double getHealthPercent() { return healthPercent; }
    }
}