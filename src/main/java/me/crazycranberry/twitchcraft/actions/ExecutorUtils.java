package me.crazycranberry.twitchcraft.actions;

import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.twitch.websocket.model.message.Message;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static me.crazycranberry.twitchcraft.config.TriggerType.CHANNEL_CHEER;
import static me.crazycranberry.twitchcraft.config.TriggerType.SUB_GIFT;

public class ExecutorUtils {
    public static final long TICKS_PER_SECOND = 20L;

    public static List<Player> getTargetedPlayers(Action action) {
        List<String> targets = Arrays.asList(action.getTarget().split(","));
        return Bukkit.getOnlinePlayers().stream().filter(p -> targets.contains(p.getName()) || targets.contains("*")).map(OfflinePlayer::getPlayer).toList();
    }

    public static String triggerer(Message twitchMessage, Action action) {
        String totalGiftedOrBits = "";
        if (action.getTrigger().getType().equals(SUB_GIFT)) {
            totalGiftedOrBits = " with " + twitchMessage.getPayload().getEvent().getTotal() + " gifted subs!";
        } else if (action.getTrigger().getType().equals(CHANNEL_CHEER)) {
            totalGiftedOrBits = " with " + twitchMessage.getPayload().getEvent().getBits() + " bits!";
        }
        String userThatTriggered = twitchMessage.getPayload().getEvent().getUser_name();
        return userThatTriggered == null ? "a channel poll" : userThatTriggered + " triggering " + action.getTrigger().getType() + totalGiftedOrBits;
    }

    public static <T> T randomFromList(List<T> list) {
        if (list.isEmpty()) {
            return null;
        }
        int randomIndex = (int) (Math.random() * list.size());
        return list.get(randomIndex);
    }

    public static void maybeSendPlayerMessage(Player p, Message twitchMessage, String message, Action action) {
        if (action.getSendMessage()) {
            if (action.getActionMessage() != null) {
                p.sendMessage(interpolateActionMessage(action.getActionMessage(), twitchMessage, action));
            } else {
                p.sendMessage(message);
            }
        }
    }

    public static void maybeSendPlayerSecondaryMessage(Player p, String message, Action action) {
        if (action.getSendMessage()) {
            p.sendMessage(message);
        }
    }

    @NotNull
    public static List<Location> getPossibleSpawnLocations(Player p, int radius) {
        return getPossibleSpawnLocations(p, radius, ExecutorUtils::isValidSpawnBlock);
    }

    @NotNull
    public static List<Location> getPossibleSpawnLocations(Player p, int radius, Function<Location, Boolean> validityFunction) {
        List<Location> possibleSpawnLocations = new ArrayList<>();
        double x = Math.floor(p.getLocation().getX()) + 0.5;
        double y = p.getLocation().getY();
        double z = Math.floor(p.getLocation().getZ()) + 0.5;
        for (int i = -radius; i < radius; i++) {
            for (int j = -radius; j < radius; j++) {
                for (int k = -radius; k < radius; k++) {
                    Location potentialLoc = new Location(p.getWorld(), x + i, y + j, z + k);
                    if (i == 0 && j == 0 && k == 0) {
                        continue; // We will always add the player location in the event that there are no valid spawns
                    }
                    if (validityFunction.apply(potentialLoc)) {
                        possibleSpawnLocations.add(potentialLoc);
                    }
                }
            }
        }
        possibleSpawnLocations.add(new Location(p.getWorld(), radius, y, z));
        return possibleSpawnLocations;
    }

    /** Makes sure there is a 1x2x1 box open at the given location. */
    public static boolean isValidSpawnBlock(Location loc, boolean allowWater) {
        Block block = loc.getBlock();
        Block blockAbove = loc.getBlock().getRelative(0, 1, 0);
        return (block.getType().equals(Material.AIR) || (block.getType().equals(Material.WATER)) && allowWater) &&
                (blockAbove.getType().equals(Material.AIR) || (blockAbove.getType().equals(Material.WATER)) && allowWater);
    }

