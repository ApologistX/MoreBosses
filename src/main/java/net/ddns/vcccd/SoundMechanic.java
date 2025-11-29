package net.ddns.vcccd;

import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * Sound Mechanic
 * Plays a sound at the target's location
 *
 * Parameters:
 * - sound: Sound name (required)
 * - volume: Volume (default: 1.0)
 * - pitch: Pitch (default: 1.0)
 */
public class SoundMechanic extends AbstractMechanic {

    public SoundMechanic(Main main) {
        super(main, "sound", "Plays a sound");
    }

    @Override
    public void execute(LivingEntity caster, LivingEntity target, Map<String, Object> parameters) {
        String soundName = getString(parameters, "sound", null);

        if (soundName == null) {
            return;
        }

        float volume = (float) getDouble(parameters, "volume", 1.0);
        float pitch = (float) getDouble(parameters, "pitch", 1.0);

        try {
            Sound sound = Sound.valueOf(soundName.toUpperCase());
            target.getWorld().playSound(target.getLocation(), sound, volume, pitch);
        } catch (IllegalArgumentException e) {
            main.getConsole().sendMessage(main.getPluginPrefix() +
                    org.bukkit.ChatColor.YELLOW + "Invalid sound: " + soundName);
        }
    }

    @Override
    public Map<String, Object> getDefaultParameters() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("sound", "");
        defaults.put("volume", 1.0);
        defaults.put("pitch", 1.0);
        return defaults;
    }
}