package me.crazycranberry.streamcraft.actions.sendtonether;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.crazycranberry.streamcraft.actions.Executor;
import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.crazycranberry.streamcraft.StreamCraft.logger;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.getPossibleSpawnLocations;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.randomFromList;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.triggerer;

public class SendToNetherExecutor implements Executor {
    private static Map<Player, SendToNetherStats> stats = new HashMap<>();

    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof SendToNether)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        World nether = Bukkit.getServer().getWorld("world_nether");
        if (nether == null) {
            logger().warning("Uh oh, couldn't find the world_nether world");
            return;
        }
        SendToNether stn = (SendToNether) action;
        for (Player p : getTargetedPlayers(stn)) {
            if (p.getWorld().equals(nether)) {
                logger().warning("Player is already in the nether.");
                return;
            }
            maybeSendPlayerMessage(p, String.format("A quick trip to the %sNether%s (return portal somewhere nearby). Courtesy of %s%s%s", ChatColor.GOLD, ChatColor.RESET, ChatColor.GOLD, triggerer(twitchMessage, action), ChatColor.RESET), action);
            Location startingLoc = p.getLocation().clone();
            p.teleport(nether.getSpawnLocation());
            List<Location> possiblePortalLocations = getPossibleSpawnLocations(p, stn.getNetherPortalPossibleRadius(), SendToNetherExecutor::isValidPortalSpot);
            Location portalCorner = randomFromList(possiblePortalLocations);
            stats.put(p, SendToNetherStats.builder()
                    .playerStartingLoc(startingLoc)
                    .portalCorner(portalCorner)
                    .build());
            buildNetherPortal(portalCorner);
        }
    }

    private void buildNetherPortal(Location portalCorner) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                if (i == 0 || i == 3 || j == 0 || j == 4) {
                    portalCorner.clone().add(i, j, 0).getBlock().setType(Material.OBSIDIAN);
                }
            }
        }
        for (int i = 1; i < 3; i++) {
            for (int j = 1; j < 4; j++) {
                portalCorner.clone().add(i, j, 0).getBlock().setType(Material.NETHER_PORTAL);
            }
        }
    }

    private static boolean isValidPortalSpot(Location loc) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                if (loc.clone().add(i, j, 0).getBlock().getType() != Material.AIR) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean wasPlayerSentToNetherFromTwitch(Player p) {
        return stats.containsKey(p);
    }

    public static Location playerStartingLoc(Player p) {
        return stats.get(p).getPlayerStartingLoc();
    }

    public static void playerTeleportingOut(Player p) {
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 5; j++) {
                Location blockLoc = stats.get(p).getPortalCorner().clone().add(i, j, 0);
                blockLoc.getBlock().setType(Material.AIR);
            }
        }
        stats.remove(p);
    }

    @Getter
    @Setter
    @Builder
    private static class SendToNetherStats {
        private Location portalCorner;
        private Location playerStartingLoc;
        private Integer taskId;
    }
}
