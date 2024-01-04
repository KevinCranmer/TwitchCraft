package me.crazycranberry.streamcraft.actions;

import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

import static me.crazycranberry.streamcraft.StreamCraft.getPlugin;

public class ExecutorUtils {
    public static final long TICKS_PER_SECOND = 20L;

    public static List<Player> getTargetedPlayers(Action action) {
        List<String> targets = Arrays.asList(action.getTarget().split(","));
        return Bukkit.getOnlinePlayers().stream().filter(p -> targets.contains(p.getName()) || targets.contains("*")).map(OfflinePlayer::getPlayer).toList();
    }

    public static String triggerer(Message twitchMessage, Action action) {
        String userThatTriggered = twitchMessage.getPayload().getEvent().getUser_name();
        return userThatTriggered == null ? "a channel poll" : userThatTriggered + " triggering " + action.getTrigger().getType();
    }

    public static <T> T randomFromList(List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        int randomIndex = (int) (Math.random() * list.size());
        return list.get(randomIndex);
    }

    public static void maybeSendPlayerMessage(Player p, String message) {
        if (getPlugin().config().isSendMessageOnEvent()) {
            p.sendMessage(message);
        }
    }
}
