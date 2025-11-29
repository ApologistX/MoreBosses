package net.ddns.vcccd;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Manages custom user-created special attacks loaded from YAML files
 */
public class CustomAttackManager {

    private final Main main;
    private static final Map<String, CustomAttack> customAttacks = new HashMap<>();

    public CustomAttackManager(Main main) {
        this.main = main;
        loadAllCustomAttacks();
    }

    /**
     * Load all custom attacks from the "Custom Attacks" folder
     */
    private void loadAllCustomAttacks() {
        customAttacks.clear();

        File attacksFolder = new File(main.getDataFolder(), "Custom Attacks");
        if (!attacksFolder.exists()) {
            // Create default file if it doesn't exist
            if (attacksFolder.mkdirs()) {
                createExampleAttack();
            }
        }

        File[] files = attacksFolder.listFiles((dir, name) -> name.endsWith(".yml") || name.endsWith(".yaml"));
        if (files == null) return;

        for (File file : files) {
            try {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);

                // If file uses new "attacks:" format, load multiple
                if (config.contains("attacks")) {
                    ConfigurationSection attacksSection = config.getConfigurationSection("attacks");
                    if (attacksSection != null) {
                        for (String key : attacksSection.getKeys(false)) {
                            ConfigurationSection attackSection = attacksSection.getConfigurationSection(key);
                            if (attackSection != null) {
                                CustomAttack attack = loadAttackFromSection(key, attackSection, file.getName());
                                if (attack != null) {
                                    customAttacks.put(attack.getId(), attack);
                                    main.getConsole().sendMessage(main.getPluginPrefix() +
                                            ChatColor.GREEN + "Loaded attack: " + attack.getId() + " from " + file.getName());
                                }
                            }
                        }
                    }
                } else {
                    // Old single-attack format
                    CustomAttack attack = loadSingleAttack(file, config);
                    if (attack != null) {
                        customAttacks.put(attack.getId(), attack);
                        main.getConsole().sendMessage(main.getPluginPrefix() +
                                ChatColor.GREEN + "Loaded attack: " + attack.getId() + " from " + file.getName());
                    }
                }
            } catch (Exception e) {
                main.getConsole().sendMessage(main.getPluginPrefix() +
                        ChatColor.RED + "Failed to load custom attacks from " + file.getName());
                e.printStackTrace();
            }
        }

