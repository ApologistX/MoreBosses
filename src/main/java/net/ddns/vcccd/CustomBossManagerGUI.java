package net.ddns.vcccd;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomBossManagerGUI {

    private final Main main;
    private static File customBossesFolder;

    public CustomBossManagerGUI(Main main) {
        this.main = main;
        initializeBossStorage();
    }

    /**
     * Initialize the Custom Bosses folder structure
     */
    private void initializeBossStorage() {
        if (customBossesFolder == null) {
            customBossesFolder = new File(main.getDataFolder(), "Custom Bosses");
            if (!customBossesFolder.exists()) {
                if (customBossesFolder.mkdirs()) {
                    main.getConsole().sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Created Custom Bosses folder!");
                } else {
                    main.getConsole().sendMessage(main.getPluginPrefix() + ChatColor.RED + "Failed to create Custom Bosses folder!");
                }
            }
        }
    }

    /**
     * Open the main manager GUI showing all saved bosses
     */
    public void openManagerGUI(Player player) {
        File[] bossFiles = customBossesFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        int bossCount = (bossFiles != null) ? bossFiles.length : 0;

        // Calculate inventory size (minimum 27, maximum 54)
        int invSize = Math.min(54, Math.max(27, ((bossCount / 9) + 1) * 9));

        Inventory managerMenu = Bukkit.createInventory(null, invSize, "Manage Custom Bosses");

        // Add all saved bosses
        int slot = 0;
        if (bossFiles != null) {
            for (File bossFile : bossFiles) {
                if (slot >= 45) break; // Leave room for buttons at bottom

                FileConfiguration bossConfig = YamlConfiguration.loadConfiguration(bossFile);
                ItemStack bossItem = createBossMenuItem(bossConfig, bossFile.getName().replace(".yml", ""));
                managerMenu.setItem(slot, bossItem);
                slot++;
            }
        }

        // Add create new boss button
        ItemStack createButton = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta createMeta = createButton.getItemMeta();
        createMeta.setDisplayName(ChatColor.GREEN + "Create New Boss");
        ArrayList<String> createLore = new ArrayList<>();
        createLore.add(ChatColor.GRAY + "Click to open the boss creator");
        createMeta.setLore(createLore);
        createButton.setItemMeta(createMeta);
        managerMenu.setItem(invSize - 9, createButton);

        // Add back button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(ChatColor.YELLOW + "Back");
        ArrayList<String> backLore = new ArrayList<>();
        backLore.add(ChatColor.GRAY + "Return to boss menu");
        backMeta.setLore(backLore);
        backButton.setItemMeta(backMeta);
        managerMenu.setItem(invSize - 1, backButton);

        // Fill empty slots with glass panes
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);

        for (int i = 0; i < invSize; i++) {
            if (managerMenu.getItem(i) == null) {
                managerMenu.setItem(i, filler);
            }
        }

        player.openInventory(managerMenu);
    }

    /**
     * Open the edit menu for a specific boss
     */
    public void openEditGUI(Player player, String bossName) {
        Inventory editMenu = Bukkit.createInventory(null, 27, "Edit Boss: " + bossName);

        // Spawn boss button
        ItemStack spawnButton = new ItemStack(Material.SPAWNER);
        ItemMeta spawnMeta = spawnButton.getItemMeta();
        spawnMeta.setDisplayName(ChatColor.GREEN + "Spawn Boss");
        ArrayList<String> spawnLore = new ArrayList<>();
        spawnLore.add(ChatColor.GRAY + "Spawn this boss at your location");
        spawnMeta.setLore(spawnLore);
        spawnButton.setItemMeta(spawnMeta);
        editMenu.setItem(10, spawnButton);

        // Edit settings button
        ItemStack editButton = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta editMeta = editButton.getItemMeta();
        editMeta.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "EDIT SETTINGS");
        ArrayList<String> editLore = new ArrayList<>();
        editLore.add(ChatColor.GRAY + "Modify this boss's configuration");
        editMeta.setLore(editLore);
        editButton.setItemMeta(editMeta);
        editMenu.setItem(12, editButton);

        // Delete button
        ItemStack deleteButton = new ItemStack(Material.BARRIER);
        ItemMeta deleteMeta = deleteButton.getItemMeta();
        deleteMeta.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "DELETE BOSS");
        ArrayList<String> deleteLore = new ArrayList<>();
        deleteLore.add(ChatColor.RED + "Permanently delete this boss");
        deleteLore.add(ChatColor.DARK_RED + "" + ChatColor.BOLD + "WARNING: This cannot be undone!");
        deleteMeta.setLore(deleteLore);
        deleteButton.setItemMeta(deleteMeta);
        editMenu.setItem(14, deleteButton);

        // Back button
        ItemStack backButton = new ItemStack(Material.ARROW);
        ItemMeta backMeta = backButton.getItemMeta();
        backMeta.setDisplayName(ChatColor.YELLOW + "Back to Manager");
        backButton.setItemMeta(backMeta);
        editMenu.setItem(22, backButton);

        // Fill empty slots
        ItemStack filler = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta fillerMeta = filler.getItemMeta();
        fillerMeta.setDisplayName(" ");
        filler.setItemMeta(fillerMeta);

        for (int i = 0; i < 27; i++) {
            if (editMenu.getItem(i) == null) {
                editMenu.setItem(i, filler);
            }
        }

        player.openInventory(editMenu);
    }

    /**
     * Create a menu item representing a boss
     */
    private ItemStack createBossMenuItem(FileConfiguration bossConfig, String fileName) {
        String displayName = bossConfig.getString("name", "Unknown Boss");
        EntityType type = EntityType.valueOf(bossConfig.getString("type", "ZOMBIE"));
        double health = bossConfig.getDouble("health", 100.0);
        List<String> specialAttacks = bossConfig.getStringList("specialAttacks");

        // Use appropriate icon based on mob type
        Material icon = getIconForEntityType(type);

        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);

        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Type: " + ChatColor.WHITE + type.name());
        lore.add(ChatColor.GRAY + "Health: " + ChatColor.WHITE + health);
        lore.add(ChatColor.GRAY + "Special Attacks: " + ChatColor.WHITE + specialAttacks.size());
        lore.add("");
        lore.add(ChatColor.YELLOW + "Left Click: " + ChatColor.WHITE + "Spawn");
        lore.add(ChatColor.YELLOW + "Right Click: " + ChatColor.WHITE + "Edit");

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Get an appropriate icon for an entity type
     */
    private Material getIconForEntityType(EntityType type) {
        switch (type) {
            case ZOMBIE:
            case ZOMBIE_VILLAGER:
            case HUSK:
            case DROWNED:
                return Material.ZOMBIE_HEAD;
            case SKELETON:
            case STRAY:
            case WITHER_SKELETON:
                return Material.SKELETON_SKULL;
            case CREEPER:
                return Material.CREEPER_HEAD;
            case ENDERMAN:
                return Material.ENDER_PEARL;
            case BLAZE:
                return Material.BLAZE_ROD;
            case SPIDER:
                return Material.SPIDER_EYE;
            case IRON_GOLEM:
                return Material.IRON_BLOCK;
            case WITHER:
                return Material.WITHER_SKELETON_SKULL;
            case ENDER_DRAGON:
                return Material.DRAGON_HEAD;
            default:
                return Material.SPAWNER;
        }
    }

    /**
     * Save a boss to a YAML file
     */
    public static boolean saveBoss(CustomBossCreatorGUI.CustomBossBuilder builder, String existingFileName) {
        String fileName;

        if (existingFileName != null) {
            // Editing existing boss - use its current file name
            fileName = sanitizeFileName(existingFileName);
        } else {
            // New boss - use its name
            fileName = sanitizeFileName(builder.getBossName());
        }

        File bossFile = new File(customBossesFolder, fileName + ".yml");

        try {
            FileConfiguration bossConfig = new YamlConfiguration();

            // Save basic properties
            bossConfig.set("name", builder.getBossName());
            bossConfig.set("type", builder.getEntityType().name());
            bossConfig.set("size", builder.getSize());
            bossConfig.set("aggressionType", builder.getAggressionType().name());
            bossConfig.set("health", builder.getHealth());

            // Save particle effect
            if (builder.getParticleEffect() != null) {
                bossConfig.set("particle", builder.getParticleEffect().name());
            }

            bossConfig.set("customModelData", builder.getCustomModelData());

            // Save equipment
            if (builder.getHelmet() != null) {
                bossConfig.set("equipment.helmet", builder.getHelmet());
            }
            if (builder.getChestplate() != null) {
                bossConfig.set("equipment.chestplate", builder.getChestplate());
            }
            if (builder.getLeggings() != null) {
                bossConfig.set("equipment.leggings", builder.getLeggings());
            }
            if (builder.getBoots() != null) {
                bossConfig.set("equipment.boots", builder.getBoots());
            }
            if (builder.getMainHand() != null) {
                bossConfig.set("equipment.mainhand", builder.getMainHand());
            }
            if (builder.getOffHand() != null) {
                bossConfig.set("equipment.offhand", builder.getOffHand());
            }

            // Save special attacks
            bossConfig.set("specialAttacks", builder.getSpecialAttacks());

            // Save natural spawning settings
            bossConfig.set("naturalSpawning", builder.isNaturalSpawning());
            bossConfig.set("spawnRarity", builder.getSpawnRarity());
            bossConfig.set("spawnWorlds", builder.getSpawnWorlds());

            bossConfig.save(bossFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load a boss configuration from its YAML file
     */
    public static CustomBossCreatorGUI.CustomBossBuilder loadBoss(String bossName) {
        String fileName = sanitizeFileName(bossName);
        File bossFile = new File(customBossesFolder, fileName + ".yml");

        if (!bossFile.exists()) {
            return null;
        }

        FileConfiguration bossConfig = YamlConfiguration.loadConfiguration(bossFile);
        CustomBossCreatorGUI.CustomBossBuilder builder = new CustomBossCreatorGUI.CustomBossBuilder();

        // Load basic properties
        builder.setBossName(bossConfig.getString("name", "Unknown"));
        builder.setEntityType(EntityType.valueOf(bossConfig.getString("type", "ZOMBIE")));
        builder.setSize(bossConfig.getInt("size", 1));

        // Load aggression type with backward compatibility
        if (bossConfig.contains("aggressionType")) {
            builder.setAggressionType(AggressionType.valueOf(bossConfig.getString("aggressionType", "AGGRESSIVE")));
        } else {
            // Backward compatibility
            boolean oldAggressive = bossConfig.getBoolean("aggressive", true);
            builder.setAggressionType(oldAggressive ? AggressionType.AGGRESSIVE : AggressionType.PASSIVE);
        }

        builder.setHealth(bossConfig.getDouble("health", 100.0));

        // Load particle effect
        if (bossConfig.contains("particle")) {
            builder.setParticleEffect(Particle.valueOf(bossConfig.getString("particle")));
        }

        builder.setCustomModelData(bossConfig.getInt("customModelData", 0));

        // Load equipment
        if (bossConfig.contains("equipment.helmet")) {
            builder.setHelmet(bossConfig.getItemStack("equipment.helmet"));
        }
        if (bossConfig.contains("equipment.chestplate")) {
            builder.setChestplate(bossConfig.getItemStack("equipment.chestplate"));
        }
        if (bossConfig.contains("equipment.leggings")) {
            builder.setLeggings(bossConfig.getItemStack("equipment.leggings"));
        }
        if (bossConfig.contains("equipment.boots")) {
            builder.setBoots(bossConfig.getItemStack("equipment.boots"));
        }
        if (bossConfig.contains("equipment.mainhand")) {
            builder.setMainHand(bossConfig.getItemStack("equipment.mainhand"));
        }
        if (bossConfig.contains("equipment.offhand")) {
            builder.setOffHand(bossConfig.getItemStack("equipment.offhand"));
        }

        // Load special attacks
        List<String> attacks = bossConfig.getStringList("specialAttacks");
        for (String attack : attacks) {
            builder.addSpecialAttack(attack);
        }

        // Load natural spawning settings
        builder.setNaturalSpawning(bossConfig.getBoolean("naturalSpawning", false));
        builder.setSpawnRarity(bossConfig.getInt("spawnRarity", 1000));

        List<String> worlds = bossConfig.getStringList("spawnWorlds");
        for (String world : worlds) {
            builder.addSpawnWorld(world);
        }

        return builder;
    }

    /**
     * Delete a boss YAML file
     */
    public static boolean deleteBoss(String bossName) {
        String fileName = sanitizeFileName(bossName);
        File bossFile = new File(customBossesFolder, fileName + ".yml");

        if (!bossFile.exists()) {
            return false;
        }

        return bossFile.delete();
    }

    /**
     * Spawn a custom boss at a location with special attacks
     */
    public static void spawnCustomBoss(CustomBossCreatorGUI.CustomBossBuilder builder, Location location, Main main) {
        World world = location.getWorld();
        if (world == null) return;

        // Spawn the entity
        LivingEntity entity = (LivingEntity) world.spawnEntity(location, builder.getEntityType());

        // Set custom name
        entity.setCustomName(builder.getBossName());
        entity.setCustomNameVisible(true);

        // Set health
        entity.getAttribute(Attribute.MAX_HEALTH).setBaseValue(builder.getHealth());
        entity.setHealth(builder.getHealth());

        // Set AI based on aggression type
        entity.setAI(builder.hasAI());

        // For passive mobs, mark them and clear targets initially
        if (builder.getAggressionType() == AggressionType.PASSIVE) {
            PassiveBossHandler.markAsPassive(entity, main);
            if (entity instanceof org.bukkit.entity.Mob) {
                ((org.bukkit.entity.Mob) entity).setTarget(null);
            }
        }

        // Set scale for the boss (1–10 from creator)
        int rawSize = builder.getSize();

        // Hard clamp between 1–10 just in case
        int size = Math.max(1, Math.min(10, rawSize));

        // Try attribute-based scaling first (1–10 → direct scale)
        if (entity.getAttribute(Attribute.SCALE) != null) {
            entity.getAttribute(Attribute.SCALE).setBaseValue(size);
        }

        // Special handling for Slimes & Magma Cubes so they actually grow/shrink
        if (entity instanceof org.bukkit.entity.Slime) {
            ((org.bukkit.entity.Slime) entity).setSize(size);
        } else if (entity instanceof org.bukkit.entity.MagmaCube) {
            ((org.bukkit.entity.MagmaCube) entity).setSize(size);
        }

        // Set equipment
        EntityEquipment equipment = entity.getEquipment();
        if (equipment != null) {
            if (builder.getHelmet() != null) equipment.setHelmet(builder.getHelmet());
            if (builder.getChestplate() != null) equipment.setChestplate(builder.getChestplate());
            if (builder.getLeggings() != null) equipment.setLeggings(builder.getLeggings());
            if (builder.getBoots() != null) equipment.setBoots(builder.getBoots());
            if (builder.getMainHand() != null) equipment.setItemInMainHand(builder.getMainHand());
            if (builder.getOffHand() != null) equipment.setItemInOffHand(builder.getOffHand());

            // Prevent equipment from dropping
            equipment.setHelmetDropChance(0.0f);
            equipment.setChestplateDropChance(0.0f);
            equipment.setLeggingsDropChance(0.0f);
            equipment.setBootsDropChance(0.0f);
            equipment.setItemInMainHandDropChance(0.0f);
            equipment.setItemInOffHandDropChance(0.0f);
        }

        // Start particle effect task if set
        if (builder.getParticleEffect() != null) {
            final Particle particle = builder.getParticleEffect();
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (entity.isDead()) {
                        cancel();
                        return;
                    }
                    world.spawnParticle(particle, entity.getLocation().add(0, 1, 0), 5, 0.5, 0.5, 0.5, 0);
                }
            }.runTaskTimer(main, 0L, 10L);
        }

        // Register boss with AI controller for special attacks
        // FIXED: Use the main instance's AI controller
        if (!builder.getSpecialAttacks().isEmpty()) {
            main.getConsole().sendMessage(main.getPluginPrefix() + ChatColor.GREEN +
                    "Registering boss with " + builder.getSpecialAttacks().size() + " attacks");

            // Log what attacks are being registered
            for (String attack : builder.getSpecialAttacks()) {
                main.getConsole().sendMessage(main.getPluginPrefix() + ChatColor.GRAY +
                        "  - " + attack);
            }

            main.getBossAIController().registerBoss(entity, builder.getSpecialAttacks());
        } else {
            main.getConsole().sendMessage(main.getPluginPrefix() + ChatColor.YELLOW +
                    "Warning: Boss spawned with no special attacks!");
        }

        // Clean up AI when boss dies
        new BukkitRunnable() {
            @Override
            public void run() {
                if (entity.isDead()) {
                    main.getBossAIController().unregisterBoss(entity.getUniqueId());
                    cancel();
                }
            }
        }.runTaskTimer(main, 20L, 20L);
    }

    /**
     * Convert a boss name to a valid file name
     */
    private static String sanitizeFileName(String name) {
        return ChatColor.stripColor(name).replaceAll("[^a-zA-Z0-9_-]", "_");
    }
}