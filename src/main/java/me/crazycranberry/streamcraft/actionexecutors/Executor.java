package me.crazycranberry.streamcraft.actionexecutors;

import me.crazycranberry.streamcraft.config.model.Action;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;

public interface Executor {
    void execute(Message twitchMessage, Action action);
}