        main.getConsole().sendMessage(main.getPluginPrefix() +
                ChatColor.AQUA + "Loaded " + customAttacks.size() + " custom attacks.");
    }

    /**
     * Create an example attack file with 8 pre-made attacks
     */
    private void createExampleAttack() {
        File exampleFile = new File(main.getDataFolder(), "Custom Attacks/example_attacks.yml");
        try {
            FileConfiguration config = new YamlConfiguration();

            // METEOR_STRIKE
            config.set("attacks.METEOR_STRIKE.displayName", "&c&lMeteor Strike");
            config.set("attacks.METEOR_STRIKE.description", "Summons meteors from the sky that explode on impact");
            config.set("attacks.METEOR_STRIKE.category", "AOE");
            config.set("attacks.METEOR_STRIKE.icon", "FIRE_CHARGE");
            config.set("attacks.METEOR_STRIKE.damage", 15.0);
            config.set("attacks.METEOR_STRIKE.range", 15.0);
            config.set("attacks.METEOR_STRIKE.effects.particles", Arrays.asList("FLAME", "EXPLOSION"));
            config.set("attacks.METEOR_STRIKE.effects.sounds", Arrays.asList("ENTITY_BLAZE_SHOOT", "ENTITY_GENERIC_EXPLODE"));
            config.set("attacks.METEOR_STRIKE.effects.projectileType", "FIREBALL");
            config.set("attacks.METEOR_STRIKE.effects.projectileCount", 5);
            config.set("attacks.METEOR_STRIKE.effects.aoeRadius", 4.0);
            config.set("attacks.METEOR_STRIKE.effects.knockback", 1.5);
            config.set("attacks.METEOR_STRIKE.effects.setFire", true);
            config.set("attacks.METEOR_STRIKE.effects.fireTicks", 100);
            config.set("attacks.METEOR_STRIKE.effects.potionEffects", Arrays.asList("SLOWNESS:60:1", "WEAKNESS:100:0"));
            // Pattern: Circle of meteors falling around target
            config.set("attacks.METEOR_STRIKE.effects.pattern.type", "CIRCLE");
            config.set("attacks.METEOR_STRIKE.effects.pattern.radius", 6.0);
            config.set("attacks.METEOR_STRIKE.effects.pattern.heightOffset", 15.0);
            config.set("attacks.METEOR_STRIKE.effects.pattern.waves", 3);
            config.set("attacks.METEOR_STRIKE.effects.pattern.meteorsPerWave", 8);
            config.set("attacks.METEOR_STRIKE.effects.pattern.intervalTicks", 10);

            // ICE_PRISON
            config.set("attacks.ICE_PRISON.displayName", "&b&lIce Prison");
            config.set("attacks.ICE_PRISON.description", "Freezes all nearby enemies in place");
            config.set("attacks.ICE_PRISON.category", "DEBUFF");
            config.set("attacks.ICE_PRISON.icon", "PACKED_ICE");
            config.set("attacks.ICE_PRISON.damage", 5.0);
            config.set("attacks.ICE_PRISON.range", 10.0);
            config.set("attacks.ICE_PRISON.effects.particles", Arrays.asList("SNOWFLAKE", "FALLING_WATER"));
            config.set("attacks.ICE_PRISON.effects.sounds", Arrays.asList("BLOCK_GLASS_BREAK", "ENTITY_PLAYER_HURT_FREEZE"));
            config.set("attacks.ICE_PRISON.effects.aoeRadius", 10.0);
            config.set("attacks.ICE_PRISON.effects.potionEffects", Arrays.asList("SLOWNESS:200:4", "WEAKNESS:100:1", "MINING_FATIGUE:200:2"));

            // VOID_RIFT
            config.set("attacks.VOID_RIFT.displayName", "&5&lVoid Rift");
            config.set("attacks.VOID_RIFT.description", "Opens a rift to summon dark creatures");
            config.set("attacks.VOID_RIFT.category", "SUMMON");
            config.set("attacks.VOID_RIFT.icon", "ENDER_EYE");
            config.set("attacks.VOID_RIFT.damage", 0.0);
            config.set("attacks.VOID_RIFT.range", 8.0);
            config.set("attacks.VOID_RIFT.effects.particles", Arrays.asList("PORTAL", "REVERSE_PORTAL", "WITCH"));
            config.set("attacks.VOID_RIFT.effects.sounds", Arrays.asList("ENTITY_ENDERMAN_TELEPORT", "ENTITY_EVOKER_PREPARE_SUMMON"));
            config.set("attacks.VOID_RIFT.effects.summonEntity", "ENDERMITE");
            config.set("attacks.VOID_RIFT.effects.summonCount", 4);
            config.set("attacks.VOID_RIFT.effects.teleportBehind", false);

            // BERSERKER_RAGE
            config.set("attacks.BERSERKER_RAGE.displayName", "&4&lBerserker Rage");
            config.set("attacks.BERSERKER_RAGE.description", "The boss enters a rage, greatly increasing power");
            config.set("attacks.BERSERKER_RAGE.category", "BUFF");
            config.set("attacks.BERSERKER_RAGE.icon", "REDSTONE_BLOCK");
            config.set("attacks.BERSERKER_RAGE.damage", 0.0);
            config.set("attacks.BERSERKER_RAGE.range", 0.0);
            config.set("attacks.BERSERKER_RAGE.effects.particles", Arrays.asList("ANGRY_VILLAGER", "LAVA"));
            config.set("attacks.BERSERKER_RAGE.effects.sounds", Arrays.asList("ENTITY_ENDER_DRAGON_GROWL", "ENTITY_RAVAGER_ROAR"));
            config.set("attacks.BERSERKER_RAGE.effects.applyToSelf", true);
            config.set("attacks.BERSERKER_RAGE.effects.potionEffects", Arrays.asList("STRENGTH:300:2", "SPEED:300:1", "REGENERATION:300:1", "RESISTANCE:300:0"));

            // TOXIC_BARRAGE
            config.set("attacks.TOXIC_BARRAGE.displayName", "&2&lToxic Barrage");
            config.set("attacks.TOXIC_BARRAGE.description", "Fires multiple poison projectiles");
            config.set("attacks.TOXIC_BARRAGE.category", "RANGED");
            config.set("attacks.TOXIC_BARRAGE.icon", "POISONOUS_POTATO");
            config.set("attacks.TOXIC_BARRAGE.damage", 6.0);
            config.set("attacks.TOXIC_BARRAGE.range", 25.0);
            config.set("attacks.TOXIC_BARRAGE.effects.particles", Arrays.asList("FALLING_SPORE_BLOSSOM", "SLIME"));
            config.set("attacks.TOXIC_BARRAGE.effects.sounds", Arrays.asList("ENTITY_SPIDER_AMBIENT", "ENTITY_WITCH_THROW"));
            config.set("attacks.TOXIC_BARRAGE.effects.projectileType", "EGG");
            config.set("attacks.TOXIC_BARRAGE.effects.projectileCount", 8);
            config.set("attacks.TOXIC_BARRAGE.effects.potionEffects", Arrays.asList("POISON:120:2", "NAUSEA:100:0"));

            // EARTHQUAKE
            config.set("attacks.EARTHQUAKE.displayName", "&6&lEarthquake");
            config.set("attacks.EARTHQUAKE.description", "Causes a devastating earthquake");
            config.set("attacks.EARTHQUAKE.category", "AOE");
            config.set("attacks.EARTHQUAKE.icon", "STONE");
            config.set("attacks.EARTHQUAKE.damage", 20.0);
            config.set("attacks.EARTHQUAKE.range", 12.0);
            config.set("attacks.EARTHQUAKE.effects.particles", Arrays.asList("BLOCK_CRACK", "EXPLOSION"));
            config.set("attacks.EARTHQUAKE.effects.sounds", Arrays.asList("ENTITY_WARDEN_SONIC_BOOM", "ENTITY_GENERIC_EXPLODE"));
            config.set("attacks.EARTHQUAKE.effects.aoeRadius", 12.0);
            config.set("attacks.EARTHQUAKE.effects.knockback", 2.0);
            config.set("attacks.EARTHQUAKE.effects.createExplosion", true);
            config.set("attacks.EARTHQUAKE.effects.explosionPower", 4.0);
            config.set("attacks.EARTHQUAKE.effects.potionEffects", Arrays.asList("SLOWNESS:100:2"));

            // SHADOW_STRIKE
            config.set("attacks.SHADOW_STRIKE.displayName", "&8&lShadow Strike");
            config.set("attacks.SHADOW_STRIKE.description", "Vanishes and strikes from behind");
            config.set("attacks.SHADOW_STRIKE.category", "SPECIAL");
            config.set("attacks.SHADOW_STRIKE.icon", "ENDER_PEARL");
            config.set("attacks.SHADOW_STRIKE.damage", 18.0);
            config.set("attacks.SHADOW_STRIKE.range", 20.0);
            config.set("attacks.SHADOW_STRIKE.effects.particles", Arrays.asList("SMOKE_LARGE", "PORTAL"));
            config.set("attacks.SHADOW_STRIKE.effects.sounds", Arrays.asList("ENTITY_ENDERMAN_TELEPORT", "ENTITY_PLAYER_ATTACK_CRIT"));
            config.set("attacks.SHADOW_STRIKE.effects.teleportBehind", true);
            config.set("attacks.SHADOW_STRIKE.effects.potionEffects", Arrays.asList("BLINDNESS:40:0"));

            // HOLY_LIGHT
            config.set("attacks.HOLY_LIGHT.displayName", "&e&lHoly Light");
            config.set("attacks.HOLY_LIGHT.description", "Channels divine energy to restore health");
            config.set("attacks.HOLY_LIGHT.category", "BUFF");
            config.set("attacks.HOLY_LIGHT.icon", "BEACON");
            config.set("attacks.HOLY_LIGHT.damage", 0.0);
            config.set("attacks.HOLY_LIGHT.range", 0.0);
            config.set("attacks.HOLY_LIGHT.effects.particles", Arrays.asList("END_ROD", "SOUL_FIRE_FLAME", "HEART"));
            config.set("attacks.HOLY_LIGHT.effects.sounds", Arrays.asList("BLOCK_BEACON_ACTIVATE", "BLOCK_ENCHANTMENT_TABLE_USE"));
            config.set("attacks.HOLY_LIGHT.effects.applyToSelf", true);
            config.set("attacks.HOLY_LIGHT.effects.potionEffects", Arrays.asList("REGENERATION:200:3", "ABSORPTION:300:2", "RESISTANCE:200:1"));

            config.save(exampleFile);
            main.getConsole().sendMessage(main.getPluginPrefix() +
                    ChatColor.GREEN + "Created example attack file: example_attacks.yml with 8 attacks");
        } catch (IOException e) {
            main.getConsole().sendMessage(main.getPluginPrefix() +
                    ChatColor.RED + "Failed to create example attack file!");
            e.printStackTrace();
        }
    }

    /**
     * Load a single attack from a configuration section
     */
    private CustomAttack loadAttackFromSection(String id, ConfigurationSection section, String fileName) {
        if (section == null) return null;

        String displayName = ChatColor.translateAlternateColorCodes('&',
                section.getString("displayName", "Custom Attack"));
        String description = section.getString("description", "A custom special attack");
        String categoryStr = section.getString("category", "SPECIAL");
        String iconStr = section.getString("icon", "NETHER_STAR");
        double damage = section.getDouble("damage", 5.0);
        double range = section.getDouble("range", 10.0);

        // Parse category
        BossSpecialAttacks.AttackCategory category;
        try {
            category = BossSpecialAttacks.AttackCategory.valueOf(categoryStr);
        } catch (IllegalArgumentException e) {
            category = BossSpecialAttacks.AttackCategory.SPECIAL;
        }

        // Parse icon
        Material icon;
        try {
            icon = Material.valueOf(iconStr);
        } catch (IllegalArgumentException e) {
            icon = Material.NETHER_STAR;
        }

        // Load effect configuration
        AttackEffects effects = new AttackEffects();
        if (section.contains("effects")) {
            ConfigurationSection effectsSection = section.getConfigurationSection("effects");
            if (effectsSection != null) {
                effects.particles = effectsSection.getStringList("particles");
                effects.sounds = effectsSection.getStringList("sounds");
                effects.projectileType = effectsSection.getString("projectileType");
                effects.projectileCount = effectsSection.getInt("projectileCount", 1);
                effects.aoeRadius = effectsSection.getDouble("aoeRadius", 5.0);
                effects.knockback = effectsSection.getDouble("knockback", 0.0);
                effects.setFire = effectsSection.getBoolean("setFire", false);
                effects.fireTicks = effectsSection.getInt("fireTicks", 0);
                effects.potionEffects = effectsSection.getStringList("potionEffects");
                effects.createExplosion = effectsSection.getBoolean("createExplosion", false);
                effects.explosionPower = (float) effectsSection.getDouble("explosionPower", 2.0);
                effects.summonEntity = effectsSection.getString("summonEntity");
                effects.summonCount = effectsSection.getInt("summonCount", 1);
                effects.teleportBehind = effectsSection.getBoolean("teleportBehind", false);
                effects.applyToSelf = effectsSection.getBoolean("applyToSelf", false);

                // Optional pattern configuration
                if (effectsSection.contains("pattern")) {
                    ConfigurationSection patternSection = effectsSection.getConfigurationSection("pattern");
                    if (patternSection != null) {
                        AttackPattern pattern = new AttackPattern();
                        pattern.type = patternSection.getString("type", "STATIC_CIRCLE");
                        pattern.radius = patternSection.getDouble("radius", 6.0);
                        pattern.heightOffset = patternSection.getDouble("heightOffset", 15.0);
                        pattern.waves = patternSection.getInt("waves", 1);
                        pattern.meteorsPerWave = patternSection.getInt("meteorsPerWave", 8);
                        pattern.intervalTicks = patternSection.getInt("intervalTicks", 10);
                        effects.pattern = pattern;
                    }
                }
            }
        }

        // Load mechanics list (NEW)
        List<Map<String, Object>> mechanics = new ArrayList<>();
        if (section.contains("mechanics")) {
            List<?> mechanicsList = section.getList("mechanics");
            if (mechanicsList != null) {
                for (Object mechObj : mechanicsList) {
                    if (mechObj instanceof Map) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> mechanicMap = (Map<String, Object>) mechObj;
                        mechanics.add(mechanicMap);
                    }
                }
            }
        }

        return new CustomAttack(id, displayName, description, category, icon,
                damage, range, effects, fileName, mechanics); // Pass mechanics
    }

    /**
     * Load a single attack from a file (old format for backwards compatibility)
     */
    private CustomAttack loadSingleAttack(File file, FileConfiguration config) {
        String id = config.getString("id", "CUSTOM_ATTACK");
        return loadAttackFromSection(id, config, file.getName());
    }

    /**
     * Get all custom attacks
     */
    public static Map<String, CustomAttack> getCustomAttacks() {
        return new HashMap<>(customAttacks);
    }

    /**
     * Get a specific custom attack by ID
     */
    public static CustomAttack getCustomAttack(String id) {
        return customAttacks.get(id);
    }

    /**
     * Check if an attack ID is a custom attack
     */
    public static boolean isCustomAttack(String attackId) {
        return customAttacks.containsKey(attackId);
    }

    /**
     * Create an ItemStack representing a custom attack
     */
    public static ItemStack createAttackItem(CustomAttack attack) {
        ItemStack item = new ItemStack(attack.getIcon());
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return item;

        meta.setDisplayName(attack.getDisplayName());

        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + "Custom Attack");
        lore.add("");
        lore.add(ChatColor.YELLOW + "ID: " + ChatColor.WHITE + attack.getId());
        lore.add(ChatColor.YELLOW + "Category: " + ChatColor.WHITE + attack.getCategory().name());
        lore.add(ChatColor.YELLOW + "Damage: " + ChatColor.RED + attack.getDamage());
        lore.add(ChatColor.YELLOW + "Range: " + ChatColor.AQUA + attack.getRange());
        lore.add("");
        lore.add(ChatColor.DARK_GRAY + attack.getDescription());
        lore.add(ChatColor.DARK_GRAY + "From: " + attack.getFileName());

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    /**
     * Reload all custom attacks
     */
    public void reloadCustomAttacks() {
        loadAllCustomAttacks();
    }

    /**
     * Data class for pattern-based spawning
     * Supported pattern types:
     * - CIRCLE: Projectiles spawn in a circle around target, falling straight down
     * - RAIN: Random projectiles raining down within radius
     * - SPIRAL: Projectiles spawn in a spiral pattern outward from target
     * - SCATTER: Completely random scatter around target
     */
    public static class AttackPattern {
        public String type = "CIRCLE";  // CIRCLE, RAIN, SPIRAL, SCATTER
        public double radius = 6.0;  // Radius of the pattern
        public double heightOffset = 15.0;  // How high above target to spawn projectiles
        public int waves = 1;  // Number of waves to spawn
        public int meteorsPerWave = 8;  // Projectiles per wave
        public int intervalTicks = 10;  // Delay between waves (in ticks)
    }

    /**
     * Data class for custom attack effects
     */
    public static class AttackEffects {
        public List<String> particles = new ArrayList<>();
        public List<String> sounds = new ArrayList<>();
        public String projectileType;
        public int projectileCount = 1;
        public double aoeRadius = 5.0;
        public double knockback = 0.0;
        public boolean setFire = false;
        public int fireTicks = 0;
        public List<String> potionEffects = new ArrayList<>();
        public boolean createExplosion = false;
        public float explosionPower = 2.0f;
        public String summonEntity;
        public int summonCount = 1;
        public boolean teleportBehind = false;
        public boolean applyToSelf = false;

        // Optional pattern behavior for projectiles/summons
        public AttackPattern pattern;
    }

    /**
     * Data class representing a custom attack
     */
    public static class CustomAttack {
        private final String id;
        private final String displayName;
        private final String description;
        private final BossSpecialAttacks.AttackCategory category;
        private final Material icon;
        private final double damage;
        private final double range;
        private final AttackEffects effects;
        private final String fileName;
        private final List<Map<String, Object>> mechanics; // NEW: Mechanics list

        public CustomAttack(String id, String displayName, String description,
                            BossSpecialAttacks.AttackCategory category, Material icon,
                            double damage, double range, AttackEffects effects, String fileName,
                            List<Map<String, Object>> mechanics) { // NEW: Add mechanics parameter
            this.id = id;
            this.displayName = displayName;
            this.description = description;
            this.category = category;
            this.icon = icon;
            this.damage = damage;
            this.range = range;
            this.effects = effects;
            this.fileName = fileName;
            this.mechanics = mechanics != null ? mechanics : new ArrayList<>(); // NEW: Initialize mechanics
        }

        // Getters
        public String getId() { return id; }
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        public BossSpecialAttacks.AttackCategory getCategory() { return category; }
        public Material getIcon() { return icon; }
        public double getDamage() { return damage; }
        public double getRange() { return range; }
        public AttackEffects getEffects() { return effects; }
        public String getFileName() { return fileName; }
        public List<Map<String, Object>> getMechanics() { return mechanics; } // NEW: Getter
        public boolean hasMechanics() { return mechanics != null && !mechanics.isEmpty(); } // NEW: Helper
    }
}