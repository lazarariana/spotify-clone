package commands.executableCommands;

import commands.jsonReader.BaseOutput;
import commands.jsonReader.Command;
import commands.jsonReader.UsersResult;
import fileio.input.ArtistInput;
import fileio.input.HostInput;
import fileio.input.LibraryInput;
import fileio.input.UserInput;

import java.util.ArrayList;

public final class GetAllUsersCommand implements Executable {

  /**
   * <p>The method first checks if the user, artist, or host with the given username exists. If not,
   * it returns an error message. If the user is not an artist, it returns an error message. It then
   * checks if a merchandise with the given name already exists for the artist. If so, it returns an
   * error message. It checks if the price of the merchandise is valid (greater than or equal to 0).
   * If not, it returns an error message. If all checks pass, it creates a new merchandise and adds
   * it to the artist's merchandise list.
   *
   * @param command The command to execute. This should be an instance of Command.
   * @return A BaseOutput object containing the result of the command execution.
   */
  @Override
  public BaseOutput executeCommand(final Command command) {
    ArrayList<String> allUsers = new ArrayList<>();

    for (UserInput user : LibraryInput.getInstance().getUsers()) {
      allUsers.add(user.getUsername());
    }

    for (ArtistInput artist : LibraryInput.getInstance().getArtists()) {
      allUsers.add(artist.getUsername());
    }

    for (HostInput host : LibraryInput.getInstance().getHosts()) {
      allUsers.add(host.getUsername());
    }

    UsersResult output = new UsersResult(command);
    output.setResult(allUsers);
    return output;
  }
}
