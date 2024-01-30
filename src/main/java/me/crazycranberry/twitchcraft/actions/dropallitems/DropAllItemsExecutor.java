package me.crazycranberry.twitchcraft.actions.dropallitems;

import me.crazycranberry.twitchcraft.actions.Executor;
import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.twitch.websocket.model.message.Message;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.List;

import static me.crazycranberry.twitchcraft.TwitchCraft.logger;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.getPossibleSpawnLocations;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.randomFromList;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.triggerer;

public class DropAllItemsExecutor implements Executor {
    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof DropAllItems)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        DropAllItems dai = (DropAllItems) action;
        for (Player p : getTargetedPlayers(dai)) {
            maybeSendPlayerMessage(p, twitchMessage, String.format("Oops, looks like you %sdropped%s something. Courtesy of %s%s%s",ChatColor.GOLD, ChatColor.RESET, ChatColor.GOLD, triggerer(twitchMessage, dai), ChatColor.RESET), action);
            List<Location> possibleLocations = getPossibleSpawnLocations(p, 5, DropAllItemsExecutor::isValidDropBlock);
            for (int i = 0; i < p.getInventory().getContents().length; i++) {
                if (p.getInventory().getContents()[i] != null) {
                    p.getWorld().dropItem(randomFromList(possibleLocations), p.getInventory().getContents()[i]);
                    p.getInventory().setItem(i, null);
                }
            }
        }
    }

    public static boolean isValidDropBlock(Location loc) {
        Block block = loc.getBlock();
        return block.getType().equals(Material.AIR) || block.getType().equals(Material.WATER);
    }
}
