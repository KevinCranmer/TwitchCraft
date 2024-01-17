package me.crazycranberry.twitchcraft.events;

import me.crazycranberry.twitchcraft.twitch.websocket.model.message.Message;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ActionEvent extends Event {
    private Message twitchMessage;

    public ActionEvent(Message twitchMessage) {
        this.twitchMessage = twitchMessage;
    }

    public Message twitchMessage() {
        return twitchMessage;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return null;
    }
}
