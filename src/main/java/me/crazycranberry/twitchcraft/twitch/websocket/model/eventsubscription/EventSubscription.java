package me.crazycranberry.twitchcraft.twitch.websocket.model.eventsubscription;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@Getter
@Setter
public class EventSubscription {
    private final String access_token;
    private final String type;
    private final String version;
    private final Condition condition;
    private final Transport transport;
    private final String secret;
}
