package me.crazycranberry.twitchcraft.managers;

import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.config.TwitchCraftConfig;
import me.crazycranberry.twitchcraft.config.TriggerType;
import me.crazycranberry.twitchcraft.events.PollEndEvent;
import me.crazycranberry.twitchcraft.events.WebSocketConnectedEvent;
import me.crazycranberry.twitchcraft.twitch.websocket.model.createpoll.CreatePoll;
import me.crazycranberry.twitchcraft.twitch.websocket.model.createpoll.PollChoice;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.crazycranberry.twitchcraft.TwitchCraft.SECRET;
import static me.crazycranberry.twitchcraft.TwitchCraft.getPlugin;
import static me.crazycranberry.twitchcraft.TwitchCraft.logger;

/** A dedicated class that makes sure the WebSocket connection has been kept alive. And Attempts to reconnect otherwise. */
public class PollManager implements Listener {
    private static boolean pollActive = false;

    @EventHandler
    private void onRefreshTokenSuccessful(WebSocketConnectedEvent event) {
        sendARandomPollInIntervalSeconds();
    }

    @EventHandler
    private void onPreviousPollEnd(PollEndEvent event) {
        if (!event.twitchMessage().getPayload().getEvent().getStatus().equals("completed")) {
            return;
        }
        setPollActive(false);
        sendARandomPollInIntervalSeconds();
    }

    public static void sendARandomPollInIntervalSeconds() {
        new java.util.Timer().schedule(
            new java.util.TimerTask() {
                @Override
                public void run() {
                    Bukkit.getServer().getScheduler().callSyncMethod(getPlugin(), () -> {
                        createRandomPoll();
                        return true;
                    });
                }
            },
            getPlugin().config().getPollInterval() * 1000
        );
    }

    public static void createRandomPoll() {
        if (isPollActive()) {
            logger().warning("A poll was requested to be created but another poll is already active. No poll will be created at this time.");
            logger().warning("A new poll should get queued for creation once the current one ends.");
            logger().warning("If you believe this to be a mistake, please message me (Crazy_Cranberry) on discord.");
            return;
        }
        TwitchCraftConfig config = getPlugin().config();
        List<Action> pollActions = new ArrayList<>(pollActions());
        List<Action> selectedPollActions = new ArrayList<>();
        if (pollActions.isEmpty()) {
            logger().info("The plugin tried to create a twitch poll but there are no poll actions so the twitch request was not sent.");
            return;
        }
        if (pollActions.size() <= config.getPollNumChoices()) {
            selectedPollActions = pollActions;
        } else if (!pollActions.isEmpty()) {
            cleanPollActions(pollActions);
            Double randMultiplier = pollActions.stream().map(a -> a.getTrigger().getWeight()).reduce(0.0, Double::sum);
            for (int i = 0; i < config.getPollNumChoices(); i++) {
                double rand = Math.random() * randMultiplier;
                Double tracker = 0.0;
                for (Action a : pollActions) {
                    if (rand < a.getTrigger().getWeight() + tracker) {
                        pollActions.remove(a);
                        selectedPollActions.add(a);
                        randMultiplier -= a.getTrigger().getWeight();
                        break;
                    } else {
                        tracker += a.getTrigger().getWeight();
                    }
                }
            }
        }
        Collections.shuffle(selectedPollActions);
        CreatePoll poll = CreatePoll.builder()
            .access_token(config.getAccessToken())
            .broadcaster_user_id(config.getBroadcasterId())
            .duration(config.getPollDuration())
            .title(config.getPollTitle())
            .choices(selectedPollActions.stream().map(a -> PollChoice.of(a.pollMessage())).toList())
            .channel_points_voting_enabled(config.isPollChannelPointsVotingEnabled())
            .channel_points_per_vote(config.getPollChannelPointsPerVote())
            .secret(SECRET)
            .build();
        getPlugin().createTwitchPoll(poll);
    }

    private static List<Action> pollActions() {
        return getPlugin().config().getActions().stream()
            .filter(a -> a.getTrigger().getType().equals(TriggerType.POLL))
            .toList();
    }

    private static void cleanPollActions(List<Action> pollActions) {
        pollActions.stream().filter(p -> p.getTrigger().getWeight() == null).forEach(p -> p.getTrigger().setWeight(getPlugin().config().getPollDefaultWeight()));
    }

    public static void setPollActive(boolean isActive) {
        pollActive = isActive;
    }

    public static boolean isPollActive() {
        return pollActive;
    }
}
