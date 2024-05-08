package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import fileio.input.LibraryInput;
import fileio.input.UserInput;
import monetization.MonetizationFactory;

import static commands.constants.UserCommandsEnums.CancelPremiumMessagesEnum.NOT_PREMIUM;
import static commands.constants.UserCommandsEnums.CancelPremiumMessagesEnum.SUCCESS_CANCEL_PREMIUM;
import static commands.constants.UserCommandsEnums.DeleteUserMessagesEnum.NO_EXIST;
import static commands.constants.UserCommandsEnums.USERNAME;

public class CancelPremiumCommand implements Executable {

    /**
     * Executes a command to cancel the premium subscription of a user and returns the result.
     * <p>
     * This method takes a Command object as input and retrieves the user associated with the
     * command from the library.
     * If the user is not premium, it returns an appropriate message.
     * Otherwise, it sets the user's premium status to false, calculates the revenue for each
     * artist based on the user's listened songs, and updates the artist's and song's revenues.
     * It then resets the user's credit and premium songs and returns a success message.
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

        if (!user.isPremium()) {
            message = username + NOT_PREMIUM.getName();
            return new Output(command, message);
        }

        MonetizationFactory.createMonetizationStrategy(user.isPremium()).monetize(user);

        user.setPremium(false);
        message = username + SUCCESS_CANCEL_PREMIUM.getName();

        return new Output(command, message);
    }
}
