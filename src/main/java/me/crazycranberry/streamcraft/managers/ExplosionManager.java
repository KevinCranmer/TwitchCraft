package me.crazycranberry.streamcraft.managers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import static me.crazycranberry.streamcraft.actionexecutors.ExplosionExecutor.protectPlayer;

public class ExplosionManager implements Listener {
    @EventHandler
    private void onExplosion(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION) && protectPlayer((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }
}
