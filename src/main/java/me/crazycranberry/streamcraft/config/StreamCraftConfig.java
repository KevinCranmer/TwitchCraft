package me.crazycranberry.streamcraft.config;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import static me.crazycranberry.streamcraft.StreamCraft.getPlugin;
import static me.crazycranberry.streamcraft.StreamCraft.logger;
import static me.crazycranberry.streamcraft.utils.FileUtils.loadOriginalConfig;

@Getter
public class StreamCraftConfig {
    private final YamlConfiguration originalConfig;
    private final YamlConfiguration config;
    private String accessToken;
    private String refreshToken;
    private String broadcasterId;
    private boolean sendActionMessageByDefault;
    private boolean followAllowRepeats;
    private Integer pollDuration;
    private Integer pollInterval;
    private Integer pollNumChoices;
    private Double pollDefaultWeight;
    private List<Action> actions;

    public StreamCraftConfig(YamlConfiguration config) {
        originalConfig = loadOriginalConfig("stream_craft.yml");
        this.config = config;
        updateOutOfDateConfig(config);
        loadConfig(config);
    }

    private void updateOutOfDateConfig(YamlConfiguration config) {
        boolean madeAChange = false;
        for (String key : originalConfig.getKeys(true)) {
            if (!config.isString(key) && !config.isConfigurationSection(key) && !config.isBoolean(key) && !config.isDouble(key) && !config.isInt(key) && !config.isList(key)) {
                logger().info("The " + key + " is missing from stream_craft.yml, adding it now.");
                config.set(key, originalConfig.get(key));
                madeAChange = true;
            }
        }

        if (madeAChange) {
            try {
                config.save(getPlugin().getDataFolder() + "" + File.separatorChar + "stream_craft.yml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadConfig(YamlConfiguration config) {
        accessToken = config.getString("access_token", originalConfig.getString("access_token")).trim();
        refreshToken = config.getString("refresh_token", originalConfig.getString("refresh_token")).trim();
        broadcasterId = config.getString("broadcaster_id", originalConfig.getString("broadcaster_id")).trim();
        sendActionMessageByDefault = config.getBoolean("send_action_message_by_default", originalConfig.getBoolean("send_action_message_by_default"));
        followAllowRepeats = config.getBoolean("channel_follows.allow_repeats", originalConfig.getBoolean("channel_follows.allow_repeats"));
        pollDuration = config.getInt("polls.duration_seconds", originalConfig.getInt("polls.duration_seconds"));
        pollInterval = config.getInt("polls.seconds_until_next_poll", originalConfig.getInt("polls.seconds_until_next_poll"));
        pollNumChoices = validatePollNumChoices(config.getInt("polls.num_choices", originalConfig.getInt("polls.num_choices")));
        pollDefaultWeight = config.getDouble("polls.default_weight", originalConfig.getInt("polls.default_weight"));
        actions = config.getList("actions", List.of()).stream().map(c -> Action.fromYaml((LinkedHashMap<String, ?>) c)).peek(System.out::println).filter(Objects::nonNull).toList();
    }

    private Integer validatePollNumChoices(Integer maybeNumChoices) {
        if (maybeNumChoices < 2 || maybeNumChoices > 5) {
            logger().warning("polls.num_choices must be between 2 and 5");
            if (maybeNumChoices < 2) return 2;
            return 5;
        }
        return maybeNumChoices;
    }

    public void setAccessToken(String accessToken) {
        config.set("access_token", accessToken);
        try {
            config.save(getPlugin().getDataFolder() + "" + File.separatorChar + "stream_craft.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.accessToken = accessToken;
    }

    public void setRefreshToken(String refreshToken) {
        config.set("refresh_token", refreshToken);
        try {
            config.save(getPlugin().getDataFolder() + "" + File.separatorChar + "stream_craft.yml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.refreshToken = refreshToken;
    }
}
