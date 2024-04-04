package me.crazycranberry.twitchcraft.twitch.websocket.model.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageSession {
    private String id;
    private String status;
    private Integer keepalive_timeout_seconds;
    private String reconnect_url;
    private String recovery_url;
    private String connected_at;
}
