package me.crazycranberry.streamcraft.actions.sendtonether;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPortalEvent;

import static me.crazycranberry.streamcraft.actions.sendtonether.SendToNetherExecutor.playerTeleportingOut;
import static me.crazycranberry.streamcraft.actions.sendtonether.SendToNetherExecutor.playerStartingLoc;
import static me.crazycranberry.streamcraft.actions.sendtonether.SendToNetherExecutor.wasPlayerSentToNetherFromTwitch;

public class SendToNetherManager implements Listener {
    @EventHandler
    private void onPlayerPortal(PlayerPortalEvent event) {
        if (wasPlayerSentToNetherFromTwitch(event.getPlayer())) {
            event.getPlayer().teleport(playerStartingLoc(event.getPlayer()));
            playerTeleportingOut(event.getPlayer());
            event.setCancelled(true);
        }
    }
}
