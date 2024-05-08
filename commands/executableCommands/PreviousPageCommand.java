package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import fileio.input.LibraryInput;
import fileio.input.UserInput;

import static commands.constants.UserCommandsEnums.PageNavigationMessagesEnum.NO_PAGES_BACKWARD;
import static commands.constants.UserCommandsEnums.PageNavigationMessagesEnum.SUCCESS_BACKWARD;
import static commands.constants.UserCommandsEnums.USER;

public class PreviousPageCommand implements Executable {
    /**
     * Executes a command to navigate backward in a user's page navigation history and returns
     * the result.
     *
     * This method takes a Command object as input and retrieves the user associated with the
     * command from the library.
     * If the user exists, it checks if the user's current page is the first page in the user's
     * page navigation history. If it is, it returns an appropriate message.
     * Otherwise, it decrements the user's last page index, updates the user's current page to
     * the previous page in the user's page navigation history, and returns a success message.
     *
     * @param command The command to be executed.
     * @return A BaseOutput object representing the result of the command execution. If the user
     * does not exist, it returns null.
     */
    @Override
    public BaseOutput executeCommand(final Command command) {
        String message;
        String username = command.getUsername();
        UserInput user = LibraryInput.getInstance().getUserByName(username);

        if (user != null) {
            if (user.getPage().equals(user.getPageNavigationHistory().get(0))) {
                message = NO_PAGES_BACKWARD.getName();
                return new Output(command, message);
            }

            user.setLastPageIndex(user.getLastPageIndex() - 1);
            user.setPage(user.getPageNavigationHistory().get(user.getLastPageIndex()));
            message = USER + username + SUCCESS_BACKWARD.getName();

            return new Output(command, message);
        }

        return null;
    }
}
