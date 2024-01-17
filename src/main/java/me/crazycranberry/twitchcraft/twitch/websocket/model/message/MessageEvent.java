package me.crazycranberry.twitchcraft.twitch.websocket.model.message;

import com.fasterxml.jackson.databind.JsonNode;
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
    private String tier;
    private Boolean is_gift;
    private JsonNode message; // This is either a string or an object. Why tf does Twitch have two typings for the same variable smh...
    private Integer cumulative_months;
    private Integer streak_months;
    private Integer duration_months;
    private Integer total;
    private Integer cumulative_total;
    private Boolean is_anonymous;
    private Integer bits;
}
