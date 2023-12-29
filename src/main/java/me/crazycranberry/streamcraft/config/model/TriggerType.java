package me.crazycranberry.streamcraft.config.model;

import lombok.AllArgsConstructor;
import me.crazycranberry.streamcraft.events.ActionEvent;
import me.crazycranberry.streamcraft.events.ChannelFollowEvent;
import me.crazycranberry.streamcraft.events.PollEndEvent;

@AllArgsConstructor
public enum TriggerType {
    CHANNEL_FOLLOW(false, "channel.follow", ChannelFollowEvent.class),
    POLL(false, "channel.poll.end", PollEndEvent.class);

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
        String t = triggerType.toUpperCase().replaceAll("[^a-zA-Z]+", "_");

        for (TriggerType b : TriggerType.values()) {
            if (b.name().equals(t)) {
                return b;
            }
        }
        return null;
    }
}