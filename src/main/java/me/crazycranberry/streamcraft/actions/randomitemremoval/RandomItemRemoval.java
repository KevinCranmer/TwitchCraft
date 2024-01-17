package me.crazycranberry.streamcraft.actions.randomitemremoval;

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
public class RandomItemRemoval extends Action {
    private Integer numStacks;
    private Integer numPerStack;

    @Builder
    private RandomItemRemoval(ActionType type, Trigger trigger, String target, String actionMessage, Boolean sendMessage, Integer numStacks, Integer numPerStack) {
        super(type, trigger, target, actionMessage, sendMessage);
        this.numStacks = numStacks;
        this.numPerStack = numPerStack;
    }

    @Override
    public String pollMessage() {
        if (this.getTrigger().getPollMessage() != null) {
            return this.getTrigger().getPollMessage();
        }
        return String.format("Remove %s x %s item%s", getNumPerStack(), getNumStacks(), getNumPerStack() * getNumStacks() > 1 ? "s" : "");
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, String actionMessage, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        Integer numStacks = validateField(input.get("num_stacks"), Integer.class, "num_stacks");
        Integer numPerStack = validateField(input.get("num_per_stack"), Integer.class, "num_per_stack");
        return RandomItemRemoval.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .sendMessage(sendMessage)
                .actionMessage(actionMessage)
                .numStacks(numStacks == null ? 1 : numStacks)
                .numPerStack(numPerStack == null ? 1 : numPerStack)
                .build();
    }
}
