package me.crazycranberry.streamcraft.actions.weepingangel;

import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import static me.crazycranberry.streamcraft.actions.ExecutorUtils.hasLineOfSight;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.isFacing;
import static me.crazycranberry.streamcraft.actions.weepingangel.WeepingAngelExecutor.activateAngel;
import static me.crazycranberry.streamcraft.actions.weepingangel.WeepingAngelExecutor.freezeAngel;
import static me.crazycranberry.streamcraft.actions.weepingangel.WeepingAngelExecutor.getAngelForPlayer;
import static me.crazycranberry.streamcraft.actions.weepingangel.WeepingAngelExecutor.removeAngel;

public class WeepingAngelManager implements Listener {
    @EventHandler
    private void onPlayerMove(PlayerMoveEvent event) {
        Player p = event.getPlayer();
        Zombie angel = getAngelForPlayer(p);
        if (isFacing(p, angel, 0.2) && hasLineOfSight(p, angel, 96)) {
            freezeAngel(p);
        } else {
            activateAngel(p);
        }
    }

    @EventHandler
    private void onHitByWeepingAngel(EntityDamageByEntityEvent event) {
        if (event.getEntity() instanceof Player && event.getDamager().getMetadata("weepingangel").stream().anyMatch(m -> "true".equals(m.value()))) {
            event.setDamage(1000);
            removeAngel((Player) event.getEntity());
        }
    }

    @EventHandler
    private void onWeepingAngelDamaged(EntityDamageEvent event) {
        if (event.getEntity().getMetadata("weepingangel").stream().anyMatch(m -> "true".equals(m.value()))) {
            event.setCancelled(true);
        }
    }
}
