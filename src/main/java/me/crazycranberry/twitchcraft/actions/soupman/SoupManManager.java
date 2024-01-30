package me.crazycranberry.twitchcraft.actions.soupman;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

import static me.crazycranberry.twitchcraft.TwitchCraft.getPlugin;
import static me.crazycranberry.twitchcraft.actions.soupman.SoupManExecutor.isPlayerTradingCorrectSoupMan;
import static me.crazycranberry.twitchcraft.actions.soupman.SoupManExecutor.playerDied;
import static me.crazycranberry.twitchcraft.actions.soupman.SoupManExecutor.soupDelivered;
import static me.crazycranberry.twitchcraft.actions.soupman.SoupManExecutor.soupManDied;
import static me.crazycranberry.twitchcraft.actions.soupman.SoupManExecutor.wasASoupTrade;
import static org.bukkit.event.inventory.InventoryType.SlotType.RESULT;

public class SoupManManager implements Listener {
    private static final List<Material> soups = List.of(
        Material.BEETROOT_SOUP,
        Material.MUSHROOM_STEW,
        Material.RABBIT_STEW,
        Material.SUSPICIOUS_STEW
    );

    @EventHandler
    private void onPunchSoupMan(EntityDamageEvent event) {
        if (event.getEntity().getMetadata("soupman").stream().anyMatch(m -> "true".equals(m.value())) && !event.getCause().equals(EntityDamageEvent.DamageCause.KILL)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onPlayerDied(PlayerDeathEvent event) {
        playerDied(event.getPlayer());
    }

    @EventHandler
    private void onSoupTradeOpened(PlayerInteractEntityEvent event) {
        if (event.getRightClicked().getMetadata("soupman").stream().anyMatch(m -> "true".equals(m.value())) && !isPlayerTradingCorrectSoupMan(event.getPlayer(), event.getRightClicked())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    private void onSoupTradeAccepted(InventoryClickEvent event) {
        if (event.getSlotType().equals(RESULT) && event.getWhoClicked() instanceof Player && wasASoupTrade(event)) {
            Player p = (Player) event.getWhoClicked();
            removeTheSoup(p.getInventory());
            soupDelivered((Player) event.getWhoClicked());
        }
    }

    @EventHandler
    private void onSoupManDied(EntityDeathEvent event) {
        if (event.getEntity().getMetadata("soupman").stream().anyMatch(m -> "true".equals(m.value()))) {
            soupManDied((WanderingTrader) event.getEntity());
        }
    }

    private void removeTheSoup(PlayerInventory inventory) {
        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        Bukkit.getServer().getScheduler().callSyncMethod(getPlugin(), () -> {
                            for (int i = 0; i < inventory.getContents().length; i++) {
                                ItemStack item = inventory.getContents()[i];
                                if (item != null && soups.contains(item.getType())) {
                                    inventory.setItem(i, null);
                                    break;
                                }
                            }
                            return true;
                        });
                    }
                },
                200
        );
    }

    @EventHandler
    private void onPotionEffect(EntityPotionEffectEvent event) {
        if (event.getEntity().getMetadata("soupman").stream().anyMatch(m -> "true".equals(m.value())) && event.getNewEffect().getType().equals(PotionEffectType.INVISIBILITY)) {
            event.setCancelled(true);
        }
    }
}
