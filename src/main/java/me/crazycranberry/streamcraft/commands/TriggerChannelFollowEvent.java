package me.crazycranberry.streamcraft.commands;

import me.crazycranberry.streamcraft.events.ChannelFollowEvent;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.MessageEvent;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.MessageMetadata;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.MessagePayload;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.MessageSubscription;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/** This is just for testing. Remove eventually. */
public class TriggerChannelFollowEvent implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("cf")) {
            Message twitchMessage = Message.builder()
                .metadata(
                    MessageMetadata.builder()
                        .message_type("notification")
                        .build()
                )
                .payload(
                    MessagePayload.builder()
                        .subscription(
                            MessageSubscription.builder()
                                .type("channel.follow")
                                .build()
                        )
                        .event(
                            MessageEvent.builder()
                                    .user_name("mild_cranberry")
                                .build()
                        )
                        .build()
                )
                .build();
            Bukkit.getPluginManager().callEvent(new ChannelFollowEvent(twitchMessage));
        }
        return true;
    }
}
