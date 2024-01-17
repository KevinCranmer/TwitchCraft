package me.crazycranberry.twitchcraft.actions;

import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.twitch.websocket.model.message.Message;

public interface Executor {
    void execute(Message twitchMessage, Action action);
}
