package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.GetNotificationsOutput;
import fileio.input.LibraryInput;
import fileio.input.UserInput;
import notifications.Notification;

import java.util.ArrayList;
import java.util.List;

public final class GetNotificationsCommand implements Executable {

    /**
     * Executes a command to get the notifications of a user and returns the result.
     *
     * This method takes a Command object as input and retrieves the user associated with the
     * command from the library. If the user exists, it retrieves the user's notifications, clears
     * the user's notifications, and returns the retrieved notifications.
     *
     * @param command The command to be executed.
     * @return A BaseOutput object representing the result of the command execution. If the user
     * does not exist, it returns null.
     */
    @Override
    public BaseOutput executeCommand(final Command command) {
        String username = command.getUsername();
        UserInput user = LibraryInput.getInstance().getUserByName(username);


        if (user != null) {
            List<Notification> notifications = new ArrayList(user.getNotifications());
            user.setNotifications(new ArrayList<>());
            return new GetNotificationsOutput(command, notifications);
        }

        return null;
    }
}
