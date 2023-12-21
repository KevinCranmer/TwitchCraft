package me.crazycranberry.streamcraft.config.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;

import static me.crazycranberry.streamcraft.StreamCraft.logger;

@Getter
@Setter
@ToString
@AllArgsConstructor
public abstract class Action {
    private ActionType type;
    private Trigger trigger;
    private String target;

    public static Action fromYaml(LinkedHashMap<String, ?> input) {
        ActionType type = validateType(input.get("type"));
        Trigger trigger = validateTrigger(input.get("trigger"));
        String target = validateTarget(input.get("target"));
        if (type == null || trigger == null) {
            return null;
        }
        try {
            Method m = type.actionDefinition().getMethod("fromYaml", ActionType.class, Trigger.class, String.class, LinkedHashMap.class);
            return (Action) m.invoke(null, type, trigger, target, input);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public abstract String pollMessage(Action action);

    private static <T> String validateTarget(T target) {
        if (target == null) {
            return "*";
        }
        if (!(target instanceof String)) {
            logger().warning("An Action target was not a String.");
            return "*";
        }
        return (String) target;
    }

    private static <T> Trigger validateTrigger(T trigger) {
        if (!(trigger instanceof LinkedHashMap)) {
            logger().warning("An Action trigger was not an Object.");
            return null;
        }
        return Trigger.fromYaml((LinkedHashMap<String, ?>) trigger);
    }

    private static <T> ActionType validateType(T type) {
        if (!(type instanceof String)) {
            logger().warning("An Action type was not a String.");
            return null;
        }
        ActionType t = ActionType.fromValue((String) type);
        if (t == null) {
            logger().warning("The following Action type is invalid: " + type);
        }
        return t;
    }

    public static <T, R> R validateField(T field, Class<R> clazz, String fieldName) {
        if (!(clazz.isInstance(field))) {
            logger().warning("A " + fieldName + " was not a " + clazz.getName());
            return null;
        }
        return clazz.cast(field);
    }
}
