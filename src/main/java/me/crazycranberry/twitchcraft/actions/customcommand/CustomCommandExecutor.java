package me.crazycranberry.twitchcraft.actions.customcommand;

import me.crazycranberry.twitchcraft.actions.Executor;
import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.twitch.websocket.model.message.Message;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import static me.crazycranberry.twitchcraft.TwitchCraft.logger;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.triggerer;
import static org.bukkit.Bukkit.dispatchCommand;
import static org.bukkit.Bukkit.getServer;

public class CustomCommandExecutor implements Executor {
    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof CustomCommand)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        CustomCommand cc = (CustomCommand) action;
        for (Player p : getTargetedPlayers(cc)) {
            String command = cc.getCommand().replaceAll("\\{PLAYER}", p.getName())
                    .replaceAll("\\{PLAYER_LOCATION}", String.format("%s %s %s", p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ()));
            maybeSendPlayerMessage(p, twitchMessage, String.format("Running %s\"%s\", courtesy of %s%s", ChatColor.GOLD, command, triggerer(twitchMessage, action), ChatColor.RESET), action);
            dispatchCommand(getServer().getConsoleSender(), command);
        }
    }
}
