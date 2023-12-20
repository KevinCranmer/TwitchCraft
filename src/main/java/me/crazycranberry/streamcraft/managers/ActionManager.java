package me.crazycranberry.streamcraft.managers;

import me.crazycranberry.streamcraft.actionexecutors.Executor;
import me.crazycranberry.streamcraft.config.model.Action;
import me.crazycranberry.streamcraft.config.model.ActionType;
import me.crazycranberry.streamcraft.config.model.TriggerType;
import me.crazycranberry.streamcraft.events.ChannelFollowEvent;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static me.crazycranberry.streamcraft.StreamCraft.getPlugin;
import static me.crazycranberry.streamcraft.StreamCraft.logger;

public class ActionManager implements Listener {
    @EventHandler
    private void onFollowTrigger(ChannelFollowEvent event) {
        List<Action> followActions = getPlugin().config().getActions().stream()
                .filter(a -> a.getTrigger().getType().equals(TriggerType.CHANNEL_FOLLOW))
                .toList();
        if (followActions.size() > 1) {
            logger().warning("Conflicting actions on follow trigger. Selecting the first action from this list: " + followActions);
        }
        if (followActions.size() > 0) {
            executeAction(event.twitchMessage(), followActions.get(0));
        }
    }

    private void executeAction(Message twitchMessage, Action action) {
        for (ActionType actionType : ActionType.values()) {
            if (actionType.equals(action.getType())) {
                try {
                    Constructor c = actionType.executor().getConstructor();
                    c.setAccessible(true);
                    Executor instance = (Executor) c.newInstance();
                    instance.execute(twitchMessage, action);
                } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
