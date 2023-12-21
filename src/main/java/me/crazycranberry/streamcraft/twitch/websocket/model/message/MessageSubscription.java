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
public class MessageSubscription {
    private String id;
    private String status;
    private String type;
    private String version;
    private Integer cost;
    private MessageCondition condition;
    private MessageTransport transport;
    private String created_at;
}
