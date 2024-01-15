package me.crazycranberry.streamcraft.actions.weepingangel;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.crazycranberry.streamcraft.actions.Executor;
import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

import static me.crazycranberry.streamcraft.StreamCraft.getPlugin;
import static me.crazycranberry.streamcraft.StreamCraft.logger;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.TICKS_PER_SECOND;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.getPossiblePerimeterSpawnLocations;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.randomFromList;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.triggerer;

public class WeepingAngelExecutor implements Executor {
    private static WeepingAngelManager listener = new WeepingAngelManager();
    private static Map<Player, WeepingAngelStats> playerAngels = new HashMap<>();

    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof WeepingAngel)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        WeepingAngel wa = (WeepingAngel) action;
        for (Player p : getTargetedPlayers(wa)) {
            maybeSendPlayerMessage(p, String.format("A %sWeeping Angel%s is hunting you. Courtesy of %s%s%s", ChatColor.GOLD, ChatColor.RESET, ChatColor.GOLD, triggerer(twitchMessage, wa), ChatColor.RESET), wa);
            Vector spawnOffset = randomFromList(getPossiblePerimeterSpawnLocations(wa.getDistanceFromPlayer(), 5, p, false));
            Zombie weepingAngel = (Zombie) p.getWorld().spawnEntity(p.getLocation().add(spawnOffset), EntityType.ZOMBIE, false);
            pimpOutAngel(weepingAngel, wa.getSecondsTillDespawn());
            weepingAngel.setTarget(p);
            WeepingAngelStats was = playerAngels.get(p);
            p.playSound(
                    Sound.sound()
                            .source(Sound.Source.AMBIENT)
                            .volume(1)
                            .seed(1)
                            .type(Key.key("ambient.cave"))
                            .build());
            if (was != null) {
                removeAngel(p);
            }
            playerAngels.put(p, WeepingAngelStats.builder()
                    .action(wa)
                    .zombie(weepingAngel)
                    .secondsLeft(wa.getSecondsTillDespawn())
                    .taskId(checkWhenToRemoveAngelTask(p))
                    .build());
        }
        Bukkit.getServer().getPluginManager().registerEvents(listener, getPlugin());
    }

    private void pimpOutAngel(Zombie weepingAngel, int durationSeconds) {
        weepingAngel.customName(Component.text("Weeping Angel", Style.style(NamedTextColor.BLACK)));
        weepingAngel.setCustomNameVisible(true);
        weepingAngel.setMetadata("weepingangel", new FixedMetadataValue(getPlugin(), "true"));
        giveArmor(weepingAngel);
        weepingAngel.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, (int)TICKS_PER_SECOND * durationSeconds, 2));
    }

    private Integer checkWhenToRemoveAngelTask(Player p) {
        return Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
            WeepingAngelStats stats = playerAngels.get(p);
            if (stats == null || stats.getSecondsLeft() <= 0) {
                removeAngel(p);
            } else {
                stats.setSecondsLeft(stats.getSecondsLeft() - 1);
            }
        }, 0 /*<-- the initial delay */, TICKS_PER_SECOND /*<-- the interval */).getTaskId();
    }

    /**
     * This should trigger on 3 cases:
     * 1. The despawn timer has finished
     * 2. The Weeping Angel has successful hit (killed) the targeted player
     * 3. A new Weeping Angel action for the Player has triggered
     */
    public static void removeAngel(Player p) {
        WeepingAngelStats stats = playerAngels.get(p);
        if (stats != null) {
            maybeSendPlayerMessage(p, "The Weeping Angel has crumbled away", stats.getAction());
            stats.getZombie().remove();
        }
        playerAngels.remove(p);
        if (playerAngels.keySet().isEmpty()) {
            HandlerList.unregisterAll(listener); // The PlayerMoveEvent sends A LOT, so lets disable it if we're not using it
        }
        Bukkit.getScheduler().cancelTask(stats.getTaskId());
    }

    public static Zombie getAngelForPlayer(Player p) {
        WeepingAngelStats was = playerAngels.get(p);
        return was == null ? null : was.getZombie();
    }

    public static void freezeAngel(Player p) {
        Zombie angel = getAngelForPlayer(p);
        if (angel == null) {
            return;
        }
        angel.setAI(false);
        angel.setCustomNameVisible(true);
    }

    public static void activateAngel(Player p) {
        Zombie angel = getAngelForPlayer(p);
        if (angel == null) {
            return;
        }
        angel.setAI(true);
        angel.setTarget(p);
        angel.setCustomNameVisible(false);
    }

    private void giveArmor(Zombie zombie) {
        setHelmet(zombie);
        setChest(zombie);
        setLeggings(zombie);
        setBoots(zombie);
    }

    private void setHelmet(Zombie zombie) {
        ItemStack helmet = new ItemStack(Material.LEATHER_HELMET);
        LeatherArmorMeta helmetMeta = (LeatherArmorMeta) helmet.getItemMeta();
        helmetMeta.setColor(Color.WHITE);
        helmet.setItemMeta(helmetMeta);
        zombie.getEquipment().setHelmet(helmet);
    }

    private void setChest(Zombie zombie) {
        ItemStack chest = new ItemStack(Material.LEATHER_CHESTPLATE);
        LeatherArmorMeta chestMeta = (LeatherArmorMeta) chest.getItemMeta();
        chestMeta.setColor(Color.WHITE);
        chest.setItemMeta(chestMeta);
        zombie.getEquipment().setChestplate(chest);
    }

    private void setLeggings(Zombie zombie) {
        ItemStack leggings = new ItemStack(Material.LEATHER_LEGGINGS);
        LeatherArmorMeta leggingsMeta = (LeatherArmorMeta) leggings.getItemMeta();
        leggingsMeta.setColor(Color.WHITE);
        leggings.setItemMeta(leggingsMeta);
        zombie.getEquipment().setLeggings(leggings);
    }

    private void setBoots(Zombie zombie) {
        ItemStack boots = new ItemStack(Material.LEATHER_BOOTS);
        boots.addEnchantment(Enchantment.DEPTH_STRIDER, 3);
        LeatherArmorMeta bootsMeta = (LeatherArmorMeta) boots.getItemMeta();
        bootsMeta.setColor(Color.WHITE);
        boots.setItemMeta(bootsMeta);
        zombie.getEquipment().setBoots(boots);
    }

    @Getter
    @Setter
    @Builder
    private static class WeepingAngelStats {
        private WeepingAngel action;
        private Zombie zombie;
        private Integer secondsLeft;
        private Integer taskId;
    }

    public static void cleanUp() {
        for (World world : Bukkit.getWorlds()) {
            for (Entity entity : world.getEntitiesByClass(Zombie.class)) {
                if (entity.getMetadata("weepingangel").stream().anyMatch(m -> "true".equals(m.value()))) {
                    entity.remove();
                }
            }
        }
    }
}
