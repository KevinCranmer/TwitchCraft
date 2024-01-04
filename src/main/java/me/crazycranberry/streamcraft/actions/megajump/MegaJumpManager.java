package me.crazycranberry.streamcraft.actions.megajump;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import static me.crazycranberry.streamcraft.StreamCraft.getPlugin;
import static me.crazycranberry.streamcraft.actions.megajump.MegaJumpExecutor.playerIgnoredMegaJumpFallDamage;
import static me.crazycranberry.streamcraft.actions.megajump.MegaJumpExecutor.playerJumped;
import static me.crazycranberry.streamcraft.actions.megajump.MegaJumpExecutor.shouldPlayerIgnoreFallDamage;
import static me.crazycranberry.streamcraft.actions.megajump.MegaJumpExecutor.shouldPlayerMegaJump;

public class MegaJumpManager implements Listener {
    @EventHandler
    private void onJump(PlayerJumpEvent event) {
        if (shouldPlayerMegaJump(event.getPlayer())) {
            Vector currentVel = event.getPlayer().getVelocity();
            event.setCancelled(true);
            new java.util.Timer().schedule(
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            Bukkit.getServer().getScheduler().callSyncMethod(getPlugin(), () -> {
                                event.getPlayer().setVelocity(new Vector(currentVel.getX(), 3.0, currentVel.getZ()));
                                playerJumped(event.getPlayer());
                                return true;
                            });
                        }
                    },
                    1
            );
        }
    }

    @EventHandler
    private void onDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player && event.getCause().equals(EntityDamageEvent.DamageCause.FALL) && shouldPlayerIgnoreFallDamage((Player) event.getEntity())) {
            event.setCancelled(true);
            playerIgnoredMegaJumpFallDamage((Player) event.getEntity());
        }
    }
}
