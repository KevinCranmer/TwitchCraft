package me.crazycranberry.streamcraft.managers;

import me.crazycranberry.streamcraft.actions.Executor;
import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.config.ActionType;
import me.crazycranberry.streamcraft.config.TriggerType;
import me.crazycranberry.streamcraft.events.ChannelFollowEvent;
import me.crazycranberry.streamcraft.events.PollEndEvent;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.MessagePollChoice;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static me.crazycranberry.streamcraft.StreamCraft.getPlugin;
import static me.crazycranberry.streamcraft.StreamCraft.logger;

public class ActionManager implements Listener {
    @EventHandler
    private void onFollowTrigger(ChannelFollowEvent event) {
        getPlugin().config().getActions().stream()
                .filter(a -> a.getTrigger().getType().equals(TriggerType.CHANNEL_FOLLOW))
                .forEach(a -> executeAction(event.twitchMessage(), a));
    }
    @EventHandler
    private void onPollEndTrigger(PollEndEvent event) {
        if (!event.twitchMessage().getPayload().getEvent().getStatus().equals("completed")) {
            return;
        }
        Optional<MessagePollChoice> winningPollChoice = event.twitchMessage().getPayload().getEvent().getChoices().stream().max(Comparator.comparingInt(MessagePollChoice::getVotes));
        if (winningPollChoice.isEmpty()) {
            return;
        }
        Optional<Action> winningAction = getPlugin().config().getActions()
                .stream()
                .filter(a -> a.pollMessage().equals(winningPollChoice.get().getTitle()))
                .findFirst();
        if (winningAction.isEmpty()) {
            logger().warning("Somehow the action for \"" + winningPollChoice.get().getTitle() + "\" could not be found.");
            return;
        }
        executeAction(event.twitchMessage(), winningAction.get());
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
