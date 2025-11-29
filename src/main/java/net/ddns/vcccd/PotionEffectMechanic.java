package net.ddns.vcccd;

import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Potion Effect Mechanic
 * Applies potion effects to target
 *
 * Parameters:
 * - effects: List of "EFFECT:duration:amplifier" strings (required)
 */
public class PotionEffectMechanic extends AbstractMechanic {

    public PotionEffectMechanic(Main main) {
        super(main, "potioneffect", "Applies potion effects to target");
    }

    @Override
    public void execute(LivingEntity caster, LivingEntity target, Map<String, Object> parameters) {
        Object effectsObj = parameters.get("effects");

        if (!(effectsObj instanceof List)) {
            return;
        }

        List<?> effectsList = (List<?>) effectsObj;

        for (Object effectObj : effectsList) {
            String effectStr = effectObj.toString();
            applyPotionEffect(target, effectStr);
        }
    }

    private void applyPotionEffect(LivingEntity target, String effectStr) {
        try {
            String[] parts = effectStr.split(":");
            if (parts.length >= 3) {
                PotionEffectType type = PotionEffectType.getByName(parts[0]);
                int duration = Integer.parseInt(parts[1]);
                int amplifier = Integer.parseInt(parts[2]);

                if (type != null) {
                    target.addPotionEffect(new PotionEffect(type, duration, amplifier));
                }
            }
        } catch (Exception e) {
            main.getConsole().sendMessage(main.getPluginPrefix() +
                    org.bukkit.ChatColor.YELLOW + "Invalid potion effect format: " + effectStr);
        }
    }

    @Override
    public Map<String, Object> getDefaultParameters() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("effects", new ArrayList<>());
        return defaults;
    }
}