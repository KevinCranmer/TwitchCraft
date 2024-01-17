package me.crazycranberry.streamcraft.commands;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.config.TriggerType;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

import static me.crazycranberry.streamcraft.StreamCraft.getPlugin;
import static me.crazycranberry.streamcraft.commands.CommandUtils.mapper;
import static me.crazycranberry.streamcraft.twitch.websocket.TwitchClient.handleNotificationMessage;

/** This is just for testing. */
public class TriggerPollEndEvent implements CommandExecutor, TabCompleter {
    @SneakyThrows
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!getPlugin().config().isAllowTestCommands()) {
            sender.sendMessage("Could not execute because the `allow_test_commands` configuration is set to false");
            return false;
        }
        if (command.getName().equalsIgnoreCase("PollResult")) {
            if (args.length <= 1) {
                sender.sendMessage("You must provide a Poll Message string");
            }
            String winningPollOption = String.join(" ", args);
            Message twitchMessage = mapper().readValue(notification(winningPollOption), Message.class);
            handleNotificationMessage(twitchMessage);
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player && command.getName().equalsIgnoreCase("PollResult")) {
            return getPlugin().config().getActions()
                    .stream()
                    .filter(a -> a.getTrigger().getType().equals(TriggerType.POLL))
                    .filter(a -> a.pollMessage().startsWith(String.join(" ", args)))
                    .map(a -> {
                        String pollMessage = a.pollMessage();
                        for (int i = 0; i < args.length - 1; i++) {
                            pollMessage = pollMessage.substring(pollMessage.indexOf(" ") + 1);
                        }
                        return pollMessage;
                    })
                    .toList();
        }
        return null;
    }

    private static String notification(String winningPollOption) {
        return String.format("""
                {
                    "metadata": {
                        "message_id": "befa7b53-d79d-478f-86b9-120f112b044e",
                        "message_type": "notification",
                        "message_timestamp": "2022-11-16T10:11:12.464757833Z",
                        "subscription_type": "channel.subscription.gift",
                        "subscription_version": "1"
                    },
                    "payload": {
                        "subscription": {
                            "id": "f1c2a387-161a-49f9-a165-0f21d7a4e1c4",
                            "type": "channel.poll.end",
                            "version": "1",
                            "status": "enabled",
                            "cost": 0,
                            "condition": {
                                "broadcaster_user_id": "1337"
                            },
                             "transport": {
                               "method": "websocket",
                               "session_id": "123"
                            },
                            "created_at": "2019-11-16T10:11:12.634234626Z"
                        },
                        "event": {
                            "id": "1243456",
                            "broadcaster_user_id": "1337",
                            "broadcaster_user_login": "Crazy_Cranberry",
                            "broadcaster_user_name": "Crazy_Cranberry",
                            "title": "Which StreamCraft Action?",
                            "choices": [
                                {"id": "123", "title": "Can't Stop, Won't Stop", "bits_votes": 50, "channel_points_votes": 0, "votes": 50},
                                {"id": "124", "title": "%s", "bits_votes": 100, "channel_points_votes": 0, "votes": 100},
                                {"id": "125", "title": "Random Bad Potion Effect", "bits_votes": 10, "channel_points_votes": 0, "votes": 10}
                            ],
                            "bits_voting": {
                                "is_enabled": false,
                                "amount_per_vote": 0
                            },
                            "channel_points_voting": {
                                "is_enabled": false,
                                "amount_per_vote": 0
                            },
                            "status": "completed",
                            "started_at": "2020-07-15T17:16:03.17106713Z",
                            "ended_at": "2020-07-15T17:16:11.17106713Z"
                        }
                   }
                }
                    """, winningPollOption);
    }
}
