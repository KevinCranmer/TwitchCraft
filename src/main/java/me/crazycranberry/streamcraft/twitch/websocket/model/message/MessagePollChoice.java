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
public class MessagePollChoice {
    private String id;
    private String title;
    private Integer votes;
    private Integer channel_points_votes;
    private Integer bits_votes;
}
