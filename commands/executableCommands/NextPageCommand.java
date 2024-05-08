package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import fileio.input.LibraryInput;
import fileio.input.UserInput;

import static commands.constants.UserCommandsEnums.PageNavigationMessagesEnum.NO_PAGES_FORWARD;
import static commands.constants.UserCommandsEnums.PageNavigationMessagesEnum.SUCCESS_FORWARD;
import static commands.constants.UserCommandsEnums.USER;


public final class NextPageCommand implements Executable {
    /**
     * Executes a command to navigate forward in a user's page navigation history and returns
     * the result.
     * <p>
     * This method takes a Command object as input and retrieves the user associated with the
     * command from the library.
     * If the user exists, it checks if the user's current page is the last page in the user's
     * page navigation history. If it is, it returns an appropriate message.
     * Otherwise, it increments the user's last page index, updates the user's current page to
     * the next page in the user's page navigation history, and returns a success message.
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
            int lastIndex = user.getPageNavigationHistory().size() - 1;

            if (user.getPage().equals(user.getPageNavigationHistory().get(lastIndex))) {
                message = NO_PAGES_FORWARD.getName();
                return new Output(command, message);
            }

            user.setLastPageIndex(user.getLastPageIndex() + 1);
            user.setPage(user.getPageNavigationHistory().get(user.getLastPageIndex()));
            message = USER + username + SUCCESS_FORWARD.getName();

            return new Output(command, message);
        }

        return null;
    }
}
