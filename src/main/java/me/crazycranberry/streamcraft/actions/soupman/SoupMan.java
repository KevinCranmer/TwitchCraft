package me.crazycranberry.streamcraft.actions.soupman;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.crazycranberry.streamcraft.config.Action;
import me.crazycranberry.streamcraft.config.ActionType;
import me.crazycranberry.streamcraft.config.Trigger;

import java.util.LinkedHashMap;

@Getter
@Setter
@ToString(callSuper = true)
public class SoupMan extends Action {
    private Integer minutesTillAngry;

    @Builder
    private SoupMan(ActionType type, Trigger trigger, String target, Boolean sendMessage, Integer minutesTillAngry) {
        super(type, trigger, target, sendMessage);
        this.minutesTillAngry = minutesTillAngry;
    }

    @Override
    public String pollMessage() {
        if (this.getTrigger().getPollMessage() != null) {
            return this.getTrigger().getPollMessage();
        }
        return "He just wants some soup";
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        Integer minutesTillAngry = validateField(input.get("minutes_till_angry"), Integer.class, "minutes_till_angry");
        if (minutesTillAngry == null) {
            return null;
        }
        return SoupMan.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .sendMessage(sendMessage)
                .minutesTillAngry(minutesTillAngry)
                .build();
    }
}
