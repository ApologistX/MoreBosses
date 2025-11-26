package net.ddns.vcccd;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Registry for preset special attacks that can be assigned to custom bosses
 * This class defines available attacks and their properties for the boss creator
 */
public class BossSpecialAttacks {

    private static final Map<String, SpecialAttack> ATTACK_REGISTRY = new HashMap<>();

    static {
        // Register all preset special attacks
        registerAttacks();
    }

    private static void registerAttacks() {
        // Melee Attacks
        ATTACK_REGISTRY.put("SLAM_ATTACK", new SpecialAttack(
                "SLAM_ATTACK",
                ChatColor.RED + "Ground Slam",
                "Slams the ground, damaging and knocking back nearby players",
                AttackCategory.MELEE,
                Material.IRON_BLOCK,
                10.0, // damage
                5.0   // range
        ));

        ATTACK_REGISTRY.put("CLEAVE", new SpecialAttack(
                "CLEAVE",
                ChatColor.RED + "Cleaving Strike",
                "Wide arc attack hitting multiple targets",
                AttackCategory.MELEE,
                Material.IRON_AXE,
                8.0,
                4.0
        ));

        ATTACK_REGISTRY.put("CHARGE", new SpecialAttack(
                "CHARGE",
                ChatColor.RED + "Bull Charge",
                "Charges at target, dealing heavy damage on impact",
                AttackCategory.MELEE,
                Material.IRON_HORSE_ARMOR,
                15.0,
                10.0
        ));

        // Ranged Attacks
        ATTACK_REGISTRY.put("FIREBALL", new SpecialAttack(
                "FIREBALL",
                ChatColor.GOLD + "Fireball Barrage",
                "Shoots explosive fireballs at target",
                AttackCategory.RANGED,
                Material.FIRE_CHARGE,
                6.0,
                30.0
        ));

        ATTACK_REGISTRY.put("LIGHTNING", new SpecialAttack(
                "LIGHTNING",
                ChatColor.AQUA + "Lightning Strike",
                "Summons lightning bolts on target location",
                AttackCategory.RANGED,
                Material.LIGHTNING_ROD,
                10.0,
                20.0
        ));

        ATTACK_REGISTRY.put("ARROW_RAIN", new SpecialAttack(
                "ARROW_RAIN",
                ChatColor.GRAY + "Arrow Rain",
                "Fires a volley of arrows into the air",
                AttackCategory.RANGED,
                Material.ARROW,
                4.0,
                25.0
        ));

        ATTACK_REGISTRY.put("ENDER_PEARL", new SpecialAttack(
                "ENDER_PEARL",
                ChatColor.DARK_PURPLE + "Teleport Strike",
                "Teleports behind target and strikes",
                AttackCategory.RANGED,
                Material.ENDER_PEARL,
                12.0,
                15.0
        ));

        // Area of Effect
        ATTACK_REGISTRY.put("POISON_CLOUD", new SpecialAttack(
                "POISON_CLOUD",
                ChatColor.GREEN + "Poison Cloud",
                "Creates a lingering poison cloud",
                AttackCategory.AOE,
                Material.LINGERING_POTION,
                2.0,
                8.0
        ));

        ATTACK_REGISTRY.put("EXPLOSION", new SpecialAttack(
                "EXPLOSION",
                ChatColor.RED + "Explosive Burst",
                "Creates an explosion at boss location",
                AttackCategory.AOE,
                Material.TNT,
                12.0,
                6.0
        ));

        ATTACK_REGISTRY.put("FROST_NOVA", new SpecialAttack(
                "FROST_NOVA",
                ChatColor.BLUE + "Frost Nova",
                "Freezes nearby enemies and deals damage",
                AttackCategory.AOE,
                Material.ICE,
                5.0,
                7.0
        ));

        ATTACK_REGISTRY.put("SHOCKWAVE", new SpecialAttack(
                "SHOCKWAVE",
                ChatColor.YELLOW + "Shockwave",
                "Sends out a damaging shockwave",
                AttackCategory.AOE,
                Material.ECHO_SHARD,
                8.0,
                10.0
        ));

        // Summon Abilities
        ATTACK_REGISTRY.put("SUMMON_MINIONS", new SpecialAttack(
                "SUMMON_MINIONS",
                ChatColor.DARK_RED + "Summon Minions",
                "Spawns hostile minions to aid in combat",
                AttackCategory.SUMMON,
                Material.SPAWNER,
                0.0,
                5.0
        ));

        ATTACK_REGISTRY.put("SUMMON_WALL", new SpecialAttack(
                "SUMMON_WALL",
                ChatColor.GRAY + "Stone Wall",
                "Summons a protective stone wall",
                AttackCategory.SUMMON,
                Material.STONE_BRICKS,
                0.0,
                8.0
        ));

        // Buff/Debuff
        ATTACK_REGISTRY.put("REGEN", new SpecialAttack(
                "REGEN",
                ChatColor.LIGHT_PURPLE + "Regeneration",
                "Heals the boss over time",
                AttackCategory.BUFF,
                Material.GOLDEN_APPLE,
                0.0,
                0.0
        ));

        ATTACK_REGISTRY.put("ENRAGE", new SpecialAttack(
                "ENRAGE",
                ChatColor.DARK_RED + "Enrage",
                "Increases damage and speed when low health",
                AttackCategory.BUFF,
                Material.REDSTONE,
                0.0,
                0.0
        ));

        ATTACK_REGISTRY.put("WEAKEN", new SpecialAttack(
                "WEAKEN",
                ChatColor.DARK_GRAY + "Weakening Aura",
                "Applies weakness to nearby players",
                AttackCategory.DEBUFF,
                Material.FERMENTED_SPIDER_EYE,
                0.0,
                10.0
        ));

        ATTACK_REGISTRY.put("BLIND", new SpecialAttack(
                "BLIND",
                ChatColor.BLACK + "Blinding Flash",
                "Blinds nearby players temporarily",
                AttackCategory.DEBUFF,
                Material.INK_SAC,
                0.0,
                8.0
        ));

        // Special Mechanics
        ATTACK_REGISTRY.put("PHASE_SHIFT", new SpecialAttack(
                "PHASE_SHIFT",
                ChatColor.AQUA + "Phase Shift",
                "Becomes invulnerable and teleports randomly",
                AttackCategory.SPECIAL,
                Material.GHAST_TEAR,
                0.0,
                0.0
        ));

        ATTACK_REGISTRY.put("LIFE_DRAIN", new SpecialAttack(
                "LIFE_DRAIN",
                ChatColor.DARK_PURPLE + "Life Drain",
                "Steals health from nearby players",
                AttackCategory.SPECIAL,
                Material.NETHER_STAR,
                3.0,
                12.0
        ));

        ATTACK_REGISTRY.put("REFLECT", new SpecialAttack(
                "REFLECT",
                ChatColor.WHITE + "Damage Reflection",
                "Reflects a portion of damage back to attacker",
                AttackCategory.SPECIAL,
                Material.SHIELD,
                0.0,
                0.0
        ));
    }

