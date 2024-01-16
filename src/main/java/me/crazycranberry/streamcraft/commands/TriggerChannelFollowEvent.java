package me.crazycranberry.streamcraft.commands;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static me.crazycranberry.streamcraft.twitch.websocket.TwitchClient.handleNotificationMessage;

/** This is just for testing. Remove eventually. */
public class TriggerChannelFollowEvent implements CommandExecutor {
    private ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);

    @SneakyThrows
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("cf")) {
            Message twitchMessage = mapper.readValue("""
                 {
                     "metadata": {
                         "message_id": "befa7b53-d79d-478f-86b9-120f112b044e",
                         "message_type": "notification",
                         "message_timestamp": "2022-11-16T10:11:12.464757833Z",
                         "subscription_type": "channel.follow",
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
                                               "method": "webhook",
                                               "callback": "https://example.com/webhooks/callback"
                                           },
                                           "created_at": "2019-11-16T10:11:12.634234626Z"
                                       },
                                       "event": {
                                           "user_id": "1234",
                                           "user_login": "cool_user",
                                           "user_name": "Cool_User",
                                           "broadcaster_user_id": "1337",
                                           "broadcaster_user_login": "cooler_user",
                                           "broadcaster_user_name": "Cooler_User",
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
                    """, Message.class);
            handleNotificationMessage(twitchMessage);
        }
        return true;
    }
}
