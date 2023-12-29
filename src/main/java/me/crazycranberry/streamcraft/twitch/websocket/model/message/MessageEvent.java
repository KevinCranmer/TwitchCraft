package me.crazycranberry.streamcraft.twitch.websocket.model.message;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageEvent {
    private String id;
    private String user_id;
    private String user_login;
    private String user_name;
    private String broadcaster_user_id;
    private String broadcaster_user_login;
    private String broadcaster_user_name;
    private String followed_at;
    private String title;
    private List<MessagePollChoice> choices;
    private MessagePollBitVoting bits_voting;
    private MessagePollChannelPointVoting channel_points_voting;
    private String started_at;
    private String ended_at;
    private String status;
}
