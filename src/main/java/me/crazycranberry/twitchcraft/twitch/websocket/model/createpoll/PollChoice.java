package me.crazycranberry.twitchcraft.twitch.websocket.model.createpoll;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@ToString
@Getter
@Setter
public class PollChoice {
    private final String title;

    public static PollChoice of(String title) {
        return PollChoice.builder().title(title.substring(0, Math.min(25, title.length()))).build();
    }
}
