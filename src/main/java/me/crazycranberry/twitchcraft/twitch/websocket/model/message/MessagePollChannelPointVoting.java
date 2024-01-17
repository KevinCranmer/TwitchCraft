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
public class MessagePollChannelPointVoting {
    private Boolean is_enabled;
    private Integer amount_per_vote;
}
