package me.crazycranberry.streamcraft.managers;

import me.crazycranberry.streamcraft.config.Action;
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
import java.util.List;

import static me.crazycranberry.streamcraft.StreamCraft.getPlugin;
import static me.crazycranberry.streamcraft.StreamCraft.logger;

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
        List<Action> pollActions = new ArrayList<>(pollActions());
        List<Action> selectedPollActions = new ArrayList<>();
        if (pollActions().size() <= getPlugin().config().getPollNumChoices()) {
            selectedPollActions = pollActions;
        } else if (validPollChances(pollActions)) {
            Double randMultiplier = 1.0;
            for (int i = 0; i < getPlugin().config().getPollNumChoices(); i++) {
                Double rand = Math.random() * randMultiplier;
                Double tracker = 0.0;
                for (Action a : pollActions) {
                    if (rand < a.getTrigger().getChance() + tracker) {
                        pollActions.remove(a);
                        selectedPollActions.add(a);
                        randMultiplier -= a.getTrigger().getChance();
                        break;
                    } else {
                        tracker += a.getTrigger().getChance();
                    }
                }
            }
        } else {
            for (int i = 0; i < getPlugin().config().getPollNumChoices(); i++) {
                int randomIndex = (int) (Math.random() * pollActions.size());
                selectedPollActions.add(pollActions.get(randomIndex));
                pollActions.remove(randomIndex);
            }
        }
        Collections.shuffle(selectedPollActions);
        CreatePoll poll = CreatePoll.builder()
            .access_token(getPlugin().config().getAccessToken())
            .broadcaster_user_id(getPlugin().config().getBroadcasterId())
            .duration(getPlugin().config().getPollDuration())
            .title("Which StreamCraft Action?")
            .choices(selectedPollActions.stream().map(a -> PollChoice.of(a.pollMessage())).toList())
            .build();
        getPlugin().createTwitchPoll(poll);
    }

    private static List<Action> pollActions() {
        return getPlugin().config().getActions().stream()
            .filter(a -> a.getTrigger().getType().equals(TriggerType.POLL))
            .toList();
    }

    private static boolean validPollChances(List<Action> pollActions) {
        if (pollActions.stream().anyMatch(a -> a.getTrigger().getChance() == null)) {
            logger().warning("At least one pollActions chance is null, giving all POLL actions equal odds");
            return false;
        }
        return pollActions.stream().map(a -> a.getTrigger().getChance()).reduce(0.0, Double::sum) == 1.0;
    }
}
