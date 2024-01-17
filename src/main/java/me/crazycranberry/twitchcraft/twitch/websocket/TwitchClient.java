package me.crazycranberry.twitchcraft.twitch.websocket;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.crazycranberry.twitchcraft.config.TwitchCraftConfig;
import me.crazycranberry.twitchcraft.config.TriggerType;
import me.crazycranberry.twitchcraft.events.ActionEvent;
import me.crazycranberry.twitchcraft.events.ReconnectRequestedEvent;
import me.crazycranberry.twitchcraft.events.WebSocketConnectedEvent;
import me.crazycranberry.twitchcraft.twitch.websocket.model.createpoll.CreatePoll;
import me.crazycranberry.twitchcraft.twitch.websocket.model.eventsubscription.Condition;
import me.crazycranberry.twitchcraft.twitch.websocket.model.eventsubscription.EventSubscription;
import me.crazycranberry.twitchcraft.twitch.websocket.model.eventsubscription.Transport;
import me.crazycranberry.twitchcraft.twitch.websocket.model.message.Message;
import me.crazycranberry.twitchcraft.twitch.websocket.model.refresh.Refresh;
import me.crazycranberry.twitchcraft.twitch.websocket.model.refresh.RefreshResponse;
import org.bukkit.Bukkit;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;

import static me.crazycranberry.twitchcraft.TwitchCraft.SECRET;
import static me.crazycranberry.twitchcraft.TwitchCraft.getPlugin;
import static me.crazycranberry.twitchcraft.TwitchCraft.logger;

public class TwitchClient {
    public final static Integer KEEP_ALIVE_SECONDS = 10;
    public final static String WEBSOCKET_CONNECTION_URL = "wss://eventsub.wss.twitch.tv/ws";
    private final static String subscriptionUrl = "https://streamcraft-0a9a58085ccc.herokuapp.com/subscribe";
    private final static String createPollUrl = "https://streamcraft-0a9a58085ccc.herokuapp.com/poll";
    private final static String refreshUrl = "https://streamcraft-0a9a58085ccc.herokuapp.com/refresh";
    private ObjectMapper mapper = new ObjectMapper().setSerializationInclusion(JsonInclude.Include.NON_NULL);
    private String sessionId = "";
    private WebSocketClient cc;
    private Instant timeOfLastMessage;

    public TwitchClient() {
        attemptToConnect(WEBSOCKET_CONNECTION_URL);
    }

    private void attemptToConnect(String connectionUrl) {
        if (!allConfigsExist()) {
            logger().warning("At least one of access_token, refresh_token, or broadcaster_id are missing. Fill them in and run /TwitchCraftRefresh and /TwitchCraftReconnect");
        } else {
            connect(connectionUrl);
        }
    }

    private void connect(String connectionUrl) {
        try {
            cc = new WebSocketClient(new URI(connectionUrl)) {

                @Override
                public void onMessage(String message) {
                    try {
                        Message twitchMessage = mapper.readValue(message, Message.class);
                        handleMessage(twitchMessage);
                    } catch (JsonProcessingException e) {
                        logger().warning("Failed to parse the twitch message: " + message);
                        e.printStackTrace();
                    }
                }

                @Override
                public void onOpen(ServerHandshake handshake) {
                    logger().info("Successfully connected to Twitch Websocket");
                }

                @Override
                public void onClose(int code, String reason, boolean remote) {
                    logger().info("You have been disconnected from: " + getURI() + "; Code: " + code + " " + reason + "\n");
                }

                @Override
                public void onError(Exception ex) {
                    logger().info("Exception occurred ...\n" + ex + "\n");
                    ex.printStackTrace();
                }
            };

            cc.connect();
        } catch (URISyntaxException ex) {
            logger().warning(connectionUrl + " is not a valid WebSocket URI\n");
        }
    }

    private boolean allConfigsExist() {
        TwitchCraftConfig config = getPlugin().config();
        if ("<retrieve from https://streamcraft-0a9a58085ccc.herokuapp.com/>".equals(config.getAccessToken()) ||
            "<retrieve from https://streamcraft-0a9a58085ccc.herokuapp.com/>".equals(config.getRefreshToken()) ||
            "<retrieve from https://streamcraft-0a9a58085ccc.herokuapp.com/>".equals(config.getBroadcasterId()) ||
            config.getAccessToken() == null || config.getRefreshToken() == null || config.getBroadcasterId() == null) {
            return false;
        }
        return true;
    }

