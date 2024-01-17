package me.crazycranberry.twitchcraft.actions.chestofgoodies;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import static me.crazycranberry.twitchcraft.actions.chestofgoodies.ChestOfGoodiesExecutor.protectPlayer;

public class ChestOfGoodiesManager implements Listener {
    @EventHandler
    private void onLightning(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause().equals(EntityDamageEvent.DamageCause.LIGHTNING) && protectPlayer((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }
}
