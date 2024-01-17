package me.crazycranberry.twitchcraft.actions.soupman;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import me.crazycranberry.twitchcraft.config.Action;
import me.crazycranberry.twitchcraft.config.ActionType;
import me.crazycranberry.twitchcraft.config.Trigger;

import java.util.LinkedHashMap;

@Getter
@Setter
@ToString(callSuper = true)
public class SoupMan extends Action {
    private Integer minutesTillAngry;
    private String halfwayMessage;
    private String angryMessage;
    private String satisfiedMessage;

    @Builder
    private SoupMan(ActionType type, Trigger trigger, String target, String actionMessage, Boolean sendMessage, Integer minutesTillAngry, String halfwayMessage, String angryMessage, String satisfiedMessage) {
        super(type, trigger, target, actionMessage, sendMessage);
        this.minutesTillAngry = minutesTillAngry;
        this.halfwayMessage = halfwayMessage;
        this.angryMessage = angryMessage;
        this.satisfiedMessage = satisfiedMessage;
    }

    @Override
    public String pollMessage() {
        if (this.getTrigger().getPollMessage() != null) {
            return this.getTrigger().getPollMessage();
        }
        return "He just wants some soup";
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, String actionMessage, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        Integer minutesTillAngry = validateField(input.get("minutes_till_angry"), Integer.class, "minutes_till_angry");
        String halfwayMessage = validateField(input.get("halfway_message"), String.class, "halfway_message", false);
        String angryMessage = validateField(input.get("angry_message"), String.class, "angry_message", false);
        String satisfiedMessage = validateField(input.get("satisfied_message"), String.class, "satisfied_message", false);
        if (minutesTillAngry == null) {
            return null;
        }
        return SoupMan.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .sendMessage(sendMessage)
                .actionMessage(actionMessage)
                .halfwayMessage(halfwayMessage)
                .angryMessage(angryMessage)
                .satisfiedMessage(satisfiedMessage)
                .minutesTillAngry(minutesTillAngry)
                .build();
    }
}
