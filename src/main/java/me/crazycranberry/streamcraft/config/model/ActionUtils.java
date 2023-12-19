package me.crazycranberry.streamcraft.config.model;

public class ActionUtils {
    public static boolean anyNull(Object ... objs) {
        for (Object o : objs) {
            if (o == null) {
                return true;
            }
        }
        return false;
    }
}
