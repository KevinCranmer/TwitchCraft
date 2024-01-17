package me.crazycranberry.streamcraft.actions.giveitem;

import me.crazycranberry.streamcraft.actions.Executor;
import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static me.crazycranberry.streamcraft.StreamCraft.logger;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.getPossibleSpawnLocations;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.randomFromList;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.triggerer;

public class GiveItemExecutor implements Executor {
    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof GiveItem)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        GiveItem gi = (GiveItem) action;
        for (Player p : getTargetedPlayers(gi)) {
            maybeSendPlayerMessage(p, twitchMessage, String.format("%s free %s%s%s%s, courtesy of %s%s%s", gi.getQuantity(), ChatColor.GOLD, gi.getItem().name().toLowerCase().replace("_", " "), gi.getQuantity() > 1 ? "'s" : "", ChatColor.RESET, ChatColor.GOLD, triggerer(twitchMessage, action), ChatColor.RESET), action);
            for (int i = gi.getQuantity() ; i >= 0; i = i - gi.getItem().getMaxStackSize()) {
                p.getInventory().addItem(new ItemStack(gi.getItem(), Math.min(gi.getItem().getMaxStackSize(), i)));
            }
        }
    }
}
