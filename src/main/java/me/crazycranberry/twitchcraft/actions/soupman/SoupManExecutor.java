package me.crazycranberry.twitchcraft.actions.soupman;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import me.crazycranberry.twitchcraft.actions.Executor;
import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.twitch.websocket.model.message.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.WanderingTrader;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.crazycranberry.twitchcraft.TwitchCraft.getPlugin;
import static me.crazycranberry.twitchcraft.TwitchCraft.logger;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.TICKS_PER_SECOND;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.beautifyActionMessage;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.getPossibleSpawnLocations;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.getTargetedPlayers;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.isAboveGround;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.maybeSendPlayerSecondaryMessage;
import static me.crazycranberry.twitchcraft.actions.ExecutorUtils.randomFromList;

public class SoupManExecutor implements Executor {
    private static final int intervalTicks = 20;
    private static Map<Player, SoupManStats> stats = new HashMap<>();

    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof SoupMan)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        SoupMan sm = (SoupMan) action;
        for (Player p : getTargetedPlayers(sm)) {
            if (stats.containsKey(p)) {
                logger().warning("A player is already being bothered by a soup man");
                continue;
            }
            maybeSendPlayerMessage(p, twitchMessage, "<SoupMan>Hello traveler! Could I bother you for some soup please?", action);
            WanderingTrader soupMan = (WanderingTrader) p.getWorld().spawnEntity(randomFromList(getPossibleSpawnLocations(p, 5)), EntityType.WANDERING_TRADER);
            soupMan.setRecipes(soupManTrades());
            soupMan.setTarget(p);
            soupMan.setMetadata("soupman", new FixedMetadataValue(getPlugin(), "true"));
            soupMan.setMetadata("ispissed", new FixedMetadataValue(getPlugin(), "false"));
            soupMan.setInvisible(false);
            soupMan.customName(Component.text("Soup Man"));
            soupMan.setCustomNameVisible(true);
            stats.put(p, SoupManStats.builder()
                    .intervalsLeft((int) (sm.getMinutesTillAngry() * (60 / (intervalTicks / TICKS_PER_SECOND))))
                    .soupMan(soupMan)
                    .action(sm)
                    .twitchMessage(twitchMessage)
                    .taskId(Bukkit.getScheduler().runTaskTimer(getPlugin(), () -> {
                        SoupManStats sms = stats.get(p);
                        if (sms != null) {
                            if (sms.getIntervalsLeft() % (60 / (intervalTicks / TICKS_PER_SECOND)) == 0 && soupMan.getLocation().distanceSquared(p.getLocation()) > 50) {
                                soupMan.teleport(p);
                            }
                            if (sms.getIntervalsLeft() == (int) (sm.getMinutesTillAngry() * (30 / (intervalTicks / TICKS_PER_SECOND)))) {
                                sendHalfwayMessage(p, twitchMessage, sm);
                            } else if (sms.getIntervalsLeft() <= 0 && sms.getSoupMan().getMetadata("ispissed").stream().anyMatch(m -> "false".equals(m.value()))) {
                                sendAngryMessage(p, twitchMessage, sm);
                                makeSoupManAngry(p, sms.getSoupMan(), sm);
                            } else if (sms.getIntervalsLeft() <= 0 && p.getLocation().toVector().subtract(sms.getSoupMan().getLocation().toVector()).lengthSquared() <= 81) {
                                int x = (int)(Math.random() * 6) - 3;
                                int z = (int)(Math.random() * 6) - 3;
                                if (isAboveGround(p))  {
                                    p.getWorld().strikeLightning(p.getLocation().add(new Vector(x, 0, z)));
                                } else {
                                    p.getWorld().createExplosion(p.getLocation().add(x, 0, z), 2);
                                }
                            }
                            soupMan.setWanderingTowards(p.getLocation());
                            sms.setIntervalsLeft(sms.getIntervalsLeft() - 1);
                        }
                    }, 1 /*<-- the initial delay */, intervalTicks /*<-- the interval */).getTaskId())
                    .build());
        }
    }

    private static void sendHalfwayMessage(Player p, Message twitchMessage, SoupMan sm) {
        String halfwayMessage = String.format("<SoupMan>If I don't get my soup in %s minutes, I might get %sANGRY!%s", (sm.getMinutesTillAngry() / 2), ChatColor.RED, ChatColor.RESET);
        if (sm.getHalfwayMessage() != null) {
            halfwayMessage = beautifyActionMessage(sm.getHalfwayMessage().replace("{TIME}", String.valueOf(sm.getMinutesTillAngry() / 2)), twitchMessage, sm);
        }
        maybeSendPlayerSecondaryMessage(p, halfwayMessage, sm);
    }

    private static void sendAngryMessage(Player p, Message twitchMessage, SoupMan sm) {
        String angryMessage = String.format("<SoupMan>%sNO SOUP?! NOW YOU MUST DIE!%s", ChatColor.RED, ChatColor.RESET);
        if (sm.getAngryMessage() != null) {
            angryMessage = beautifyActionMessage(sm.getAngryMessage(), twitchMessage, sm);
        }
        maybeSendPlayerSecondaryMessage(p, angryMessage, sm);
    }

    private static void sendSatisfiedMessage(Player p, Message twitchMessage, SoupMan sm) {
        String satisfiedMessage = "<SoupMan>Ahh delicious! Thank you Traveler!";
        if (sm.getSatisfiedMessage() != null) {
            satisfiedMessage = beautifyActionMessage(sm.getSatisfiedMessage(), twitchMessage, sm);
        }
        maybeSendPlayerSecondaryMessage(p, satisfiedMessage, sm);
    }

    private List<MerchantRecipe> soupManTrades() {
        MerchantRecipe seedTrade = new MerchantRecipe(new ItemStack(Material.BEETROOT_SEEDS, 6), 1);
        seedTrade.addIngredient(new ItemStack(Material.WHEAT_SEEDS));
        MerchantRecipe soupTrade1 = new MerchantRecipe(new ItemStack(Material.EXPERIENCE_BOTTLE), 1);
        soupTrade1.addIngredient(new ItemStack(Material.BEETROOT_SOUP));
        MerchantRecipe soupTrade2 = new MerchantRecipe(new ItemStack(Material.EXPERIENCE_BOTTLE), 1);
        soupTrade2.addIngredient(new ItemStack(Material.MUSHROOM_STEW));
        MerchantRecipe soupTrade3 = new MerchantRecipe(new ItemStack(Material.EXPERIENCE_BOTTLE), 1);
        soupTrade3.addIngredient(new ItemStack(Material.RABBIT_STEW));
        MerchantRecipe soupTrade4 = new MerchantRecipe(new ItemStack(Material.EXPERIENCE_BOTTLE), 1);
        soupTrade4.addIngredient(new ItemStack(Material.SUSPICIOUS_STEW));
        return List.of(seedTrade, soupTrade1, soupTrade2, soupTrade3, soupTrade4);
    }

    private void makeSoupManAngry(Player p, WanderingTrader soupMan, SoupMan sm) {
        soupMan.customName(Component.text("ANGRY!", Style.style(NamedTextColor.RED)));
        soupMan.setMetadata("ispissed", new FixedMetadataValue(getPlugin(), "true"));
        soupMan.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 36000, 2));
    }

    public static void playerDied(Player p) {
        if (!stats.containsKey(p) || stats.get(p).getIntervalsLeft() >= 0) {
            return;
        }
        doneWithSoupMan(p);
    }

    public static void soupDelivered(Player p) {
        sendSatisfiedMessage(p, stats.get(p).getTwitchMessage(), stats.get(p).getAction());
        doneWithSoupMan(p);
    }

    public static void soupManDied(WanderingTrader soupMan) {
        Optional<Player> playerOptional = stats.entrySet().stream().filter(e -> e.getValue().getSoupMan().equals(soupMan)).map(e -> e.getKey()).findFirst();
        playerOptional.ifPresent(SoupManExecutor::doneWithSoupMan);
    }

    private static void doneWithSoupMan(Player p) {
        if (!stats.containsKey(p)) {
            return;
        }
        Bukkit.getScheduler().cancelTask(stats.get(p).taskId);
        stats.get(p).getSoupMan().remove();
        stats.remove(p);
    }

    public static boolean isPlayerTradingCorrectSoupMan(Player p, Entity rightClicked) {
        return rightClicked.getMetadata("soupman").stream().anyMatch(m -> "true".equals(m.value())) && stats.entrySet().stream().anyMatch(e -> e.getValue().getSoupMan().equals(rightClicked) && e.getKey().equals(p));
    }

    public static boolean wasASoupTrade(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) {
            return false;
        }
        return event.getCurrentItem().getType().equals(Material.EXPERIENCE_BOTTLE);
    }

    @Getter
    @Setter
    @Builder
    private static final class SoupManStats {
        private Integer taskId;
        private Integer intervalsLeft;
        private WanderingTrader soupMan;
        private SoupMan action;
        private Message twitchMessage;
    }
}
