package me.crazycranberry.streamcraft;

import lombok.SneakyThrows;
import me.crazycranberry.streamcraft.actions.sendtonether.SendToNetherManager;
import me.crazycranberry.streamcraft.actions.soupman.SoupManManager;
import me.crazycranberry.streamcraft.commands.CreatePollCommand;
import me.crazycranberry.streamcraft.commands.ReconnectToTwitchCommand;
import me.crazycranberry.streamcraft.commands.RefreshConfigCommand;
import me.crazycranberry.streamcraft.commands.TriggerChannelCheerEvent;
import me.crazycranberry.streamcraft.commands.TriggerChannelFollowEvent;
import me.crazycranberry.streamcraft.commands.TriggerChannelResubscribeEvent;
import me.crazycranberry.streamcraft.commands.TriggerChannelSubscribeEvent;
import me.crazycranberry.streamcraft.commands.TriggerPollEndEvent;
import me.crazycranberry.streamcraft.commands.TriggerSubscriptionGiftEvent;
import me.crazycranberry.streamcraft.config.StreamCraftConfig;
import me.crazycranberry.streamcraft.managers.ActionManager;
import me.crazycranberry.streamcraft.actions.chestofgoodies.ChestOfGoodiesManager;
import me.crazycranberry.streamcraft.actions.explosion.ExplosionManager;
import me.crazycranberry.streamcraft.actions.flyingcow.FlyingCowManager;
import me.crazycranberry.streamcraft.managers.CleanUpManager;
import me.crazycranberry.streamcraft.managers.KeepAliveManager;
import me.crazycranberry.streamcraft.actions.megajump.MegaJumpManager;
import me.crazycranberry.streamcraft.actions.nojumping.NoJumpingManager;
import me.crazycranberry.streamcraft.actions.pinatachickens.PinataChickenManager;
import me.crazycranberry.streamcraft.managers.PollManager;
import me.crazycranberry.streamcraft.managers.ReconnectRequestedManager;
import me.crazycranberry.streamcraft.twitch.websocket.TwitchClient;
import me.crazycranberry.streamcraft.twitch.websocket.model.createpoll.CreatePoll;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Scanner;
import java.util.logging.Logger;

import static me.crazycranberry.streamcraft.utils.FileUtils.loadConfig;

public final class StreamCraft extends JavaPlugin {
    private static Logger logger;
    private static StreamCraft plugin;
    private StreamCraftConfig config;
    private TwitchClient twitchClient;
    public final static String SECRET = getSecret();

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        logger = this.getLogger();
        refreshConfigs();
        twitchClient = new TwitchClient();
        registerCommands();
        registerManagers();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        twitchClient.close();
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
        setCommandManager("screfresh", new RefreshConfigCommand());
        setCommandManager("screconnect", new ReconnectToTwitchCommand());
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

    public void reconnectToTwitch(String connectionUrl) {
        twitchClient.reconnect(connectionUrl);
    }

    public void createTwitchPoll(CreatePoll poll) {
        HttpResponse<?> r = twitchClient.sendCreatePoll(poll);
    }

    public Instant timeOfLastTwitchMessage() {
        return twitchClient.getTimeOfLastMessage();
    }

    public static Logger logger() {
        return logger;
    }

    public StreamCraftConfig config() {
        return config;
    }

    public static StreamCraft getPlugin() {
        return plugin;
    }

    public String refreshConfigs() {
        try {
            config = new StreamCraftConfig(loadConfig("stream_craft.yml"));
            return "Successfully loaded configs.";
        } catch (Exception e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }

    @SneakyThrows
    private static String getSecret() {
        InputStream stream = StreamCraft.class.getClassLoader().getResource("secret.txt").openStream();
        Scanner scan = new Scanner(stream);
        String secret = scan.next();
        System.out.println("The secret: " + secret);
        return secret;
    }
}
