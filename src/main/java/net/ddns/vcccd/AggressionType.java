package net.ddns.vcccd;

import org.bukkit.ChatColor;

/**
 * Enum representing different aggression behaviors for custom bosses
 */
public enum AggressionType {
    PASSIVE("Passive (Until Hit)", false, true),
    AGGRESSIVE("Aggressive", true, true),
    NO_AI("No AI", false, false);

    private final String displayName;
    private final boolean aggressive;
    private final boolean hasAI;

    AggressionType(String displayName, boolean aggressive, boolean hasAI) {
        this.displayName = displayName;
        this.aggressive = aggressive;
        this.hasAI = hasAI;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isAggressive() {
        return aggressive;
    }

    public boolean hasAI() {
        return hasAI;
    }

    public String getColoredName() {
        switch (this) {
            case PASSIVE:
                return ChatColor.GREEN + displayName;
            case AGGRESSIVE:
                return ChatColor.RED + displayName;
            case NO_AI:
                return ChatColor.GRAY + displayName;
            default:
                return displayName;
        }
    }

    /**
     * Parse AggressionType from string (case-insensitive)
     */
    public static AggressionType fromString(String str) {
        for (AggressionType type : values()) {
            if (type.name().equalsIgnoreCase(str) || type.getDisplayName().equalsIgnoreCase(str)) {
                return type;
            }
        }
        return AGGRESSIVE; // Default
    }
}