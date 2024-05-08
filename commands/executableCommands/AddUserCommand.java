package commands.executableCommands;

import commands.constants.Constants;
import commands.constants.UserCommandsEnums;
import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.Output;
import fileio.input.ArtistInput;
import fileio.input.HostInput;
import fileio.input.LibraryInput;
import fileio.input.UserInput;

import java.util.List;

public final class AddUserCommand implements Executable {

  /**
   * The method first checks if the username is already taken by any artist, host, or user. If so,
   * it returns an error message. If the username is not taken, it creates a new user, artist, or
   * host based on the type specified in the command and adds it to the library.
   *
   * @param command The command to execute. This should be an instance of Command.
   * @return A BaseOutput object containing the result of the command execution.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {
    List<ArtistInput> artists = LibraryInput.getInstance().getArtists();
    List<HostInput> hosts = LibraryInput.getInstance().getHosts();
    List<UserInput> users = LibraryInput.getInstance().getUsers();
    String username = command.getUsername();
    String message;

    for (ArtistInput artist : artists) {
      if (artist.getUsername().equals(username)) {
        message =
            UserCommandsEnums.USERNAME
                + username
                + UserCommandsEnums.AddUserMessagesEnum.TAKEN.getName();
        return new Output(command, message);
      }
    }

    for (HostInput host : hosts) {
      if (host.getUsername().equals(username)) {
        message =
            UserCommandsEnums.USERNAME
                + username
                + UserCommandsEnums.AddUserMessagesEnum.TAKEN.getName();
        return new Output(command, message);
      }
    }

    for (UserInput user : users) {
      if (user.getUsername().equals(username)) {
        message =
            UserCommandsEnums.USERNAME
                + username
                + UserCommandsEnums.AddUserMessagesEnum.TAKEN.getName();
        return new Output(command, message);
      }
    }

    if (command.getType().equals(Constants.USER)) {
      UserInput newUser = new UserInput(command);
      newUser.setPremium(false);
      LibraryInput.getInstance().getUsers().add(newUser);
    } else if (command.getType().equals(Constants.ARTIST)) {
      ArtistInput newArtist = new ArtistInput(command);
      LibraryInput.getInstance().getArtists().add(newArtist);
    } else {
      HostInput newHost = new HostInput(command);
      LibraryInput.getInstance().getHosts().add(newHost);
    }

    message =
        UserCommandsEnums.USERNAME
            + username
            + UserCommandsEnums.AddUserMessagesEnum.SUCCESS_ADD.getName();

    return new Output(command, message);
  }
}