    /** Makes sure there is a 1x2x1 box open at the given location. */
    public static boolean isValidSpawnBlock(Location loc) {
        return isValidSpawnBlock(loc, true);
    }

    public static List<Vector> getPossiblePerimeterSpawnLocations(int distanceFromPlayer, int ySpawnRange, Player p) {
        return getPossiblePerimeterSpawnLocations(distanceFromPlayer, ySpawnRange, p, true);
    }

    public static List<Vector> getPossiblePerimeterSpawnLocations(int distanceFromPlayer, int ySpawnRange, Player p, boolean allowWater) {
        List<Vector> possibleSpawnOffsets = new ArrayList<>();
        double x = Math.floor(p.getLocation().getX()) + 0.5;
        double y = p.getLocation().getY();
        double z = Math.floor(p.getLocation().getZ()) + 0.5;
        for (int i = -distanceFromPlayer; i < distanceFromPlayer; i++) {
            for (int k = -ySpawnRange; k < ySpawnRange; k++) {
                Vector potentialOffset1 = new Vector(i, k, distanceFromPlayer);
                Vector potentialOffset2 = new Vector(i, k, -distanceFromPlayer);
                if (isValidSpawnBlock(new Location(p.getWorld(),potentialOffset1.getX() + x, potentialOffset1.getY() + y, potentialOffset1.getZ() + z), allowWater)) {
                    possibleSpawnOffsets.add(potentialOffset1);
                }
                if (isValidSpawnBlock(new Location(p.getWorld(),potentialOffset2.getX() + x, potentialOffset2.getY() + y, potentialOffset2.getZ() + z), allowWater)) {
                    possibleSpawnOffsets.add(potentialOffset2);
                }
            }
        }
        for (int j = -distanceFromPlayer; j < distanceFromPlayer; j++) {
            for (int k = -ySpawnRange; k < ySpawnRange; k++) {
                Vector potentialOffset1 = new Vector(distanceFromPlayer, k, j);
                Vector potentialOffset2 = new Vector(-distanceFromPlayer, k, j);
                if (isValidSpawnBlock(new Location(p.getWorld(),potentialOffset1.getX() + x, potentialOffset1.getY() + y, potentialOffset1.getZ() + z), allowWater)) {
                    possibleSpawnOffsets.add(potentialOffset1);
                }
                if (isValidSpawnBlock(new Location(p.getWorld(),potentialOffset2.getX() + x, potentialOffset2.getY() + y, potentialOffset2.getZ() + z), allowWater)) {
                    possibleSpawnOffsets.add(potentialOffset2);
                }
            }
        }
        return possibleSpawnOffsets;
    }

    public static boolean isFacing(Player p, Entity target, double lenience) {
        if (p == null || target == null) {
            return false;
        }
        Location targetLocation = target.getLocation().clone();
        Location playerLocation = p.getLocation().clone();
        Vector locationDifferenceNomalized = targetLocation.toVector().subtract(playerLocation.toVector()).normalize();
        Vector locationDifference = targetLocation.toVector().subtract(playerLocation.toVector());
        Vector playerDirection = playerLocation.getDirection();
        Vector difference = locationDifferenceNomalized.subtract(playerDirection);
        double dist = Math.sqrt(locationDifference.lengthSquared());
        if (dist < 10) {
            return difference.lengthSquared() <= (lenience * Math.pow(10 / dist, 0.75));
        }
        return difference.lengthSquared() <= lenience;
    }

