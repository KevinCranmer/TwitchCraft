package me.crazycranberry.twitchcraft.events;

import me.crazycranberry.twitchcraft.twitch.websocket.model.message.Message;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ChannelFollowEvent extends ActionEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    public ChannelFollowEvent(Message twitchMessage) {
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
