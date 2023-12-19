package me.crazycranberry.streamcraft.config.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum ActionType {
    ENTITY_SPAWN("ENTITY_SPAWN");

    private String value;

    public static ActionType fromValue(String actionType) {
        String a = actionType.toUpperCase().replaceAll("[^a-zA-Z]+", "_");

        for (ActionType b : ActionType.values()) {
            if (b.value.equals(a)) {
                return b;
            }
        }
        return null;
    }
}
