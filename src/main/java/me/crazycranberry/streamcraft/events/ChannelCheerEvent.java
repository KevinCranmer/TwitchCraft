package me.crazycranberry.streamcraft.events;

import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ChannelCheerEvent extends ActionEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    public ChannelCheerEvent(Message twitchMessage) {
        super(twitchMessage);
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
