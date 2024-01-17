package me.crazycranberry.twitchcraft.twitch.websocket.model.createpoll;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Builder
@ToString
@Getter
@Setter
public class CreatePoll {
    private final String access_token;
    private final String broadcaster_user_id;
    private final String title;
    private final Integer duration;
    private final List<PollChoice> choices;
    private final String secret;
}
