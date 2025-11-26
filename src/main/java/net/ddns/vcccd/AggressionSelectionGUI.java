package net.ddns.vcccd;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * GUI for selecting mob aggression behavior
 */
class AggressionSelectionGUI {
    private final Main main;

    public AggressionSelectionGUI(Main main) {
        this.main = main;
    }

    public void openGUI(Player player) {
        Inventory gui = Bukkit.createInventory(null, 27, "Select Aggression Type");

        // Passive (until provoked)
        ItemStack passive = new ItemStack(Material.WHITE_WOOL);
        ItemMeta passiveMeta = passive.getItemMeta();
        passiveMeta.setDisplayName(ChatColor.GREEN + "Passive (Until Hit)");
        ArrayList<String> passiveLore = new ArrayList<>();
        passiveLore.add(ChatColor.GRAY + "Boss will not attack players");
        passiveLore.add(ChatColor.GRAY + "unless attacked first");
        passiveLore.add(ChatColor.YELLOW + "Best for: Peaceful encounters");
        passiveMeta.setLore(passiveLore);
        passive.setItemMeta(passiveMeta);
        gui.setItem(10, passive);

        // Aggressive (active seeking)
        ItemStack aggressive = new ItemStack(Material.RED_WOOL);
        ItemMeta aggressiveMeta = aggressive.getItemMeta();
        aggressiveMeta.setDisplayName(ChatColor.RED + "Aggressive");
        ArrayList<String> aggressiveLore = new ArrayList<>();
        aggressiveLore.add(ChatColor.GRAY + "Boss will actively seek out");
        aggressiveLore.add(ChatColor.GRAY + "and attack nearby players");
        aggressiveLore.add(ChatColor.YELLOW + "Best for: Boss fights");
        aggressiveMeta.setLore(aggressiveLore);
        aggressive.setItemMeta(aggressiveMeta);
        gui.setItem(12, aggressive);

        // No AI
        ItemStack noAi = new ItemStack(Material.BARRIER);
        ItemMeta noAiMeta = noAi.getItemMeta();
        noAiMeta.setDisplayName(ChatColor.GRAY + "No AI");
        ArrayList<String> noAiLore = new ArrayList<>();
        noAiLore.add(ChatColor.GRAY + "Boss will stand still and");
        noAiLore.add(ChatColor.GRAY + "not move or attack");
        noAiLore.add(ChatColor.YELLOW + "Best for: Statues/decorations");
        noAiMeta.setLore(noAiLore);
        noAi.setItemMeta(noAiMeta);
        gui.setItem(14, noAi);

        // Current selection indicator
        CustomBossCreatorGUI.CustomBossBuilder builder = CustomBossCreatorGUI.getBuildSession(player.getUniqueId());
        if (builder != null) {
            ItemStack current = new ItemStack(Material.ARROW);
            ItemMeta currentMeta = current.getItemMeta();
            currentMeta.setDisplayName(ChatColor.GOLD + "Current: " + builder.getAggressionType().getDisplayName());
            current.setItemMeta(currentMeta);
            gui.setItem(22, current);
        }

        // Back button
        ItemStack back = new ItemStack(Material.ARROW);
        ItemMeta backMeta = back.getItemMeta();
        backMeta.setDisplayName(ChatColor.YELLOW + "Back to Creator");
        back.setItemMeta(backMeta);
        gui.setItem(18, back);

        player.openInventory(gui);
    }
}