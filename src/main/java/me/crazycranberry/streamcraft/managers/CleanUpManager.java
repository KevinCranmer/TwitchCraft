package me.crazycranberry.streamcraft.managers;

import me.crazycranberry.streamcraft.actions.weepingangel.WeepingAngelExecutor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CleanUpManager implements Listener {
    public static boolean noOneOnline = true;

    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        if (noOneOnline) {
            noOneOnline = false;
            WeepingAngelExecutor.cleanUp();
        }
    }

    @EventHandler
    private void onPlayerLeave(PlayerQuitEvent event) {
        if (Bukkit.getOnlinePlayers().size() <= 1) { // This means the last person online is quitting
            noOneOnline = true;
        }
    }
}
