package me.crazycranberry.twitchcraft.actions.customcommand;

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
public class CustomCommand extends Action {
    private String command;

    @Builder
    private CustomCommand(ActionType type, Trigger trigger, String target, String actionMessage, Boolean sendMessage, String command) {
        super(type, trigger, target, actionMessage, sendMessage);
        this.command = command;
    }

    @Override
    public String pollMessage() {
        return this.getTrigger().getPollMessage() == null ? command.substring(0, 24) : this.getTrigger().getPollMessage();
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, String actionMessage, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        String command = validateField(input.get("command"), String.class, "command");
        if (command == null) {
            return null;
        }
        command = command.startsWith("/") ? command.substring(1) : command;
        return CustomCommand.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .sendMessage(sendMessage)
                .actionMessage(actionMessage)
                .command(command)
                .build();
    }
}
