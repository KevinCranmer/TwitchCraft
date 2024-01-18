package me.crazycranberry.twitchcraft.actions.randomitemremoval;

import me.crazycranberry.twitchcraft.actions.Executor;
import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.twitch.websocket.model.message.Message;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.crazycranberry.twitchcraft.TwitchCraft.logger;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.triggerer;

public class RandomItemRemovalExecutor implements Executor {
    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof RandomItemRemoval)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        RandomItemRemoval rir = (RandomItemRemoval) action;
        for (Player p : getTargetedPlayers(rir)) {
            Map<String, Integer> itemsBeingRemoved = new HashMap<>();
            List<Integer> potentialItemIndices = new ArrayList<>();
            for (int i = 0; i < p.getInventory().getContents().length; i++) {
                if (p.getInventory().getContents()[i] != null) {
                    potentialItemIndices.add(i);
                }
            }
            for (int j = 0; j < rir.getNumStacks(); j++) {
                int randomIndex = (int) (Math.random() * potentialItemIndices.size());
                int indexToRemove = potentialItemIndices.get(randomIndex);
                ItemStack itemToRemove = p.getInventory().getItem(indexToRemove);
                itemsBeingRemoved.put(itemToRemove.getType().name(), Math.min(itemToRemove.getAmount(), rir.getNumPerStack()));
                itemToRemove.setAmount(Math.max(itemToRemove.getAmount() - rir.getNumPerStack(), 0));
                p.getInventory().setItem(indexToRemove, itemToRemove);
                potentialItemIndices.remove(randomIndex);
            }
            sendPlayerMessage(p, twitchMessage, rir, itemsBeingRemoved);
        }
    }

    private void sendPlayerMessage(Player p, Message twitchMessage, RandomItemRemoval action, Map<String, Integer> itemsBeingRemoved) {
        String itemsBeingRemovedStr = itemsBeingRemoved.entrySet()
                .stream()
                .map(e -> String.format("%s %s%s", e.getValue(), e.getKey(), e.getValue() > 1 ? "s" : ""))
                .collect(Collectors.joining(", "));
        StringBuilder strb = new StringBuilder(itemsBeingRemovedStr);
        int lastIndexOfComma = strb.lastIndexOf(", ");
        itemsBeingRemovedStr = strb.replace(lastIndexOfComma, ", ".length() + lastIndexOfComma, " and ").toString();
        if (itemsBeingRemovedStr.isBlank()) {
            return;
        }
        maybeSendPlayerMessage(p, twitchMessage, String.format("Randomly removed %s%s%s, courtesy of %s%s%s",
                ChatColor.GOLD,
                itemsBeingRemovedStr,
                ChatColor.RESET,
                ChatColor.GOLD,
                triggerer(twitchMessage, action),
                ChatColor.RESET),
                action);
    }
}
