package me.crazycranberry.streamcraft.actionexecutors;

import me.crazycranberry.streamcraft.config.model.Action;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class Executors {
    public static List<Player> getTargetedPlayers(Action action) {
        List<String> targets = Arrays.asList(action.getTarget().split(","));
        return Bukkit.getOnlinePlayers().stream().filter(p -> targets.contains(p.getName()) || targets.contains("*")).map(OfflinePlayer::getPlayer).toList();
    }
}
