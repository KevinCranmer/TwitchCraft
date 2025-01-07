package me.crazycranberry.twitchcraft.actions.entityspawn;

import me.crazycranberry.twitchcraft.actions.Executor;
import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.twitch.websocket.model.message.Message;
import me.crazycranberry.twitchcraft.twitch.websocket.model.message.MessageEvent;
import me.crazycranberry.twitchcraft.twitch.websocket.model.message.MessagePayload;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import java.util.List;
import java.util.Optional;

import static me.crazycranberry.twitchcraft.TwitchCraft.logger;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.getPossibleSpawnLocations;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.randomFromList;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.triggerer;

public class EntitySpawnExecutor implements Executor {
    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof EntitySpawn)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        EntitySpawn es = (EntitySpawn) action;
        for (Player p : getTargetedPlayers(es)) {
            maybeSendPlayerMessage(p, twitchMessage, String.format("Spawning %s%s's%s, courtesy of %s%s%s", ChatColor.GOLD, es.getEntity().name(), ChatColor.RESET, ChatColor.GOLD, triggerer(twitchMessage, action), ChatColor.RESET), action);
            Integer numEntities = es.getQuantity();
            if (es.isUseTriggerQuantity()) {
                Integer numBits = Optional.ofNullable(twitchMessage.getPayload()).map(MessagePayload::getEvent).map(MessageEvent::getBits).orElse(-1);
                Integer numGifts = Optional.ofNullable(twitchMessage.getPayload()).map(MessagePayload::getEvent).map(MessageEvent::getTotal).orElse(-1);
                numEntities = (int) (Math.max(numBits, numGifts) * es.getQuantityFactor());
            }
            List<Location> possibleSpawnLocations = getPossibleSpawnLocations(p, es.getRadiusFromPlayer());
            for (int l = 0; l < numEntities; l++) {
                Entity entity = p.getWorld().spawnEntity(randomFromList(possibleSpawnLocations), es.getEntity());
                if (entity instanceof Ageable && es.isBaby()) {
                    ((Ageable) entity).setBaby();
                }
                if (twitchMessage.getPayload().getEvent().getUser_name() != null) {
                    entity.setCustomName(twitchMessage.getPayload().getEvent().getUser_name());
                    entity.setCustomNameVisible(true);
                }
            }
        }
    }
}
