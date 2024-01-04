package me.crazycranberry.streamcraft.actions.potioneffect;

import me.crazycranberry.streamcraft.actions.Executor;
import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.twitch.websocket.model.message.Message;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.stream.Stream;

import static me.crazycranberry.streamcraft.StreamCraft.logger;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.maybeSendPlayerMessage;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.randomFromList;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.triggerer;
import static me.crazycranberry.streamcraft.actions.ExecutorUtils.getTargetedPlayers;

public class PotionEffectExecutor implements Executor {
    @Override
    public void execute(Message twitchMessage, Action action) {
        if (!(action instanceof PotionEffect)) {
            logger().warning("Somehow the following action was passed to " + this.getClass().getName() + ": " + action + "\nAborting execution.");
            return;
        }
        PotionEffect pe = (PotionEffect) action;
        for (Player p : getTargetedPlayers(pe)) {
            PotionEffectType type = pe.getPotionType();
            if (type == null && pe.getPotionRandom() != null) {
                type = getRandomPotionType(pe.getPotionRandom());
            }
            String isRandomPotion = pe.getPotionRandom() == null ? "" : pe.getPotionRandom().name() + " ";
            maybeSendPlayerMessage(p, String.format("Giving you the %s%s%s%s potion effect, courtesy of %s%s%s", ChatColor.GOLD, isRandomPotion, type.getName(), ChatColor.RESET, ChatColor.GOLD, triggerer(twitchMessage, action), ChatColor.RESET));
            if (!applyPossibleInstantEffects(p, type, pe.getLevel())) {
                p.addPotionEffect(new org.bukkit.potion.PotionEffect(type, pe.getDurationSeconds() * 20, pe.getLevel() - 1));
            }
        }
    }

    private boolean applyPossibleInstantEffects(Player p, PotionEffectType type, int amplifier) {
        if (type.equals(PotionEffectType.HEAL)) {
            p.setHealth(Math.min(p.getHealth() + 4 * amplifier, p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue()));
            return true;
        } else if (type.equals(PotionEffectType.HEALTH_BOOST)) {
            p.setAbsorptionAmount(p.getAbsorptionAmount() + 4 * amplifier);
            return true;
        } else if (type.equals(PotionEffectType.HARM)) {
            p.damage(4 * amplifier);
            return true;
        }
        return false;
    }

    private PotionEffectType getRandomPotionType(PotionEffect.PotionRandom potionRandom) {
        switch (potionRandom) {
            case RANDOM_BAD:
                return randomFromList(badPotionEffects);
            case RANDOM_GOOD:
                return randomFromList(goodPotionEffects);
            default:
                return randomFromList(Stream.concat(badPotionEffects.stream(), goodPotionEffects.stream()).toList());
        }
    }


    private static final List<PotionEffectType> badPotionEffects = List.of(
            PotionEffectType.POISON,
            PotionEffectType.BLINDNESS,
            PotionEffectType.CONFUSION,
            PotionEffectType.DARKNESS,
            PotionEffectType.HARM,
            PotionEffectType.HUNGER,
            PotionEffectType.LEVITATION,
            PotionEffectType.SLOW,
            PotionEffectType.SLOW_DIGGING,
            PotionEffectType.WEAKNESS,
            PotionEffectType.WITHER,
            PotionEffectType.GLOWING);

    private static final List<PotionEffectType> goodPotionEffects = List.of(
            PotionEffectType.SPEED,
            PotionEffectType.FAST_DIGGING,
            PotionEffectType.INCREASE_DAMAGE,
            PotionEffectType.HEAL,
            PotionEffectType.JUMP,
            PotionEffectType.REGENERATION,
            PotionEffectType.DAMAGE_RESISTANCE,
            PotionEffectType.FIRE_RESISTANCE,
            PotionEffectType.WATER_BREATHING,
            PotionEffectType.INVISIBILITY,
            PotionEffectType.NIGHT_VISION,
            PotionEffectType.HEALTH_BOOST,
            PotionEffectType.ABSORPTION,
            PotionEffectType.SATURATION,
            PotionEffectType.SLOW_FALLING,
            PotionEffectType.CONDUIT_POWER,
            PotionEffectType.DOLPHINS_GRACE);
}
