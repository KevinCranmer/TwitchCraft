package me.crazycranberry.streamcraft.commands;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import static me.crazycranberry.streamcraft.commands.CommandUtils.mapper;
import static me.crazycranberry.streamcraft.twitch.websocket.TwitchClient.handleNotificationMessage;

/** This is just for testing. */
public class TriggerPollEndEvent implements CommandExecutor {
    @SneakyThrows
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("PollResult")) {
            if (args.length <= 1 && sender instanceof Player) {
                ((Player) sender).sendMessage("You must provide a Poll Option Name");
            }
            String winningPollOption = args[0];
            Message twitchMessage = mapper().readValue(notification(winningPollOption), Message.class);
            handleNotificationMessage(twitchMessage);
        }
        return true;
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
                                {"id": "125", "title": "Random Bad Potion Effect": 10, "channel_points_votes": 0, "votes": 10}
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
