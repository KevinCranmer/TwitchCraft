package me.crazycranberry.twitchcraft.config;

public class ActionUtils {
    public static boolean anyNull(Object ... objs) {
        for (Object o : objs) {
            if (o == null) {
                return true;
            }
        }
        return false;
    }

    public static boolean allNull(Object ... objs) {
        for (Object o : objs) {
            if (o != null) {
                return false;
            }
        }
        return true;
    }
}
