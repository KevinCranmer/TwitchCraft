package me.crazycranberry.streamcraft.actions.rotatinghotbar;

import me.crazycranberry.streamcraft.actions.Executor;
import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

import static me.crazycranberry.streamcraft.StreamCraft.getPlugin;
import static me.crazycranberry.streamcraft.StreamCraft.logger;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.triggerer;

public class RotatingHotbarExecutor implements Executor {
    Map<Player, Integer> rotationsLeft = new HashMap<>();

    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof RotatingHotbar)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        RotatingHotbar rh = (RotatingHotbar) action;
        for (Player p : getTargetedPlayers(rh)) {
            maybeSendPlayerMessage(p, twitchMessage, String.format("Rotating Hotbar!%s Courtesy of %s%s", ChatColor.GOLD, triggerer(twitchMessage, action), ChatColor.RESET), action);
            rotationsLeft.put(p, rh.getNumRotations());
            rotateHotbar(p, rh);
        }
    }

    private void rotateHotbar(Player p, RotatingHotbar rh) {
        ItemStack[] contents = p.getInventory().getContents();
        ItemStack lastItem = contents[8];
        for (int i = 0; i < 9; i++) {
            p.getInventory().setItem(i + 1, contents[i]);
        }
        p.getInventory().setItem(0, lastItem);
        if (rotationsLeft.get(p) > 0) {
            rotationsLeft.put(p, rotationsLeft.get(p) - 1);
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            Bukkit.getServer().getScheduler().callSyncMethod(getPlugin(), () -> {
                                rotateHotbar(p, rh);
                                return true;
                            });
                        }
                    },
                    1000 * rh.getSecondsBetweenRotations()
            );
        }
    }
}
