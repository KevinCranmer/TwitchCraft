package me.crazycranberry.streamcraft.commands;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static me.crazycranberry.streamcraft.commands.CommandUtils.mapper;
import static me.crazycranberry.streamcraft.twitch.websocket.TwitchClient.handleNotificationMessage;

/** This is just for testing. */
public class TriggerChannelResubscribeEvent implements CommandExecutor {
    @SneakyThrows
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("ChannelResubscribe")) {
            Message twitchMessage = mapper().readValue(notification(), Message.class);
            handleNotificationMessage(twitchMessage);
        }
        return true;
    }

    private static String notification() {
        return """
                {
                    "metadata": {
                        "message_id": "befa7b53-d79d-478f-86b9-120f112b044e",
                        "message_type": "notification",
                        "message_timestamp": "2022-11-16T10:11:12.464757833Z",
                        "subscription_type": "channel.subscription.message",
                        "subscription_version": "1"
                    },
                    "payload": {
                        "subscription": {
                           "id": "f1c2a387-161a-49f9-a165-0f21d7a4e1c4",
                           "type": "channel.subscription.message",
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
                           "user_id": "1234",
                           "user_login": "Crazy_Cranberry",
                           "user_name": "Crazy_Cranberry",
                           "broadcaster_user_id": "1337",
                           "broadcaster_user_login": "Crazy_Cranberry",
                           "broadcaster_user_name": "Crazy_Cranberry",
                           "tier": "1000",
                           "message": {
                               "text": "Love the stream! FevziGG",
                               "emotes": [
                                   {
                                       "begin": 23,
                                       "end": 30,
                                       "id": "302976485"
                                   }
                               ]
                           },
                           "cumulative_months": 15,
                           "streak_months": 1,
                           "duration_months": 6
                        }
                    }
                }
                    """;
    }
}
