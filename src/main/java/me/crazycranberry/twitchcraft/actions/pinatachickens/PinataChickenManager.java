package me.crazycranberry.twitchcraft.actions.pinatachickens;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Firework;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.util.Vector;

import java.util.List;

import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.randomFromList;

public class PinataChickenManager implements Listener {
    private static final List<Color> fireworkColors = List.of(
            Color.BLUE,
            Color.WHITE,
            Color.SILVER,
            Color.GRAY,
            Color.BLACK,
            Color.RED,
            Color.MAROON,
            Color.YELLOW,
            Color.OLIVE,
            Color.LIME,
            Color.GREEN,
            Color.AQUA,
            Color.TEAL,
            Color.NAVY,
            Color.FUCHSIA,
            Color.PURPLE,
            Color.ORANGE
    );

    @EventHandler
    private void onChickenHurtByPlayer(EntityDamageEvent event) {
        if ("Piñata".equals(event.getEntity().getCustomName()) && event.getEntity() instanceof Chicken && event.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
            event.setDamage(10000);
        }
    }

    @EventHandler
    private void onChickenDeath(EntityDeathEvent event) {
        if("Piñata".equals(event.getEntity().getCustomName())) {
            event.setDroppedExp(0);
            event.getDrops().clear();
            int randomIndex = (int) (Math.random() * (PinataChickensExecutor.nonItemGoodies.size() + PinataChickensExecutor.Goodies.droppableItems.size()));
            spawnFirework(event.getEntity());
            if (randomIndex < PinataChickensExecutor.nonItemGoodies.size()) {
                PinataChickensExecutor.nonItemGoodies.get(randomIndex).accept(event.getEntity().getLocation());
            } else {
                PinataChickensExecutor.Goodies.dropItem(event.getEntity().getLocation());
            }
        }
    }

    private void spawnFirework(Entity chicken) {
        Firework fw = (Firework) chicken.getWorld().spawnEntity(chicken.getLocation().add(new Vector(0, 0.25, 0)), EntityType.FIREWORK);
        FireworkMeta fwm = fw.getFireworkMeta();
        fwm.setPower(1);
        fwm.addEffect(FireworkEffect.builder().withColor(randomFromList(fireworkColors)).flicker(true).build());
        fwm.setDisplayName("PinataFirework");
        fw.setFireworkMeta(fwm);
        fw.detonate();
    }

    @EventHandler
    private void onFireworkDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Firework) {
            Firework fw = (Firework) event.getDamager();
            if (fw.getFireworkMeta().getDisplayName().equalsIgnoreCase("PinataFirework")) {
                event.setCancelled(true);
            }
        }
    }
}
