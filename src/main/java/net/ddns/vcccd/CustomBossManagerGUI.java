package net.ddns.vcccd;

import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CustomBossManagerGUI {

    private final Main main;
    private static File bossesFile;
    private static FileConfiguration bossesConfig;

    public CustomBossManagerGUI(Main main) {
        this.main = main;
        initializeBossStorage();
    }

    /**
     * Initialize the custom bosses storage file
     */
    private void initializeBossStorage() {
        if (bossesFile == null) {
            bossesFile = new File(main.getDataFolder(), "custom_bosses.yml");
            if (!bossesFile.exists()) {
                try {
                    bossesFile.createNewFile();
                } catch (IOException e) {
                    main.getConsole().sendMessage(main.getPluginPrefix() + ChatColor.RED + "Could not create custom_bosses.yml!");
                    e.printStackTrace();
                }
            }
            bossesConfig = YamlConfiguration.loadConfiguration(bossesFile);
        }
    }

    /**
     * Open the main manager GUI showing all saved bosses
     */
    public void openManagerGUI(Player player) {
        reloadBossConfig();

        ConfigurationSection bosses = bossesConfig.getConfigurationSection("bosses");
        int bossCount = (bosses != null) ? bosses.getKeys(false).size() : 0;

        // Calculate inventory size (minimum 27, maximum 54)
        int invSize = Math.min(54, Math.max(27, ((bossCount / 9) + 1) * 9));

        Inventory managerMenu = Bukkit.createInventory(null, invSize, "Manage Custom Bosses");

        // Add all saved bosses
        int slot = 0;
        if (bosses != null) {
            for (String bossKey : bosses.getKeys(false)) {
                if (slot >= 45) break; // Leave room for buttons at bottom

                ConfigurationSection bossSection = bosses.getConfigurationSection(bossKey);
                if (bossSection != null) {
                    ItemStack bossItem = createBossMenuItem(bossSection);
                    managerMenu.setItem(slot, bossItem);
                    slot++;
                }
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
    private ItemStack createBossMenuItem(ConfigurationSection bossSection) {
        String displayName = bossSection.getString("name", "Unknown Boss");
        EntityType type = EntityType.valueOf(bossSection.getString("type", "ZOMBIE"));
        double health = bossSection.getDouble("health", 100.0);

        // Use appropriate icon based on mob type
        Material icon = getIconForEntityType(type);

        ItemStack item = new ItemStack(icon);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);

        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Type: " + ChatColor.WHITE + type.name());
        lore.add(ChatColor.GRAY + "Health: " + ChatColor.WHITE + health);
        lore.add("");
        lore.add(ChatColor.YELLOW + "Left Click: " + ChatColor.WHITE + "Spawn");
        lore.add(ChatColor.YELLOW + "Right Click: " + ChatColor.WHITE + "Edit");

        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    /**
     * Get an appropriate icon material for an entity type
     */
    private Material getIconForEntityType(EntityType type) {
        switch (type) {
            case ZOMBIE: return Material.ZOMBIE_HEAD;
            case SKELETON: return Material.SKELETON_SKULL;
            case CREEPER: return Material.CREEPER_HEAD;
            case WITHER_SKELETON: return Material.WITHER_SKELETON_SKULL;
            case SLIME: return Material.SLIME_BLOCK;
            case MAGMA_CUBE: return Material.MAGMA_BLOCK;
            case BLAZE: return Material.BLAZE_ROD;
            case ENDERMAN: return Material.ENDER_PEARL;
            case SPIDER: return Material.SPIDER_EYE;
            case IRON_GOLEM: return Material.IRON_BLOCK;
            default: return Material.SPAWNER;
        }
    }

    /**
     * Save a boss configuration to file
     */
    public static boolean saveBoss(CustomBossCreatorGUI.CustomBossBuilder builder, String originalName) {
        try {
            reloadBossConfig();

            // Use original name if editing, otherwise use current name
            String bossKey = (originalName != null) ? sanitizeKey(originalName) : sanitizeKey(builder.getBossName());
            String path = "bosses." + bossKey;

            // Save basic properties
            bossesConfig.set(path + ".name", builder.getBossName());
            bossesConfig.set(path + ".type", builder.getEntityType().name());
            bossesConfig.set(path + ".size", builder.getSize());
            bossesConfig.set(path + ".aggressionType", builder.getAggressionType().name());
            bossesConfig.set(path + ".health", builder.getHealth());

            // Save particle effect
            if (builder.getParticleEffect() != null) {
                bossesConfig.set(path + ".particle", builder.getParticleEffect().name());
            }

            bossesConfig.set(path + ".customModelData", builder.getCustomModelData());

            // Save equipment
            if (builder.getHelmet() != null) {
                bossesConfig.set(path + ".equipment.helmet", builder.getHelmet());
            }
            if (builder.getChestplate() != null) {
                bossesConfig.set(path + ".equipment.chestplate", builder.getChestplate());
            }
            if (builder.getLeggings() != null) {
                bossesConfig.set(path + ".equipment.leggings", builder.getLeggings());
            }
            if (builder.getBoots() != null) {
                bossesConfig.set(path + ".equipment.boots", builder.getBoots());
            }
            if (builder.getMainHand() != null) {
                bossesConfig.set(path + ".equipment.mainhand", builder.getMainHand());
            }
            if (builder.getOffHand() != null) {
                bossesConfig.set(path + ".equipment.offhand", builder.getOffHand());
            }

            // Save special attacks
            bossesConfig.set(path + ".specialAttacks", builder.getSpecialAttacks());

            // Save natural spawning settings
            bossesConfig.set(path + ".naturalSpawning", builder.isNaturalSpawning());
            bossesConfig.set(path + ".spawnRarity", builder.getSpawnRarity());
            bossesConfig.set(path + ".spawnWorlds", builder.getSpawnWorlds());

            bossesConfig.save(bossesFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load a boss configuration from file
     */
    public static CustomBossCreatorGUI.CustomBossBuilder loadBoss(String bossName) {
        reloadBossConfig();

        String bossKey = sanitizeKey(bossName);
        String path = "bosses." + bossKey;

        if (!bossesConfig.contains(path)) {
            return null;
        }

        CustomBossCreatorGUI.CustomBossBuilder builder = new CustomBossCreatorGUI.CustomBossBuilder();

        // Load basic properties
        builder.setBossName(bossesConfig.getString(path + ".name", "Unknown"));
        builder.setEntityType(EntityType.valueOf(bossesConfig.getString(path + ".type", "ZOMBIE")));
        builder.setSize(bossesConfig.getInt(path + ".size", 1));

        // Load aggression type with backward compatibility
        if (bossesConfig.contains(path + ".aggressionType")) {
            builder.setAggressionType(AggressionType.valueOf(bossesConfig.getString(path + ".aggressionType", "AGGRESSIVE")));
        } else {
            // Backward compatibility: convert old boolean to new system
            boolean oldAggressive = bossesConfig.getBoolean(path + ".aggressive", true);
            builder.setAggressionType(oldAggressive ? AggressionType.AGGRESSIVE : AggressionType.PASSIVE);
        }

        builder.setHealth(bossesConfig.getDouble(path + ".health", 100.0));

        // Load particle effect
        if (bossesConfig.contains(path + ".particle")) {
            builder.setParticleEffect(Particle.valueOf(bossesConfig.getString(path + ".particle")));
        }

        builder.setCustomModelData(bossesConfig.getInt(path + ".customModelData", 0));

        // Load equipment
        if (bossesConfig.contains(path + ".equipment.helmet")) {
            builder.setHelmet(bossesConfig.getItemStack(path + ".equipment.helmet"));
        }
        if (bossesConfig.contains(path + ".equipment.chestplate")) {
            builder.setChestplate(bossesConfig.getItemStack(path + ".equipment.chestplate"));
        }
        if (bossesConfig.contains(path + ".equipment.leggings")) {
            builder.setLeggings(bossesConfig.getItemStack(path + ".equipment.leggings"));
        }
        if (bossesConfig.contains(path + ".equipment.boots")) {
            builder.setBoots(bossesConfig.getItemStack(path + ".equipment.boots"));
        }
        if (bossesConfig.contains(path + ".equipment.mainhand")) {
            builder.setMainHand(bossesConfig.getItemStack(path + ".equipment.mainhand"));
        }
        if (bossesConfig.contains(path + ".equipment.offhand")) {
            builder.setOffHand(bossesConfig.getItemStack(path + ".equipment.offhand"));
        }

        // Load special attacks
        List<String> attacks = bossesConfig.getStringList(path + ".specialAttacks");
        for (String attack : attacks) {
            builder.addSpecialAttack(attack);
        }

        // Load natural spawning settings
        builder.setNaturalSpawning(bossesConfig.getBoolean(path + ".naturalSpawning", false));
        builder.setSpawnRarity(bossesConfig.getInt(path + ".spawnRarity", 1000));

        List<String> worlds = bossesConfig.getStringList(path + ".spawnWorlds");
        for (String world : worlds) {
            builder.addSpawnWorld(world);
        }

        return builder;
    }

    /**
     * Delete a boss from the configuration
     */
    public static boolean deleteBoss(String bossName) {
        try {
            reloadBossConfig();

            String bossKey = sanitizeKey(bossName);
            String path = "bosses." + bossKey;

            if (!bossesConfig.contains(path)) {
                return false;
            }

            bossesConfig.set(path, null);
            bossesConfig.save(bossesFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Spawn a custom boss at a location
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

        // Set scale (minecraft:generic.scale) for the boss
        if (entity.getAttribute(Attribute.SCALE) != null) {
            entity.getAttribute(Attribute.SCALE).setBaseValue(builder.getSize());
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
            Bukkit.getScheduler().runTaskTimer(main, () -> {
                if (entity.isDead()) return;
                world.spawnParticle(particle, entity.getLocation().add(0, 1, 0), 5, 0.5, 0.5, 0.5, 0);
            }, 0L, 10L);
        }

        // TODO: Implement special attacks AI
        // TODO: Implement minion spawning
    }

    /**
     * Reload the boss configuration from disk
     */
    private static void reloadBossConfig() {
        if (bossesFile != null && bossesConfig != null) {
            bossesConfig = YamlConfiguration.loadConfiguration(bossesFile);
        }
    }

    /**
     * Convert a boss name to a valid configuration key
     */
    private static String sanitizeKey(String name) {
        return ChatColor.stripColor(name).toLowerCase().replaceAll("[^a-z0-9_]", "_");
    }
}