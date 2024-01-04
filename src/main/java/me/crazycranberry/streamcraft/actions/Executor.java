package me.crazycranberry.streamcraft.actions;

import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;

public interface Executor {
    void execute(Message twitchMessage, Action action);
}
