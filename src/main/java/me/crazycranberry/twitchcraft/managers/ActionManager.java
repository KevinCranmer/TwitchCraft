package me.crazycranberry.twitchcraft.managers;

import me.crazycranberry.twitchcraft.actions.Executor;
import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.config.ActionType;
import me.crazycranberry.twitchcraft.config.TriggerType;
import me.crazycranberry.twitchcraft.events.ChannelCheerEvent;
import me.crazycranberry.twitchcraft.events.ChannelFollowEvent;
import me.crazycranberry.twitchcraft.events.ChannelResubscribeEvent;
import me.crazycranberry.twitchcraft.events.ChannelSubscribeEvent;
import me.crazycranberry.twitchcraft.events.ChannelSubscriptionGiftEvent;
import me.crazycranberry.twitchcraft.events.PollEndEvent;
import me.crazycranberry.twitchcraft.twitch.websocket.model.message.Message;
import me.crazycranberry.twitchcraft.twitch.websocket.model.message.MessagePollChoice;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static me.crazycranberry.twitchcraft.TwitchCraft.getPlugin;
import static me.crazycranberry.twitchcraft.TwitchCraft.logger;

public class ActionManager implements Listener {
    private Set<String> usersWhoveAlreadyFollowed = new HashSet<>();

    @EventHandler
    private void onFollowTrigger(ChannelFollowEvent event) {
        String username = event.twitchMessage().getPayload().getEvent().getUser_name();
        if (!getPlugin().config().isFollowAllowRepeats() && usersWhoveAlreadyFollowed.contains(username)) {
            return;
        }
        usersWhoveAlreadyFollowed.add(username);
        getPlugin().config().getActions().stream()
                .filter(a -> a.getTrigger().getType().equals(TriggerType.CHANNEL_FOLLOW))
                .forEach(a -> executeAction(event.twitchMessage(), a));
    }

    @EventHandler
    private void onSubscribeTrigger(ChannelSubscribeEvent event) {
        if (event.twitchMessage().getPayload().getEvent().getIs_gift()) {
            return;
        }
        getPlugin().config().getActions().stream()
                .filter(a -> a.getTrigger().getType().equals(TriggerType.CHANNEL_SUBSCRIBE))
                .forEach(a -> executeAction(event.twitchMessage(), a));
    }

    @EventHandler
    private void onResubscribeTrigger(ChannelResubscribeEvent event) {
        getPlugin().config().getActions().stream()
                .filter(a -> a.getTrigger().getType().equals(TriggerType.CHANNEL_RESUBSCRIBE))
                .forEach(a -> executeAction(event.twitchMessage(), a));
    }

    @EventHandler
    private void onGiftSub(ChannelSubscriptionGiftEvent event) {
        int totalGifts = event.twitchMessage().getPayload().getEvent().getTotal();
        getPlugin().config().getActions().stream()
                .filter(a -> a.getTrigger().getType().equals(TriggerType.SUB_GIFT))
                .filter(a -> totalGifts >= a.getTrigger().getMin() && totalGifts <= a.getTrigger().getMax())
                .forEach(a -> executeAction(event.twitchMessage(), a));
    }

    @EventHandler
    private void onCheer(ChannelCheerEvent event) {
        int totalBits = event.twitchMessage().getPayload().getEvent().getBits();
        getPlugin().config().getActions().stream()
                .filter(a -> a.getTrigger().getType().equals(TriggerType.CHANNEL_CHEER))
                .filter(a -> totalBits >= a.getTrigger().getMin() && totalBits <= a.getTrigger().getMax())
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
                .filter(a -> a.getTrigger().getType().equals(TriggerType.POLL))
                .filter(a -> a.pollMessage().equals(winningPollChoice.get().getTitle()))
                .findFirst();
        if (winningAction.isEmpty()) {
            logger().warning("Somehow the action for \"" + winningPollChoice.get().getTitle() + "\" could not be found.");
            return;
        }
        if (winningPollChoice.get().getVotes() > 0) {
            executeAction(event.twitchMessage(), winningAction.get());
        } else {
            logger().warning("No votes recorded for the poll");
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
