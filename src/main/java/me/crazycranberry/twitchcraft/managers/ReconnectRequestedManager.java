package me.crazycranberry.twitchcraft.managers;

import me.crazycranberry.twitchcraft.events.ReconnectRequestedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static me.crazycranberry.twitchcraft.TwitchCraft.getPlugin;

public class ReconnectRequestedManager implements Listener {
    @EventHandler
    private void onReconnectRequested(ReconnectRequestedEvent event) {
        getPlugin().reconnectToTwitch(event.getConnectionUrl());
    }
}
