package me.crazycranberry.twitchcraft.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import static me.crazycranberry.twitchcraft.managers.PollManager.createRandomPoll;

/** This is just for testing. Remove eventually. */
public class CreatePollCommand implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("cp")) {
            createRandomPoll();
        }
        return true;
    }
}
