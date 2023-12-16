package me.crazycranberry.streamcraft.twitch.websocket.model.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class MessagePayload {
    private MessageSession session;
    private MessageSubscription subscription;
    private MessageEvent event;
}
