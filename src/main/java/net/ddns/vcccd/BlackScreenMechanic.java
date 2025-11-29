package net.ddns.vcccd;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

/**
 * Black Screen Mechanic
 * Blacks out the player's screen using blindness effect
 *
 * Parameters:
 * - duration: Duration in ticks (default: 40)
 */
public class BlackScreenMechanic extends AbstractMechanic {

    public BlackScreenMechanic(Main main) {
        super(main, "blackscreen", "Blacks out player's screen");
    }

    @Override
    public void execute(LivingEntity caster, LivingEntity target, Map<String, Object> parameters) {
        int duration = getInt(parameters, "duration", 40);

        if (target instanceof Player) {
            Player player = (Player) target;
            player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, duration, 1, false, false));
        }
    }

    @Override
    public Map<String, Object> getDefaultParameters() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("duration", 40);
        return defaults;
    }
}