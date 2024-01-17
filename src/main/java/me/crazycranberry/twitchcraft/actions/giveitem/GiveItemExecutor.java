package me.crazycranberry.twitchcraft.actions.giveitem;

import me.crazycranberry.twitchcraft.actions.Executor;
import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.twitch.websocket.model.message.Message;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.crazycranberry.twitchcraft.TwitchCraft.logger;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.triggerer;

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
