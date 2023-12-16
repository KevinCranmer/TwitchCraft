package me.crazycranberry.streamcraft.twitch.websocket.model.refresh;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Builder
@ToString
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshResponse {
    private String access_token;
    private Integer expires_in;
    private String refresh_token;
    private List<String> scope;
    private String token_type;
}
