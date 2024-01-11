package me.crazycranberry.streamcraft.actions.customcommand;

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
public class CustomCommand extends Action {
    private String command;
    private String pollMessage;

    @Builder
    private CustomCommand(ActionType type, Trigger trigger, String target, Boolean sendMessage, String command, String pollMessage) {
        super(type, trigger, target, sendMessage);
        this.command = command;
        this.pollMessage = pollMessage;
    }

    @Override
    public String pollMessage() {
        return command.substring(0, 24);
    }

    /** Any Action subclass MUST implement this method or it will not be able to be created in Action.java. */
    public static Action fromYaml(ActionType actionType, Trigger trigger, String target, Boolean sendMessage, LinkedHashMap<String, ?> input) {
        String command = validateField(input.get("command"), String.class, "command");
        String pollMessage = validateField(input.get("poll_message"), String.class, "poll_message");
        if (command == null) {
            return null;
        }
        command = command.startsWith("/") ? command.substring(1) : command;
        if (pollMessage == null) {
            pollMessage = command.substring(0, Math.min(25, command.length()));
        }
        return CustomCommand.builder()
                .type(actionType)
                .trigger(trigger)
                .target(target)
                .sendMessage(sendMessage)
                .command(command)
                .pollMessage(pollMessage)
                .build();
    }
}
