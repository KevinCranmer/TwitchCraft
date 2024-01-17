package me.crazycranberry.twitchcraft.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

import static me.crazycranberry.twitchcraft.twitch.websocket.TwitchClient.WEBSOCKET_CONNECTION_URL;

public class ReconnectRequestedEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    private final String connectionUrl;

    public ReconnectRequestedEvent() {
        this.connectionUrl = WEBSOCKET_CONNECTION_URL;
    }

    public ReconnectRequestedEvent(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public String getConnectionUrl() {
        return connectionUrl;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
