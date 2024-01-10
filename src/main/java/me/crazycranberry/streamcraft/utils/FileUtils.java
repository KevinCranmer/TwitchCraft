package me.crazycranberry.streamcraft.utils;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static me.crazycranberry.streamcraft.StreamCraft.getPlugin;
import static me.crazycranberry.streamcraft.StreamCraft.logger;

public class FileUtils {
    public static YamlConfiguration loadConfig(String configName) throws InvalidConfigurationException {
        File configFile = new File(getPlugin().getDataFolder() + "" + File.separatorChar + configName);
        if(!configFile.exists()){
            getPlugin().saveResource(configName, true);
            logger().info(String.format("%s not found! copied %s to %s", configName, configName, getPlugin().getDataFolder()));
        }
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.load(configFile);
        } catch (InvalidConfigurationException | IOException e) {
            throw new InvalidConfigurationException("An error occured while trying to load " + configName);
        }
        return config;
    }

    public static YamlConfiguration loadOriginalConfig(String configName) {
        YamlConfiguration config = new YamlConfiguration();
        try {
            InputStream defaultConfigStream = getPlugin().getResource(configName);
            assert defaultConfigStream != null;
            InputStreamReader defaultConfigReader = new InputStreamReader(defaultConfigStream);
            config.load(defaultConfigReader);
        } catch (InvalidConfigurationException | IOException e) {
            logger().info("An error occured while trying to load the (default) " + configName + " file.");
            e.printStackTrace();
        }
        return config;
    }
}
