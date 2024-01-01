package me.crazycranberry.streamcraft.managers;

import org.bukkit.entity.Cow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class FlyingCowManager implements Listener {
    @EventHandler
    private void onCowFall(EntityDamageEvent event) {
        if (event.getEntity() instanceof Cow && event.getCause().equals(EntityDamageEvent.DamageCause.FALL) && event.getEntity().getMetadata("flyingcow").stream().anyMatch(m -> "true".equals(m.value()))) {
            event.setCancelled(true);
        }
    }
}
