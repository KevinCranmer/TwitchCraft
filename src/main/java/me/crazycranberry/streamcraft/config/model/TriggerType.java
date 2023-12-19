package me.crazycranberry.streamcraft.config.model;

import lombok.AllArgsConstructor;
import me.crazycranberry.streamcraft.events.ActionEvent;
import me.crazycranberry.streamcraft.events.ChannelFollowEvent;

@AllArgsConstructor
public enum TriggerType {
    CHANNEL_FOLLOW(false, "channel.follow", ChannelFollowEvent.class);

    private boolean requirePredicate;
    private String value;
    private Class<? extends ActionEvent> event;

    public boolean requirePredicate() {
        return requirePredicate;
    }

    public String value() {
        return value;
    }

    public Class<? extends ActionEvent> event() {
        return event;
    }

    public static TriggerType fromValue(String triggerType) {
        String t = triggerType.toLowerCase().replaceAll("[^a-zA-Z]+", ".");

        for (TriggerType b : TriggerType.values()) {
            if (b.value.equals(t)) {
                return b;
            }
        }
        return null;
    }
}