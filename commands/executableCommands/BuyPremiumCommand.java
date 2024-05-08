package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import fileio.input.LibraryInput;
import fileio.input.UserInput;

import java.util.ArrayList;

import static commands.constants.UserCommandsEnums.BuyPremiumMessagesEnum.ALREADY_PREMIUM;
import static commands.constants.UserCommandsEnums.BuyPremiumMessagesEnum.SUCCESS_BUY_PREMIUM;
import static commands.constants.UserCommandsEnums.DeleteUserMessagesEnum.NO_EXIST;
import static commands.constants.UserCommandsEnums.USERNAME;

public class BuyPremiumCommand implements Executable {
    /**
     * Executes a command to buy a premium subscription for a user and returns the result.
     *
     * This method takes a Command object as input and retrieves the user associated with the
     * command from the library.
     * If the user is already premium, it returns an appropriate message.
     * Otherwise, it sets the user's premium status to true, sets the user's credit to a predefined
     * credit value, resets the user's premium songs, and returns a success message.
     *
     * @param command The command to be executed.
     * @return A BaseOutput object representing the result of the command execution.
     */
    public BaseOutput executeCommand(final Command command) {
        String message;
        String username = command.getUsername();
        UserInput user = LibraryInput.getInstance().getUserByName(username);

        if (user == null) {
            message = USERNAME + username + NO_EXIST.getName();
            return new Output(command, message);
        }

        if (user.isPremium()) {
            message = username + ALREADY_PREMIUM.getName();
            return new Output(command, message);
        }

        user.setPremium(true);
        user.setPremiumSongs(new ArrayList<>());
        message = username + SUCCESS_BUY_PREMIUM.getName();

        return new Output(command, message);
    }
}