    private void handleMessage(Message twitchMessage) {
        timeOfLastMessage = Instant.now();
        switch(twitchMessage.getMetadata().getMessage_type()) {
            case "session_welcome":
                handleWelcomeMessage(twitchMessage);
                break;
            case "notification":
                logger().info("It's a notification message: " + twitchMessage);
                handleNotificationMessage(twitchMessage);
                break;
            case "session_keepalive":
                break;
            case "session_reconnect":
                handleReconnectMessage(twitchMessage);
                break;
            default:
                logger().info("Unkown Twitch Message Type: " + twitchMessage.getMetadata().getMessage_type());
        }
    }

    public static void handleNotificationMessage(Message twitchMessage) {
        for (TriggerType triggerType : TriggerType.values()) {
            if (triggerType.value().equals(twitchMessage.getPayload().getSubscription().getType())) {
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                Bukkit.getServer().getScheduler().callSyncMethod(getPlugin(), () -> {
                                    Constructor c = triggerType.event().getConstructor(Message.class);
                                    c.setAccessible(true);
                                    ActionEvent instance = (ActionEvent) c.newInstance(twitchMessage);
                                    Bukkit.getPluginManager().callEvent(instance);
                                    return true;
                                });
                            }
                        },
                        1
                );
            }
        }
    }

    private void handleReconnectMessage(Message twitchMessage) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        Bukkit.getServer().getScheduler().callSyncMethod(getPlugin(), () -> {
                            Bukkit.getPluginManager().callEvent(new ReconnectRequestedEvent(twitchMessage.getPayload().getSession().getReconnect_url()));
                            return true;
                        });
                    }
                },
                1
        );
    }

    private void handleWelcomeMessage(Message twitchMessage) {
        logger().info("Welcome message received");
        sessionId = twitchMessage.getPayload().getSession().getId();
        subscribeToEvents();
        sendWebSocketConnectedEvent();
    }

    private void sendWebSocketConnectedEvent() {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        Bukkit.getServer().getScheduler().callSyncMethod(getPlugin(), () -> {
                            Bukkit.getPluginManager().callEvent(new WebSocketConnectedEvent());
                            return true;
                        });
                    }
                },
                1
        );
    }

    private void subscribeToEvents() {
        subscribeToFollowEvents();
        subscribeToPollEvents();
        subscribeToCheerEvents();
        subscribetoResubscribeEvents();
        subscribetoSubscribeEvents();
        subscribeToGiftedEvents();
    }

    private boolean subscribeToGiftedEvents() {
        EventSubscription eventSubscription = eventSubscription("channel.subscription.gift", "1", sessionId);
        HttpResponse<?> response = sendTwitchSubscription(eventSubscription);
        logger().info("response from subscribeToGiftedEvents: " + response.statusCode() + response.body());
        return logResponse(response);
    }

    private boolean subscribetoSubscribeEvents() {
        EventSubscription eventSubscription = eventSubscription("channel.subscribe", "1", sessionId);
        HttpResponse<?> response = sendTwitchSubscription(eventSubscription);
        logger().info("response from subscribetoSubscribeEvents: " + response.statusCode() + response.body());
        return logResponse(response);
    }

    private boolean subscribetoResubscribeEvents() {
        EventSubscription eventSubscription = eventSubscription("channel.subscription.message", "1", sessionId);
        HttpResponse<?> response = sendTwitchSubscription(eventSubscription);
        logger().info("response from subscribetoResubscribeEvents: " + response.statusCode() + response.body());
        return logResponse(response);
    }

    private boolean subscribeToCheerEvents() {
        EventSubscription eventSubscription = eventSubscription("channel.cheer", "1", sessionId);
        HttpResponse<?> response = sendTwitchSubscription(eventSubscription);
        logger().info("response from subscribeToCheerEvents: " + response.statusCode() + response.body());
        return logResponse(response);
    }

    private boolean subscribeToPollEvents() {
        EventSubscription eventSubscription = eventSubscription("channel.follow", "2", sessionId);
        HttpResponse<?> response = sendTwitchSubscription(eventSubscription);
        logger().info("response from subscribeToPollEvents: " + response.statusCode() + response.body());
        return logResponse(response);
    }

    private boolean subscribeToFollowEvents() {
        EventSubscription eventSubscription = eventSubscription("channel.poll.end", "1", sessionId);
        HttpResponse<?> response = sendTwitchSubscription(eventSubscription);
        logger().info("response from subscribeToFollowEvents: " + response.statusCode() + response.body());
        return logResponse(response);
    }

    private boolean logResponse(HttpResponse<?> response) {
        switch(response.statusCode()) {
            case 202:
                return true;
            case 400:
                logger().warning("Bad Request received from twitch: " + response.body());
                return false;
            case 401:
                logger().warning("Unauthorized response received from twitch: " + response.body() + " Attempting to refresh access_token and then reconnect.");
                if (refreshAccessToken()) {
                    attemptToReconnectInSeparateThread();
                }
                return false;
            case 403:
                logger().warning("Access token missing scopes response received from twitch: " + response.body());
                return false;
            case 409:
                logger().warning("Already subscribed to this event response received from twitch: " + response.body());
                return false;
            case 429:
                logger().warning("Exceeding request limit: " + response.body());
                return false;
            default:
                logger().warning("Unhandled response code received: " + response.statusCode() + " body: " + response.body());
                return false;
        }
    }

    private void attemptToReconnectInSeparateThread() {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        Bukkit.getServer().getScheduler().callSyncMethod(getPlugin(), () -> {
                            Bukkit.getPluginManager().callEvent(new ReconnectRequestedEvent());
                            return true;
                        });
                    }
                },
                1
        );

    }

    private boolean refreshAccessToken() {
        try {
            HttpClient httpClient = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(refreshUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(
                            mapper.writeValueAsString(
                                    Refresh.builder()
                                            .secret(SECRET)
                                            .refresh_token(getPlugin().config().getRefreshToken())
                                            .build())))
                    .build();
            HttpResponse<?> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            logger().info("response from refresh: " + response.statusCode() + response.body());
            if (response.statusCode() != 200) {
                logger().warning("The refresh token call was unsuccessful. Please contact CrazyCranberry at https://discord.gg/s4F8VEHegN and show him these logs.");
                logger().warning("Response status: " + response.statusCode());
                logger().warning("Response body: " + response.body());
                close();
                return false;
            }
            RefreshResponse refreshResponse = mapper.readValue(response.body().toString(), RefreshResponse.class);
            getPlugin().config().setAccessToken(refreshResponse.getAccess_token());
            getPlugin().config().setRefreshToken(refreshResponse.getRefresh_token());
            logger().info("Successfully refreshed access_token");
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    private HttpResponse<?> sendTwitchSubscription(EventSubscription subscription) {
        try {
            HttpClient httpClient = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(subscriptionUrl))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(subscription)))
                .build();
        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public HttpResponse<?> sendCreatePoll(CreatePoll createPoll) {
        try {
            HttpClient httpClient = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(createPollUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(createPoll)))
                    .build();
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private EventSubscription eventSubscription(String type, String version, String sessionId) {
        return EventSubscription.builder()
                .access_token(getPlugin().config().getAccessToken())
                .type(type)
                .version(version)
                .condition(Condition.builder()
                        .broadcaster_user_id(getPlugin().config().getBroadcasterId())
                        .moderator_user_id(getPlugin().config().getBroadcasterId())
                        .build())
                .transport(Transport.builder()
                        .method("websocket")
                        .session_id(sessionId)
                        .build())
                .secret(SECRET)
                .build();
    }

    public void reconnect(String connectionUrl) {
        if (cc == null) {
            attemptToConnect(connectionUrl);
        } else {
            cc.close();
            connect(connectionUrl);
        }
    }

    public void close() {
        if (cc != null) {
            cc.close();
        }
    }

    public Instant getTimeOfLastMessage() {
        return timeOfLastMessage;
    }
}
