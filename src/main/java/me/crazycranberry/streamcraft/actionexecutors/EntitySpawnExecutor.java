package me.crazycranberry.streamcraft.actionexecutors;

import me.crazycranberry.streamcraft.config.model.actions.EntitySpawn;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import static me.crazycranberry.streamcraft.actionexecutors.Executors.getTargetedPlayers;

public class EntitySpawnExecutor {
    public static void execute(Message twitchMessage, EntitySpawn action) {
        for (Player p : getTargetedPlayers(action)) {
            List<Location> possibleSpawnLocations = new ArrayList<>();
            for (int i = -action.getRadiusFromPlayer(); i < action.getRadiusFromPlayer(); i++) {
                for (int j = -action.getRadiusFromPlayer(); j < action.getRadiusFromPlayer(); j++) {
                    for (int k = -action.getRadiusFromPlayer(); k < action.getRadiusFromPlayer(); k++) {
                        Location potentialLoc = p.getLocation().add(i, j, k);
                        if (potentialLoc.equals(p.getLocation())) {
                            continue; // We will always add the player location in the event that there are no valid spawns
                        }
                        if (potentialLoc.add(0, 1, 0).getBlock().getType().equals(Material.AIR) && potentialLoc.add(0, 2, 0).getBlock().getType().equals(Material.AIR)) {
                            possibleSpawnLocations.add(potentialLoc);
                        }
                    }
                }
            }
            possibleSpawnLocations.add(p.getLocation());
            for (int l = 0; l < action.getQuantity(); l++) {
                int randomIndex = (int) (Math.random() * possibleSpawnLocations.size());
                Entity entity = p.getWorld().spawnEntity(possibleSpawnLocations.get(randomIndex), action.getEntity());
                entity.setCustomName(twitchMessage.getPayload().getEvent().getUser_name());
                entity.setCustomNameVisible(true);
            }
        }

    }
}
