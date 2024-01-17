package me.crazycranberry.twitchcraft.actions.nojumping;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import static me.crazycranberry.twitchcraft.actions.nojumping.NoJumpingExecutor.playerCanJump;

public class NoJumpingManager implements Listener {
    @EventHandler
    private void onJump(PlayerJumpEvent event) {
        if (!playerCanJump(event.getPlayer())) {
            event.setCancelled(true);
        }
    }
}
