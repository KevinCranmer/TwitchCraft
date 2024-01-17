package me.crazycranberry.twitchcraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.crazycranberry.twitchcraft.TwitchCraft.getPlugin;
import static me.crazycranberry.twitchcraft.twitch.websocket.TwitchClient.WEBSOCKET_CONNECTION_URL;

public class ReconnectToTwitchCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("TwitchCraftReconnect")) {
            getPlugin().reconnectToTwitch(WEBSOCKET_CONNECTION_URL);
            if (sender instanceof Player) {
                Player p = (Player) sender;
                p.sendMessage("Attempting to reconnect to Twitch WebSocket");
            }
        }
        return true;
    }
}
