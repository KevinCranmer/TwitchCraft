package me.crazycranberry.streamcraft.twitch.websocket.model.refresh;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@Getter
@Setter
public class Refresh {
    private String refresh_token;
}
