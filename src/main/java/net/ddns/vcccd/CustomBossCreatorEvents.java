package net.ddns.vcccd;

import org.bukkit.ChatColor;
import org.bukkit.Particle;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CustomBossCreatorEvents implements Listener {

    private final Main main;
    private static Map<UUID, CreatorInputState> awaitingInput = new HashMap<>();

    public CustomBossCreatorEvents(Main main) {
        this.main = main;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        String title = event.getView().getTitle();

        // Handle Custom Boss Creator GUI
        if (title.equals("Custom Boss Creator")) {
            event.setCancelled(true);
            handleCreatorGUI(event, player);
        }
        // Handle Mob Type Selection GUI
        else if (title.equals("Select Mob Type")) {
            event.setCancelled(true);
            handleMobTypeSelection(event, player);
        }
        // Handle Particle Selection GUI
        else if (title.equals("Select Particle Effect")) {
            event.setCancelled(true);
            handleParticleSelection(event, player);
        }
        // Handle Special Attacks Selection GUI
        else if (title.equals("Select Special Attacks")) {
            event.setCancelled(true);
            handleSpecialAttacksSelection(event, player);
        }
        // Handle Natural Spawn Settings GUI
        else if (title.equals("Natural Spawn Settings")) {
            event.setCancelled(true);
            handleNaturalSpawnSettings(event, player);
        }
        // Handle Aggression Selection GUI
        else if (title.equals("Select Aggression Type")) {
            event.setCancelled(true);
            handleAggressionSelection(event, player);
        }
        // Handle Manage Custom Bosses GUI
        else if (title.equals("Manage Custom Bosses")) {
            event.setCancelled(true);
            handleManagerGUI(event, player);
        }
        // Handle Edit Boss GUI
        else if (title.startsWith("Edit Boss: ")) {
            event.setCancelled(true);
            handleEditBossGUI(event, player);
        }
    }

    private void handleCreatorGUI(InventoryClickEvent event, Player player) {
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
            return;
        }

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        if (itemName.equals(ChatColor.GOLD + "Set Boss Name")) {
            player.closeInventory();
            player.sendMessage(main.getPluginPrefix() + ChatColor.YELLOW + "Enter the boss name in chat:");
            awaitingInput.put(player.getUniqueId(), CreatorInputState.NAME);

        } else if (itemName.equals(ChatColor.GOLD + "Choose Mob Type")) {
            player.closeInventory();
            new MobTypeSelectionGUI(main).openGUI(player);

        } else if (itemName.equals(ChatColor.GOLD + "Set Size")) {
            player.closeInventory();
            player.sendMessage(main.getPluginPrefix() + ChatColor.YELLOW + "Enter mob size (1-10):");
            awaitingInput.put(player.getUniqueId(), CreatorInputState.SIZE);

        } else if (itemName.equals(ChatColor.GOLD + "Set Aggression")) {
            player.closeInventory();
            new AggressionSelectionGUI(main).openGUI(player);

        } else if (itemName.equals(ChatColor.GOLD + "Set Health")) {
            player.closeInventory();
            player.sendMessage(main.getPluginPrefix() + ChatColor.YELLOW + "Enter boss health (1-10000):");
            awaitingInput.put(player.getUniqueId(), CreatorInputState.HEALTH);

        } else if (itemName.equals(ChatColor.AQUA + "Particle Effects")) {
            player.closeInventory();
            new ParticleSelectionGUI(main).openGUI(player);

        } else if (itemName.equals(ChatColor.AQUA + "Custom Model Data")) {
            player.closeInventory();
            player.sendMessage(main.getPluginPrefix() + ChatColor.YELLOW + "Custom model data configuration coming soon!");

        } else if (itemName.contains("Helmet") || itemName.contains("Chestplate") ||
                itemName.contains("Leggings") || itemName.contains("Boots") ||
                itemName.contains("Main Hand") || itemName.contains("Off Hand")) {
            player.closeInventory();
            player.sendMessage(main.getPluginPrefix() + ChatColor.YELLOW + "Hold the item you want to set and type 'set' in chat");
            player.sendMessage(main.getPluginPrefix() + ChatColor.GRAY + "Type 'cancel' to go back");

            if (itemName.contains("Helmet")) {
                awaitingInput.put(player.getUniqueId(), CreatorInputState.HELMET);
            } else if (itemName.contains("Chestplate")) {
                awaitingInput.put(player.getUniqueId(), CreatorInputState.CHESTPLATE);
            } else if (itemName.contains("Leggings")) {
                awaitingInput.put(player.getUniqueId(), CreatorInputState.LEGGINGS);
            } else if (itemName.contains("Boots")) {
                awaitingInput.put(player.getUniqueId(), CreatorInputState.BOOTS);
            } else if (itemName.contains("Main Hand")) {
                awaitingInput.put(player.getUniqueId(), CreatorInputState.MAINHAND);
            } else if (itemName.contains("Off Hand")) {
                awaitingInput.put(player.getUniqueId(), CreatorInputState.OFFHAND);
            }

        } else if (itemName.equals(ChatColor.LIGHT_PURPLE + "Special Attacks")) {
            player.closeInventory();
            new SpecialAttacksSelectionGUI(main).openGUI(player);

        } else if (itemName.equals(ChatColor.LIGHT_PURPLE + "Minion Spawning")) {
            player.closeInventory();
            player.sendMessage(main.getPluginPrefix() + ChatColor.YELLOW + "Minion spawning configuration coming soon!");

        } else if (itemName.equals(ChatColor.LIGHT_PURPLE + "Potion Effects")) {
            player.closeInventory();
            player.sendMessage(main.getPluginPrefix() + ChatColor.YELLOW + "Potion effects configuration coming soon!");

        } else if (itemName.equals(ChatColor.YELLOW + "Natural Spawning")) {
            player.closeInventory();
            new NaturalSpawnSettingsGUI(main).openGUI(player);

        } else if (itemName.equals(ChatColor.GOLD + "Death Drops")) {
            player.closeInventory();
            player.sendMessage(main.getPluginPrefix() + ChatColor.YELLOW + "Death drops configuration coming soon!");

        } else if (itemName.contains("SAVE BOSS")) {
            player.closeInventory();
            saveBoss(player);

        } else if (itemName.contains("CANCEL")) {
            player.closeInventory();
            CustomBossCreatorGUI.removeBuildSession(player.getUniqueId());
            player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Boss creation cancelled.");
        }
    }

    private void handleManagerGUI(InventoryClickEvent event, Player player) {
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
            return;
        }

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        // Back button
        if (itemName.equals(ChatColor.YELLOW + "Back")) {
            player.closeInventory();
            player.performCommand("morebosses");
            return;
        }

        // Create new boss button
        if (itemName.equals(ChatColor.GREEN + "Create New Boss")) {
            player.closeInventory();
            new CustomBossCreatorGUI(main).openCreatorGUI(player);
            return;
        }

        // Get boss name from clicked item (strip color codes)
        String bossName = ChatColor.stripColor(itemName);

        // Left click = Spawn boss
        if (event.getClick() == ClickType.LEFT) {
            CustomBossCreatorGUI.CustomBossBuilder builder = CustomBossManagerGUI.loadBoss(bossName);
            if (builder != null) {
                CustomBossManagerGUI.spawnCustomBoss(builder, player.getLocation(), main);
                player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Spawned boss: " + builder.getBossName());
            } else {
                player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Failed to spawn boss!");
            }
        }
        // Right click = Edit boss
        else if (event.getClick() == ClickType.RIGHT) {
            player.closeInventory();
            new CustomBossManagerGUI(main).openEditGUI(player, bossName);
        }
    }

    private void handleEditBossGUI(InventoryClickEvent event, Player player) {
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
            return;
        }

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
        String title = event.getView().getTitle();
        String bossName = title.replace("Edit Boss: ", "");

        // Back button
        if (itemName.equals(ChatColor.YELLOW + "Back to Manager")) {
            player.closeInventory();
            new BukkitRunnable() {
                @Override
                public void run() {
                    new CustomBossManagerGUI(main).openManagerGUI(player);
                }
            }.runTaskLater(main, 2L);
            return;
        }

        // Delete button
        if (itemName.equals(ChatColor.RED + "" + ChatColor.BOLD + "DELETE BOSS")) {
            if (CustomBossManagerGUI.deleteBoss(bossName)) {
                player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Boss '" + bossName + "' deleted successfully!");
                player.closeInventory();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        new CustomBossManagerGUI(main).openManagerGUI(player);
                    }
                }.runTaskLater(main, 2L);
            } else {
                player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Failed to delete boss!");
            }
            return;
        }

        // Edit button - load boss into builder and open creator
        if (itemName.equals(ChatColor.GOLD + "" + ChatColor.BOLD + "EDIT SETTINGS")) {
            CustomBossCreatorGUI.CustomBossBuilder builder = CustomBossManagerGUI.loadBoss(bossName);
            if (builder != null) {
                // Put builder into active session
                CustomBossCreatorGUI.setEditSession(player.getUniqueId(), builder, bossName);
                player.closeInventory();
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        new CustomBossCreatorGUI(main).openCreatorGUI(player);
                    }
                }.runTaskLater(main, 2L);
                player.sendMessage(main.getPluginPrefix() + ChatColor.YELLOW + "Now editing: " + builder.getBossName());
            } else {
                player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Failed to load boss!");
            }
        }
    }

    private void handleMobTypeSelection(InventoryClickEvent event, Player player) {
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
            return;
        }

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        if (itemName.equals(ChatColor.YELLOW + "Back to Creator")) {
            player.closeInventory();
            new CustomBossCreatorGUI(main).openCreatorGUI(player);
            return;
        }

        // Get builder
        CustomBossCreatorGUI.CustomBossBuilder builder = CustomBossCreatorGUI.getBuildSession(player.getUniqueId());
        if (builder == null) {
            player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Error: No active build session.");
            player.closeInventory();
            return;
        }

        // Parse mob type from item name (remove color codes)
        String mobName = ChatColor.stripColor(itemName).toUpperCase().replace(" ", "_");
        try {
            EntityType type = EntityType.valueOf(mobName);
            builder.setEntityType(type);
            player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Mob type set to: " + ChatColor.stripColor(itemName));
            player.closeInventory();

            // Reopen creator GUI after a short delay
            new BukkitRunnable() {
                @Override
                public void run() {
                    new CustomBossCreatorGUI(main).openCreatorGUI(player);
                }
            }.runTaskLater(main, 2L);

        } catch (IllegalArgumentException e) {
            player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Invalid mob type!");
        }
    }

    private void handleParticleSelection(InventoryClickEvent event, Player player) {
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
            return;
        }

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        if (itemName.equals(ChatColor.YELLOW + "Back to Creator")) {
            player.closeInventory();
            new CustomBossCreatorGUI(main).openCreatorGUI(player);
            return;
        }

        CustomBossCreatorGUI.CustomBossBuilder builder = CustomBossCreatorGUI.getBuildSession(player.getUniqueId());
        if (builder == null) {
            player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Error: No active build session.");
            player.closeInventory();
            return;
        }

        if (itemName.equals(ChatColor.RED + "No Particles")) {
            builder.setParticleEffect(null);
            player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Particle effects removed!");
        } else {
            // Extract particle type from lore
            if (event.getCurrentItem().getItemMeta().hasLore()) {
                String lore = event.getCurrentItem().getItemMeta().getLore().get(0);
                String particleName = ChatColor.stripColor(lore).replace("Particle: ", "");
                try {
                    Particle particle = Particle.valueOf(particleName);
                    builder.setParticleEffect(particle);
                    player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Particle effect set to: " + ChatColor.stripColor(itemName));
                } catch (IllegalArgumentException e) {
                    player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Invalid particle type!");
                }
            }
        }

        player.closeInventory();
        new BukkitRunnable() {
            @Override
            public void run() {
                new CustomBossCreatorGUI(main).openCreatorGUI(player);
            }
        }.runTaskLater(main, 2L);
    }

    private void handleSpecialAttacksSelection(InventoryClickEvent event, Player player) {
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
            return;
        }

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        if (itemName.equals(ChatColor.YELLOW + "Back to Creator")) {
            player.closeInventory();
            new CustomBossCreatorGUI(main).openCreatorGUI(player);
            return;
        }

        CustomBossCreatorGUI.CustomBossBuilder builder = CustomBossCreatorGUI.getBuildSession(player.getUniqueId());
        if (builder == null) {
            player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Error: No active build session.");
            player.closeInventory();
            return;
        }

        // First check preset attacks
        boolean foundAttack = false;
        for (Map.Entry<String, BossSpecialAttacks.SpecialAttack> entry : BossSpecialAttacks.getAllAttacks().entrySet()) {
            if (entry.getValue().getDisplayName().equals(itemName)) {
                String attackId = entry.getKey();

                // Toggle attack (add if not present, remove if present)
                if (builder.getSpecialAttacks().contains(attackId)) {
                    builder.removeSpecialAttack(attackId);
                    player.sendMessage(main.getPluginPrefix() + ChatColor.YELLOW + "Removed attack: " + ChatColor.stripColor(itemName));
                } else {
                    builder.addSpecialAttack(attackId);
                    player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Added attack: " + ChatColor.stripColor(itemName));
                }
                foundAttack = true;
                break;
            }
        }

        // If not found in preset attacks, check custom attacks
        if (!foundAttack) {
            for (Map.Entry<String, CustomAttackManager.CustomAttack> entry : CustomAttackManager.getCustomAttacks().entrySet()) {
                if (entry.getValue().getDisplayName().equals(itemName)) {
                    String attackId = entry.getKey();

                    // Toggle attack (add if not present, remove if present)
                    if (builder.getSpecialAttacks().contains(attackId)) {
                        builder.removeSpecialAttack(attackId);
                        player.sendMessage(main.getPluginPrefix() + ChatColor.YELLOW + "Removed custom attack: " + ChatColor.stripColor(itemName));
                    } else {
                        builder.addSpecialAttack(attackId);
                        player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Added custom attack: " + ChatColor.stripColor(itemName));
                    }
                    break;
                }
            }
        }

        // Don't close inventory, let player select multiple attacks
    }

    private void handleAggressionSelection(InventoryClickEvent event, Player player) {
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
            return;
        }

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        if (itemName.equals(ChatColor.YELLOW + "Back to Creator")) {
            player.closeInventory();
            new CustomBossCreatorGUI(main).openCreatorGUI(player);
            return;
        }

        CustomBossCreatorGUI.CustomBossBuilder builder = CustomBossCreatorGUI.getBuildSession(player.getUniqueId());
        if (builder == null) {
            player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Error: No active build session.");
            player.closeInventory();
            return;
        }

        AggressionType selectedType = null;

        if (itemName.equals(ChatColor.GREEN + "Passive (Until Hit)")) {
            selectedType = AggressionType.PASSIVE;
        } else if (itemName.equals(ChatColor.RED + "Aggressive")) {
            selectedType = AggressionType.AGGRESSIVE;
        } else if (itemName.equals(ChatColor.GRAY + "No AI")) {
            selectedType = AggressionType.NO_AI;
        }

        if (selectedType != null) {
            builder.setAggressionType(selectedType);
            player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Aggression set to: " + selectedType.getColoredName());

            player.closeInventory();
            new BukkitRunnable() {
                @Override
                public void run() {
                    new CustomBossCreatorGUI(main).openCreatorGUI(player);
                }
            }.runTaskLater(main, 2L);
        }
    }

    private void handleNaturalSpawnSettings(InventoryClickEvent event, Player player) {
        if (event.getCurrentItem() == null || !event.getCurrentItem().hasItemMeta()) {
            return;
        }

        String itemName = event.getCurrentItem().getItemMeta().getDisplayName();

        if (itemName.equals(ChatColor.YELLOW + "Back to Creator")) {
            player.closeInventory();
            new CustomBossCreatorGUI(main).openCreatorGUI(player);
            return;
        }

        CustomBossCreatorGUI.CustomBossBuilder builder = CustomBossCreatorGUI.getBuildSession(player.getUniqueId());
        if (builder == null) {
            player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Error: No active build session.");
            player.closeInventory();
            return;
        }

        if (itemName.contains("Natural Spawning:")) {
            // Toggle natural spawning
            builder.setNaturalSpawning(!builder.isNaturalSpawning());
            player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Natural spawning " +
                    (builder.isNaturalSpawning() ? "enabled" : "disabled"));

            // Refresh GUI
            player.closeInventory();
            new BukkitRunnable() {
                @Override
                public void run() {
                    new NaturalSpawnSettingsGUI(main).openGUI(player);
                }
            }.runTaskLater(main, 2L);

        } else if (itemName.equals(ChatColor.GOLD + "Spawn Rarity")) {
            player.closeInventory();
            player.sendMessage(main.getPluginPrefix() + ChatColor.YELLOW + "Enter spawn rarity (1-10000):");
            player.sendMessage(main.getPluginPrefix() + ChatColor.GRAY + "Higher = more rare");
            awaitingInput.put(player.getUniqueId(), CreatorInputState.SPAWN_RARITY);

        } else if (itemName.equals(ChatColor.GOLD + "Spawn Worlds")) {
            player.closeInventory();
            player.sendMessage(main.getPluginPrefix() + ChatColor.YELLOW + "World selection coming soon!");
            new BukkitRunnable() {
                @Override
                public void run() {
                    new NaturalSpawnSettingsGUI(main).openGUI(player);
                }
            }.runTaskLater(main, 20L);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        if (!awaitingInput.containsKey(playerUUID)) return;

        event.setCancelled(true);
        String input = event.getMessage();
        CreatorInputState state = awaitingInput.get(playerUUID);

        CustomBossCreatorGUI.CustomBossBuilder builder = CustomBossCreatorGUI.getBuildSession(playerUUID);
        if (builder == null) {
            player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Error: No active build session found.");
            awaitingInput.remove(playerUUID);
            return;
        }

        switch (state) {
            case NAME:
                builder.setBossName(ChatColor.translateAlternateColorCodes('&', input));
                player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Boss name set to: " + builder.getBossName());
                awaitingInput.remove(playerUUID);

                // Reopen creator GUI on main thread with delay
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        new CustomBossCreatorGUI(main).openCreatorGUI(player);
                    }
                }.runTaskLater(main, 2L);
                break;

            case SIZE:
                try {
                    int size = Integer.parseInt(input);
                    if (size < 1 || size > 10) {
                        player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Size must be between 1 and 10!");
                        return;
                    }
                    builder.setSize(size);
                    player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Boss size set to: " + size);
                    awaitingInput.remove(playerUUID);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            new CustomBossCreatorGUI(main).openCreatorGUI(player);
                        }
                    }.runTaskLater(main, 2L);
                } catch (NumberFormatException e) {
                    player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Invalid number! Please enter a number between 1 and 10.");
                }
                break;

            case HEALTH:
                try {
                    double health = Double.parseDouble(input);
                    if (health < 1 || health > 10000) {
                        player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Health must be between 1 and 10000!");
                        return;
                    }
                    builder.setHealth(health);
                    player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Boss health set to: " + health);
                    awaitingInput.remove(playerUUID);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            new CustomBossCreatorGUI(main).openCreatorGUI(player);
                        }
                    }.runTaskLater(main, 2L);
                } catch (NumberFormatException e) {
                    player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Invalid number! Please enter a number between 1 and 10000.");
                }
                break;

            case SPAWN_RARITY:
                try {
                    int rarity = Integer.parseInt(input);
                    if (rarity < 1 || rarity > 10000) {
                        player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Rarity must be between 1 and 10000!");
                        return;
                    }
                    builder.setSpawnRarity(rarity);
                    player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Spawn rarity set to: 1 in " + rarity);
                    awaitingInput.remove(playerUUID);

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            new NaturalSpawnSettingsGUI(main).openGUI(player);
                        }
                    }.runTaskLater(main, 2L);
                } catch (NumberFormatException e) {
                    player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Invalid number! Please enter a number between 1 and 10000.");
                }
                break;

            case HELMET:
            case CHESTPLATE:
            case LEGGINGS:
            case BOOTS:
            case MAINHAND:
            case OFFHAND:
                if (input.equalsIgnoreCase("cancel")) {
                    awaitingInput.remove(playerUUID);
                    player.sendMessage(main.getPluginPrefix() + ChatColor.YELLOW + "Cancelled.");

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            new CustomBossCreatorGUI(main).openCreatorGUI(player);
                        }
                    }.runTaskLater(main, 2L);
                    return;
                }

                if (input.equalsIgnoreCase("set")) {
                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            org.bukkit.inventory.ItemStack heldItem = player.getInventory().getItemInMainHand();
                            if (heldItem == null || heldItem.getType() == org.bukkit.Material.AIR) {
                                player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "You must be holding an item!");
                                return;
                            }

                            switch (state) {
                                case HELMET:
                                    builder.setHelmet(heldItem.clone());
                                    player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Helmet set!");
                                    break;
                                case CHESTPLATE:
                                    builder.setChestplate(heldItem.clone());
                                    player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Chestplate set!");
                                    break;
                                case LEGGINGS:
                                    builder.setLeggings(heldItem.clone());
                                    player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Leggings set!");
                                    break;
                                case BOOTS:
                                    builder.setBoots(heldItem.clone());
                                    player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Boots set!");
                                    break;
                                case MAINHAND:
                                    builder.setMainHand(heldItem.clone());
                                    player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Main hand item set!");
                                    break;
                                case OFFHAND:
                                    builder.setOffHand(heldItem.clone());
                                    player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Off hand item set!");
                                    break;
                            }

                            awaitingInput.remove(playerUUID);
                            new CustomBossCreatorGUI(main).openCreatorGUI(player);
                        }
                    }.runTask(main);
                }
                break;
        }
    }

    private void saveBoss(Player player) {
        CustomBossCreatorGUI.CustomBossBuilder builder = CustomBossCreatorGUI.getBuildSession(player.getUniqueId());
        if (builder == null) {
            player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Error: No active build session found.");
            return;
        }

        // Check if this is an edit session
        String editingBossName = CustomBossCreatorGUI.getEditingBossName(player.getUniqueId());

        if (editingBossName != null) {
            // Update existing boss
            if (CustomBossManagerGUI.saveBoss(builder, editingBossName)) {
                player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Boss '" + builder.getBossName() + "' updated successfully!");
            } else {
                player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Failed to update boss!");
            }
            CustomBossCreatorGUI.clearEditSession(player.getUniqueId());
        } else {
            // Save new boss
            if (CustomBossManagerGUI.saveBoss(builder, null)) {
                player.sendMessage(main.getPluginPrefix() + ChatColor.GREEN + "Boss '" + builder.getBossName() + "' saved successfully!");
            } else {
                player.sendMessage(main.getPluginPrefix() + ChatColor.RED + "Failed to save boss!");
            }
        }

        player.sendMessage(main.getPluginPrefix() + ChatColor.GRAY + "Type: " + builder.getEntityType().name());
        player.sendMessage(main.getPluginPrefix() + ChatColor.GRAY + "Health: " + builder.getHealth());

        CustomBossCreatorGUI.removeBuildSession(player.getUniqueId());
    }

    // Enum to track what input we're waiting for
    enum CreatorInputState {
        NAME,
        SIZE,
        HEALTH,
        SPAWN_RARITY,
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS,
        MAINHAND,
        OFFHAND
    }
}