package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import fileio.input.ArtistInput;
import fileio.input.HostInput;
import fileio.input.LibraryInput;
import fileio.input.UserInput;
import notifications.BalanceObserver;

import static commands.constants.UserCommandsEnums.DeleteUserMessagesEnum.NO_EXIST;
import static commands.constants.UserCommandsEnums.SubscribeMessagesEnum.NOT_ON_PAGE;
import static commands.constants.UserCommandsEnums.SubscribeMessagesEnum.SUCCESSFULLY;
import static commands.constants.UserCommandsEnums.SubscribeMessagesEnum.SUCCESS_SUBSCRIBE;
import static commands.constants.UserCommandsEnums.SubscribeMessagesEnum.SUCCESS_UNSUBSCRIBE;
import static commands.constants.UserCommandsEnums.USERNAME;

public class SubscribeCommand implements Executable {
    /**
     * This method takes a command as input and performs actions based on the command's details.
     * It first checks if the user exists. If the user does not exist, it returns an output
     * with an error message.
     * If the user exists, it checks if the user is an artist or a host.
     * If the user is an artist or a host, it checks if the user is already a subscriber.
     * If the user is a subscriber, it unsubscribes the user and returns a success message.
     * If the user is not a subscriber, it subscribes the user and returns a success message.
     * If the user is neither an artist nor a host, it returns null.
     *
     * @param command The command to be executed.
     * @return An instance of BaseOutput which contains the command and a message indicating the
     * result of the command execution.
     */
    @Override
    public BaseOutput executeCommand(final Command command) {
        String message;
        String username = command.getUsername();
        UserInput user = LibraryInput.getInstance().getUserByName(username);

        if (user == null) {
            message = USERNAME + username + NO_EXIST.getName();
            return new Output(command, message);
        }

        String name = user.getPage().getOwner();
        ArtistInput artist = LibraryInput.getInstance().getArtistByName(name);
        HostInput host = LibraryInput.getInstance().getHostByName(command.getUsername());

        if (artist == null && host == null) {
            message = NOT_ON_PAGE.getName();
            return new Output(command, message);
        }

        if (artist != null) {
            for (BalanceObserver subscriber : artist.getSubscribers()) {
                if (subscriber.equals(user)) {
                    artist.unsubscribe(user);

                    message = username + SUCCESS_UNSUBSCRIBE.getName() + name
                            + SUCCESSFULLY.getName();
                    return new Output(command, message);
                }
            }
            artist.subscribe(user);

            message = username + SUCCESS_SUBSCRIBE.getName() + name + SUCCESSFULLY.getName();
            return new Output(command, message);
        }

        if (host != null) {
            for (BalanceObserver subscriber : host.getSubscribers()) {
                if (subscriber.equals(user)) {
                    host.unsubscribe(user);

                    message = username + SUCCESS_UNSUBSCRIBE.getName() + name
                            + SUCCESSFULLY.getName();
                    return new Output(command, message);
                }
            }
            host.subscribe(user);

            message = username + SUCCESS_SUBSCRIBE.getName() + name + SUCCESSFULLY.getName();
            return new Output(command, message);
        }

        return null;
    }
}
