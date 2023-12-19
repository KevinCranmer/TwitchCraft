package me.crazycranberry.streamcraft.managers;

import me.crazycranberry.streamcraft.actionexecutors.EntitySpawnExecutor;
import me.crazycranberry.streamcraft.config.model.Action;
import me.crazycranberry.streamcraft.config.model.TriggerType;
import me.crazycranberry.streamcraft.config.model.actions.EntitySpawn;
import me.crazycranberry.streamcraft.events.ChannelFollowEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

import static me.crazycranberry.streamcraft.StreamCraft.getPlugin;
import static me.crazycranberry.streamcraft.StreamCraft.logger;

public class ActionManager implements Listener {
    List<Action> actions;

    public ActionManager() {
        actions = getPlugin().config().getActions();
    }

    @EventHandler
    private void onFollowAction(ChannelFollowEvent event) {
        List<EntitySpawn> followActions = actions.stream()
                .filter(a -> a.getTrigger().getType().equals(TriggerType.CHANNEL_FOLLOW))
                .filter(a -> a instanceof EntitySpawn)
                .map(a -> (EntitySpawn) a)
                .toList();
        if (followActions.size() > 1) {
            logger().warning("Conflicting actions on follow trigger. Selecting the first action from this list: " + followActions);
        }
        if (followActions.size() > 0) {
            EntitySpawnExecutor.execute(event.twitchMessage(), followActions.get(0));
        }
    }
}
