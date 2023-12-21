package me.crazycranberry.streamcraft.twitch.websocket.model.message;

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
public class MessageMetadata {
    private String message_id;
    private String message_type;
    private String message_timestamp;
    private String subscription_type;
    private String subscription_version;
}