    public static Map<String, SpecialAttack> getAllAttacks() {
        return new HashMap<>(ATTACK_REGISTRY);
    }

    public static SpecialAttack getAttack(String id) {
        return ATTACK_REGISTRY.get(id);
    }

    public static ArrayList<SpecialAttack> getAttacksByCategory(AttackCategory category) {
        ArrayList<SpecialAttack> attacks = new ArrayList<>();
        for (SpecialAttack attack : ATTACK_REGISTRY.values()) {
            if (attack.getCategory() == category) {
                attacks.add(attack);
            }
        }
        return attacks;
    }

    /**
     * Creates a menu item for displaying the special attack in GUIs
     */
    public static ItemStack createAttackMenuItem(String attackId) {
        SpecialAttack attack = ATTACK_REGISTRY.get(attackId);
        if (attack == null) return null;

        ItemStack item = new ItemStack(attack.getIcon());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(attack.getDisplayName());

        ArrayList<String> lore = new ArrayList<>();
        lore.add(ChatColor.GRAY + attack.getDescription());
        lore.add("");
        lore.add(ChatColor.YELLOW + "Category: " + ChatColor.WHITE + attack.getCategory().name());
        if (attack.getDamage() > 0) {
            lore.add(ChatColor.RED + "Damage: " + ChatColor.WHITE + attack.getDamage());
        }
        if (attack.getRange() > 0) {
            lore.add(ChatColor.AQUA + "Range: " + ChatColor.WHITE + attack.getRange() + " blocks");
        }

        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    // Enum for attack categories
    public enum AttackCategory {
        MELEE,      // Close range physical attacks
        RANGED,     // Long range projectile attacks
        AOE,        // Area of effect attacks
        SUMMON,     // Summons entities or structures
        BUFF,       // Buffs the boss
        DEBUFF,     // Debuffs players
        SPECIAL     // Unique mechanics
    }

    /**
     * Data class representing a special attack
     */
    public static class SpecialAttack {
        private final String id;
        private final String displayName;
        private final String description;
        private final AttackCategory category;
        private final Material icon;
        private final double damage;
        private final double range;

        public SpecialAttack(String id, String displayName, String description,
                             AttackCategory category, Material icon, double damage, double range) {
            this.id = id;
            this.displayName = displayName;
            this.description = description;
            this.category = category;
            this.icon = icon;
            this.damage = damage;
            this.range = range;
        }

        // Getters
        public String getId() { return id; }
        public String getDisplayName() { return displayName; }
        public String getDescription() { return description; }
        public AttackCategory getCategory() { return category; }
        public Material getIcon() { return icon; }
        public double getDamage() { return damage; }
        public double getRange() { return range; }
    }

    /**
     * Execute a special attack (called during boss combat)
     * This is where the actual attack logic will be implemented
     */
    public static void executeAttack(String attackId, LivingEntity boss, Player target, Main main) {
        // TODO: Implement individual attack behaviors
        // This method will be called by boss AI during combat
        // Each attack ID will have its own implementation

        switch (attackId) {
            case "SLAM_ATTACK":
                // TODO: Implement slam attack logic
                break;
            case "FIREBALL":
                // TODO: Implement fireball logic
                break;
            case "LIGHTNING":
                // TODO: Implement lightning logic
                break;
            // ... implement all other attacks
            default:
                break;
        }
    }
}