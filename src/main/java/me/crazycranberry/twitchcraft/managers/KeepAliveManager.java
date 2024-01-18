package me.crazycranberry.twitchcraft.managers;

import me.crazycranberry.twitchcraft.events.ReconnectRequestedEvent;
import me.crazycranberry.twitchcraft.events.WebSocketConnectedEvent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.time.Duration;
import java.time.Instant;

import static me.crazycranberry.twitchcraft.TwitchCraft.getPlugin;
import static me.crazycranberry.twitchcraft.TwitchCraft.logger;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.TICKS_PER_SECOND;
import static me.crazycranberry.twitchcraft.twitch.websocket.TwitchClient.KEEP_ALIVE_SECONDS;

/** A dedicated class that makes sure the WebSocket connection has been kept alive. And Attempts to reconnect otherwise. */
public class KeepAliveManager implements Listener {
    private Integer taskId;

    @EventHandler
    private void onRefreshTokenSuccessful(WebSocketConnectedEvent event) {
        if (taskId != null) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
        taskId = Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
            if (getPlugin().isClientNull()) {
                logger().info("The client is null. Turning off the KeepAliveManager");
                if (taskId != null) {
                    Bukkit.getScheduler().cancelTask(taskId);
                }
                return;
            }
            Duration duration = Duration.between(getPlugin().timeOfLastTwitchMessage(), Instant.now());
            if (duration.getSeconds() > KEEP_ALIVE_SECONDS) {
                logger().warning("The Websocket connection has timed out. Attempting to reconnect.");
                Bukkit.getPluginManager().callEvent(new ReconnectRequestedEvent());
            }
        }, KEEP_ALIVE_SECONDS /*<-- the initial delay */, TICKS_PER_SECOND * KEEP_ALIVE_SECONDS /*<-- the interval */).getTaskId();
    }
}
