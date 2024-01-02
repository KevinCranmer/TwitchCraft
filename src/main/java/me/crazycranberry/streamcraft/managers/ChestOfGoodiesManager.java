package me.crazycranberry.streamcraft.managers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import static me.crazycranberry.streamcraft.actionexecutors.ChestOfGoodiesExecutor.protectPlayer;

public class ChestOfGoodiesManager implements Listener {
    @EventHandler
    private void onLightning(EntityDamageEvent event) {
        System.out.println("Damage was from: " + event.getCause());
        System.out.println(event.getEntity() instanceof Player);
        if (event.getEntity() instanceof Player && event.getCause().equals(EntityDamageEvent.DamageCause.LIGHTNING) && protectPlayer((Player) event.getEntity())) {
            event.setCancelled(true);
        }
    }
}
