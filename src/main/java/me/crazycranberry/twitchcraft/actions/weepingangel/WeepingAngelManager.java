package me.crazycranberry.twitchcraft.actions.weepingangel;

import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.hasLineOfSight;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.isFacing;
import static me.crazycranberry.twitchcraft.actions.weepingangel.WeepingAngelExecutor.activateAngel;
import static me.crazycranberry.twitchcraft.actions.weepingangel.WeepingAngelExecutor.freezeAngel;
import static me.crazycranberry.twitchcraft.actions.weepingangel.WeepingAngelExecutor.getAngelForPlayer;
import static me.crazycranberry.twitchcraft.actions.weepingangel.WeepingAngelExecutor.removeAngel;

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
            event.setDamage(EntityDamageEvent.DamageModifier.BASE, 20);
            event.setDamage(EntityDamageEvent.DamageModifier.HARD_HAT, 0);
            event.setDamage(EntityDamageEvent.DamageModifier.BLOCKING, 0);
            event.setDamage(EntityDamageEvent.DamageModifier.ARMOR, 0);
            event.setDamage(EntityDamageEvent.DamageModifier.RESISTANCE, 0);
            event.setDamage(EntityDamageEvent.DamageModifier.MAGIC, 0);
            event.setDamage(EntityDamageEvent.DamageModifier.ABSORPTION, 0);
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
