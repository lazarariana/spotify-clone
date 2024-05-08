package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import commands.jsonReader.SeeMerchOutput;
import fileio.input.LibraryInput;
import fileio.input.MerchInput;
import fileio.input.UserInput;

import java.util.ArrayList;
import java.util.List;

import static commands.constants.UserCommandsEnums.DeleteUserMessagesEnum.NO_EXIST;
import static commands.constants.UserCommandsEnums.USERNAME;

public final class SeeMerchCommand implements Executable {
    /**
     * Executes a command to see the merchandise bought by a user and returns the result.
     *
     * This method takes a Command object as input and retrieves the user associated with the
     * command from the library.
     * If the user exists, it retrieves the names of the merchandise bought by the user and
     * returns them.
     *
     * @param command The command to be executed.
     * @return A BaseOutput object representing the result of the command execution. If the user
     * does not exist, it returns an appropriate message.
     */
    @Override
    public BaseOutput executeCommand(final Command command) {
        String message;
        String username = command.getUsername();
        UserInput user = LibraryInput.getInstance().getUserByName(username);
        List<String> merchNames = new ArrayList<>();

        if (user == null) {
            message = USERNAME + username + NO_EXIST.getName();
            return new Output(command, message);
        }

        for (MerchInput merch : user.getBoughtMerch()) {
            merchNames.add(merch.getName());
        }

        return new SeeMerchOutput(command, merchNames);
    }
}
