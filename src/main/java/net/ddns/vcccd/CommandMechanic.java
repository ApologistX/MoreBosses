package net.ddns.vcccd;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

/**
 * Command Mechanic
 * Executes a command
 *
 * Parameters:
 * - command: Command to execute (required)
 * - ascaster: Execute as caster (default: false)
 * - asop: Execute with OP permissions (default: false)
 * - astarget: Execute as target (default: false)
 */
public class CommandMechanic extends AbstractMechanic {

    public CommandMechanic(Main main) {
        super(main, "command", "Executes a command");
    }

    @Override
    public void execute(LivingEntity caster, LivingEntity target, Map<String, Object> parameters) {
        String command = getString(parameters, "command", null);

        if (command == null || command.isEmpty()) {
            return;
        }

        boolean asCaster = getBoolean(parameters, "ascaster", false);
        boolean asOp = getBoolean(parameters, "asop", false);
        boolean asTarget = getBoolean(parameters, "astarget", false);

        // Replace placeholders
        command = command.replace("<caster.name>", caster.getName());
        command = command.replace("<caster.uuid>", caster.getUniqueId().toString());

        if (target != null) {
            command = command.replace("<target.name>", target.getName());
            command = command.replace("<target.uuid>", target.getUniqueId().toString());
        }

        // Execute command
        if (asTarget && target instanceof Player) {
            Player player = (Player) target;
            boolean wasOp = player.isOp();

            if (asOp && !wasOp) {
                player.setOp(true);
            }

            player.performCommand(command);

            if (asOp && !wasOp) {
                player.setOp(false);
            }
        } else if (asCaster && caster instanceof Player) {
            Player player = (Player) caster;
            boolean wasOp = player.isOp();

            if (asOp && !wasOp) {
                player.setOp(true);
            }

            player.performCommand(command);

            if (asOp && !wasOp) {
                player.setOp(false);
            }
        } else {
            // Execute from console
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }
    }

    @Override
    public Map<String, Object> getDefaultParameters() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("command", "");
        defaults.put("ascaster", false);
        defaults.put("asop", false);
        defaults.put("astarget", false);
        return defaults;
    }
}