package me.crazycranberry.streamcraft.actions.soupman;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.VillagerAcquireTradeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.TradeSelectEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import static me.crazycranberry.streamcraft.actions.soupman.SoupManExecutor.doneWithSoupMan;
import static me.crazycranberry.streamcraft.actions.soupman.SoupManExecutor.isPlayerTradingWrongSoupMan;
import static me.crazycranberry.streamcraft.actions.soupman.SoupManExecutor.playerDied;
import static me.crazycranberry.streamcraft.actions.soupman.SoupManExecutor.soupDelivered;
import static me.crazycranberry.streamcraft.actions.soupman.SoupManExecutor.wasASoupTrade;
import static org.bukkit.event.inventory.InventoryType.SlotType.RESULT;

public class SoupManManager implements Listener {
    @EventHandler
    private void onPunchSoupMan(EntityDamageEvent event) {
        if (event.getEntity().getMetadata("soupman").stream().anyMatch(m -> "true".equals(m.value()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerDied(PlayerDeathEvent event) {
        playerDied(event.getPlayer());
    }

    @EventHandler
    private void onSoupTradeOpened(PlayerInteractEntityEvent event) {
        if (isPlayerTradingWrongSoupMan(event.getPlayer(), event.getRightClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onSoupTradeAccepted(InventoryClickEvent event) {
        if (event.getSlotType().equals(RESULT)) {
            System.out.println("Well it was a result");
            if (event.getWhoClicked() instanceof Player) {
                System.out.println("And it was a player");
            }
            if (wasASoupTrade(event.getWhoClicked(), event.getClickedInventory(), event.getCurrentItem())) {
                System.out.println("Wow it actually was a soup trade");
                soupDelivered((Player) event.getWhoClicked());
            }
        }
    }
}
