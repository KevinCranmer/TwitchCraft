package me.crazycranberry.streamcraft.managers;

import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.config.StreamCraftConfig;
import me.crazycranberry.streamcraft.config.Trigger;
import me.crazycranberry.streamcraft.config.TriggerType;
import me.crazycranberry.streamcraft.events.PollEndEvent;
import me.crazycranberry.streamcraft.events.WebSocketConnectedEvent;
import me.crazycranberry.streamcraft.twitch.websocket.model.createpoll.CreatePoll;
import me.crazycranberry.streamcraft.twitch.websocket.model.createpoll.PollChoice;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static me.crazycranberry.streamcraft.StreamCraft.getPlugin;

/** A dedicated class that makes sure the WebSocket connection has been kept alive. And Attempts to reconnect otherwise. */
public class PollManager implements Listener {
    @EventHandler
    private void onRefreshTokenSuccessful(WebSocketConnectedEvent event) {
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

    @EventHandler
    private void onPreviousPollEnd(PollEndEvent event) {
        if (!event.twitchMessage().getPayload().getEvent().getStatus().equals("completed")) {
            return;
        }
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
        StreamCraftConfig config = getPlugin().config();
        List<Action> pollActions = new ArrayList<>(pollActions());
        List<Action> selectedPollActions = new ArrayList<>();
        if (pollActions.size() <= config.getPollNumChoices()) {
            selectedPollActions = pollActions;
        } else {
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
}
