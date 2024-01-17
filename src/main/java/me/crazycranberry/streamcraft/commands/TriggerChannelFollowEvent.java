package me.crazycranberry.streamcraft.commands;

import lombok.SneakyThrows;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static me.crazycranberry.streamcraft.StreamCraft.getPlugin;
import static me.crazycranberry.streamcraft.commands.CommandUtils.mapper;
import static me.crazycranberry.streamcraft.twitch.websocket.TwitchClient.handleNotificationMessage;

/** This is just for testing. */
public class TriggerChannelFollowEvent implements CommandExecutor {
    @SneakyThrows
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!getPlugin().config().isAllowTestCommands()) {
            sender.sendMessage("Could not execute because the `allow_test_commands` configuration is set to false");
            return false;
        }
        if (command.getName().equalsIgnoreCase("ChannelFollow")) {
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
                        "subscription_type": "channel.follow",
                        "subscription_version": "1"
                    },
                    "payload": {
                        "subscription": {
                            "id": "f1c2a387-161a-49f9-a165-0f21d7a4e1c4",
                            "type": "channel.follow",
                            "version": "2",
                            "status": "enabled",
                            "cost": 0,
                            "condition": {
                               "broadcaster_user_id": "1337",
                               "moderator_user_id": "1337"
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
                            "followed_at": "2020-07-15T18:16:11.17106713Z"
                        }
                    }
                }
                    """;
    }
}
