package me.crazycranberry.streamcraft.managers;

import me.crazycranberry.streamcraft.events.ReconnectRequestedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static me.crazycranberry.streamcraft.StreamCraft.getPlugin;

public class ReconnectRequestedManager implements Listener {
    @EventHandler
    private void onReconnectRequested(ReconnectRequestedEvent event) {
        getPlugin().reconnectToTwitch(event.getConnectionUrl());
    }
}
