package me.crazycranberry.streamcraft.config;

import lombok.AllArgsConstructor;
import me.crazycranberry.streamcraft.events.ActionEvent;
import me.crazycranberry.streamcraft.events.ChannelCheerEvent;
import me.crazycranberry.streamcraft.events.ChannelFollowEvent;
import me.crazycranberry.streamcraft.events.ChannelResubscribeEvent;
import me.crazycranberry.streamcraft.events.ChannelSubscribeEvent;
import me.crazycranberry.streamcraft.events.ChannelSubscriptionGiftEvent;
import me.crazycranberry.streamcraft.events.PollEndEvent;

@AllArgsConstructor
public enum TriggerType {
    CHANNEL_FOLLOW("channel.follow", ChannelFollowEvent.class),
    CHANNEL_SUBSCRIBE("channel.subscribe", ChannelSubscribeEvent.class),
    CHANNEL_RESUBSCRIBE("channel.subscription.message", ChannelResubscribeEvent.class),
    SUB_GIFT("channel.subscription.gift", ChannelSubscriptionGiftEvent.class),
    CHANNEL_CHEER("channel.cheer", ChannelCheerEvent.class),
    POLL("channel.poll.end", PollEndEvent.class);

    private String value;
    private Class<? extends ActionEvent> event;

    public String value() {
        return value;
    }

    public Class<? extends ActionEvent> event() {
        return event;
    }

    public static TriggerType fromValue(String triggerType) {
        String t = triggerType.toUpperCase().replaceAll("[^a-zA-Z]+", "_");

        for (TriggerType b : TriggerType.values()) {
            if (b.name().equals(t)) {
                return b;
            }
        }
        return null;
    }
}