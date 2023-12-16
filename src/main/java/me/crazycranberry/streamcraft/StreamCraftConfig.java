package me.crazycranberry.streamcraft;

import lombok.Getter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

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
