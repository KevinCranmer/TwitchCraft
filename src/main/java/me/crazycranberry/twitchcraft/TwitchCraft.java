package me.crazycranberry.twitchcraft;

import lombok.SneakyThrows;
import me.crazycranberry.twitchcraft.actions.sendtonether.SendToNetherManager;
import me.crazycranberry.twitchcraft.actions.soupman.SoupManManager;
import me.crazycranberry.twitchcraft.commands.CreatePollCommand;
import me.crazycranberry.twitchcraft.commands.ReconnectToTwitchCommand;
import me.crazycranberry.twitchcraft.commands.RefreshConfigCommand;
import me.crazycranberry.twitchcraft.commands.TriggerChannelCheerEvent;
import me.crazycranberry.twitchcraft.commands.TriggerChannelFollowEvent;
import me.crazycranberry.twitchcraft.commands.TriggerChannelResubscribeEvent;
import me.crazycranberry.twitchcraft.commands.TriggerChannelSubscribeEvent;
import me.crazycranberry.twitchcraft.commands.TriggerPollEndEvent;
import me.crazycranberry.twitchcraft.commands.TriggerSubscriptionGiftEvent;
import me.crazycranberry.twitchcraft.config.TwitchCraftConfig;
import me.crazycranberry.twitchcraft.managers.ActionManager;
import me.crazycranberry.twitchcraft.actions.chestofgoodies.ChestOfGoodiesManager;
import me.crazycranberry.twitchcraft.actions.explosion.ExplosionManager;
import me.crazycranberry.twitchcraft.actions.flyingcow.FlyingCowManager;
import me.crazycranberry.twitchcraft.managers.CleanUpManager;
import me.crazycranberry.twitchcraft.managers.KeepAliveManager;
import me.crazycranberry.twitchcraft.actions.megajump.MegaJumpManager;
import me.crazycranberry.twitchcraft.actions.nojumping.NoJumpingManager;
import me.crazycranberry.twitchcraft.actions.pinatachickens.PinataChickenManager;
import me.crazycranberry.twitchcraft.managers.PollManager;
import me.crazycranberry.twitchcraft.managers.ReconnectRequestedManager;
import me.crazycranberry.twitchcraft.twitch.websocket.TwitchClient;
import me.crazycranberry.twitchcraft.twitch.websocket.model.createpoll.CreatePoll;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Scanner;
import java.util.logging.Logger;

import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.interpolateColors;
import static me.crazycranberry.twitchcraft.managers.PollManager.sendARandomPollInIntervalSeconds;
import static me.crazycranberry.twitchcraft.managers.PollManager.setPollActive;
import static me.crazycranberry.twitchcraft.utils.FileUtils.loadConfig;

public final class TwitchCraft extends JavaPlugin {
    private static Logger logger;
    private static TwitchCraft plugin;
    private TwitchCraftConfig config;
    private TwitchClient twitchClient;
    public final static String SECRET = getSecret();

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        logger = this.getLogger();
        refreshConfigs();
        registerCommands();
        registerManagers();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (twitchClient != null) {
            twitchClient.close();
        }
    }

    private void registerManagers() {
        getServer().getPluginManager().registerEvents(new ActionManager(), this);
        getServer().getPluginManager().registerEvents(new ChestOfGoodiesManager(), this);
        getServer().getPluginManager().registerEvents(new CleanUpManager(), this);
        getServer().getPluginManager().registerEvents(new ExplosionManager(), this);
        getServer().getPluginManager().registerEvents(new FlyingCowManager(), this);
        getServer().getPluginManager().registerEvents(new KeepAliveManager(), this);
        getServer().getPluginManager().registerEvents(new MegaJumpManager(), this);
        getServer().getPluginManager().registerEvents(new NoJumpingManager(), this);
        getServer().getPluginManager().registerEvents(new PinataChickenManager(), this);
        getServer().getPluginManager().registerEvents(new PollManager(), this);
        getServer().getPluginManager().registerEvents(new ReconnectRequestedManager(), this);
        getServer().getPluginManager().registerEvents(new SendToNetherManager(), this);
        getServer().getPluginManager().registerEvents(new SoupManManager(), this);
    }

    private void registerCommands() {
        setCommandManager("TwitchCraftRefresh", new RefreshConfigCommand());
        setCommandManager("TwitchCraftReconnect", new ReconnectToTwitchCommand());
        setCommandManager("ChannelFollow", new TriggerChannelFollowEvent());
        setCommandManager("ChannelSubscribe", new TriggerChannelSubscribeEvent());
        setCommandManager("ChannelResubscribe", new TriggerChannelResubscribeEvent());
        setCommandManager("ChannelCheer", new TriggerChannelCheerEvent());
        setCommandManager("SubGift", new TriggerSubscriptionGiftEvent());
        setCommandManager("PollResult", new TriggerPollEndEvent());
        setCommandManager("CreatePoll", new CreatePollCommand());
    }

    private void setCommandManager(String command, CommandExecutor commandManager) {
        PluginCommand pc = getCommand(command);
        if (pc == null) {
            logger().warning(String.format("[ ERROR ] - Error loading the %s command", command));
        } else {
            pc.setExecutor(commandManager);
        }
    }

    public boolean isClientNull() {
        return twitchClient == null;
    }

    public void reconnectToTwitch(String connectionUrl) {
        twitchClient.reconnect(connectionUrl);
    }

    public void createTwitchPoll(CreatePoll poll) {
        HttpResponse<?> r = twitchClient.sendCreatePoll(poll);
        logger().info("Status trying to create poll: " + r.statusCode());
        logger().info("Response trying to create poll: " + r.body());
        if (r.statusCode() == 400) {
            logger().warning("A 400 trying to create the poll is likely because another poll is already active.");
            logger().info("The plugin will retry sending a poll in " + config().getPollInterval() + " seconds.");
            sendARandomPollInIntervalSeconds();
        } else if (r.statusCode() == 200) {
            setPollActive(true);
            getServer().sendMessage(Component.text(interpolateColors(config().getPollMessageWhenLive())));
        } else {
            logger().severe("I was not expecting this response from Twitch. Please contact Crazy_Cranberry on discord to debug this.");
        }
    }

    public Instant timeOfLastTwitchMessage() {
        return twitchClient.getTimeOfLastMessage();
    }

    public static Logger logger() {
        return logger;
    }

    public TwitchCraftConfig config() {
        return config;
    }

    public static TwitchCraft getPlugin() {
        return plugin;
    }

    public String refreshConfigs() {
        try {
            config = new TwitchCraftConfig(loadConfig("twitch_craft.yml"));
            if (config().isConnectToTwitch() && twitchClient == null) {
                twitchClient = new TwitchClient();
            } else if (!config().isConnectToTwitch() && twitchClient != null) {
                twitchClient.close();
                twitchClient = null;
            }
            return "Successfully loaded configs.";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @SneakyThrows
    private static String getSecret() {
        InputStream stream = TwitchCraft.class.getClassLoader().getResource("secret.txt").openStream();
        Scanner scan = new Scanner(stream);
        String secret = scan.next();
        return secret;
    }
}
