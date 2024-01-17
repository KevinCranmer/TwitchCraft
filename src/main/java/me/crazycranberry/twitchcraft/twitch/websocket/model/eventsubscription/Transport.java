package me.crazycranberry.twitchcraft.twitch.websocket.model.eventsubscription;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@Getter
@Setter
public class Transport {
    private final String method;
    private final String callback;
    private final String secret;
    private final String session_id;
    private final String connected_at;
    private final String disconnected_at;
}