    public static boolean hasLineOfSight(Player p, Entity target, double maxDistance) {
        if (p == null || target == null) {
            return false;
        }
        Location targetLocation = target.getLocation().clone();
        Location playerLocation = p.getLocation(); // to get to eye level
        // Be lenient on the los, try to find los in a box around the players head
        Boolean atLeastALittleLOS = false;
        for (double i = -0.5; i <= 0.5; i+=0.5) {
            for (double j = -0.5; j <= 0.5; j+=0.5) {
                for (double k = -0.5; k <= 0.5; k+=0.5) {
                    RayTraceResult result = p.getWorld().rayTraceBlocks(playerLocation.clone().add(i, j + 1, k), playerLocation.getDirection(), maxDistance);
                    if (result == null) {
                        atLeastALittleLOS = true;
                        break;
                    }
                    Block blockBeingLookedAt = result.getHitBlock();
                    if (blockBeingLookedAt == null) {
                        atLeastALittleLOS = true;
                        break;
                    }
                    Vector targetLocationDifference = targetLocation.toVector().subtract(playerLocation.toVector());
                    Vector blockLocationDifference = blockBeingLookedAt.getLocation().toVector().subtract(playerLocation.toVector());
                    if (targetLocationDifference.lengthSquared() < blockLocationDifference.lengthSquared() ||
                            (blockBeingLookedAt.getY() <= playerLocation.getY() && blockBeingLookedAt.getY() <= targetLocation.getY())) { // if you're just looking at the block beneath you and the target, it shouldn't count as blocking los
                        atLeastALittleLOS = true;
                        break;
                    }
                }
                if (atLeastALittleLOS) {
                    break;
                }
            }
            if (atLeastALittleLOS) {
                break;
            }
        }
        return atLeastALittleLOS;
    }

    public static boolean isAboveGround(Player p) {
        RayTraceResult result = p.getWorld().rayTraceBlocks(p.getLocation(), new Vector(0, 1, 0), 256);
        return result == null || result.getHitBlock() == null;
    }

    public static String interpolateColors(String str) {
        return str
                .replace("{COLOR:AQUA}", ChatColor.AQUA.toString())
                .replace("{COLOR:BLACK}",  ChatColor.BLACK.toString())
                .replace("{COLOR:BLUE}",  ChatColor.BLUE.toString())
                .replace("{COLOR:BOLD}",  ChatColor.BOLD.toString())
                .replace("{COLOR:DARK_AQUA}",  ChatColor.DARK_AQUA.toString())
                .replace("{COLOR:DARK_BLUE}",  ChatColor.DARK_BLUE.toString())
                .replace("{COLOR:DARK_GRAY}",  ChatColor.DARK_GRAY.toString())
                .replace("{COLOR:DARK_GREEN}",  ChatColor.DARK_GREEN.toString())
                .replace("{COLOR:DARK_PURPLE}",  ChatColor.DARK_PURPLE.toString())
                .replace("{COLOR:DARK_RED}",  ChatColor.DARK_RED.toString())
                .replace("{COLOR:GOLD}",  ChatColor.GOLD.toString())
                .replace("{COLOR:GRAY}",  ChatColor.GRAY.toString())
                .replace("{COLOR:GREEN}",  ChatColor.GREEN.toString())
                .replace("{COLOR:ITALIC}",  ChatColor.ITALIC.toString())
                .replace("{COLOR:LIGHT_PURPLE}",  ChatColor.LIGHT_PURPLE.toString())
                .replace("{COLOR:MAGIC}",  ChatColor.MAGIC.toString())
                .replace("{COLOR:RED}",  ChatColor.RED.toString())
                .replace("{COLOR:STRIKETHROUGH}",  ChatColor.STRIKETHROUGH.toString())
                .replace("{COLOR:UNDERLINE}",  ChatColor.UNDERLINE.toString())
                .replace("{COLOR:WHITE}",  ChatColor.WHITE.toString())
                .replace("{COLOR:YELLOW}",  ChatColor.YELLOW.toString());
    }

    public static String interpolateActionMessage(String actionMessage, Message twitchMessage, Action action) {
        String userThatTriggered = twitchMessage.getPayload().getEvent().getUser_name();
        Integer quantity = null;
        if (action.getTrigger().getType().equals(SUB_GIFT)) {
            quantity = twitchMessage.getPayload().getEvent().getTotal();
        } else {
            quantity = twitchMessage.getPayload().getEvent().getBits();
        }
        return interpolateColors(actionMessage)
                .replace("{TRIGGER_TYPE}", action.getTrigger().getType().toString())
                .replace("{TRIGGER_CAUSE}", userThatTriggered == null ? "a channel poll" : userThatTriggered)
                .replace("{QUANTITY}", quantity == null ? "ERROR" : String.valueOf(quantity));
    }
}
