package me.crazycranberry.streamcraft.twitch.websocket.model.eventsubscription;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@Getter
@Setter
public class Condition {
    private final String broadcaster_user_id;
    private final String moderator_user_id;
}
